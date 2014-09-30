package by.slutskiy.busschedule.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import by.slutskiy.busschedule.utils.UpdateUtils;

public class BootReceiver extends BroadcastReceiver {


    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootReceiver", "onReceive: " + intent);

        UpdateUtils.setRepeating(context);
    }
}
