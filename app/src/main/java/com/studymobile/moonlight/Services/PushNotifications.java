package com.studymobile.moonlight.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.studymobile.moonlight.Activities.ActivityCart;
import com.studymobile.moonlight.Activities.ActivityDishDetails;
import com.studymobile.moonlight.Activities.ActivityDishesList;
import com.studymobile.moonlight.Activities.ActivityHome;
import com.studymobile.moonlight.R;

import java.util.Map;
import java.util.Objects;

import static com.studymobile.moonlight.Services.NotificationChannels.CHAN_HOT_DEAL_ID;
import static com.studymobile.moonlight.Services.NotificationChannels.CHAN_NEW_PRODUCT_ID;
import static com.studymobile.moonlight.Services.NotificationChannels.CHAN_PROMOTION_ID;

public class PushNotifications extends FirebaseMessagingService
{
    private static final String CONTEXT = "#context#";
    public static final String ACTION = "action";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String SMALL_ICON = "small icon";

    public static final String PROMOTION_CAMPAING = "promotion";
    public static final String NEW_PRODUCT_CAMPAING = "new product";
    public static final String HOT_DEAL_CAMPAING = "hot deal";

    public static final String PROMOTION_CATEGORY_ID = "08";  //sushi category
    public static final String NEW_PRODUCT_ID = "044";        //tokyo pizza
    public static final String HOT_DEAL_ID = "003";           //black burger

    private int m_Color;
    private int m_Icon;
    private Uri m_SoundUri;
    private String m_Title;
    private String m_Body;
    private String m_ChannelId;
    private Map<String,String> m_Data;
    private RemoteMessage.Notification m_Notification;
    private NotificationManager m_NotificationManager;
    private NotificationCompat.Action m_Action;

    @Override
    public void onMessageReceived(RemoteMessage i_RemoteMessage)
    {
        m_Data = i_RemoteMessage.getData();
        m_Notification = i_RemoteMessage.getNotification();
        m_NotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        createNotification();
    }

    private void createNotification()
    {
        m_Icon = R.drawable.ic_notifications_black_24dp;
        m_SoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        m_Color = Color.BLUE;
        m_ChannelId = CHAN_PROMOTION_ID;
        Intent HomeIntent = new Intent(this, ActivityHome.class);
        PendingIntent actionIntent = PendingIntent.getActivity(this, 0 , HomeIntent, PendingIntent.FLAG_ONE_SHOT);
        m_Action = new NotificationCompat.Action(0, "Go to menu",actionIntent);

        createTitle();
        createBody();
        createAction();
        build();
    }

    private void createTitle()
    {
        if (m_Notification != null)
        {
            m_Title = m_Notification.getTitle();
        }
        if (m_Data.size() > 0)
        {
            if (m_Data.get(TITLE) != null)
            {
                m_Title = m_Data.get(TITLE);
            }
        }
    }

    private void createBody()
    {
        if (m_Notification != null)
        {
            m_Body = m_Notification.getBody();
        }
        if (m_Data.size() > 0)
        {
            if (m_Data.get(BODY) != null)
            {
                m_Body = m_Data.get(BODY);
            }
        }
    }

    private void createAction()
    {
        if(m_Data.size() > 0)
        {
            String actionType = m_Data.get(ACTION);

            if (!Objects.requireNonNull(actionType).isEmpty())
            {
                if (actionType.contains(PROMOTION_CAMPAING))
                {
                    m_Icon = R.drawable.ic_flag_black_24dp;
                    m_Color = Color.GREEN;
                    m_ChannelId = CHAN_PROMOTION_ID;
                    Intent DishesListIntent = new Intent(this, ActivityDishesList.class);
                    DishesListIntent.putExtra(CONTEXT, PROMOTION_CATEGORY_ID);
                    PendingIntent actionIntent = PendingIntent.getActivity(this, 0 , DishesListIntent, PendingIntent.FLAG_ONE_SHOT);
                    m_Action = new NotificationCompat.Action(0, "Make order",actionIntent);
                }
                else if (actionType.contains(NEW_PRODUCT_CAMPAING))
                {
                    m_Icon = R.drawable.ic_fiber_new_black_24dp;
                    m_Color = Color.MAGENTA;
                    m_ChannelId = CHAN_NEW_PRODUCT_ID;
                    Intent DishDetailsIntent = new Intent(this, ActivityDishDetails.class);
                    DishDetailsIntent.putExtra("DishId", NEW_PRODUCT_ID);
                    PendingIntent actionIntent = PendingIntent.getActivity(this, 0 , DishDetailsIntent, PendingIntent.FLAG_ONE_SHOT);
                    m_Action = new NotificationCompat.Action(0, "View details",actionIntent);
                }
                else if (actionType.contains(HOT_DEAL_CAMPAING))
                {
                    m_Icon = R.drawable.ic_flash_on_black_24dp;
                    m_Color = Color.RED;
                    m_ChannelId = CHAN_HOT_DEAL_ID;
                    Intent CartIntent = new Intent(this, ActivityCart.class);
                    CartIntent.putExtra("DishId", HOT_DEAL_ID);
                    PendingIntent actionIntent = PendingIntent.getActivity(this, 0 , CartIntent, PendingIntent.FLAG_ONE_SHOT);
                    m_Action = new NotificationCompat.Action(0, "Get it now",actionIntent);
                }
            }
        }
    }

    private void build()
    {
        Bitmap largIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_short_yellow);
        Notification notification =
                new NotificationCompat.Builder(this, m_ChannelId)
                        .setLargeIcon(largIcon)
                        .setContentTitle(m_Title)
                        .setContentText(m_Body)
                        .setColor(m_Color)
                        .setSmallIcon(m_Icon)
                        .setSound(m_SoundUri)
                        // .setContentIntent(actionIntent)
                        .setAutoCancel(true)
                        .addAction(m_Action)
                        .build();
        m_NotificationManager.notify(1 , notification);
    }
}
