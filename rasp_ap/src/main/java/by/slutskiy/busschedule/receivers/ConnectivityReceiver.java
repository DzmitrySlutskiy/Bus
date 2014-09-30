package by.slutskiy.busschedule.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

public class ConnectivityReceiver extends BroadcastReceiver {
    public ConnectivityReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = manager.getActiveNetworkInfo();
        Log.d("ConnectivityReceiver", "onReceive: " + intent + " netInfo:" + netInfo);

        if (netInfo != null) {
            int type = netInfo.getType();
            Log.d("ConnectivityReceiver", "onReceive: net type" + type);
        }
    }
    /*
    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }*/
}
