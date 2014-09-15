package by.slutskiy.busschedule.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import by.slutskiy.busschedule.BuildConfig;
import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.DBReader;
import by.slutskiy.busschedule.data.DBUpdater;
import by.slutskiy.busschedule.data.XLSHelper;
import by.slutskiy.busschedule.ui.activity.MainActivity;
import by.slutskiy.busschedule.utils.NotificationUtils;
import by.slutskiy.busschedule.utils.StringUtils;
import jxl.Cell;
import jxl.Sheet;
import jxl.read.biff.BiffException;

/**
 * UpdateService
 * Version 1.0
 * 27.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class UpdateService extends IntentService {

    public static final String CHECK_UPDATE = "CHECK_UPDATE";
    public static final String MESSENGER = "MESSENGER";

    public static final String USED_DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    public static final int MSG_UPDATE_NOT_NEED = 0;
    public static final int MSG_LAST_UPDATE = 3;
    public static final int MSG_UPDATE_FINISH = 10;
    public static final int MSG_NO_INTERNET = 11;
    public static final int MSG_IO_ERROR = 13;
    public static final int MSG_UPDATE_FILE_STRUCTURE_ERROR = 20;
    public static final int MSG_UPDATE_DB_WORK_ERROR = 21;
    public static final int MSG_UPDATE_BIFF_ERROR = 22;
    public static final int MSG_APP_ERROR = 23;

    private static final String LOG_TAG = UpdateService.class.getSimpleName();

    /*   constants for update    */
    private static final String BUS_PARK_URL = "http://www.ap1.by/download/";
    private static final String FILE_NAME = "raspisanie_gorod.xls";
    /*  preferences params  */
    public static final String PREF_LAST_UPDATE = "lastUpdate";
    public static final String BUS_NUMBER_PATTERN = "^[0-9]{1,3}(Э|э)?$";
    /**
     * full bus list, String Key - bus number, Integer Value - ID record in DB
     */
    private HashMap<String, Integer> mBusList;
    /**
     * full stop list, String Key - stop name, Integer Value - ID record in DB
     */
    private HashMap<String, Integer> mStopList;
    /**
     * type list for all bus ("вых", "раб", "суб", "воскр" и т.д.)
     * , String Key - type string, Integer Value - ID record in DB
     */
    private HashMap<String, Integer> mTypeList;
    /**
     * full route list, String Key - route name, Integer Value - ID record in DB
     */
    private HashMap<String, Integer> mRouteList;

    /**
     * type list only for current stop in bus route
     */
    private final ArrayList<ScheduleDayType> mDayTypes = new ArrayList<ScheduleDayType>();

    private XLSHelper mXlsHelper = null;

    private int mCurrentBusId;          // current parsing bus id
    private int mLastRouteId;
    private int mCurrentRouteId;
    private int mStopIndex;             //stop index in bus route (increment from start stop)
    private int mLastHourColumnIndex;

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

    private static final int MAX_BUFFER = 1024;
    private static final int EOF = - 1;

    private DBUpdater mDbUpdater;

    private int mNotificationId;

    private Messenger mMessenger;

    /*  public constructors */

    public UpdateService() {
        super("UpdateService");
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
        initLists();

        mMessenger = (Messenger) intent.getExtras().get(MESSENGER);

        URL fURL = getUrl(BUS_PARK_URL + FILE_NAME);

        URLConnection uCon = null;
        InputStream stream = null;

        int what = MSG_LAST_UPDATE;

        Date lastUpdate;

        /*   try open internet connection to remote host  */
        try {
            uCon = fURL.openConnection();
            stream = uCon.getInputStream();               //check internet IOException throws
            lastUpdate = new Date(uCon.getLastModified());
            Log.i(LOG_TAG, "last modified: " + lastUpdate);
        } catch (IOException e) {
            what = MSG_NO_INTERNET;
            lastUpdate = null;
        }

        Date dbUpdateDate = MainActivity.getLastUpdateDate(getApplicationContext());

        //  if need only check file modification date
        if (intent.getBooleanExtra(CHECK_UPDATE, false)) {
            if (what != MSG_NO_INTERNET) {
                if ((dbUpdateDate != null) && ! dbUpdateDate.before(lastUpdate)) {
                    sendMessage(MSG_UPDATE_NOT_NEED);
                } else {
                    sendMessage(what, lastUpdate);
                }
            } else {
                sendMessage(what);
            }
            return;
        }

        if (what == MSG_NO_INTERNET) {
            sendMessage(MSG_NO_INTERNET);
            return;
        }

        if ((dbUpdateDate != null) && ! dbUpdateDate.before(lastUpdate)) {
            sendMessage(MSG_UPDATE_NOT_NEED);
            return;
        }

        mNotificationId = NotificationUtils.createNotification(getApplicationContext(),
                getString(R.string.notification_title_update),
                getString(R.string.notification_message_try_update),
                R.drawable.ic_launcher);

        mDbUpdater = DBUpdater.getInstance(getApplicationContext());
        String filePath = getApplicationContext().getFilesDir().getPath() + "/" + FILE_NAME;


        if (saveStreamToFile(stream, filePath, uCon.getContentLength())) {
            try {
                writeLogDebug("Begin transaction");
                mDbUpdater.beginTran();                              //begin transaction
                mDbUpdater.clearDB();

                NotificationUtils.updateNotification(mNotificationId,
                        getString(R.string.notification_title_update),
                        getString(R.string.notification_message_open_file));

                NotificationUtils.showIndeterminateProgress(mNotificationId);

                writeLogDebug("Open xls file");
                mXlsHelper = new XLSHelper(filePath);               //open xls file
                writeLogDebug("Xls file opened");

                NotificationUtils.updateNotification(mNotificationId,
                        getString(R.string.notification_title_update),
                        getString(R.string.notification_message_update_db));

                extractNews(mXlsHelper);                            //get news from first sheet

                int sheetCount;
                if (BuildConfig.SHEET_COUNT > 1) {
                    sheetCount = BuildConfig.SHEET_COUNT;
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

                saveUpdateDate(lastUpdate);

                mDbUpdater.setTranSuccessful();          //commit transaction
            } catch (IOException e) {
                writeLogDebug("IOException:" + e.getMessage());
                sendMessage(MSG_IO_ERROR, e.getMessage());
            } catch (BiffException e) {
                writeLogDebug("BiffException:" + e.getMessage());
                sendMessage(MSG_UPDATE_BIFF_ERROR, e.getMessage());
            } catch (FileStructureErrorException e) {
                writeLogDebug("FileStructureErrorException:" + e.getMessage());
                sendMessage(MSG_UPDATE_FILE_STRUCTURE_ERROR);
            } catch (DBUpdateWorkException e) {
                writeLogDebug("DBUpdateWorkException:" + e.getMessage());
                sendMessage(MSG_UPDATE_DB_WORK_ERROR);
            } finally {
                mDbUpdater.endTran();                    //end transaction
                mDbUpdater.close();                      //close updated db connection
                mDbUpdater = null;

                Log.d(LOG_TAG, "Close main DB connection");
                DBReader dbReader = DBReader.getInstance(getApplicationContext());
                dbReader.setUpdateState(true);        //disallow open DB
                dbReader.close();                     //close main database connection

                Log.d(LOG_TAG, "Delete main DB file");
                delFile(getApplicationContext().getDatabasePath(DBReader.DEFAULT_DB_NAME).getPath());

                File sourceFile = new File(
                        getApplicationContext().getDatabasePath(DBUpdater.DB_NAME).getPath());

                File destinationFile = new File(
                        getApplicationContext().getDatabasePath(DBReader.DEFAULT_DB_NAME).getPath());

                Log.d(LOG_TAG, "Rename file DB result: " + sourceFile.renameTo(destinationFile));

                dbReader.setUpdateState(false);             //allow open DB

                if (mXlsHelper != null) {
                    mXlsHelper.closeWorkbook();         //close workbook
                    mXlsHelper = null;
                }
                NotificationUtils.cancelNotification(mNotificationId);

                delFile(filePath);                       //delete temporary xls file

                sendMessage(MSG_UPDATE_FINISH);

                clearReference();
            }
        }
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
            writeLogDebug("Get new bus:" + mBusNumber);

            mCurrentBusId = checkBus(mBusNumber);

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
            writeLogDebug("Get sheet name:" + mBusNumber);
        }
    }

    /**
     * parse stop info (schedule for current bus)
     *
     * @param sheet    current sheet
     * @param rowIndex row index where stop info found (tag "A" found)
     * @return stop size - count rows used for current stop info
     * @throws FileStructureErrorException if parsing stop failed (out of bound or any error)
     * @throws DBUpdateWorkException       if adding to database return error
     */
    private int parseStop(Sheet sheet, int rowIndex) throws FileStructureErrorException,
            DBUpdateWorkException {

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
        int mRouteListId = mDbUpdater.addRouteList(mCurrentRouteId, stopId, mStopIndex);
        if (mRouteListId < 0) {
            throw new DBUpdateWorkException("Error add RouteList stop: " +
                    stopName + " route: " + routeName);
        }

        /*   day schedule type processing   */
        mLastHourColumnIndex = XLSHelper.getMergedCellSize(sheet, mTagAColumnIndex +
                XLSHelper.ROUTE_NAME_COLUMN_OFFSET, rowIndex +
                XLSHelper.ROUTE_NAME_ROW_OFFSET + 1, XLSHelper.MergeSearchType.Column);
        mDayTypes.clear();

        fillDayType(sheet, rowIndex, stopSize);

        for (int hourIndex = 1; hourIndex < mLastHourColumnIndex; hourIndex++) {
            Cell cell = getCell(sheet, hourIndex, rowIndex + 1);
            int hour = StringUtils.strToInt(cell.getContents().trim());
            int minuteRowIndex = rowIndex + 4;
            for (ScheduleDayType type : mDayTypes) {
                String minStr = "";
                for (int rIndex = minuteRowIndex; rIndex < minuteRowIndex + type.typeRowSize;
                     rIndex++) {

                    minStr += XLSHelper.processingMinutes(
                            getCell(sheet, hourIndex, rIndex).getContents()) + " ";
                }
                if (mDbUpdater.addTime(mRouteListId, hour, minStr.trim(), type.typeID) < 0) {
                    throw new DBUpdateWorkException("Error add time for stop: " +
                            stopName + " for route " + routeName);
                }
                minuteRowIndex += type.typeRowSize;
            }
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
     * @throws DBUpdateWorkException error adding to database
     */
    private void fillDayType(Sheet sheet, int rowIndex, int stopSize) throws DBUpdateWorkException {
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

            /*  сохраняем в БД и в mTypeList значение типа (если его там еще нет)*/
            if (! mTypeList.containsKey(cellContent)) {
                int typeId = mDbUpdater.addType(cellContent);
                if (typeId == - 1) {
                    throw new DBUpdateWorkException("Error add type: " + cellContent);
                }
                mTypeList.put(cellContent, typeId);
            }
            ScheduleDayType type = new ScheduleDayType();
            type.typeID = mTypeList.get(cellContent);
            type.typeRowSize = typeRowSize;

            /*  save day type for current stop in bus route */
            mDayTypes.add(type);

            typeIndex += typeRowSize;
        }
    }

    /**
     * check route name - split route to first and last stops in route, add this route to database
     * and save route id info in list mRouteList
     *
     * @param routeName route name for checking
     * @return id record in database about this route
     * @throws DBUpdateWorkException if can't add info to database
     */
    private int checkRoute(String routeName) throws DBUpdateWorkException {
        int routeId;

        /*  split info about stops  */
        String firstStop = StringUtils.getSubStringByIndex(routeName, 0, XLSHelper.TAG_STOP_DIVIDER);
        firstStop = StringUtils.deleteSubString(firstStop, XLSHelper.DELETING_SUBSTRING);

        String lastStop = StringUtils.getSubStringByIndex(routeName, 1, XLSHelper.TAG_STOP_DIVIDER);
        lastStop = StringUtils.deleteSubString(lastStop, XLSHelper.DELETING_SUBSTRING);

        /*   check stops in stop list   */
        int firstStopId = checkStop(firstStop);
        int lastStopId = checkStop(lastStop);

        /*   full route name with bus number   */
        String fullRouteName = mBusNumber + " " + routeName;

        /*  check route list mRouteList for info about route fullRouteName */
        if (! mRouteList.containsKey(fullRouteName)) {

            /*   if this route not exists in route list add this route to database   */
            routeId = mDbUpdater.addRoutes(mCurrentBusId, firstStopId, lastStopId);
            if (routeId >= 0) {

                /*  save this route in route list with routeId info*/
                mRouteList.put(fullRouteName, routeId);
            } else {
                throw new DBUpdateWorkException("Error add route: " + fullRouteName);
            }
        } else {    //if route contains in list return route id
            routeId = mRouteList.get(fullRouteName);
        }
        return routeId;
    }

    private void initLists() {
        mBusList = new HashMap<String, Integer>();
        mStopList = new HashMap<String, Integer>();
        mTypeList = new HashMap<String, Integer>();
        mRouteList = new HashMap<String, Integer>();
    }

    /**
     * clear private reference
     */
    private void clearReference() {
        mBusList = null;
        mStopList = null;
        mTypeList = null;
        mRouteList = null;
        mMessenger = null;
    }

    /**
     * generate URL for fileURL string
     *
     * @param fileURL string for URL
     * @return instance URL
     */
    private URL getUrl(String fileURL) {
        try {
            return new URL(fileURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Bad URL string: " + fileURL);
        }
        return null;
    }

    /**
     * add news to database from first sheet
     *
     * @param xlsHelper XLSHelper instance
     * @throws DBUpdateWorkException if can't add row to database
     */
    private void extractNews(XLSHelper xlsHelper) throws DBUpdateWorkException {
        List<String> newsList = xlsHelper.getNewsList();
        for (String news : newsList) {
            if (mDbUpdater.addNews(news) < 0) {
                throw new DBUpdateWorkException("Error add news: " + news);
            }
        }
    }

    /**
     * Save update date
     *
     * @param updateDate update date
     */
    private void saveUpdateDate(Date updateDate) {
        if (updateDate != null) {
            SharedPreferences preferences = getApplicationContext().
                    getSharedPreferences(BuildConfig.PACKAGE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(PREF_LAST_UPDATE, updateDate.getTime());
            editor.commit();
        }
    }

    /**
     * Check stop name in HashMap mStopList
     * if object not found, save checked stop name to database and add to mStopList
     *
     * @param stopName stop name for checking
     * @return id for checked stop in database
     * @throws DBUpdateWorkException if can't add record to database
     */
    private int checkStop(String stopName) throws DBUpdateWorkException {
        int stopId;
        if (! mStopList.containsKey(stopName)) {
            stopId = mDbUpdater.addStop(stopName);
            if (stopId >= 0) {
                mStopList.put(stopName, stopId);
            } else {
                throw new DBUpdateWorkException("Error add stop:" + stopName);
            }
        } else {
            stopId = mStopList.get(stopName);
        }
        return stopId;
    }

    /**
     * Check bus in HashMap mBusList
     * if object not found, save checked bus to database and add to mBusList
     *
     * @param busNumber bus number for checking
     * @return id record in database
     */
    private int checkBus(String busNumber) throws DBUpdateWorkException {
        int busId;
        if (! mBusList.containsKey(busNumber)) {
            busId = mDbUpdater.addBus(busNumber);
            if (busId >= 0) {
                mBusList.put(busNumber, busId);
            } else {
                throw new DBUpdateWorkException("Error add bus: " + busNumber);
            }
        } else {
            busId = mBusList.get(busNumber);
        }
        return busId;
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
        Pattern busNumberPattern = Pattern.compile(BUS_NUMBER_PATTERN);
        return busNumberPattern.matcher(busNumber.trim()).find();
    }

    private static final int NOTIFY_PERCENT = 1;

    /**
     * saveStreamToFile
     *
     * @param inputStream stream for saving
     * @param filePath    path for saving
     * @param fileSize    file size
     * @return true - true If stream has been downloaded and saved, false otherwise
     */
    private boolean saveStreamToFile(InputStream inputStream, String filePath, int fileSize) {

        OutputStream outStream = null;

        /*   download file and save to filePath   */
        if (fileSize > 0) {

            NotificationUtils.updateNotification(mNotificationId,
                    getString(R.string.notification_message_update_db),
                    getString(R.string.notification_message_download_file));
        }
        try {
            File file = new File(filePath);
            inputStream = new BufferedInputStream(inputStream);
            outStream = new BufferedOutputStream(new FileOutputStream(file));

            /*  cycle count for NOTIFY_PERCENT - if read maxCount time - we read more than
            * NOTIFY_PERCENT % and can notify user*/
            int maxCount = fileSize / MAX_BUFFER / 100 * NOTIFY_PERCENT;
            int currentCount = 0;       //current cycle count

            byte[] buffer = new byte[MAX_BUFFER];
            int readBytes;
            int readBytesSum = 0;
            while ((readBytes = inputStream.read(buffer)) != EOF) {
                outStream.write(buffer, 0, readBytes);
                readBytesSum += readBytes;

                /*   send read bytes count to update dialog   */
                if (fileSize > 0 &&
                        (currentCount++ > maxCount)) {          //if true next NOTIFY_PERCENT % read
                    currentCount = 0;                           //reset counter

                    NotificationUtils.showProgress(mNotificationId, fileSize, readBytesSum);
                }
            }

            if (fileSize > 0) {                                 //show 100% read
                NotificationUtils.showProgress(mNotificationId, fileSize, readBytesSum);
            }
        } catch (IOException e) {
            writeLogDebug("IOException in saveStreamToFile:" + e);
            sendMessage(MSG_IO_ERROR, e.getLocalizedMessage());
            return false;
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                writeLogDebug("IOException in saveStreamToFile while close out stream:" + e);
                sendMessage(MSG_IO_ERROR, e.getLocalizedMessage());
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                writeLogDebug("IOException in saveStreamToFile while close input stream:" + e);
                sendMessage(MSG_IO_ERROR, e.getLocalizedMessage());
            }
        }
        writeLogDebug("Download complete file size: " + fileSize);
        return true;
    }

    private void writeLogDebug(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, msg);
        }
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

    /**
     * delete file in @code filePath}
     *
     * @param filePath file path
     */
    private static void delFile(String filePath) {
        File fl = new File(filePath);
        Log.i(LOG_TAG, "Try delete file:" + filePath);
        if (fl.exists()) {
            Log.i(LOG_TAG, (fl.delete() ? "File deleted:" : "Can't delete file:") +
                    filePath);
        }
    }
    /*   private classes   */

    private class ScheduleDayType {         //class using like data structure without behavior
        //        public String type;
        public int typeID;                  //id in DB for current time type (по вых, по раб etc)
        public int typeRowSize;             //sometimes take 2 and more row for one time type
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
     * @param obj  used for send String value
     */
    private void sendMessage(int type, Object obj) {
        if (mMessenger != null) {
            Message msg = new Message();
            msg.what = type;

            if (obj != null) {
                msg.obj = obj;
            }

            try {
                mMessenger.send(msg);
            } catch (RemoteException e) {
                //if the target Handler no longer exists - Handler - Activity (if closed)
                //do nothing
                Log.e(LOG_TAG, "send message error: " + e.getMessage());
            }
        }
    }

    private void sendMessage(int type) {
        sendMessage(type, null);
    }
}
