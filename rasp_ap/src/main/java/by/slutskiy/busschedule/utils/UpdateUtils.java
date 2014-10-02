package by.slutskiy.busschedule.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.receivers.AlarmReceiver;
import by.slutskiy.busschedule.services.UpdateService;

/**
 * UpdateUtils
 * Version 1.0
 * 30.09.2014
 * Created by Dzmitry Slutskiy.
 */
public class UpdateUtils {
    private static final String LOG_TAG = "UpdateUtils";
    private static final long DELTA = 1000;
    private static final int MILLIS_IN_DAY = 24 *    //hour in day
            60 *    //min in hour
            60 *    //sec in min
            1000;   //millis in sec

    private UpdateUtils() {/*   code    */}


    public static void setRepeatingAlarm(Context context) {
        long time = getScheduleTimeInMillis(context);

        if (PreferenceUtils.isUpdateAllowed(context) && time != 0) {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            manager.setRepeating(AlarmManager.ELAPSED_REALTIME, time, time,
                    getPendingIntent(context));

            Log.d("AlarmUtils", "Set repeating: " + time);
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
        boolean allowUpdate = PreferenceUtils.isUpdateAllowed(context);

        //DELTA - погрешность измерений времени (равна секунде)
        Long elapsedTime = System.currentTimeMillis() - PreferenceUtils.getLastCheckDate(context) + DELTA;

        allowUpdate = allowUpdate &&                                    //if allowed update
                (elapsedTime >= getScheduleTimeInMillis(context)) &&    //and elapsed time allow
                ! PreferenceUtils.isUpdateFound(context) &&               //and update not be found earlier
                checkNetwork(context);                                  //and network state allow update

        Log.d(LOG_TAG, "CanCheckUpdate say:" + allowUpdate);

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

    private static PendingIntent getPendingIntent(Context context) {
        Intent serviceIntent = new Intent(context, AlarmReceiver.class);
        serviceIntent.putExtra(UpdateService.CHECK_UPDATE, true);

        return PendingIntent.getBroadcast(context, 0, serviceIntent, 0);
    }
}