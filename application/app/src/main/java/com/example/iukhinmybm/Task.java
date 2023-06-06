package com.example.iukhinmybm;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "task")
public class Task implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String time;
    private String date;
    private String timePump;
    private int requestCode;
    private int numberOfDays;

    public Task(String time,String date,String timePump,int requestCode,int numberOfDays){
        this.time = time;
        this.date = date;
        this.timePump = timePump;
        this.requestCode = requestCode;
        this.numberOfDays = numberOfDays;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimePump() {
        return timePump;
    }

    public void setTimePump(String timePump) {
        this.timePump = timePump;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }
}
