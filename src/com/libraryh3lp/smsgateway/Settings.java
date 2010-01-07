package com.libraryh3lp.smsgateway;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;

public class Settings extends PreferenceActivity {
    @Override 
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        addPreferencesFromResource(R.xml.settings);
    }

    public static String getQueueName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("queue", "");
    }
    
    public static String getPhoneID(ContentResolver resolver) {
    	return Secure.getString(resolver, Secure.ANDROID_ID);
    }

    public static boolean getStartup(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("startup", false);
    }
}
