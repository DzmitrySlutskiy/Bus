package by.slutskiy.busschedule.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.zip.ZipInputStream;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.entities.BusList;
import by.slutskiy.busschedule.data.entities.News;
import by.slutskiy.busschedule.data.entities.RouteList;
import by.slutskiy.busschedule.data.entities.Routes;
import by.slutskiy.busschedule.data.entities.StopList;
import by.slutskiy.busschedule.data.entities.TimeList;
import by.slutskiy.busschedule.data.entities.TypeList;

/**
 * OrmDBHelper
 * Version 1.0
 * 28.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class OrmDBHelper extends OrmLiteSqliteOpenHelper {
    /**
     * Работа по обновлению проводится через инстанс для записи получаемый при помощи
     * getWriteInstance при этом передается имя временной базы данных.
     * После обновления хелпер закрывается, удаляется основная база данных
     * временная переименовывается в DEFAULT_DB_NAME - имя основной БД (т.е. база подменяется).
     * после чего программа начинает работать с обновленной базой.
     * Процесс подмены базы происходит в конце обновления, ридер закрывается
     * устанавливается в режим обновления (метод setUpdateState(true)) после чего
     * все попытки получить инстанс ридера приведут к получению NULL. после подмены
     * БД вызывается метод setUpdateState(false) и ридер сможет получить уже новый инстанс
     * хелпера, старый "уходит к GC"
     */

    public static final String DEFAULT_DB_NAME = "ap_orm.db";

    private static final int BUFFER_SIZE = 8192;

    private static final String LOG_TAG = OrmDBHelper.class.getSimpleName();

    private static final int DB_VERSION = 1;

    private static OrmDBHelper sOrmDbReaderInstance = null;

    private static OrmDBHelper sOrmDbWriterInstance = null;

    private final Class[] mTables = {News.class, BusList.class, StopList.class,
            Routes.class, RouteList.class, TimeList.class, TypeList.class};

    /*  DAO objects    */
    private Dao<BusList, Integer> mBusListDao;
    private Dao<StopList, Integer> mStopListDao;
    private Dao<Routes, Integer> mRoutesDao;
    private Dao<TypeList, Integer> mTypeListDao;
    private Dao<News, Integer> mNewsDao;
    private Dao<RouteList, Integer> mRouteListDao;
    private Dao<TimeList, Integer> mTimeListDao;

    private volatile boolean mIsInUpdateState = false;

    /*  private constructor*/
    private OrmDBHelper(Context context) {
        super(context, DEFAULT_DB_NAME, null, DB_VERSION);
    }

    private OrmDBHelper(Context context, String dbName) {
        super(context, dbName, null, DB_VERSION);
    }

    /**
     * Create OrmDBHelper class instance.
     *
     * @param context context to use to open or create the database
     * @return OrmDBHelper class instance
     */
    public static OrmDBHelper getReaderInstance(Context context) {

        if (sOrmDbReaderInstance == null) {

            /*  check if file database exists and unzip if file not found   */
            String dbPath = context.getDatabasePath(DEFAULT_DB_NAME).getPath();
            File baseFile = new File(dbPath);
            if (! baseFile.exists()) {
                extractDB(context, dbPath);
            }

            sOrmDbReaderInstance = new OrmDBHelper(context);
        } else {
            /*if database right now update - return null
            * loaders must check null reference*/
            if (sOrmDbReaderInstance.isInUpdateState()) {
                return null;
            }

            /*Check db state and create new DBHelper instance if DB Connection closed*/
            if (! sOrmDbReaderInstance.isOpen()) {
                sOrmDbReaderInstance = new OrmDBHelper(context);
            }
        }

        return sOrmDbReaderInstance;
    }

    /**
     * Create OrmDBHelper class instance. Used for get update and save to temporary database with
     * name dbName
     *
     * @param context context to use to open or create the database
     * @param dbName  db name
     * @return OrmDBHelper class instance for write
     */
    public static OrmDBHelper getWriterInstance(Context context, String dbName) {
        if (sOrmDbWriterInstance == null) {
            sOrmDbWriterInstance = new OrmDBHelper(context, dbName);
        }
        return sOrmDbWriterInstance;
    }

    /**
     * Close db connection and clear private static reference
     */
    public static void clearWriter() {
        if (sOrmDbWriterInstance != null && sOrmDbWriterInstance.isOpen()) {
            sOrmDbWriterInstance.close();
        }
        sOrmDbWriterInstance = null;
    }

    /*  public methods  */

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            for (Class mClass : mTables) {
                TableUtils.createTable(connectionSource, mClass);
            }
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Could not create new table", e);
            //no sense continue work
            throw new RuntimeException("Error creating DB. Can't continue work. Cause " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        try {
            Log.e(LOG_TAG, "onUpgrade db");
            for (Class mClass : mTables) {
                TableUtils.dropTable(connectionSource, mClass, true);
            }
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Could not upgrade db", e);
            //no sense continue work
            throw new RuntimeException("Error upgrade DB. Can't continue work. Cause " + e.getMessage());
        }
    }

    /**
     * get DAO for BusList table
     *
     * @return Dao object
     * @throws SQLException
     */
    public Dao<BusList, Integer> getBusDao() throws SQLException {
        if (mBusListDao == null) {
            mBusListDao = getDao(BusList.class);
        }

        return mBusListDao;
    }

    /**
     * get DAO for StopList table
     *
     * @return Dao object
     * @throws SQLException
     */
    public Dao<StopList, Integer> getStopDao() throws SQLException {
        if (mStopListDao == null) {
            mStopListDao = getDao(StopList.class);
        }

        return mStopListDao;
    }

    /**
     * get DAO for Routes table
     *
     * @return Dao object
     * @throws SQLException
     */
    public Dao<Routes, Integer> getRoutesDao() throws SQLException {
        if (mRoutesDao == null) {
            mRoutesDao = getDao(Routes.class);
        }

        return mRoutesDao;
    }

    /**
     * get DAO for NewsList table
     *
     * @return Dao object
     * @throws SQLException
     */
    public Dao<News, Integer> getNewsDao() throws SQLException {
        if (mNewsDao == null) {
            mNewsDao = getDao(News.class);
        }

        return mNewsDao;
    }

    /**
     * get DAO for RouteList table
     *
     * @return Dao object
     * @throws SQLException
     */
    public Dao<RouteList, Integer> getRouteListDao() throws SQLException {
        if (mRouteListDao == null) {
            mRouteListDao = getDao(RouteList.class);
        }

        return mRouteListDao;
    }

    /**
     * get DAO for TimeList table
     *
     * @return Dao object
     * @throws SQLException
     */
    public Dao<TimeList, Integer> getTimeListDao() throws SQLException {
        if (mTimeListDao == null) {
            mTimeListDao = getDao(TimeList.class);
        }

        return mTimeListDao;
    }

    /**
     * get DAO for TypeList table
     *
     * @return Dao object
     * @throws SQLException
     */
    public Dao<TypeList, Integer> getTypeListDao() throws SQLException {
        if (mTypeListDao == null) {
            mTypeListDao = getDao(TypeList.class);
        }

        return mTypeListDao;
    }

    /*  private methods */
    private static void extractDB(Context context, String mDbPath) {

        /*cut mDbName from path - get full path */
        mDbPath = mDbPath.substring(0, mDbPath.length() - DEFAULT_DB_NAME.length() - 1);

        Log.i(LOG_TAG, "try extract db from zip file");

        File dirCreator = new File(mDbPath);
        if (! dirCreator.mkdirs()) {
            Log.i(LOG_TAG, "make dir return false! path:" + dirCreator.getPath());
        }
        File fileDst = new File(mDbPath + "/" + DEFAULT_DB_NAME);

        /*   streams for unzip file and save to fileDst   */
        InputStream inputStream = context.getResources().openRawResource(R.raw.ap);

        ZipInputStream zipInputStream = null;
        OutputStream outputStream = null;
        try {
            zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
            outputStream = new BufferedOutputStream(new FileOutputStream(fileDst));

            byte[] buffer = new byte[BUFFER_SIZE];
            int count;
            while (zipInputStream.getNextEntry() != null) {
                while ((count = zipInputStream.read(buffer)) != - 1) {
                    outputStream.write(buffer, 0, count);
                }
            }

            Log.i(LOG_TAG, "extract db from zip file complete");
        } catch (IOException ioe) {
            Log.e(LOG_TAG, "Unzip error: " + ioe.getMessage() + ". Run createDB()");
            //will create clear DB - run onCreate method when OrmDBHelper will create any instance
        } finally {

            /*  close all streams   */
            if (inputStream != null) {
                closeStream(inputStream);
            }

            if (outputStream != null) {
                closeStream(outputStream);
            }

            if (zipInputStream != null) {
                closeStream(zipInputStream);
            }
        }
    }

    /**
     * Close stream
     *
     * @param needClose stream implemented interface Closeable
     */
    private static void closeStream(Closeable needClose) {
        try {
            needClose.close();
        } catch (IOException ioError) {
            Log.e(LOG_TAG, "closeStream: " + ioError.getMessage());
        }
    }

    /**
     * Set update state.
     * @param state set or reset update state
     */
    public synchronized void setUpdateState(boolean state) {
        mIsInUpdateState = state;
        Log.d(LOG_TAG, "DBReader set isInUpdateState to:" + state);
    }

    /**
     * method return db state.
     * @return true if database in update state
     */
    private synchronized boolean isInUpdateState() {
        return mIsInUpdateState;
    }

    @Override
    public void close() {
        super.close();

        mBusListDao = null;
        mStopListDao = null;
        mRoutesDao = null;
        mTypeListDao = null;
        mNewsDao = null;
        mRouteListDao = null;
        mTimeListDao = null;
    }
}
