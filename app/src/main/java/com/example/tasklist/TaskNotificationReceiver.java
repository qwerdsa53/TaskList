package com.example.tasklist;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TaskNotificationReceiver extends BroadcastReceiver {
    @SuppressLint({"MissingPermission", "UnsafeProtectedBroadcastReceiver"})
    @Override
    public void onReceive(Context context, Intent intent) {
        String taskText = intent.getStringExtra("text");
        int taskId = intent.getIntExtra("id", 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Notification")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Task Reminder")
                .setContentText(taskText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(taskId, builder.build());
    }
}