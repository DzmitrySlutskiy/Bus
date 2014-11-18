package by.slutskiy.busschedule.services;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import by.slutskiy.busschedule.BuildConfig;
import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.providers.contracts.BaseContract;
import by.slutskiy.busschedule.providers.contracts.NewsContract;
import by.slutskiy.busschedule.providers.contracts.RouteContract;
import by.slutskiy.busschedule.providers.contracts.RouteListContract;
import by.slutskiy.busschedule.providers.contracts.StopContract;
import by.slutskiy.busschedule.providers.contracts.TimeListContract;
import by.slutskiy.busschedule.providers.contracts.TypeContract;
import by.slutskiy.busschedule.utils.BroadcastUtils;
import by.slutskiy.busschedule.utils.IOUtils;
import by.slutskiy.busschedule.utils.NotificationUtils;
import by.slutskiy.busschedule.utils.PreferenceUtils;
import by.slutskiy.busschedule.utils.StringUtils;
import by.slutskiy.busschedule.utils.UpdateUtils;
import by.slutskiy.busschedule.utils.XLSHelper;
import jxl.Cell;
import jxl.Sheet;
import jxl.read.biff.BiffException;

/**
 * UpdateService
 * Version 1.0
 * 27.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class UpdateService extends IntentService implements IOUtils.LoadProgressListener {

    private static final String TAG = UpdateService.class.getSimpleName();

    private static final String BUS_NUMBER_PATTERN = "^[0-9]{1,3}(Э|э)?$";
    public static final String TYPE_DELIMITER = ";";
    private static final String ROUTE_DIVIDER = "@";

    /**
     * full stop list, String Key - stop name, Integer Value - ID record in DB
     */
    private HashMap<String, Integer> mStopMap;
    /**
     * type list for all bus ("вых", "раб", "суб", "воскр" и т.д.)
     * , String Key - type string, Integer Value - ID record in DB
     */
    private HashMap<String, Integer> mTypeMap;
    private HashMap<String, Integer> mFullTypeMap;
    /**
     * full route list, String Key - route name, Integer Value - ID record in DB
     */
    private HashMap<String, Integer> mRouteMap;

    /**
     * type list only for current stop in bus route
     */
    private final ArrayList<ScheduleDayType> mDayTypes = new ArrayList<ScheduleDayType>();

    private XLSHelper mXlsHelper = null;

    private int mLastRouteId;
    private int mCurrentRouteId;
    private int mStopIndex;             //stop index in bus route (increment from start stop)
    private int mLastHourColumnIndex;
    private int mTypeCounter = 0;
    private String mBusNumber;          //bus number like "16" or "40Э"

    /**
     * call Sheet method getRows and getColumns take more time
     * large number of calls increase parsing sheet
     * but for current sheet can save value in some variable and use it
     */
    private int mSheetRows;             //count row in current parse sheet
    private int mSheetColumns;          //count column in current parse sheet

    /**
     * default tag "A" in the sheet at column index = 0, but not always
     */
    private int mTagAColumnIndex;

    private int mNotificationId;

    private final Pattern mBusNumberPattern;

    private ContentResolver mResolver;
    private ArrayList<ContentProviderOperation> mOperations;
    private int mCurrentOperationIndex;

    private final int OPERATION_MASK = 0xF0000000;

    /*  public constructors */

    public UpdateService() {
        super(TAG);
        mBusNumberPattern = Pattern.compile(BUS_NUMBER_PATTERN);
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               android.content.Context#startService(android.content.Intent)}.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        long startUpdate = System.currentTimeMillis();
        mResolver = getApplicationContext().getContentResolver();

        initLists();

        Date lastUpdate = IOUtils.getLastModifiedDate(UpdateUtils.BUS_PARK_URL + UpdateUtils.FILE_NAME);

        if (lastUpdate == null) {
            sendMessage(UpdateUtils.MSG_NO_INTERNET);
            return;
        }

        mNotificationId = NotificationUtils.createNotification(getApplicationContext(),
                getString(R.string.notification_title_update),
                getString(R.string.notification_message_try_update),
                R.drawable.ic_launcher,
                true);

        String filePath = getApplicationContext().getFilesDir().getPath() + "/" + UpdateUtils.FILE_NAME;

        if (IOUtils.saveUrlToFile(UpdateUtils.BUS_PARK_URL + UpdateUtils.FILE_NAME, filePath, this)) {
            try {

                NotificationUtils.updateNotification(mNotificationId,
                        getString(R.string.notification_title_update),
                        getString(R.string.notification_message_open_file),true);

                NotificationUtils.showIndeterminateProgress(mNotificationId);

                Log.d(TAG, "Open xls file");
                mXlsHelper = new XLSHelper(filePath);               //open xls file
                Log.d(TAG, "Xls file opened");

                NotificationUtils.updateNotification(mNotificationId,
                        getString(R.string.notification_title_update),
                        getString(R.string.notification_message_update_db),true);

                extractNews(mXlsHelper);                            //get news from first sheet

                int sheetCount;
                if (BuildConfig.SHEET_COUNT > 1) {
                    sheetCount = mXlsHelper.getSheetCount();
                } else {
                    sheetCount = mXlsHelper.getSheetCount();
                }

                double percent = 0.0;
                if (sheetCount > 0) {
                    percent = 100.0 / sheetCount;
                }

                for (int i = 0; i < sheetCount; i++) {
                    parseSheet(mXlsHelper.getSheet(i));
                    //show progress - every sheet more than 1 percent
                    NotificationUtils.showProgress(mNotificationId, 100, (int) (percent * (i + 1)));
                }
                closeXlsFile();

                long currentTime = System.currentTimeMillis();
                Log.d(TAG, "try apply batch. Size: " + mOperations.size());
                mResolver.applyBatch(BaseContract.AUTHORITY, mOperations);
                Log.d(TAG, "Apply successful. used time(ms): " + (System.currentTimeMillis() - currentTime));
                PreferenceUtils.setUpdateDate(getApplicationContext(), lastUpdate);
                Log.d(TAG, "Full time update (ms): " + (System.currentTimeMillis() - startUpdate));
            } catch (IOException e) {
                processError(UpdateUtils.MSG_IO_ERROR, e.getMessage());
            } catch (BiffException e) {
                processError(UpdateUtils.MSG_UPDATE_BIFF_ERROR, e.getMessage());
            } catch (FileStructureErrorException e) {
                processError(UpdateUtils.MSG_UPDATE_FILE_STRUCTURE_ERROR, e.getMessage());
            } catch (DBUpdateWorkException e) {
                processError(UpdateUtils.MSG_UPDATE_DB_WORK_ERROR, e.getMessage());
            } catch (RemoteException e) {
                processError(UpdateUtils.MSG_UPDATE_DB_WORK_ERROR, e.getMessage());
            } catch (OperationApplicationException e) {
                processError(UpdateUtils.MSG_UPDATE_DB_WORK_ERROR, e.getMessage());
            } finally {
                closeXlsFile();
                NotificationUtils.cancelNotification(mNotificationId);

                IOUtils.delFile(filePath);                       //delete temporary xls file

                sendMessage(UpdateUtils.MSG_UPDATE_FINISH);

                clearReference();

                PreferenceUtils.setUpdateState(getApplicationContext(), false);
            }
        }
    }

    private void closeXlsFile() {
        if (mXlsHelper != null) {
            mXlsHelper.closeWorkbook();         //close workbook
            mXlsHelper = null;
        }
    }

    private void processError(int type, String msg) {
        Log.d(TAG, msg);
        sendMessage(type, msg);
    }

    private void initOperationList() {
        mOperations = new ArrayList<ContentProviderOperation>();
        mCurrentOperationIndex = 0;

        //this delete all data from table TimeList
        addOperation(ContentProviderOperation.newDelete(TimeListContract.CONTENT_URI).build());

        //this delete all data from table RouteList
        addOperation(ContentProviderOperation.newDelete(RouteListContract.CONTENT_URI).build());

        //this delete all data from table News
        addOperation(ContentProviderOperation.newDelete(NewsContract.CONTENT_URI).build());

        //this delete all data from table Routes
        addOperation(ContentProviderOperation.newDelete(RouteContract.CONTENT_URI).build());
    }

    /**
     * parse current sheet
     *
     * @param sheet current sheet for parsing
     * @throws FileStructureErrorException if parsing stop failed (out of bound or any error)
     * @throws DBUpdateWorkException       if adding to database return error
     */
    private void parseSheet(Sheet sheet) throws FileStructureErrorException, DBUpdateWorkException {
        mSheetRows = sheet.getRows();
        mSheetColumns = sheet.getColumns();
        XLSHelper.updateSheetSize(sheet);
        XLSHelper.updateMergedCell(sheet);

        /*  parsing bus number from sheet name  */
        mBusNumber = StringUtils.deleteSubString(sheet.getName(), XLSHelper.DELETING_SUBSTRING);

        if (isBusNumber(mBusNumber)) {
            Log.d(TAG, "Get new bus:" + mBusNumber);

            int rowIndex = 0;
            mStopIndex = 0;
            mLastRouteId = - 1;
            mCurrentRouteId = - 1;                                          //reset routeID

            /*Иногда расписание находится/начинается не в 1ом столбце и даже строке
            * необходимо найти тег "А" пройдя по строкам и столбцам
            * обычно расписание начинается с 0 индекса - XLSHelper.TAG_A_COLUMN_INDEX_DEFAULT
            * но у автобуса №6 начинается с 2 индекса*/
            mTagAColumnIndex = XLSHelper.TAG_A_COLUMN_INDEX_DEFAULT;
            for (int i = 0; i < mSheetRows; i++) {
                for (int j = 0; j < mSheetColumns; j++) {
                    Cell cell = getCell(sheet, j, i);
                    if (cell.getContents().compareTo(XLSHelper.TAG_A) == 0) {//if "A" in that cell
                        mTagAColumnIndex = j;
                        i = mSheetRows;
                        break;
                    }
                }
            }

            /* проход по строкам в поисках тегов "А" - блок расписания на текущую
            * остановку маршрута и запус разбора остановки - parseStop    */
            while (rowIndex < mSheetRows) {
                String cellContent = XLSHelper.getCellContent(sheet, mTagAColumnIndex, rowIndex);
                if (cellContent.compareTo(XLSHelper.TAG_A) == 0) {   //if A char in that cell
                    rowIndex += parseStop(sheet, rowIndex);
                } else {
                    rowIndex++;
                }
            }
        } else {
            Log.d(TAG, "Get sheet name:" + mBusNumber);
        }
    }

    /**
     * parse stop info (schedule for current bus)
     *
     * @param sheet    current sheet
     * @param rowIndex row index where stop info found (tag "A" found)
     * @return stop size - count rows used for current stop info
     * @throws FileStructureErrorException if parsing stop failed (out of bound or any error)
     */
    private int parseStop(Sheet sheet, int rowIndex) throws FileStructureErrorException {

        /*stopSize - размер блока расписания в высоту (RowSize)
        * + 2 - так как 2 строки от тега "А" до номера автобуса*/
        int stopSize = XLSHelper.getMergedCellSize(sheet, mTagAColumnIndex, rowIndex +
                XLSHelper.ROUTE_NAME_ROW_OFFSET, XLSHelper.MergeSearchType.Row) +
                XLSHelper.STOP_ROW_OFFSET;

        /*   processing stop name   */
        String cellContent = XLSHelper.getCellContent(sheet, mTagAColumnIndex +
                XLSHelper.STOP_COLUMN_INDEX, rowIndex);
        String stopName = StringUtils.deleteSubString(cellContent, XLSHelper.DELETING_SUBSTRING);

        int stopId = checkStop(stopName);

        /*   processing route info   */
        String routeName = XLSHelper.getCellContent(sheet, mTagAColumnIndex +
                XLSHelper.ROUTE_NAME_COLUMN_OFFSET, rowIndex + XLSHelper.ROUTE_NAME_ROW_OFFSET);
        mCurrentRouteId = checkRoute(routeName);
        if (mCurrentRouteId != mLastRouteId) {
            mStopIndex = 0;
            mLastRouteId = mCurrentRouteId;
        }
        ContentProviderOperation.Builder currentOperation = ContentProviderOperation.newInsert(RouteListContract.CONTENT_URI);

        if ((mCurrentRouteId & OPERATION_MASK) != 0) {
            currentOperation.withValueBackReference(RouteListContract.COLUMN_ROUTE_ID,
                    getRealIndex(mCurrentRouteId));
        } else {
            currentOperation.withValue(RouteListContract.COLUMN_ROUTE_ID, mCurrentRouteId);
        }

        if ((stopId & OPERATION_MASK) != 0) {
            currentOperation.withValueBackReference(RouteListContract.COLUMN_STOP_ID,
                    getRealIndex(stopId));
        } else {
            currentOperation.withValue(RouteListContract.COLUMN_STOP_ID, stopId);
        }
        currentOperation.withValue(RouteListContract.COLUMN_STOP_INDEX, mStopIndex);

        int mRouteListId = addOperation(currentOperation.build());

        /*   day schedule type processing   */
        mLastHourColumnIndex = XLSHelper.getMergedCellSize(sheet, mTagAColumnIndex +
                XLSHelper.ROUTE_NAME_COLUMN_OFFSET, rowIndex +
                XLSHelper.ROUTE_NAME_ROW_OFFSET + 1, XLSHelper.MergeSearchType.Column);
        mDayTypes.clear();

        fillDayType(sheet, rowIndex, stopSize);

        for (int hourIndex = 1; hourIndex < mLastHourColumnIndex; hourIndex++) {
            String minStr = "";
            String typeStr = "";
            Cell cell = getCell(sheet, hourIndex, rowIndex + 1);
            int hour = StringUtils.strToInt(cell.getContents().trim());
            int minuteRowIndex = rowIndex + 4;

            for (int typeIndex = 0; typeIndex < mDayTypes.size(); typeIndex++) {
                ScheduleDayType type = mDayTypes.get(typeIndex);

                typeStr += type.typeString;

                for (int rIndex = minuteRowIndex; rIndex < minuteRowIndex + type.typeRowSize;
                     rIndex++) {

                    minStr += XLSHelper.processingMinutes(
                            getCell(sheet, hourIndex, rIndex).getContents()) + " ";
                }
                //add delimiter between types
                if (typeIndex < (mDayTypes.size() - 1)) {
                    typeStr = typeStr.trim() + TYPE_DELIMITER;
                    minStr = minStr.trim() + TYPE_DELIMITER;
                }

                minuteRowIndex += type.typeRowSize;
            }
            int typeId;
            if (mFullTypeMap.containsKey(typeStr)) {
                typeId = mFullTypeMap.get(typeStr);
            } else {
                typeId = addOperation(ContentProviderOperation.newInsert(TypeContract.CONTENT_URI)
                        .withValue(TypeContract.COLUMN_TYPE_NAME, typeStr)
                        .build());
                mFullTypeMap.put(typeStr, typeId);
            }

            currentOperation = ContentProviderOperation.newInsert(TimeListContract.CONTENT_URI);
            currentOperation.withValue(TimeListContract.COLUMN_HOUR, hour)
                    .withValue(TimeListContract.COLUMN_MINUTES, minStr.trim());

            if ((typeId & OPERATION_MASK) != 0) {
                currentOperation.withValueBackReference(TimeListContract.COLUMN_TYPE_ID,
                        getRealIndex(typeId));
            } else {
                currentOperation.withValue(TimeListContract.COLUMN_TYPE_ID, typeId);
            }
            currentOperation.withValueBackReference(TimeListContract.COLUMN_ROUTE_LIST_ID, getRealIndex(mRouteListId));
            addOperation(currentOperation.build());
        }
        mStopIndex++;
        return stopSize;
    }

    /**
     * fill day type info for current stop to ArrayList mDayTypes
     *
     * @param sheet    current sheet
     * @param rowIndex row index
     * @param stopSize stop size (row count used for stop info)
     */
    private void fillDayType(Sheet sheet, int rowIndex, int stopSize) {
        for (int typeIndex = rowIndex + XLSHelper.TYPE_DAY_ROW_OFFSET; typeIndex < rowIndex + stopSize; ) {
            int lastColumn = mTagAColumnIndex + mLastHourColumnIndex;

            String cellContent = XLSHelper.getCellContent(sheet, lastColumn, typeIndex, false);

            /* В расписании иногда наблюдается сдвиг колонок "по раб/вых и т.д." правее на 1 столбец
            *  т.к. обычно размещается в столбце "1" часа, а некоторые автобусы ходят и после часа
            *  пример автобус №15 К-ТР ВОСТОК - происходит "разрушение" структуры расписания
            *  в сравнении с другими остановками. Поля "по вых" как-будто вырваны в сторону.
            *  поэтому если в ячейке типа расписания находим число, читаем данные из правого столбца
            * */
            if ((StringUtils.strToInt(cellContent, Integer.MAX_VALUE) != Integer.MAX_VALUE) &&
                    (lastColumn + 1 < mSheetColumns)) {
                cellContent = XLSHelper.getCellContent(sheet, ++ lastColumn, typeIndex, false);
            }
            int typeRowSize = XLSHelper.getMergedCellSize(sheet, lastColumn,
                    typeIndex, XLSHelper.MergeSearchType.Row);

            if (XLSHelper.isBadScheduleType(cellContent)) {
                if (cellContent.equals(StringUtils.EMPTY_STRING)) {
                    cellContent = XLSHelper.DEFAULT_SCHEDULE_TYPE;
                } else {

                    //бывает что в расписании "по раб" и "по вых" разбиты на 2 ячейки
                    //обыгрываем эту ситуацию чтением сразу 2х ячеек подряд если в первой
                    //считали только слово "по"!!
                    cellContent = XLSHelper.getCellContent(sheet, lastColumn, typeIndex + 1, false);

                    //в некоторых позициях не проставлено значение типа
                    // рассписания (вместо по раб. 3 пустые ячейки и т.п.)
                    if (typeRowSize + typeIndex + 1 < mSheetRows) {//не допускаем выход за границы
                        typeRowSize++;
                    }
                }
            }

            /*  удаляем лишние предлоги и символы    */
            cellContent = XLSHelper.processingDayType(cellContent);

            /*  сохраняем в mTypeMap значение типа (если его там еще нет)*/
            if (! mTypeMap.containsKey(cellContent)) {
                mTypeMap.put(cellContent, mTypeCounter++);
            }
            ScheduleDayType type = new ScheduleDayType();
            type.typeRowSize = typeRowSize;
            type.typeString = cellContent;

            /*  save day type for current stop in bus route */
            mDayTypes.add(type);

            typeIndex += typeRowSize;
        }
    }

    /**
     * check route name - split route to first and last stops in route, add this route to database
     * and save route id info in list mRouteMap
     *
     * @param routeName route name for checking
     * @return id record in database about this route
     */
    private int checkRoute(String routeName) {
        int routeId;

        String route = StringUtils.deleteSubString(routeName, XLSHelper.DELETING_SUBSTRING);
        /*   full route name with bus number   */
        String fullRouteName = mBusNumber + ROUTE_DIVIDER + routeName;

        /*  check route list mRouteMap for info about route fullRouteName */
        if (! mRouteMap.containsKey(fullRouteName)) {

            /*   if this route not exists in route list add this route to database   */
            ContentValues values = new ContentValues();
            values.put(RouteContract.COLUMN_BUS, mBusNumber);
            values.put(RouteContract.COLUMN_ROUTE_NAME, route);

            routeId = addOperation(ContentProviderOperation.newInsert(RouteContract.CONTENT_URI)
                    .withValues(values)
                    .build());
            mRouteMap.put(fullRouteName, routeId);
        } else {    //if route contains in list return route id
            routeId = mRouteMap.get(fullRouteName);
        }
        return routeId;
    }

    private void initRouteList() {
        mRouteMap = new HashMap<String, Integer>();
    }

    private int getRealIndex(int maskedIndex) {
        return maskedIndex & (~ OPERATION_MASK);
    }

    private int addOperation(ContentProviderOperation operation) {
        mOperations.add(operation);

        return mCurrentOperationIndex++ | OPERATION_MASK;
    }

    private void fillMapAndCloseCursor(HashMap<String, Integer> map, String columnsKey, String columnValue, Cursor cursor) {
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        int value = cursor.getInt(cursor.getColumnIndex(columnValue));
                        String key = cursor.getString(cursor.getColumnIndex(columnsKey));
                        map.put(key, value);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
    }

    private void initStopList() {
        mStopMap = new HashMap<String, Integer>();
        Cursor cursor = mResolver.query(StopContract.CONTENT_URI,
                StopContract.availableColumns, null, null, null);
        fillMapAndCloseCursor(mStopMap, StopContract.COLUMN_STOP_NAME, StopContract.COLUMN_ID, cursor);
    }

    private void initTypeMap() {
        mFullTypeMap = new HashMap<String, Integer>();
        Cursor cursor = mResolver.query(TypeContract.CONTENT_URI,
                TypeContract.availableColumns, null, null, null);
        fillMapAndCloseCursor(mFullTypeMap, TypeContract.COLUMN_TYPE_NAME, TypeContract.COLUMN_ID, cursor);
    }

    private void initLists() {
        initOperationList();
        initRouteList();
        initStopList();
        initTypeMap();
        mTypeMap = new HashMap<String, Integer>();
    }

    /**
     * clear private reference
     */
    private void clearReference() {
        mStopMap = null;
        mTypeMap = null;
        mRouteMap = null;
        mFullTypeMap = null;
    }


    /**
     * add news to database from first sheet
     *
     * @param xlsHelper XLSHelper instance
     */
    private void extractNews(XLSHelper xlsHelper) {
        List<String> newsList = xlsHelper.getNewsList();

        for (String news : newsList) {
            addOperation(ContentProviderOperation.newInsert(NewsContract.CONTENT_URI)
                    .withValue(NewsContract.COLUMN_NEWS, news)
                    .build());
        }
    }

    /**
     * Check stop name in HashMap mStopMap
     * if object not found, save checked stop name to database and add to mStopMap
     *
     * @param stopName stop name for checking
     * @return id for checked stop in database
     */
    private int checkStop(String stopName) {
        int stopId;
        if (! mStopMap.containsKey(stopName)) {
            stopId = addOperation(ContentProviderOperation.newInsert(StopContract.CONTENT_URI)
                    .withValue(StopContract.COLUMN_STOP_NAME, stopName)
                    .build());
            mStopMap.put(stopName, stopId);
        } else {
            stopId = mStopMap.get(stopName);
        }
        return stopId;
    }

    /**
     * check busNumber. Sometimes in workbooks get a sheet with no data, we need for checking
     * this sheet name. check first char string for integer value (because exists bus with
     * prefix "Э" etc)
     *
     * @param busNumber - bus number/name for checking
     * @return true - true if a valid bus number, false otherwise
     */
    private boolean isBusNumber(String busNumber) {
        return mBusNumberPattern.matcher(busNumber.trim()).find();
    }

    /**
     * Get cell for sheet with indexes column and row
     *
     * @param sheet  sheet
     * @param column column index
     * @param row    row index
     * @return cell object
     * @throws FileStructureErrorException if row or column out of bound it's indicate what
     *                                     error in file structure or parsing file not valid
     */
    private Cell getCell(Sheet sheet, int column, int row) throws FileStructureErrorException {
        if ((mSheetRows > row) && (mSheetColumns > column)) {
            return sheet.getCell(column, row);
        } else {
            throw new FileStructureErrorException("getCell out of bound");
        }
    }


    /*      interface IOUtils.LoadProgressListener      */
    @Override
    public void onStartLoad(int fileSize) {
        if (fileSize > 0) {
            NotificationUtils.updateNotification(mNotificationId,
                    getString(R.string.notification_message_update_db),
                    getString(R.string.notification_message_download_file));
        }
    }

    @Override
    public void onProgressLoad(int current, int total) {
        NotificationUtils.showProgress(mNotificationId, total, current);
    }

    @Override
    public void onFinishLoad() {
        //empty body
    }

    /*   private classes   */

    private class ScheduleDayType {         //class using like data structure without behavior
        public int typeRowSize;             //sometimes take 2 and more row for one time type
        public String typeString;
    }

    /**
     * class show that the structure does not match the algorithm,
     * algorithm needs to be updated to new file structure
     */
    private class FileStructureErrorException extends Exception {
        public FileStructureErrorException(String msg) {
            super(msg);
        }
    }

    /**
     * Class exception show what any problems have when update db
     * (error adding rows to databases and other)
     */
    private class DBUpdateWorkException extends Exception {
        public DBUpdateWorkException(String msg) {
            super(msg);
        }
    }

    /**
     * Send message to main thread using mHandler (show update process information)
     *
     * @param type message type, this type define in
     * @param msg  used for send String value
     */
    private void sendMessage(int type, String msg) {
        BroadcastUtils.sendLocal(this,
                UpdateUtils.getUpdRcvIntent(type, msg));
    }

    /**
     * Send message to main thread using mHandler (show update process information)
     *
     * @param type message type, this type define in
     */
    private void sendMessage(int type) {
        sendMessage(type, null);
    }
}
