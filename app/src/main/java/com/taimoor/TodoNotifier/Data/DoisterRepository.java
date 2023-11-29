package com.taimoor.TodoNotifier.Data;


import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.taimoor.TodoNotifier.Model.Task;
import com.taimoor.TodoNotifier.Utilities.TaskRoomDatabase;

import java.util.List;

//Central place to save all the data
public class DoisterRepository {

    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTask;

    //Constructor to instantiate Database with the Application context
    public DoisterRepository(Application application) {
        TaskRoomDatabase database = TaskRoomDatabase.getDatabase(application);
        taskDao = database.taskDao();
        allTask = taskDao.getTask();

    }

    public LiveData<List<Task>> getAllTask() {
        return allTask;
    }


    public LiveData<List<Task>> getCompleteTask(boolean status) {
        return taskDao.getstatus(status);
    }

    public void insert(Task task) {
        TaskRoomDatabase.databaseWriterExecutor.execute(() -> taskDao.insertTask(task));
    }

    public LiveData<Task> get(long id){return taskDao.get(id);}


    public void update(Task task){
        TaskRoomDatabase.databaseWriterExecutor.execute(()-> taskDao.update(task));
    }

    public void delete(Task task){
        TaskRoomDatabase.databaseWriterExecutor.execute(()-> taskDao.delete(task));
    }

}
