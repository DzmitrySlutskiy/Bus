package by.slutskiy.busschedule.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import by.slutskiy.busschedule.ui.activity.MainActivity;

/**
 * NotificationUtils
 * Version 1.0
 * 31.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class NotificationUtils {

    private static int NOTIFICATION_ID = 0;
    /*  private fields  */
    private NotificationManager mNotificationManager;

    private NotificationCompat.Builder mBuilder;
    private Context mContext;

    private int mNotificationId;
    private int mIconId;
    /*  public constructors */

    public NotificationUtils(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext);
    }

    public void createNotification(Class activityClass, String title, String text, int iconId) {
        mNotificationId = NOTIFICATION_ID++;
        mIconId = iconId;
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, activityClass);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        updateNotification(title, text);
    }

    public void updateNotification(String title, String text) {
        if (mBuilder != null && mNotificationManager != null) {
            mBuilder.setSmallIcon(mIconId);
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(text);

            mNotificationManager.notify(mNotificationId, mBuilder.build());
        }
    }

    public void showProgressNotification(int max, int progress) {
        if (mBuilder != null && mNotificationManager != null) {
            mBuilder.setProgress(max, progress, false);
            // Displays the progress bar for the first time.
            mNotificationManager.notify(mNotificationId, mBuilder.build());
        }
    }

    public void deleteNotification() {
        mNotificationManager.cancel(mNotificationId);
    }
}
