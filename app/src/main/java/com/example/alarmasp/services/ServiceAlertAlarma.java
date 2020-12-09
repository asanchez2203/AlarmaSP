package com.example.alarmasp.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.alarmasp.commands.VerifyAlarmaStatus;
import com.example.alarmasp.utils.Preferences;

public class ServiceAlertAlarma extends Service {

    Handler handler = new Handler();
    private VerifyAlarmaStatus verifyAlarmaStatus;
    private Context context = this;
    //private Preferences preferences = new Preferences(context);

    private Runnable periodicGet = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(periodicGet, 20*1000);
            verifyAlarmaStatus = new VerifyAlarmaStatus(context);
            verifyAlarmaStatus.execute();
        }
    };
    @Override
    public void onCreate(){

    }

    @Override
    public int onStartCommand(Intent intent, int flag, int idProcess){
        handler.post(periodicGet);
        return START_STICKY;
    }

    @Override
    public void onDestroy(){

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
