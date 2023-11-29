package com.taimoor.TodoNotifier.Utilities;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.taimoor.TodoNotifier.Data.TaskDao;
import com.taimoor.TodoNotifier.Model.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//add the entities of the Task class
//and add the type converter class
@Database(entities = {Task.class}, version = 1, exportSchema = false)
@TypeConverters({Converter.class})
public abstract class TaskRoomDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "todoister_database";
    public static final int NUMBER_OF_THREADS = 4;
    private static volatile TaskRoomDatabase Instance;
    public static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public static final RoomDatabase.Callback sRoomDatabaseCallBack = new RoomDatabase.Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    databaseWriterExecutor.execute(() -> {
                        //invoke Dao, and perform CRUD operations
                        TaskDao taskDao = Instance.taskDao();
                        taskDao.deleteAll();

                    });
                }
            };

    public static TaskRoomDatabase getDatabase(final Context context) {
        //this is to make sure everything  goes back to the background thread
        if (Instance == null) {
            synchronized (TaskRoomDatabase.class) {
                //this is to make sure there's only one instance of the database created throughout the whole project
                if (Instance == null) {
                    //Build the database
                    Instance = Room.databaseBuilder(context.getApplicationContext(), TaskRoomDatabase.class, DATABASE_NAME)
                            .addCallback(sRoomDatabaseCallBack)
                            .build();
                }
            }
        }
        return Instance;
    }

    //make an interface Class named TaskDao to perform CRUD operations
    public abstract TaskDao taskDao();

}
