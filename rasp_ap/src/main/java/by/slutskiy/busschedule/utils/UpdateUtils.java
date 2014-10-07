package by.slutskiy.busschedule.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;

import by.slutskiy.busschedule.BuildConfig;
import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.receivers.AlarmReceiver;
import by.slutskiy.busschedule.services.CheckUpdateService;
import by.slutskiy.busschedule.services.UpdateService;
import by.slutskiy.busschedule.ui.activity.MainActivity;

/**
 * UpdateUtils
 * Version 1.0
 * 30.09.2014
 * Created by Dzmitry Slutskiy.
 */
public class UpdateUtils {

    public static final int MSG_UPDATE_NOT_NEED = 0;
    public static final int MSG_LAST_UPDATE = 3;
    public static final int MSG_UPDATE_FINISH = 10;
    public static final int MSG_NO_INTERNET = 11;
    public static final int MSG_IO_ERROR = 13;
    public static final int MSG_UPDATE_FILE_STRUCTURE_ERROR = 20;
    public static final int MSG_UPDATE_DB_WORK_ERROR = 21;
    public static final int MSG_UPDATE_BIFF_ERROR = 22;
    public static final int MSG_APP_ERROR = 23;
    public static final String TYPE = "TYPE";
    public static final String MESSAGE = "MESSAGE";

    private static final String LOG_TAG = "UpdateUtils";
    private static final long DELTA = 1000;
    private static final long TRIGGER_AT = 2000;
    private static final int MILLIS_IN_DAY = 24 *    //hour in day
            60 *    //min in hour
            60 *    //sec in min
            1000;   //millis in sec
    /*   constants for update    */
    public static final String BUS_PARK_URL = "http://www.ap1.by/download/";
    public static final String FILE_NAME = "raspisanie_gorod.xls";

    private UpdateUtils() {/*   code    */}


    public static void setRepeatingAlarm(Context context) {
        long intervalTime = getScheduleTimeInMillis(context);

        if (PreferenceUtils.isUpdateAllowed(context) && intervalTime != 0) {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + TRIGGER_AT      //run after TRIGGER_AT ms
                    , intervalTime,
                    getPendingIntent(context));

            Log.d("AlarmUtils", "Set repeating: " + intervalTime);
        }
    }

    public static void cancelAlarm(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(getPendingIntent(context));
        Log.d("AlarmUtils", "Cancel alarm.");
    }

    /**
     * this method control can we check update now: if user set daily update check
     * method return true only if more than 24 hour passed
     *
     * @param context context for access to shared preferences
     * @return true if can check update
     */
    public static boolean canCheck(Context context) {

        //DELTA - погрешность измерений времени (равна секунде)
        Long elapsedTime = System.currentTimeMillis() -
                PreferenceUtils.getLastCheckDate(context) + DELTA;

        boolean allowUpdate = PreferenceUtils.isUpdateAllowed(context) &&   //if allowed update
                (elapsedTime >= getScheduleTimeInMillis(context)) &&        //and elapsed time allow
                ! PreferenceUtils.isUpdateFound(context) &&                 //and update not be found earlier
                checkNetwork(context);                                      //and network state allow update

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "CanCheckUpdate say:" + allowUpdate +
                    " in: allow update: "+PreferenceUtils.isUpdateAllowed(context)+
                    " elapsed time: " + (elapsedTime >= getScheduleTimeInMillis(context)) +
                    " ! isFound: " + ! PreferenceUtils.isUpdateFound(context) +
                    " network state: " + checkNetwork(context));
        }

        return allowUpdate;
    }

    public static long getScheduleTimeInMillis(Context context) {
//        return PreferenceUtils.getUpdateFreq(context) * (BuildConfig.DEBUG ? 10000 : MILLIS_IN_DAY);
        return PreferenceUtils.getUpdateFreq(context) * MILLIS_IN_DAY;
    }

    public static boolean checkNetwork(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = manager.getActiveNetworkInfo();

        if (netInfo != null) {
            int type = netInfo.getType();

            Log.d(LOG_TAG, "checkNetwork: " + netInfo.getTypeName());

            boolean canUseMobile = PreferenceUtils.getBoolean(context,
                    context.getString(R.string.preference_key_use_mobile), false);

            //if network connected and we can check update
            //if connected mobile and we can use mobile network
            //or connected WiFi
            return netInfo.isConnected() &&
                    ((type == ConnectivityManager.TYPE_MOBILE && canUseMobile) ||
                            type == ConnectivityManager.TYPE_WIFI);
        }
        return false;
    }

    public static Intent getUpdRcvIntent(int type) {
        return getUpdRcvIntent(type, "");
    }

    public static Intent getUpdRcvIntent(int type, String msg) {
        return new Intent(MainActivity.UPDATE_AVAIL_RECEIVER)
                .putExtra(UpdateUtils.TYPE, type)
                .putExtra(UpdateUtils.MESSAGE, msg);
    }

    private static PendingIntent getPendingIntent(Context context) {
        return PendingIntent.getBroadcast(context, 0,
                new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
    }


    /**
     * start check updates immediately (without check for allow update etc)
     *
     * @param context context for service run
     */
    public static void runCheckUpdateServiceImmediately(Context context) {
        runCheckUpdateService(context, true);
    }

    /**
     * start check update service. if user disable update - service is not run
     * if user set check interval to daily and less than 24 hour elapsed from last check
     * service is not run, network type also will be checked
     *
     * @param context context for service run
     */
    public static void runCheckUpdateService(Context context) {
        runCheckUpdateService(context, false);
    }

    /**
     * start update service
     *
     * @param context context for service run
     */
    public static void runUpdateService(Context context) {
        context.startService(new Intent(context, UpdateService.class));
    }

    /**
     * start check update service.
     *
     * @param context     context for service run
     * @param immediately flag, if == true service start without checking
     */
    private static void runCheckUpdateService(Context context, boolean immediately) {
        Intent intent = new Intent(context, CheckUpdateService.class);
        intent.putExtra(CheckUpdateService.IMMEDIATELY, immediately);

        context.startService(intent);
    }
}