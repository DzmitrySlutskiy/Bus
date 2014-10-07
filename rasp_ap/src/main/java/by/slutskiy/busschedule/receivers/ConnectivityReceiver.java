package by.slutskiy.busschedule.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import by.slutskiy.busschedule.utils.UpdateUtils;

public class ConnectivityReceiver extends BroadcastReceiver {
    public ConnectivityReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ConnectivityReceiver","onReceive. Do check update...");
        UpdateUtils.runCheckUpdateService(context);
    }
}
