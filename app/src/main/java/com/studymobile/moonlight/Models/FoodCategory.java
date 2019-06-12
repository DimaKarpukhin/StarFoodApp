package com.studymobile.moonlight.Models;

public class FoodCategory
{
    private String name, imageLink;

    public FoodCategory(){}

    public FoodCategory(String i_Name, String i_ImageLink)
    {
        this.name = i_Name;
        this.imageLink = i_ImageLink;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String i_Name)
    {
        this.name = i_Name;
    }

    public String getImageLink()
    {
        return imageLink;
    }

    public void setImageLink(String i_ImageLink)
    {
        this.imageLink = i_ImageLink;
    }
}
