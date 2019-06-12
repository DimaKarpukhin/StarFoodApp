package com.studymobile.moonlight.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.studymobile.moonlight.Models.Feedback;
import com.studymobile.moonlight.R;
import com.studymobile.moonlight.ViewHolders.ViewHolderComment;

public class ActivityComments extends AppCompatActivity
{
    private RecyclerView m_RecyclerView;
    private RecyclerView.LayoutManager m_LayoutManager;
    private FirebaseDatabase m_Database;
    private SwipeRefreshLayout m_SwipeRefreshLayout;
    private FirebaseRecyclerAdapter<Feedback,ViewHolderComment> m_Adapter;
    private String m_DishId;

    @Override
    protected void onCreate(Bundle i_SavedInstanceState)
    {
        super.onCreate(i_SavedInstanceState);
        setContentView(R.layout.activity_comments);

        setFirebaseData();
        setContentViewFields();
        setSwipeLayout();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(m_Adapter != null)
        {
            m_Adapter.startListening();
        }
    }

    @Override
    protected void attachBaseContext(Context i_NewBase)
    {
        super.attachBaseContext(i_NewBase);
    }

    private void setFirebaseData()
    {
        m_Database = FirebaseDatabase.getInstance();
    }

    private void setContentViewFields()
    {
        m_RecyclerView = findViewById(R.id.recycler_comments);
        m_LayoutManager = new LinearLayoutManager(this);
        m_RecyclerView.setLayoutManager(m_LayoutManager);
    }

    private void setSwipeLayout()
    {
        m_SwipeRefreshLayout = findViewById(R.id.layout_swipe);
        m_SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getIntent() != null)
                {
                    m_DishId = getIntent().getStringExtra("DishId");
                }
                if(!m_DishId.isEmpty())
                {
                    loadComments();
                }else {
                    Toast.makeText(ActivityComments.this,
                            "ERROR: There is no instance in database",Toast.LENGTH_SHORT).show();
                }
            }
        });

        m_SwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                m_SwipeRefreshLayout.setRefreshing(true);
                if(getIntent() != null)
                {
                    m_DishId = getIntent().getStringExtra("DishId");
                }
                if(!m_DishId.isEmpty())
                {
                    loadComments();
                }else {
                    Toast.makeText(ActivityComments.this,
                            "ERROR: There is no instance in database",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadComments()
    {
        Query query = m_Database.getReference("Feedback").orderByChild("dishId").equalTo(m_DishId);
        FirebaseRecyclerOptions<Feedback> options = new FirebaseRecyclerOptions.Builder<Feedback>()
                .setQuery(query,Feedback.class)
                .build();

        m_Adapter = new FirebaseRecyclerAdapter<Feedback, ViewHolderComment>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderComment holder, int position,
                                            @NonNull Feedback model) {
                holder.SetUserName(model.getUserName());
                holder.SetRating(model.getRateMark());
                holder.SetComment(model.getComment());
            }

            @NonNull
            @Override
            public ViewHolderComment onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.item_comment,viewGroup,false);
                return new ViewHolderComment(view);
            }
        };

        m_Adapter.startListening();
        m_RecyclerView.setAdapter(m_Adapter);
        m_SwipeRefreshLayout.setRefreshing(false);
    }
}
