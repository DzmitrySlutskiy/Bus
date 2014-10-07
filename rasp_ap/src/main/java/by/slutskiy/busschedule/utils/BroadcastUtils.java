package by.slutskiy.busschedule.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * BroadcastUtils
 * Version 1.0
 * 07.10.2014
 * Created by Dzmitry Slutskiy.
 */
public class BroadcastUtils {

    /*  private fields  */
    private static LocalBroadcastManager mBroadcastManager;

    /*  public constructors */

    public BroadcastUtils() {/*   code    */}

    /*  public methods  */
    public static void sendLocal(Context context, Intent intent) {
        getLocalBManager(context).sendBroadcast(intent);
    }

    public static void registerReceiver(Context context, BroadcastReceiver receiver, String filter) {
        getLocalBManager(context).registerReceiver(receiver,
                new IntentFilter(filter));
    }

    public static void unregisterReceiver(Context context, BroadcastReceiver receiver) {
        getLocalBManager(context).unregisterReceiver(receiver);
    }

    /*  private methods */

    /**
     * get instance local broadcast manager
     *
     * @param context context
     * @return LocalBroadcastManager instance
     */
    private static LocalBroadcastManager getLocalBManager(Context context) {
        if (mBroadcastManager == null) {
            mBroadcastManager = LocalBroadcastManager.getInstance(context);
        }
        return mBroadcastManager;
    }
}
