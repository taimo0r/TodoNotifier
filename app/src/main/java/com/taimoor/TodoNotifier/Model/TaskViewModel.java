package com.taimoor.TodoNotifier.Model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.taimoor.TodoNotifier.Data.DoisterRepository;
import com.taimoor.TodoNotifier.Utilities.TaskRoomDatabase;

import java.util.List;

//Used to pass data from activity to other activities or fragment
public class TaskViewModel extends AndroidViewModel{

    public static DoisterRepository repository;
    public final LiveData<List<Task>> allTasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new DoisterRepository(application);
        allTasks = repository.getAllTask();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<Task>> getTaskState(boolean state) {
        return repository.getCompleteTask(state);
    }

    public static void insert(Task task) {
        repository.insert(task);
    }

    public LiveData<Task> get(long id) {
        return repository.get(id);
    }

    public static void update(Task task) {
        repository.update(task);
    }

    public static void delete(Task task) {
        repository.delete(task);
    }

}
