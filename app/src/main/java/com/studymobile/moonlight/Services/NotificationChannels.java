package com.studymobile.moonlight.Services;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationChannels extends Application
{
    public static final String CHAN_PROMOTION_ID = "channel1";
    public static final String CHAN_NEW_PRODUCT_ID = "channel2";
    public static final String CHAN_HOT_DEAL_ID = "channel3";

    public static final String CHAN_PROMOTION_NAME = "Promotions";
    public static final String CHAN_NEW_PRODUCT_NAME = "New products";
    public static final String CHAN_HOT_DEAL_NAME = "Hot deals";

    @Override
    public void onCreate()
    {
        super.onCreate();
        createNotificationChannel(CHAN_PROMOTION_ID, CHAN_PROMOTION_NAME);
        createNotificationChannel(CHAN_NEW_PRODUCT_ID, CHAN_NEW_PRODUCT_NAME);
        createNotificationChannel(CHAN_HOT_DEAL_ID, CHAN_HOT_DEAL_NAME);
    }

    private void createNotificationChannel(String i_ChannelId, String i_ChannelName)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(
                    i_ChannelId,
                    i_ChannelName,
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
