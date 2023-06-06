package com.example.iukhinmybm;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iukhinmybm.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    public static ActivityMainBinding binding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataApptoMCU = database.getReference("dataApptoMCU");
    DatabaseReference dataMCUtoApp = database.getReference("dataMCUtoApp");
    DatabaseReference wifi = database.getReference("wifi");
    int OnPump = 1;
    int OffPump = 0;
    static int onlyOne = 0;
    static int ActivityLifeCycle;
    static int currentStatusPump;
    static int previousStatusPump;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonON.setBackgroundColor(Color.GREEN);
        binding.buttonOFF.setBackgroundColor(Color.RED);

        clickButton();
        queryFireBase();
        ActivityLifeCycle = 1;

    }
    @Override
    protected void onPause() {
        super.onPause();
        ActivityLifeCycle = 0;
    }
    @Override
    protected void onStop() {
        super.onStop();
        ActivityLifeCycle = 0;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ActivityLifeCycle = 1;
        onlyOne = 0;
    }


    private void clickButton()
    {
        binding.buttonON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(queryInternet.isNetworkAvailable(getApplicationContext())){
                    dataApptoMCU.child("Pump").setValue(OnPump);
                    startAnimation();
                    binding.buttonON.setBackgroundColor(Color.BLACK);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Không có kết nối mạng",Toast.LENGTH_SHORT).show();

                }

            }
        });

        binding.buttonOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(queryInternet.isNetworkAvailable(getApplicationContext())){
                    dataApptoMCU.child("Pump").setValue(OffPump);
                    dataApptoMCU.child("alarmPump").setValue(OffPump);
                    dataApptoMCU.child("startHour").setValue(OffPump);
                    dataApptoMCU.child("startMinute").setValue(OffPump);
                    dataApptoMCU.child("endHour").setValue(OffPump);
                    dataApptoMCU.child("endMinute").setValue(OffPump);
                    stopAnimation();
                    binding.buttonON.setBackgroundColor(Color.GREEN);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Không có kết nối mạng",Toast.LENGTH_SHORT).show();

                }

            }
        });

        binding.buttonAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, alarmList.class);
                    startActivity(intent);
            }
        });


    }


    private void queryFireBase()
    {

        /* Checking status Pump */
        dataMCUtoApp.child("statusPump").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String statusP = Objects.requireNonNull(snapshot.getValue()).toString();
                int statusPump = Integer.parseInt(statusP);
                if (statusPump == 1) {
                    binding.statusPump.setText("Máy bơm đang bật");
                    binding.statusPump.setTextColor(Color.BLACK);
                    binding.colorBackGround.setBackgroundColor(Color.WHITE);
                    startAnimation();

                }
                else {
                    binding.statusPump.setText("Máy bơm đang tắt");
                    binding.statusPump.setTextColor(Color.BLACK);
                    binding.buttonON.setBackgroundColor(Color.GREEN);
                    stopAnimation();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /* checking status Wifi of ESP32 */
        dataMCUtoApp.child("statusWifi").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String status_Wifi = Objects.requireNonNull(snapshot.getValue()).toString();
                    currentStatusPump = Integer.parseInt(status_Wifi);
                    if(onlyOne == 1){
                        if(ActivityLifeCycle == 1){
                            if(currentStatusPump != previousStatusPump){
                                final Toast toast = Toast.makeText(getApplicationContext(),"MÁY BƠM ĐÃ KẾT NỐI MẠNG",Toast.LENGTH_SHORT);
                                new CountDownTimer(2000, 1000)
                                {
                                    public void onTick(long millisUntilFinished) {toast.show();}
                                    public void onFinish() {
                                        toast.cancel();
                                    }
                                }.start();
                            }
                        }
                    }
                    if(onlyOne == 0 && ActivityLifeCycle == 1){
                        Toast.makeText(getApplicationContext(),"Đang kiểm tra kết nối",Toast.LENGTH_SHORT).show();
                        onlyOne = 1;
                    }
                    previousStatusPump = currentStatusPump ;

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        /* Checking status Pump */
//        dataApptoMCU.child("alarmPump").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String alarmP = Objects.requireNonNull(snapshot.getValue()).toString();
//                int alarmPump = Integer.parseInt(alarmP);
//                if (alarmPump == 1) {
//                    final Toast toast = Toast.makeText(getApplicationContext(),"MÁY BƠM BẮT ĐẦU HẸN GIỜ",Toast.LENGTH_SHORT);
//                    new CountDownTimer(2000, 1000)
//                    {
//                        public void onTick(long millisUntilFinished) {toast.show();}
//                        public void onFinish() {
//                            toast.cancel();
//                        }
//                    }.start();
//                }
//                else {
//                    final Toast toast = Toast.makeText(getApplicationContext(),"MÁY BƠM BẮT ĐẦU HẸN GIỜ",Toast.LENGTH_SHORT);
//                    new CountDownTimer(2000, 1000)
//                    {
//                        public void onTick(long millisUntilFinished) {toast.show();}
//                        public void onFinish() {
//                            toast.cancel();
//                        }
//                    }.start();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }

    private void startAnimation()
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                binding.imagefan.animate().rotationBy(360).withEndAction(this).setDuration(5000)
                        .setInterpolator(new LinearInterpolator()).start();
            }
        };
        binding.imagefan.animate().rotationBy(360).withEndAction(runnable).setDuration(5000)
                .setInterpolator(new LinearInterpolator()).start();
    }
    private void stopAnimation()
    {
        binding.imagefan.animate().cancel();
    }


}
