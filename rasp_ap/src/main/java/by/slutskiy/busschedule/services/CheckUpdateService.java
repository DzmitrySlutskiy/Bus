package by.slutskiy.busschedule.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.utils.BroadcastUtils;
import by.slutskiy.busschedule.utils.IOUtils;
import by.slutskiy.busschedule.utils.NotificationUtils;
import by.slutskiy.busschedule.utils.PreferenceUtils;
import by.slutskiy.busschedule.utils.StringUtils;
import by.slutskiy.busschedule.utils.UpdateUtils;

/**
 * CheckUpdateService
 * Version 1.0
 * 07.10.2014
 * Created by Dzmitry Slutskiy.
 */
public class CheckUpdateService extends IntentService {
    public static final String IMMEDIATELY = "immediately";
    private static final String TAG = CheckUpdateService.class.getSimpleName();

    /*  public constructors */

    public CheckUpdateService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        if (! intent.getBooleanExtra(IMMEDIATELY, false) && ! UpdateUtils.canCheck(this)) {
            Log.d(TAG, "Run check update disabled in onHandleIntent");
            return;
        }

        Date lastUpdate = IOUtils.getLastModifiedDate(UpdateUtils.BUS_PARK_URL + UpdateUtils.FILE_NAME);
        Date dbUpdateDate = PreferenceUtils.getLastUpdateDate(getApplicationContext());

        if (lastUpdate != null) {
            if (dbUpdateDate.before(lastUpdate)) {
                /*show notification only if autocheck update
                * because in manual mode dialog will be shown*/
                if (! PreferenceUtils.isManualUpdate(this)) {
                    NotificationUtils.createNotification(getApplicationContext(),
                            getString(R.string.notification_title_update_avail),
                            getString(R.string.notification_message_update_avail) +
                                    StringUtils.formatDate(lastUpdate), R.drawable.ic_launcher);
                }
                BroadcastUtils.sendLocal(this,
                        UpdateUtils.getUpdRcvIntent(UpdateUtils.MSG_LAST_UPDATE));

                PreferenceUtils.setUpdateState(getApplicationContext(), true);
            } else {
                PreferenceUtils.setLastCheckDate(getApplicationContext());
                BroadcastUtils.sendLocal(this,
                        UpdateUtils.getUpdRcvIntent(UpdateUtils.MSG_UPDATE_NOT_NEED));
            }
        }
    }
}
