package com.example.iukhinmybm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iukhinmybm.databinding.SetAlarmBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import database.TaskDatabase;

public class setAlarm extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference requestCodeDB = database.getReference("requestCode");

    SetAlarmBinding binding;
    Calendar calendar;
    DatePicker datePicker;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    int monday = 0;
    int tuesday = 0;
    int thursday = 0;
    int wednesday = 0;
    int friday = 0 ;
    int saturday = 0;
    int sunday = 0;
    static int onlyone = 0;

    static int requestCode = 0;
    ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();

    /* variable for save RoomDabtabase*/
    int hourDatabase = 0;
    int minuteDatabase = 0;
    String dayDatabase = "";
    int timePumpDatabase = 0;
    int rqCodeDatabase = 0;
    int numberOfDayDatabase = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SetAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        calendar = Calendar.getInstance();
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        queryDateButton();
        queryFunctionButton();
        readRequestCode();

    }


    private void queryDateButton()
    {

        binding.monday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (binding.monday.isChecked()){
                    monday = 1;
                }
                else{
                    monday = 0;
                }
            }
        });
        binding.tuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (binding.tuesday.isChecked()){
                    tuesday = 1;
                }
                else{
                    tuesday = 0;
                }
            }
        });
        binding.wednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (binding.wednesday.isChecked()){
                    wednesday = 1;
                }
                else{
                    wednesday = 0;
                }
            }
        });
        binding.thursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (binding.thursday.isChecked()){
                    thursday = 1;
                }
                else{
                    thursday = 0;
                }
            }
        });
        binding.friday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (binding.friday.isChecked()){
                    friday = 1;
                }
                else{
                    friday = 0;
                }
            }
        });
        binding.saturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (binding.saturday.isChecked()){
                    saturday = 1;
                }
                else{
                    saturday = 0;
                }
            }
        });
        binding.sunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (binding.sunday.isChecked()){
                    sunday = 1;
                }
                else{
                    sunday = 0;
                }
            }
        });

    }
    private void queryFunctionButton()
    {
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(setAlarm.this,alarmList.class);
                startActivity(intent);
            }
        });

        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if(queryInternet.isNetworkAvailable(getApplicationContext())){
                    if(queryTimePump()==1){
                        sheduleNotification();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Không có kết nối mạng",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }



    private void readRequestCode(){
        requestCodeDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String request = Objects.requireNonNull(snapshot.getValue()).toString();
                    requestCode = Integer.parseInt(request);
                    //requestCode++;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    
    private int queryTimePump(){
        int flag = 0;

        String timeP = binding.editText.getText().toString();
        int timePlay = Integer.parseInt(timeP);

        if(timePlay > 60 || timePlay <= 0){
            Toast.makeText(getApplicationContext(),"Giá trị hợp lệ từ 1 đến 60",Toast.LENGTH_LONG).show();
            flag = 0;
        }
        else{
            Toast.makeText(getApplicationContext(),"Đặt chuông báo thành công",Toast.LENGTH_LONG).show();
            flag = 1;
        }
        return flag;
    }
    
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sheduleNotification()
    {
        clear_variableDatabase();
        if((monday+tuesday+wednesday+thursday+friday+saturday+sunday) >=1 ){
            if(monday == 1){
                getTime(2);
                dayDatabase = "2 ";
                numberOfDayDatabase++;
            }
            if(tuesday == 1){
                getTime(3);
                dayDatabase = dayDatabase + "3 ";
                numberOfDayDatabase++;
            }
            if(wednesday == 1){
                getTime(4);
                dayDatabase = dayDatabase + "4 ";
                numberOfDayDatabase++;
            }
            if(thursday == 1){
                getTime(5);
                dayDatabase = dayDatabase + "5 ";
                numberOfDayDatabase++;
            }
            if(friday == 1){
                getTime(6);
                dayDatabase = dayDatabase + "6 ";
                numberOfDayDatabase++;
            }
            if(saturday == 1){
                getTime(7);
                dayDatabase = dayDatabase + "7 ";
                numberOfDayDatabase++;
            }
            if(sunday == 1){
                getTime(1);
                dayDatabase = dayDatabase + "CN";
                numberOfDayDatabase++;
            }
        }
        else{
            getTime(0);
            numberOfDayDatabase = 1;
        }

        /* Save Database */
        addTask(hourDatabase,minuteDatabase,dayDatabase,timePumpDatabase,rqCodeDatabase,numberOfDayDatabase);
        clear_choice();
        clear_variableDatabase();
    }


    /* Get time and Set Alarm */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getTime(int date)
    {
        if(date>0){
            if(calendar.get(Calendar.DAY_OF_WEEK)==1){
                if(date==1){
                    calendar.set(Calendar.DAY_OF_WEEK,date);
                }
                else{
                    int j = calendar.get(Calendar.WEEK_OF_MONTH);
                    calendar.set(Calendar.WEEK_OF_MONTH,++j);
                    calendar.set(Calendar.DAY_OF_WEEK,date);
                    onlyone = 1;
                }
            }
            else if(date < calendar.get(Calendar.DAY_OF_WEEK)){
                int i = calendar.get(Calendar.WEEK_OF_MONTH);
                calendar.set(Calendar.WEEK_OF_MONTH,++i);
                calendar.set(Calendar.DAY_OF_WEEK,date);
                onlyone = 1;
            }
            else if(date >= calendar.get(Calendar.DAY_OF_WEEK)){
                calendar.set(Calendar.DAY_OF_WEEK,date);
            }
            //calendar.set(Calendar.DAY_OF_MONTH,binding.datePicker.month)
        }
        else{
            int i =  calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH,i);
        }
        calendar.set(Calendar.HOUR_OF_DAY,binding.timePicker.getHour());
        calendar.set(Calendar.MINUTE,binding.timePicker.getMinute());
        calendar.set(Calendar.SECOND,0);
        //new setAlarm(date,calendar.getTimeInMillis());


        /* Set Alarm */
        String timeP = binding.editText.getText().toString();
        int timePlay = Integer.parseInt(timeP);

        Intent intent = new Intent(this,AlarmReceiver.class);
        intent.putExtra("startHour",binding.timePicker.getHour());
        intent.putExtra("startMinute",binding.timePicker.getMinute());
        intent.putExtra("time",timePlay);


        if(date > 0){
            pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(),
                    requestCode,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY*7,
                    pendingIntent);
        }
        else{
            pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(),
                    requestCode,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }

        if(onlyone == 1){
            int i = calendar.get(Calendar.WEEK_OF_MONTH);
            calendar.set(Calendar.WEEK_OF_MONTH,--i);
            onlyone = 0;
        }


        /* Save Room Database */
        hourDatabase = binding.timePicker.getHour();
        minuteDatabase = binding.timePicker.getMinute();
        timePumpDatabase = timePlay;
        rqCodeDatabase = requestCode;

        requestCode++;

        /* Save request Code Firebase */
        requestCodeDB.setValue(requestCode);
        intentArray.add(pendingIntent);
    }


    private void addTask(int hour,int minute,String day,int timeP,int request,int numberOfDay)
    {
        String min = "";
        String time = "";
        if(minute < 10){
            min = "0" + minute;
            time = hour + ":" + min;
        }
        else{
            time = hour + ":" + minute;
        }

        String timePump = String.valueOf(timeP) + " phút";
        String dayDatabase = day;
        int requestCode = request;


        Task task = new Task(time,dayDatabase,timePump,requestCode,numberOfDay);
        TaskDatabase.getInstance(this).taskDAO().insertTask(task);

        Intent intent = new Intent(this,alarmList.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_task", task);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private void clear_choice()
    {
        monday = 0;
        tuesday = 0;
        wednesday = 0;
        thursday = 0;
        friday = 0;
        saturday = 0;
        sunday = 0;
    }
    private void clear_variableDatabase()
    {
        hourDatabase = 0;
        minuteDatabase = 0;
        dayDatabase = "";
        timePumpDatabase = 0;
        rqCodeDatabase = 0;
        numberOfDayDatabase = 0;
    }

}
