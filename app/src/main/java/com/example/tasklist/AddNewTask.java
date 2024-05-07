package com.example.tasklist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.tasklist.Model.ToDoModel;
import com.example.tasklist.DB.DatabaseHandler;
import com.example.tasklist.Notification.NotificationService;
import com.example.tasklist.databinding.NewTaskBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;
import java.util.Objects;

public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";
    private DatabaseHandler db;
    private NewTaskBinding binding;

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = NewTaskBinding.inflate(inflater, container, false);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return binding.getRoot();
    }

    @SuppressLint("UseRequireInsteadOfGet")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            String date = bundle.getString("date");
            String time = bundle.getString("time");
            binding.newTaskText.setText(task);
            binding.setDateB.setText(date);
            binding.setTimeB.setText(time);
            assert task != null;
            if(!task.isEmpty())
                binding.newTaskButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimaryDark));
        }

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        binding.newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()){
                    binding.newTaskButton.setEnabled(false);
                    binding.newTaskButton.setTextColor(Color.GRAY);
                }
                else{
                    binding.newTaskButton.setEnabled(true);
                    binding.newTaskButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimaryDark));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        binding.setDateB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get date
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                // initialization DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // it will work after selection
                                String selectedDate;
                                selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                binding.setDateB.setText(selectedDate);
                                binding.setDateB.invalidate();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        binding.setTimeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get time
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                // initialization TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // it will work after selection
                                String selectedDate;
                                if (minute>9)
                                    selectedDate = hourOfDay + ":" + minute;
                                else
                                    selectedDate = hourOfDay + ":0" + minute;
                                binding.setTimeB.setText(selectedDate);
                                binding.setTimeB.invalidate();
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });


        final boolean finalIsUpdate = isUpdate;
        binding.newTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.newTaskButton.isEnabled()){
                    String text = binding.newTaskText.getText().toString();
                    String date = binding.setDateB.getText().toString();
                    String time = binding.setTimeB.getText().toString();
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setTaskDate(date);
                    task.setTaskTime(time);
                    task.setStatus(0);
                    if(bundle != null && finalIsUpdate){
                        // updating an existing task
                        int id = bundle.getInt("id");
                        db.updateTask(id, text, date, time);
                    } else {
                        // adding new task
                        db.insertTask(task);
                    }

                    dismiss();
                }
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener)
            ((DialogCloseListener)activity).handleDialogClose(dialog);
        Intent serviceIntent = new Intent(getContext(), NotificationService.class);
        Objects.requireNonNull(activity).startService(serviceIntent);
    }
}
