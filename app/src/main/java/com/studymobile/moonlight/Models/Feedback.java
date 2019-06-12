package com.studymobile.moonlight.Models;

public class Feedback
{
    private String userName, dishId, comment;
    private int rateMark;

    public Feedback() {}

    public Feedback(String i_UserName, String i_DishId, int i_RateMark, String i_Comment)
    {
        this.userName = i_UserName;
        this.dishId = i_DishId;
        this.rateMark = i_RateMark;
        this.comment = i_Comment;
    }

    public String getUserName()
    {
        return userName;
    }

    public int getRateMark()
    {
        return rateMark;
    }

    public String getComment()
    {
        return comment;
    }

    public String getDishId()
    {
        return dishId;
    }
}
