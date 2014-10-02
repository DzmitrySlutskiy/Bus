package by.slutskiy.busschedule.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import by.slutskiy.busschedule.services.UpdateService;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver","onReceive. Do check update...");
        UpdateService.runCheckUpdateService(context);
    }
}
