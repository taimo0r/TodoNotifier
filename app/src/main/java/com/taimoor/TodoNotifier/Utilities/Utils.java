package com.taimoor.TodoNotifier.Utilities;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.taimoor.TodoNotifier.Model.Priority;
import com.taimoor.TodoNotifier.Model.Task;

import java.text.SimpleDateFormat;
import java.util.Date;

//This class contains helper functions and methods to format date and time
public class Utils {

    //Getting the date in the required format
    public static String formatDate(Date date){
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern("EEE, MMM  dd");
        return simpleDateFormat.format(date);
    }

    //Method to hide on screen Keyboard
    public static void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    //Method to set the color of data according to the priority selected
    public static int priorityColor(Task task) {
        int color;
        if (task.getPriority() == Priority.HIGH){
            color = Color.argb(200,201,21,23);
        }else if (task.getPriority()==Priority.MEDIUM){
            color = Color.argb(200,155,179,0);
        }else{
            color = Color.argb(200,51,181,129);
        }
        return color;
    }

}
