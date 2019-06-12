package com.studymobile.moonlight.Common;

public class CommonUser
{
    private static String m_Name, m_Context;
    private static boolean m_IsAnonymous;

    public static String getName()
    {
        return m_Name;
    }

    public static void setName(String i_Name)
    {
        CommonUser.m_Name = i_Name;
    }

    public static String getContext()
    {
        return m_Context;
    }

    public static void setContext(String i_Context)
    {
        CommonUser.m_Context = i_Context;
    }

    public static boolean isAnonymous()
    {
        return m_IsAnonymous;
    }

    public static void setIsAnonymous(boolean i_IsAnonymous)
    {
        CommonUser.m_IsAnonymous = i_IsAnonymous;
    }
}
