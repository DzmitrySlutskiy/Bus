package by.slutskiy.busschedule.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.utils.PreferenceUtils;
import by.slutskiy.busschedule.utils.UpdateUtils;

/**
 * PrefActivity
 * Version 1.0
 * 26.09.2014
 * Created by Dzmitry Slutskiy.
 */
public class PrefActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    /*  private fields  */

    /*  public constructors */

    public PrefActivity() {/*   code    */}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(getString(R.string.preference_key_update_freq))) {
            long freq = Integer.parseInt(sharedPreferences.getString(key, "1"));
            if (freq != 0) {
                UpdateUtils.setRepeatingAlarm(getApplicationContext());  //set new alarm
            } else {
                UpdateUtils.cancelAlarm(getApplicationContext());
            }
        } else if (key.equals(getString(R.string.preference_key_allow_update))) {
            if (sharedPreferences.getBoolean(
                    getString(R.string.preference_key_allow_update), true)) {
                UpdateUtils.setRepeatingAlarm(getApplicationContext());  //set new alarm
            } else {
                UpdateUtils.cancelAlarm(getApplicationContext());   //cancel old alarm
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceUtils.registerListener(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceUtils.unregisterListener(this, this);
    }
}
