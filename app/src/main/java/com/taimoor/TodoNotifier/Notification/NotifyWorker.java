package com.taimoor.TodoNotifier.Notification;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.taimoor.TodoNotifier.Utilities.App.Channel_1_id;
import static com.taimoor.TodoNotifier.Utilities.App.Channel_2_id;
import static com.taimoor.TodoNotifier.Utilities.App.Channel_3_id;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.taimoor.TodoNotifier.Activities.MainActivity;
import com.taimoor.TodoNotifier.R;

import java.util.Random;


//Class for WorkManager
//Using for Scheduling notifications
public class NotifyWorker extends Worker {
    String msg;
    int Priority1, key;
    byte[] image;
    Bitmap picture;

    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {
        //Getting passed data for notification
        msg = getInputData().getString("Msg");
        Priority1 = getInputData().getInt("priority", 1);
        key = getInputData().getInt("key", 1);

        //Calling the notification method according to priority
        if (Priority1 == NotificationCompat.PRIORITY_HIGH) {
            SetNotifications_High();
        } else if (Priority1 == NotificationCompat.PRIORITY_DEFAULT) {
            SetNotifications_Medium();
        } else {
            SetNotifications_Low();
        }

        return Result.success();
    }

    private void SetNotifications_Low() {
        Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, 0);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), Channel_3_id)
                .setSmallIcon(R.drawable.low_notify)
                .setContentTitle("Todo Reminder")
                .setContentText(msg)
                .setPriority(Priority1)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.todo_icon_low, "Open", contentIntent)
                .setColor(Color.argb(200, 51, 181, 129))
                .build();

        notificationManager.notify(key, notification);
    }

    private void SetNotifications_Medium() {
        Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, 0);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), Channel_2_id)
                .setSmallIcon(R.drawable.medium_notify)
                .setContentTitle("Todo Reminder")
                .setContentText(msg)
                .setPriority(Priority1)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.todo_icon_med, "Open", contentIntent)
                .setColor(Color.argb(200, 155, 179, 0))
                .build();


        notificationManager.notify(key, notification);
    }

    private void SetNotifications_High() {

        Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, 0);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), Channel_1_id)
                .setSmallIcon(R.drawable.high_notify)
                .setContentTitle("Todo Reminder")
                .setContentText(msg)
                .setPriority(Priority1)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .addAction(R.drawable.todo_icon_high,"Open",contentIntent)
                .setAutoCancel(true)
                .setColor(Color.argb(200, 201, 21, 23))
                .build();

        notificationManager.notify(key, notification);
    }
}
