package com.taimoor.TodoNotifier.Utilities;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

//Application class that wraps the whole application
//Using to create notification channels as soon as the app is installed
public class App extends Application {


    public static final String Channel_1_id = "channel1";
    public static final String Channel_2_id = "channel2";
    public static final String Channel_3_id = "channel3";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationsChannel();
    }

    private void createNotificationsChannel() {
        //Creating three different Notification channels for different priorities
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            NotificationChannel channelA = new NotificationChannel(Channel_1_id,"Channel 1",NotificationManager.IMPORTANCE_HIGH);
            channelA.setDescription("High Priority Notifications");

            NotificationChannel channelB = new NotificationChannel(Channel_2_id,"Channel 2",NotificationManager.IMPORTANCE_DEFAULT);
            channelB.setDescription("Medium Priority Notifications");

            NotificationChannel channelC = new NotificationChannel(Channel_3_id,"Channel 3",NotificationManager.IMPORTANCE_LOW);
            channelC.setDescription("Low Priority Notifications");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channelA);
            notificationManager.createNotificationChannel(channelB);
            notificationManager.createNotificationChannel(channelC);
        }
    }
}
