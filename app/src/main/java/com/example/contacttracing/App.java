package com.example.contacttracing;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class App extends Application {
    public static final String CHANNEL_ID = "ContactTracingChannel";
    public static final String EXPOSURE_ID = "EXPOSURE";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Contact Tracing Service",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationChannel exposureChannel = new NotificationChannel(
                EXPOSURE_ID,
                "Exposure notification",
                NotificationManager.IMPORTANCE_HIGH
        );
        exposureChannel.setDescription("Exposure Notification");


        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(serviceChannel);
        notificationManager.createNotificationChannel(exposureChannel);
    }
}
