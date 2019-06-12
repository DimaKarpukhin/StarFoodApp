package com.studymobile.moonlight.ViewHolders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.studymobile.moonlight.R;

public class ViewHolderComment extends RecyclerView.ViewHolder
{
    private TextView m_UserName, m_Comment;
    private RatingBar m_RatingBar;

    public ViewHolderComment(@NonNull View i_ItemView)
    {
        super(i_ItemView);
        m_UserName = i_ItemView.findViewById(R.id.txt_name);
        m_Comment = i_ItemView.findViewById(R.id.txt_comment);
        m_RatingBar = i_ItemView.findViewById(R.id.ratingBar_small);
    }

    public void SetUserName(String i_UserName)
    {
        m_UserName.setText(i_UserName);
    }

    public void SetComment(String i_Comment)
    {
        m_Comment.setText(i_Comment);
    }

    public void SetRating(int i_Rating)
    {
        m_RatingBar.setRating(i_Rating);
    }
}
