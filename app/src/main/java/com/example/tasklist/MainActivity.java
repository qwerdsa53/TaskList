package com.example.tasklist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.tasklist.Adapter.ToDoAdapter;
import com.example.tasklist.Model.ToDOModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView tasksRecycleView;
    private ToDoAdapter tasksAdapter;
    private List<ToDOModel> taskList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        taskList = new ArrayList<>();

        tasksRecycleView = findViewById(R.id.TasksRecyclerView);
        tasksRecycleView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ToDoAdapter(this);
        tasksRecycleView.setAdapter(tasksAdapter);

        ToDOModel task = new ToDOModel();
        task.setTask("This is a task");
        task.setStatus(0);
        task.setId(1);

        taskList.add(task);



        tasksAdapter.setTasks(taskList);
    }
}