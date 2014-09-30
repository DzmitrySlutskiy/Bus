package by.slutskiy.busschedule.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import by.slutskiy.busschedule.services.UpdateService;
import by.slutskiy.busschedule.utils.UpdateUtils;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    private static long lastReceive = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("AlarmReceived", "Start service. Elapsed time: " +
                (System.currentTimeMillis() - lastReceive) +
                " needed elapsed time: " + UpdateUtils.getScheduleTimeInMillis(context));

        lastReceive = System.currentTimeMillis();

        if (UpdateUtils.canCheck(context)) {
            Intent serviceIntent = new Intent(context, UpdateService.class);
            serviceIntent.putExtra(UpdateService.CHECK_UPDATE, true);

            context.startService(serviceIntent);
        }
    }
}
