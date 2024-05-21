package com.example.tasklist.DB;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tasklist.Model.ToDoModel;


import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String CREATE_TODO_TABLE =
            "CREATE TABLE " + TODO_TABLE +
                    "(" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TASK + " TEXT, " +
                    STATUS + " INTEGER, "+
                    DATE + " TEXT, "+
                    TIME + " TEXT"+
                    ")";

    private SQLiteDatabase db;
    private FirebaseSyncManager firebaseSyncManager;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
        firebaseSyncManager = new FirebaseSyncManager();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
        firebaseSyncManager = new FirebaseSyncManager();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        cv.put(DATE, task.getTaskDate());
        cv.put(TIME, task.getTaskTime());
        long id = db.insert(TODO_TABLE, null, cv);
        int intId = (int) id;
        task.setId(intId);
        if (firebaseSyncManager != null) {
            firebaseSyncManager.insertTask(task);
        } else {
            // Обработка случая, если firebaseSyncManager равен null
            Log.e("DatabaseHandler", "firebaseSyncManager не инициализирован");
        }
    }

    @SuppressLint("Range")
    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.query(TODO_TABLE, null, null, null, null, null, null, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        ToDoModel task = new ToDoModel();
                        task.setId(cur.getInt(cur.getColumnIndex(ID)));
                        task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        task.setTaskDate(cur.getString(cur.getColumnIndex(DATE)));
                        task.setTaskTime(cur.getString(cur.getColumnIndex(TIME)));
                        taskList.add(task);
                    } while (cur.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return taskList;
    }


    public void updateStatus(int id, int status) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(id)});
        ToDoModel task = new ToDoModel();
        task.setId(id);
        task.setStatus(status);
        firebaseSyncManager.updateStatus(task.getId(),status);
    }

    public void updateTask(int id, String task, String date, String time) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        cv.put(DATE, date);
        cv.put(TIME, time);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(id)});
        ToDoModel taskModel = new ToDoModel();
        taskModel.setId(id);
        taskModel.setTask(task);
        taskModel.setTaskDate(date);
        taskModel.setTaskTime(time);
        firebaseSyncManager.updateTask(taskModel);
    }

    public void deleteTask(int id) {
        db.delete(TODO_TABLE, ID + "= ?", new String[]{String.valueOf(id)});
        firebaseSyncManager.deleteTask(id);

    }
}