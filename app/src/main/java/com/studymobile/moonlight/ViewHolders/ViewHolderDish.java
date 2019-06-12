package com.studymobile.moonlight.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.studymobile.moonlight.Interfaces.ItemClickListener;
import com.studymobile.moonlight.R;

public class ViewHolderDish extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private ImageView m_DishImage;
    private TextView m_DishName, m_DishPrice;
    private ItemClickListener m_ItemClickListener;

    public ViewHolderDish(View i_ItemView)
    {
        super(i_ItemView);
        m_DishName = i_ItemView.findViewById(R.id.txt_dish_name);
        m_DishImage = i_ItemView.findViewById(R.id.img_dish);
        m_DishPrice = i_ItemView.findViewById(R.id.txt_dish_price);
        i_ItemView.setOnClickListener(this);
    }

    public ImageView GetDishImage()
    {
        return m_DishImage;
    }

    public void SetDishName(String i_DishName)
    {
        m_DishName.setText(i_DishName);
    }

    public void SetDishPrice(String i_DishPrice)
    {
        m_DishPrice.setText(i_DishPrice);
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