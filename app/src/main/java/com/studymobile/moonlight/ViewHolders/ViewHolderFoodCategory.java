package com.studymobile.moonlight.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.studymobile.moonlight.Interfaces.ItemClickListener;
import com.studymobile.moonlight.R;

public class ViewHolderFoodCategory extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private ImageView m_CategoryImage;
    private TextView m_CategoryName;
    private ItemClickListener m_ItemClickListener;

    public ViewHolderFoodCategory(View i_ItemView)
    {
        super(i_ItemView);
        m_CategoryName = i_ItemView.findViewById(R.id.txt_food_category);
        m_CategoryImage = i_ItemView.findViewById(R.id.img_food_category);
        i_ItemView.setOnClickListener(this);
    }

    public ImageView GetCategoryImage()
    {
        return m_CategoryImage;
    }

    public void SetCategoryName(String i_CategoryName)
    {
        m_CategoryName.setText(i_CategoryName);
    }

    public void SetItemClickListener(ItemClickListener i_ItemClickListener)
    {
        this.m_ItemClickListener = i_ItemClickListener;
    }

    @Override
    public void onClick(View v)
    {
        m_ItemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
