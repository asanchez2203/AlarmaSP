package com.example.alarmasp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    public static final String NOTIFICATION_KEY = "NOTIFICATION_ENABLED";
    public static final String SECURITY_COPY = "SECURITY_ENABLED";
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public Preferences(Context context){
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(!sharedPreferences.contains(NOTIFICATION_KEY)){
            editor = sharedPreferences.edit();
            editor.putBoolean(NOTIFICATION_KEY,true);
            editor.apply();
        }
        if(!sharedPreferences.contains(SECURITY_COPY)){
            editor = sharedPreferences.edit();
            editor.putBoolean(SECURITY_COPY,false);
            editor.apply();
        }
    }

    public void setNotificationEnabled(boolean value){
        editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATION_KEY,value);
        editor.apply();
    }

    public boolean getNotificationEnabled(){
        return sharedPreferences.getBoolean(NOTIFICATION_KEY,true);
    }

    public void setSecurityCopyEnabled(boolean value){
        editor = sharedPreferences.edit();
        editor.putBoolean(SECURITY_COPY,value);
        editor.apply();
    }

    public boolean getSecurityCopyEnabled(){
        return  sharedPreferences.getBoolean(SECURITY_COPY,false);
    }
}
