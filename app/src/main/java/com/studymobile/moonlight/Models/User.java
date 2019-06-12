package com.studymobile.moonlight.Models;

public class User {

    private String context, name, email, phone;

    public User(){}

    public User(String context, String i_Name, String i_Email, String i_Phone)
    {
        this.context = context;
        this.name = i_Name;
        this.email = i_Email;
        this.phone = i_Phone;
    }

    public String getContext()
    {
        return context;
    }

    public void setContext(String context)
    {
        this.context = context;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String i_Name)
    {
        this.name = i_Name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String i_Email)
    {
        this.email = i_Email;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String i_Phone)
    {
        this.phone = i_Phone;
    }
}
