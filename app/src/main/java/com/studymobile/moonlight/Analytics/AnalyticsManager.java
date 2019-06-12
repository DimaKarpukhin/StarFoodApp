package com.studymobile.moonlight.Analytics;

import android.content.Context;
import android.os.Bundle;

import com.appsee.Appsee;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.studymobile.moonlight.Models.Dish;

import java.util.HashMap;
import java.util.Map;

public class AnalyticsManager
{
    private static final String MIXPANEL_TOKEN = "f3870ce1d0c15fc5caf9d7b70e859fa3";
    private static AnalyticsManager m_Instance = null;
    private FirebaseAnalytics m_FirebaseAnalytics;
    private MixpanelAPI m_Mixpanel;


    private AnalyticsManager() { }

    public static AnalyticsManager GetInstance()
    {
        if (m_Instance == null)
        {
            m_Instance = new AnalyticsManager();
        }

        return (m_Instance);
    }

    public void Init(Context i_Context)
    {
        //FireBase
        m_FirebaseAnalytics = FirebaseAnalytics.getInstance(i_Context);

        //AppSee
        Appsee.start();

        //MixPanel
        m_Mixpanel = MixpanelAPI.getInstance(i_Context, MIXPANEL_TOKEN);
    }

    public void TrackSearchEvent(String i_SearchString)
    {
        //FireBase
        String eventName = "Dish_Searching";
        Bundle bundle = new Bundle();
        bundle.putString("Dish_Searching", i_SearchString);
        m_FirebaseAnalytics.logEvent(eventName,bundle);

        //AppSee
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("dish", i_SearchString);
        Appsee.addEvent(eventName,props);

        //MixPanel
        m_Mixpanel.trackMap(eventName,props);
    }

    public void TrackSignUpEvent(String i_SignUpMethod)
    {
        //FireBase
        String eventName = "Sign_Up";
        Bundle bundle = new Bundle();
        bundle.putString("signUp_method", i_SignUpMethod);
        m_FirebaseAnalytics.logEvent(eventName,bundle);

        //AppSee
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("signUp_method", i_SignUpMethod);
        Appsee.addEvent(eventName,props);

        //MixPanel
        m_Mixpanel.trackMap(eventName,props);
    }

    public void TrackLoginEvent(String i_LoginMethod)
    {
        //FireBase
        String eventName = "Login";
        Bundle bundle = new Bundle();
        bundle.putString("login_method", i_LoginMethod);
        m_FirebaseAnalytics.logEvent(eventName,bundle);

        //AppSee
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("login_method", i_LoginMethod);
        Appsee.addEvent(eventName,props);

        //MixPanel
        m_Mixpanel.trackMap(eventName,props);
    }

    public void TrackFoodCategoryChoiceEvent(String i_CategoryName)
    {
        //FireBase
        String eventName = "Food_Category_Choice";
        Bundle bundle = new Bundle();
        bundle.putString("category_name", i_CategoryName);
        m_FirebaseAnalytics.logEvent(eventName,bundle);

        //AppSee
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("category_name", i_CategoryName);
        Appsee.addEvent(eventName,props);

        //MixPanel
        m_Mixpanel.trackMap(eventName,props);
    }

    public void TrackVeggieFilteringEvent()
    {
        //FireBase
        String eventName = "Veggie_Dishes";
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "nav_menu_veggie_link");
        m_FirebaseAnalytics.logEvent(eventName,bundle);

        //AppSee
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("item_id", "nav_menu_veggie_link");
        Appsee.addEvent(eventName,props);

        //MixPanel
        m_Mixpanel.trackMap(eventName,props);
    }

    public void TrackSpicyFilteringEvent()
    {
        //FireBase
        String eventName = "Spicy_Dishes";
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "nav_menu_spicy_link");
        m_FirebaseAnalytics.logEvent(eventName,bundle);

        //AppSee
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("item_id", "nav_menu_spicy_link");
        Appsee.addEvent(eventName,props);

        //MixPanel
        m_Mixpanel.trackMap(eventName,props);
    }

    public void TrackNotSpicyFilteringEvent()
    {
        //FireBase
        String eventName = "Not_Spicy_Dishes";
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "nav_menu_not_spicy_link" );
        m_FirebaseAnalytics.logEvent(eventName,bundle);

        //AppSee
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("item_id", "nav_menu_not_spicy_link");
        Appsee.addEvent(eventName,props);

        //MixPanel
        m_Mixpanel.trackMap(eventName,props);
    }

    public void TrackDishRatingEvent(Dish i_Dish)
    {
        //FireBase
        String eventName = "Dish_Rating";
        Bundle bundle = buildDishBundle(i_Dish);
        m_FirebaseAnalytics.logEvent(eventName,bundle);

        //AppSee
        Map<String, Object> props =  buildDishProps(i_Dish);
        Appsee.addEvent(eventName,props);

        //MixPanel
        m_Mixpanel.trackMap(eventName,props);
    }

    public void TrackPurchaseEvent(Dish i_Dish)
    {
        //FireBase
        String eventName = "Purchase";
        Bundle bundle = buildDishBundle(i_Dish);
        m_FirebaseAnalytics.logEvent(eventName,bundle);

        //AppSee
        Map<String, Object> props =  buildDishProps(i_Dish);
        Appsee.addEvent(eventName,props);

        //MixPanel
        m_Mixpanel.trackMap(eventName,props);
    }

    public void TrackDishChoiceEvent(Dish i_Dish)
    {
        //FireBase
        String eventName = "Dish_Choice";
        Bundle bundle = buildDishBundle(i_Dish);
        m_FirebaseAnalytics.logEvent(eventName,bundle);

        //AppSee
        Map<String, Object> props =  buildDishProps(i_Dish);
        Appsee.addEvent(eventName,props);

        //MixPanel
        m_Mixpanel.trackMap(eventName,props);
    }

    public void SetUserID(String i_Id, boolean i_NewUser)
    {
        //FireBase
        m_FirebaseAnalytics.setUserId(i_Id);

        //AppSee
        Appsee.setUserId(i_Id);

        //MixPanel
        if (i_NewUser) {
            m_Mixpanel.alias(i_Id, null);
        }
        m_Mixpanel.identify(i_Id);
        m_Mixpanel.getPeople().identify(m_Mixpanel.getDistinctId());
    }

    public void SetUserProperty(String i_Name , String i_Value)
    {
        //FireBase
        m_FirebaseAnalytics.setUserProperty(i_Name,i_Value);

        //MixPanel
        m_Mixpanel.getPeople().set(i_Name,i_Value);
    }

    private Bundle buildDishBundle(Dish i_Dish)
    {
        Bundle bundle = new Bundle();
        bundle.putString("dish_name", i_Dish.getName());
        bundle.putString("dish_calories", i_Dish.getCalories());
        bundle.putString("dish_discount",i_Dish.getDiscount());
        bundle.putInt("dish_price",i_Dish.getPrice());
        float rating = i_Dish.getRating();
        if(rating != 0){rating = rating*-1;}
        bundle.putFloat("dish_rating",rating);

        return bundle;
    }

    private Map<String,Object> buildDishProps(Dish i_Dish)
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("dish_name", i_Dish.getName());
        props.put("dish_calories", String.valueOf(i_Dish.getCalories()));
        props.put("dish_discount",String.valueOf(i_Dish.getDiscount()));
        props.put("dish_price",String.valueOf(i_Dish.getPrice()));
        float rating = i_Dish.getRating();
        if(rating != 0){rating = rating*-1;}
        props.put("dish_rating",String.valueOf(rating));

        return props;
    }
}
