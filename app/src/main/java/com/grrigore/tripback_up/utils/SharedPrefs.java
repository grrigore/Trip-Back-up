package com.grrigore.tripback_up.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static com.grrigore.tripback_up.utils.Constants.SHARED_PREFERENCES;

public class SharedPrefs {

    private static SharedPrefs sharedPrefs;
    protected Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    private SharedPrefs(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
    }

    /**
     * Creates single instance of SharedPreferenceUtils
     *
     * @param context context of Activity or Service
     * @return Returns instance of SharedPreferenceUtils
     */
    public static synchronized SharedPrefs getInstance(Context context) {
        if (sharedPrefs == null) {
            sharedPrefs = new SharedPrefs(context.getApplicationContext());
        }

        return sharedPrefs;
    }

    /**
     * Stores String value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    public void setValue(String key, String value) {
        sharedPreferencesEditor.putString(key, value);
        sharedPreferencesEditor.commit();
    }

    /**
     * Retrieves String value from preference
     *
     * @param key          key of preference
     * @param defaultValue default value if no key found
     */
    public String getStringValue(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    /**
     * Removes key from preference
     *
     * @param key key of preference that is to be deleted
     */
    public void removeKey(String key) {
        if (sharedPreferencesEditor != null) {
            sharedPreferencesEditor.remove(key);
            sharedPreferencesEditor.commit();
        }
    }


    /**
     * Clears all the preferences stored
     */
    public void clear() {
        sharedPreferencesEditor.clear().commit();
    }
}
