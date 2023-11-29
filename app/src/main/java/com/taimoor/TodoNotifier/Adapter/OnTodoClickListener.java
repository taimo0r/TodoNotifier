package com.taimoor.TodoNotifier.Adapter;

import com.taimoor.TodoNotifier.Model.Task;

//This interface will be used to get the onCLick functionality of the TodoRowItem
public interface OnTodoClickListener {

    void onTodoClick(Task task);


}
