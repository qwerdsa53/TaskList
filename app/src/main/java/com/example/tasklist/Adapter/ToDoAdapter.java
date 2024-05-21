package com.example.tasklist.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasklist.AddNewTask;
import com.example.tasklist.MainActivity;
import com.example.tasklist.Model.ToDoModel;
import com.example.tasklist.Notification.NotificationService;
import com.example.tasklist.DB.DatabaseHandler;
import com.example.tasklist.databinding.TaskLayoutBinding;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    private List<ToDoModel> todoList;
    private MainActivity activity;
    private final DatabaseHandler db;

    public ToDoAdapter(DatabaseHandler db, MainActivity activity){
        this.db = db;
        this.activity = activity;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        TaskLayoutBinding binding = TaskLayoutBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }
    private void sortTasks() {
        todoList.sort(new Comparator<ToDoModel>() {
            @Override
            public int compare(ToDoModel o1, ToDoModel o2) {
                if (!o1.getTaskDate().equals(o2.getTaskDate())) {
                    return o1.getTaskDate().compareTo(o2.getTaskDate());
                } else {
                    return o1.getTaskTime().compareTo(o2.getTaskTime());
                }
            }
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position){
        db.openDatabase();
        Intent serviceIntent = new Intent(getContext(), NotificationService.class);
        final ToDoModel item = todoList.get(position);
        holder.binding.todoCheckBox.setText(item.getTask());
        holder.binding.todoCheckBox.setChecked(toBoolean(item.getStatus()));
        holder.binding.q123.setText(item.getTaskDate());
        holder.binding.TIME.setText(item.getTaskTime());
        holder.binding.todoCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                db.updateStatus(item.getId(), 1);
                activity.startService(serviceIntent);
            }else{
                db.updateStatus(item.getId(),0);
            }
        });
    }

    public int getItemCount(){
        return todoList.size();
    }

    private boolean toBoolean(int n){
        return n!=0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTasks(List<ToDoModel> todoList){
        this.todoList = todoList;
        sortTasks();
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);

        Intent serviceIntent = new Intent(getContext(), NotificationService.class);
        serviceIntent.putExtra("taskId", item.getId());
        getContext().startService(serviceIntent);
    }

    public Context getContext() {
        return activity;
    }

    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        bundle.putString("date", item.getTaskDate());
        bundle.putString("time", item.getTaskTime());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TaskLayoutBinding binding;

        ViewHolder(TaskLayoutBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

