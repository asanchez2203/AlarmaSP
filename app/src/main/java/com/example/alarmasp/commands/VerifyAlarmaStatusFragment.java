package com.example.alarmasp.commands;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alarmasp.R;
import com.example.alarmasp.utils.Connection;
import com.example.alarmasp.utils.Notification;
import com.example.alarmasp.utils.Preferences;
import com.example.alarmasp.values.AlarmValues;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VerifyAlarmaStatusFragment extends AsyncTask<Void,Integer,Integer> {

    private Connection cn;
    private Notification notification;
    private Context context;
    private Preferences preferences;
    private ImageView img;
    private TextView suceso;
    private TextView status;

    private int alarmStatus;

    public VerifyAlarmaStatusFragment(Context context,ImageView img, TextView suceso,TextView status){
        this.context = context;
        preferences = new Preferences(context);
        this.suceso = suceso;
        this.img = img;
        this.status = status;
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
        if(s == AlarmValues.ALARM_ACTIVE_AND_OK){
            img.setImageResource(context.getResources().getIdentifier("ic_baseline_alarma_ok_80", "drawable", context.getPackageName()));
            img.setTag(context.getResources().getIdentifier("ic_baseline_alarma_ok_80", "drawable", context.getPackageName()));
            status.setText("La alarma está activada");
            suceso.setText("No hay Sucesos");
        }else if(s == AlarmValues.ALARM_DESACTIVATED){
            img.setImageResource(context.getResources().getIdentifier("ic_baseline_alarma_off_80", "drawable", context.getPackageName()));
            img.setTag(context.getResources().getIdentifier("ic_baseline_alarma_off_80", "drawable", context.getPackageName()));
            status.setText("La alarma está desactivada");
            suceso.setText("La alarma se encuentra desactivada, por favor activela");
        }else if(s == AlarmValues.ALARM_MOTION_DETECTED){
            img.setImageResource(context.getResources().getIdentifier("ic_baseline_info_80", "drawable", context.getPackageName()));
            img.setTag(context.getResources().getIdentifier("ic_baseline_info_80", "drawable", context.getPackageName()));
            status.setText("La alarma ha detectado un problema");
            suceso.setText("Se ha detectado movimiento en la casa");
        }
    }
}
