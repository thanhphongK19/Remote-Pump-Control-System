package com.example.iukhinmybm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataApptoMCU = database.getReference("dataApptoMCU");


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Hi", "I am here");

        int endHour = 0;
        int endMinute = 0;
        int alarmPump = 1;

        int startHour = intent.getIntExtra("startHour", 0);
        int startMinute = intent.getIntExtra("startMinute", 0);
        int time = intent.getIntExtra("time", 0);
        int timePlay = Integer.parseInt(String.valueOf(time));

        if ((startMinute + timePlay) < 60) {
            endMinute = startMinute + timePlay;
            endHour = startHour;
        } else {
            endMinute = (startMinute + timePlay) - 60;
            endHour = startHour + 1;
        }

        dataApptoMCU.child("startHour").setValue(startHour);
        dataApptoMCU.child("startMinute").setValue(startMinute);
        dataApptoMCU.child("endHour").setValue(endHour);
        dataApptoMCU.child("endMinute").setValue(endMinute);
        dataApptoMCU.child("alarmPump").setValue(alarmPump);


        Notification notification = new Notification.Builder(context)
                .setContentTitle("Máy bơm đang bật")
                .setContentText("Thời gian bơm" + timePlay)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notification);
        //}
    }
}
