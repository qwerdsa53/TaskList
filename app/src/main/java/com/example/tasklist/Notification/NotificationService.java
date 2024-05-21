package com.example.tasklist.Notification;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.tasklist.Model.ToDoModel;
import com.example.tasklist.DB.DatabaseHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NotificationService extends Service {
    private DatabaseHandler db;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private List<ToDoModel> todoList;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int taskId = intent.getIntExtra("taskId", -1);
        if (taskId != -1) {
            cancelNotification(taskId);
        }
        db = new DatabaseHandler(this);
        db.openDatabase();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.openDatabase();
                todoList = db.getAllTasks();
                for (ToDoModel task : todoList) {
                    String text = task.getTask();
                    String time = task.getTaskTime();
                    String taskDate = task.getTaskDate();
                    int status = task.getStatus();
                    int id = task.getId();
                    scheduleNotification(text,time,taskDate,status,id);
                }
            }
        });
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @SuppressLint({"ScheduleExactAlarm", "ObsoleteSdkInt"})
    public void scheduleNotification(String text, String time, String taskDate, int status, int id) {
        Intent intent = new Intent(this, TaskNotificationReceiver.class);
        intent.putExtra("text", text);
        intent.putExtra("status", status);
        intent.putExtra("id", id);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            Date date = format.parse(taskDate + " " + time);
            long timeInMillis = Objects.requireNonNull(date).getTime();
            long curTime = Calendar.getInstance().getTimeInMillis();
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (timeInMillis > curTime) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,timeInMillis,pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("ObsoleteSdkInt")
    public void cancelNotification(int taskId) {
        Intent intent = new Intent(getApplicationContext(), TaskNotificationReceiver.class);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(), taskId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );
        } else {
            pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(), taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT
            );
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

}
