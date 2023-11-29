package com.taimoor.TodoNotifier.Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.taimoor.TodoNotifier.Model.Task;

import java.util.List;

//Annotate @Dao to make it recognized as a data access object
//which will allow us to do CRUD operations on database
@Dao
public interface TaskDao {

    @Insert
    void insertTask(Task task);

    //Delete all the tasks
    @Query(" DELETE FROM TASK_TABLE")
    void deleteAll();

    //Get a list of all the tasks
    @Query(" SELECT * FROM TASK_TABLE ")
    LiveData<List<Task>> getTask();

    //get one task by passing the id of task
    @Query(" SELECT * FROM task_table WHERE task_table.task_id == :id ")
    LiveData<Task> get(long id);

    //Get Completed Tasks
    @Query(" SELECT * FROM task_table WHERE task_table.is_done == :done ")
    LiveData<List<Task>> getstatus(boolean done);

    //update a task
    @Update
    void update(Task task);

    //delete a task
    @Delete
    void delete(Task task);

}
