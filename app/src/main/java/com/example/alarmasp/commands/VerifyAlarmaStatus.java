package com.example.alarmasp.commands;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.alarmasp.utils.Connection;
import com.example.alarmasp.utils.Notification;
import com.example.alarmasp.utils.Preferences;
import com.example.alarmasp.values.AlarmValues;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VerifyAlarmaStatus extends AsyncTask<Void,Integer,Integer> {

    private Connection cn;
    private Notification notification;
    private Context context;
    private Preferences preferences;

    private int alarmStatus;

    public VerifyAlarmaStatus(Context context){
        this.context = context;
        preferences = new Preferences(context);
    }

    public int getAlarmStatus() {
        return alarmStatus;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        cn = new Connection();
        java.sql.Connection cnEnv = cn.connect();
        if(cnEnv!=null){
            Log.println(Log.INFO,"MySQLConnection","Conexión para Estatus de Alarma OK");
            try {
                Statement st = cnEnv.createStatement();
                ResultSet rs = st.executeQuery("SELECT typeStatus FROM Alarm ORDER BY dateAlarm DESC LIMIT 1");

                rs.next();
                alarmStatus = rs.getInt("typeStatus");
                Log.println(Log.INFO,"Alarm Status","Alarm type status: "+ alarmStatus);
                rs.close();
                st.close();
                cnEnv.close();
                cn.close();
                return alarmStatus;
            } catch (SQLException throwables) {
                Log.println(Log.ERROR,"Fail Get Alarm Status",
                        throwables.getMessage());
                alarmStatus = -1;
            }
        }else{
            Log.println(Log.ERROR,"MySQLConnection","Conexión para Estatus de Alarma FAIL");
            alarmStatus = -1;
        }
        return alarmStatus;
    }

    @Override
    protected void onPostExecute(Integer s) {
        Log.println(Log.INFO,"NOTIFICATION STATUS","Estado de la notificación:" + preferences.getNotificationEnabled());
        if(preferences.getNotificationEnabled()){
            if(getAlarmStatus() == AlarmValues.ALARM_DESACTIVATED ||
                    getAlarmStatus() == AlarmValues.ALARM_MOTION_DETECTED){
                notification = new Notification(context);
                notification.showNotification(getAlarmStatus());
            }
        }
    }
}
