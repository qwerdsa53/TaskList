package com.example.tasklist;
import com.example.tasklist.Model.ToDoModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
public class ToDoModelComparator implements Comparator<ToDoModel> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    @Override
    public int compare(ToDoModel task1, ToDoModel task2) {
        // Сначала сравниваем по статусу
        int statusComparison = Integer.compare(task1.getStatus(), task2.getStatus());
        if (statusComparison != 0) {
            return statusComparison;
        }
        // Затем сравниваем по дате
        try {
            Date date1 = dateFormat.parse(task1.getTaskDate());
            Date date2 = dateFormat.parse(task2.getTaskDate());
            int dateComparison = date1.compareTo(date2);
            if (dateComparison != 0) {
                return dateComparison;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            Date time1 = timeFormat.parse(task1.getTaskTime());
            Date time2 = timeFormat.parse(task2.getTaskTime());
            return time1.compareTo(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }
}