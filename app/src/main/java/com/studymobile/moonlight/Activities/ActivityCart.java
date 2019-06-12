package com.studymobile.moonlight.Activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.studymobile.moonlight.Models.Dish;
import com.studymobile.moonlight.R;

import java.util.Objects;

public class ActivityCart extends AppCompatActivity implements View.OnClickListener
{
    private RelativeLayout m_LayoutCart, m_LayoutTODO;
    private FirebaseDatabase m_Database;
    private TextView m_DishName, m_DishCalories;
    private ImageView m_DishImg;
    private String m_DishId;
    private Button m_BtnPay;

    @Override
    protected void onCreate(Bundle i_SavedInstanceState)
    {
        super.onCreate(i_SavedInstanceState);
        setContentView(R.layout.activity_cart);
        setContentViewFields();
        setFirebaseData();

        if(getIntent() != null)
        {
            m_DishId = getIntent().getStringExtra("DishId");
        }
        if(!m_DishId.isEmpty())
        {
            m_LayoutCart.setVisibility(View.VISIBLE);
            m_LayoutTODO.setVisibility(View.GONE);
            m_BtnPay.setVisibility(View.GONE);
            loadDishDetails(m_DishId);
            startBlinking();
        }else {
            m_LayoutCart.setVisibility(View.GONE);
            m_LayoutTODO.setVisibility(View.VISIBLE);
            m_BtnPay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View i_View)
    {
        int id = i_View.getId();

        if (id == R.id.link_home)
        {
            startActivity(new Intent(ActivityCart.this, ActivityHome.class));
        }
        else if (id == R.id.btn_pay)
        {
            Toast.makeText(ActivityCart.this,
                    "Will be implemented soon",Toast.LENGTH_SHORT).show();
        }
    }

    private void setFirebaseData()
    {
        m_Database = FirebaseDatabase.getInstance();
    }

    private void setContentViewFields()
    {
        m_LayoutCart = findViewById(R.id.layout_cart);
        m_LayoutTODO = findViewById(R.id.layout_todo);
        m_BtnPay = findViewById(R.id.btn_pay);
        m_BtnPay.setOnClickListener(this);
        findViewById(R.id.link_home).setOnClickListener(this);
        m_DishName = findViewById(R.id.txt_dish_to_buy);
        m_DishCalories = findViewById(R.id.txt_calories);
        m_DishImg = findViewById(R.id.img_dish_to_buy);
    }

    private void loadDishDetails(String i_DishId)
    {
        m_Database.getReference("Dishes").child(i_DishId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Dish dish = dataSnapshot.getValue(Dish.class);
                Picasso.get().load(Objects.requireNonNull(dish).getImageLink()).into(m_DishImg);
                m_DishName.setText(dish.getName());
                m_DishCalories.setText(String.format("Just %s kCal",dish.getCalories()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void startBlinking()
    {
        ObjectAnimator animator = ObjectAnimator.ofInt(m_DishCalories,
                "backgroundColor",
                Color.GREEN,
                Color.YELLOW,
                Color.GRAY);
        animator.setDuration(800);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setRepeatMode(Animation.ABSOLUTE);
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();
    }
}
