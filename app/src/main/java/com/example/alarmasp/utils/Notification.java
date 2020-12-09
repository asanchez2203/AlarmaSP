package com.example.alarmasp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.alarmasp.MainActivity;
import com.example.alarmasp.values.AlarmValues;
import com.example.alarmasp.values.ServicesValues;

public class Notification {
    private Context context;
    private NotificationCompat.Builder builder;

    public Notification(Context context){
        this.context = context;
        builder = new NotificationCompat.Builder(context,ServicesValues.CHANNEL_NOTIFICATION);
        builder.setAutoCancel(true);
    }

    public void showNotification(int statusAlarm){
        RingtoneManager rm = new RingtoneManager(context);
        Uri alarmSound = rm.getRingtoneUri(RingtoneManager.TYPE_NOTIFICATION);
        if(statusAlarm == AlarmValues.ALARM_DESACTIVATED){
            builder.setSmallIcon(context.getResources().getIdentifier("ic_baseline_report_problem_24", "drawable", context.getPackageName()));
            builder.setTicker("Alarma de Hogar Desactivada");
            builder.setContentTitle("La Alarma se encuentra desactivada");
            builder.setContentText("La Alarma se encuentra desactivada, no olvide activarla cuando salga de casa");
        }else if (statusAlarm == AlarmValues.ALARM_MOTION_DETECTED){
            builder.setSmallIcon(context.getResources().getIdentifier("ic_baseline_error_24", "drawable", context.getPackageName()));
            builder.setTicker("Alarma de Hogar - Movimiento detectado");
            builder.setContentTitle("La Alarma ha detectado movimiento");
            builder.setContentText("La Alarma ha detectado movimiento, se aseguraron los accesos");
        }
        builder.setWhen(System.currentTimeMillis());
        Intent intent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

       if(Build.VERSION.SDK_INT >= 26){
            NotificationChannel channel = new NotificationChannel(ServicesValues.CHANNEL_NOTIFICATION,
                    ServicesValues.CHANNEL_NOTIFICATION,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(ServicesValues.CHANNEL_NOTIFICATION);
           AudioAttributes audioAttributes = new AudioAttributes.Builder()
                   .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                   .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                   .build();
           
            channel.setSound(alarmSound,audioAttributes);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(ServicesValues.ID_NOTIFICACION,builder.build());
        Log.println(Log.INFO,"Notification","Notificación Lanzada");
    }
}
