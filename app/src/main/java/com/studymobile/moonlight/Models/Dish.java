package com.studymobile.moonlight.Models;

public class Dish
{
    private String name, imageLink, discount, description, menuId, isSpicyStr, isVeggieStr, calories;
    private int price;
    private float rating;

    public Dish(){}

    public Dish(String i_Name, String i_ImageLink, String i_Discount, String i_Description, String i_MenuId,
                String i_IsSpicyStr, String i_IsVeggieStr, String i_Calories, int i_Price, float i_Rating)
    {
        this.name = i_Name;
        this.imageLink = i_ImageLink;
        this.discount = i_Discount;
        this.description = i_Description;
        this.menuId = i_MenuId;
        this.isSpicyStr = i_IsSpicyStr;
        this.isVeggieStr = i_IsVeggieStr;
        this.calories = i_Calories;
        this.price = i_Price;
        this.rating = i_Rating;
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

    public String getDiscount()
    {
        return discount;
    }

    public void setDiscount(String i_Discount)
    {
        this.discount = i_Discount;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String i_Description)
    {
        this.description = i_Description;
    }

    public String getMenuId()
    {
        return menuId;
    }

    public void setMenuId(String i_MenuId)
    {
        this.menuId = i_MenuId;
    }

    public String getIsSpicyStr()
    {
        return isSpicyStr;
    }

    public void setIsSpicyStr(String i_IsSpicyStr)
    {
        this.isSpicyStr = i_IsSpicyStr;
    }

    public String getIsVeggieStr()
    {
        return isVeggieStr;
    }

    public void setIsVeggieStr(String i_IsVeggieStr)
    {
        this.isVeggieStr = i_IsVeggieStr;
    }

    public String getCalories()
    {
        return calories;
    }

    public void setCalories(String i_Calories)
    {
        this.calories = i_Calories;
    }

    public int getPrice()
    {
        return price;
    }

    public void setPrice(int i_Price)
    {
        this.price = i_Price;
    }

    public float getRating()
    {
        return rating;
    }

    public void setRating(float i_Rating)
    {
        this.rating = i_Rating;
    }
}
