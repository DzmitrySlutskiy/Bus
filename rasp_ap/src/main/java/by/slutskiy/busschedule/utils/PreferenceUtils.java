package by.slutskiy.busschedule.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import by.slutskiy.busschedule.R;

/**
 * PreferenceUtils
 * Version 1.0
 * 30.09.2014
 * Created by Dzmitry Slutskiy.
 */
public class PreferenceUtils {

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
}
