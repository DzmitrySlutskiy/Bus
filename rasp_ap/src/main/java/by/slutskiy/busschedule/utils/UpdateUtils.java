package by.slutskiy.busschedule.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import by.slutskiy.busschedule.BuildConfig;
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
    public static final int MILLIS_IN_DAY = 24 *    //hour in day
            60 *    //min in hour
            60 *    //sec in min
            1000;   //millis in sec

    private UpdateUtils() {/*   code    */}


    public static void setRepeating(Context context) {
        long time = getScheduleTimeInMillis(context);

        if (isUpdateAllowed(context) && time != 0) {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, time,
                    getPendingIntent(context));

            Log.d("AlarmUtils", "Set repeating: " + time);
        }
    }

    public static void cancelAlarm(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(getPendingIntent(context));
        Log.d("AlarmUtils", "Cancel alarm.");
    }

    public static boolean canCheck(Context context) {
        boolean allowUpdate = isUpdateAllowed(context);

        Long elapsedTime = System.currentTimeMillis() - getLastCheckDate(context) + DELTA;

        allowUpdate = allowUpdate && (elapsedTime >= getScheduleTimeInMillis(context));

        Log.d(LOG_TAG, "CanCheckUpdate say:" + allowUpdate);

        return allowUpdate;
    }

    public static boolean isUpdateAllowed(Context context) {
        return PreferenceUtils.getBoolean(context,
                context.getString(R.string.preference_key_allow_update), true);
    }

    public static long getScheduleTimeInMillis(Context context) {
        long result;
        String str = PreferenceUtils.getString(context,
                context.getString(R.string.preference_key_update_freq), "1");
        try {
            result = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            Log.d(LOG_TAG, "Parse int error:" + e.getMessage());
            result = 0;
        }

        return result * (BuildConfig.DEBUG ? 1000 : MILLIS_IN_DAY);
    }

    private static long getLastCheckDate(Context context) {
        return PreferenceUtils.getLong(context,
                context.getString(R.string.preference_key_last_check), 0L);
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent serviceIntent = new Intent(context, AlarmReceiver.class);
        serviceIntent.putExtra(UpdateService.CHECK_UPDATE, true);

        return PendingIntent.getBroadcast(context, 0, serviceIntent, 0);
    }
}