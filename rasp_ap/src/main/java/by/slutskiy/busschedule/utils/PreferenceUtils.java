package by.slutskiy.busschedule.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;

import by.slutskiy.busschedule.R;

/**
 * PreferenceUtils
 * Version 1.0
 * 30.09.2014
 * Created by Dzmitry Slutskiy.
 */
public class PreferenceUtils {

    /*  preferences params  */
    public static final String PREF_LAST_UPDATE = "lastUpdate";
    public static final String PREF_UPDATE_BUTTON_STATE = "UpdateButtonState";
    private static SharedPreferences mSharedPref = null;
    /*  public constructors */

    private PreferenceUtils() {
    }

    /*  public methods  */
    public static boolean getBoolean(Context context, String key, Boolean defaultValue) {
        return getPref(context).getBoolean(key, defaultValue);
    }

    public static String getString(Context context, String key, String defaultValue) {
        return getPref(context).getString(key, defaultValue);
    }

    public static Long getLong(Context context, String key, Long defaultValue) {
        return getPref(context).getLong(key, defaultValue);
    }

    public static void putLong(Context context, String key, Long value) {
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void putBoolean(Context context, String key, Boolean value) {
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void initDefaultPreference(Context context) {
        //set default prefs only first run

        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
    }

    private static SharedPreferences getPref(Context context) {
        if (mSharedPref == null) {
            mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return mSharedPref;
    }

    public static void registerListener(Context context,
                                        SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getPref(context).registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterListener(Context context,
                                          SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getPref(context).unregisterOnSharedPreferenceChangeListener(listener);
    }


    /**
     * get last update field from shared preference
     *
     * @param context context for getting shared preference
     * @return Date saved on shared preference, or 0
     */
    public static Date getLastUpdateDate(Context context) {
        return new Date(getLong(context, PREF_LAST_UPDATE, 0L));
    }

    /**
     * Save update date
     *
     * @param updateDate update date
     */
    public static void setUpdateDate(Context context, Date updateDate) {
        if (updateDate != null) {
            putLong(context, PREF_LAST_UPDATE, updateDate.getTime());
        }
    }

    public static void setLastCheckDate(Context context) {
        putLong(context, context.getString(R.string.preference_key_last_check),
                System.currentTimeMillis());
    }

    /**
     * Set update state to state (save to shared prefs).
     * In MainActivity this flag used for show or hide button
     * for run update service
     *
     * @param state state for saving
     */
    public static void setUpdateState(Context context, boolean state) {
        putBoolean(context, PREF_UPDATE_BUTTON_STATE, state);
        setLastCheckDate(context);

        if (state) {                                 //if == true - update found
            UpdateUtils.cancelAlarm(context);       //delete alarm - not needed
        } else if (! PreferenceUtils.isManualUpdate(context)) {
            //set alarm because new update installed now and need check update in future
            //only if user set frequency not manual

            UpdateUtils.setRepeatingAlarm(context);
        }
    }

    public static boolean isUpdateFound(Context context) {
        return getBoolean(context, PREF_UPDATE_BUTTON_STATE, false);
    }

    public static boolean isUpdateAllowed(Context context) {
        return getBoolean(context, context.getString(R.string.preference_key_allow_update), true);
    }

    public static long getLastCheckDate(Context context) {
        return getLong(context, context.getString(R.string.preference_key_last_check), 0L);
    }

    public static int getUpdateFreq(Context context) {
        try {
            return Integer.parseInt(getString(context,
                    context.getString(R.string.preference_key_update_freq), "1"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static boolean isManualUpdate(Context context) {
        return getUpdateFreq(context) == 0;
    }
}
