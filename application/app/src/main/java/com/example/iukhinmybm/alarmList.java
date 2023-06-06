package com.example.iukhinmybm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.iukhinmybm.databinding.AlarmListBinding;

import java.util.ArrayList;
import java.util.List;

import database.TaskDatabase;

public class alarmList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton buttonAdd;
    private ImageButton buttonBack;
    private TaskAdapter taskAdapter;
    private List<Task> mlistTask;
    AlarmListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alarm_list);
        recyclerView = findViewById(R.id.recyclerView);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonBack = findViewById(R.id.buttonBack);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(queryInternet.isNetworkAvailable(getApplicationContext())){
                    Intent intent  = new Intent(alarmList.this,setAlarm.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Không có kết nối mạng",Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(alarmList.this,MainActivity.class);
                startActivity(intent);
            }
        });

        taskAdapter = new TaskAdapter(new TaskAdapter.IClickItemTask() {
            @Override
            public void deleteTask(Task task) {
                clickDeleteTask(task);
            }
        });

        mlistTask = new ArrayList<>();
        mlistTask = TaskDatabase.getInstance(this).taskDAO().getListTask();
        taskAdapter.setData(mlistTask);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(taskAdapter);

        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        else {
            Task task = (Task) bundle.get("object_task");
            mlistTask = TaskDatabase.getInstance(this).taskDAO().getListTask();
            taskAdapter.setData(mlistTask);
        }

    }

    /* Load list alarm from Room Database */
    private void loadData()
    {
        mlistTask = TaskDatabase.getInstance(this).taskDAO().getListTask();
        taskAdapter.setData(mlistTask);
    }

    /* Delete Information of Alarm */
    private void clickDeleteTask(Task task)
    {
        TaskDatabase.getInstance(alarmList.this).taskDAO().getListTask();
        int requestCode = task.getRequestCode();
        int numberOfDays = task.getNumberOfDays();
        if(numberOfDays < 2){
            cancelALarm(requestCode);
        }
        else{
            while(numberOfDays>0){
                cancelALarm(requestCode--);
                numberOfDays--;
            }
        }
        TaskDatabase.getInstance(alarmList.this).taskDAO().deleteTask(task);
        loadData();
    }

    /* Cancel Alarm */
    private void cancelALarm(int requestCode)
    {
        Intent intent = new Intent(getApplicationContext(),AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
                );
        alarmManager.cancel(pendingIntent);
    }
}
