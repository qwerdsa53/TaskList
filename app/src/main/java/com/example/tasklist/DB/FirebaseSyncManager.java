package com.example.tasklist.DB;

import android.util.Log;

import com.example.tasklist.Model.ToDoModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FirebaseSyncManager {

    private static final String TAG = "FirebaseSyncManager";
    private final DatabaseReference mDatabase;

    public FirebaseSyncManager() {
        mDatabase = FirebaseDatabase.getInstance("https://task-list-9cd41-default-rtdb.europe-west1.firebasedatabase.app").getReference("tasks");
    }

    public void insertTask(ToDoModel task) {
        String key = String.valueOf(task.getId());
        mDatabase.child(key).setValue(task)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Task inserted in Firebase"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to insert task in Firebase", e));

    }

    public void updateTask(ToDoModel task) {
        mDatabase.child(String.valueOf(task.getId())).setValue(task)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Task updated in Firebase"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update task in Firebase", e));
    }
    public void updateStatus(int taskId, int newState) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", newState);

        mDatabase.child(String.valueOf(taskId)).updateChildren(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Status updated in Firebase"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update status in Firebase", e));
    }
    public void deleteTask(int id) {
        mDatabase.child(String.valueOf(id)).removeValue()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Task deleted in Firebase"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to delete task in Firebase", e));
    }
}