package by.slutskiy.busschedule.utils;

import android.app.NotificationManager;
import android.content.Context;

import static android.support.v4.app.NotificationCompat.Builder;

/**
 * NotificationUtils
 * Version 1.0
 * 31.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class NotificationUtils {

    private static int NOTIFICATION_ID = 0;         //used for generate notification id
    /*  private fields  */
    private static NotificationManager mNotificationManager;
    private static Builder mBuilder;

    private NotificationUtils() {
    }

    /**
     * Create notification and show it
     *
     * @param context   context
     * @param title     notification title
     * @param text      notification text
     * @param iconId    notification icon
     * @param isOngoing if = true notification set to ongoing
     * @return Id notification for next update
     */
    public static int createNotification(Context context,
                                         String title, String text, int iconId, boolean isOngoing) {
        int notificationId = NOTIFICATION_ID++;

        Builder builder = getBuilder(context);  //initialize builder
        builder.setSmallIcon(iconId);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setOngoing(isOngoing);

        //initialize manager
        NotificationManager nManager = getNotificationManager(context);
        nManager.notify(notificationId, builder.build());

        return notificationId;
    }

    public static int createNotification(Context context, String title, String text, int iconId) {
        return createNotification(context, title, text, iconId, false);
    }

    /**
     * Update notification with specified id
     *
     * @param id        notification id for update
     * @param title     notification text
     * @param isOngoing if = true notification set to ongoing
     * @param text      notification icon
     */
    public static void updateNotification(int id, String title, String text, boolean isOngoing) {
        if (mBuilder != null && mNotificationManager != null) {

            mBuilder.setContentTitle(title);
            mBuilder.setContentText(text);
            mBuilder.setOngoing(isOngoing);

            mNotificationManager.notify(id, mBuilder.build());
        }
    }

    public static void updateNotification(int id, String title, String text) {
        updateNotification(id, title, text, false);
    }

    /**
     * Show progress bar in notification with specified id as indeterminate progress
     *
     * @param id notification id for update
     */
    public static void showIndeterminateProgress(int id) {
        showProgress(id, 0, 0, true);
    }

    /**
     * Show progress bar in notification with specified id
     *
     * @param id       notification id for update
     * @param max      max position in progress bar
     * @param progress progress position in progress bar
     */
    public static void showProgress(int id, int max, int progress) {
        showProgress(id, max, progress, false);
    }

    private static void showProgress(int id, int max, int progress, boolean isIndeterminate) {
        if (mBuilder != null && mNotificationManager != null) {
            mBuilder.setProgress(max, progress, isIndeterminate);

            mNotificationManager.notify(id, mBuilder.build());
        }
    }

    /**
     * delete notification with specified id
     *
     * @param id notification id for cancel
     */
    public static void cancelNotification(int id) {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(id);
        }
    }

    private static Builder getBuilder(Context context) {
        if (mBuilder == null) {
            mBuilder = new Builder(context);
        }
        return mBuilder;
    }

    private static NotificationManager getNotificationManager(Context context) {
        if (mNotificationManager == null) {
            mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }
}
