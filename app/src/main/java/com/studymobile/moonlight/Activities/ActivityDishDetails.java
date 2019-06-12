package com.studymobile.moonlight.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;
import com.studymobile.moonlight.Analytics.AnalyticsManager;
import com.studymobile.moonlight.Common.CommonUser;
import com.studymobile.moonlight.Models.Dish;
import com.studymobile.moonlight.Models.Feedback;
import com.studymobile.moonlight.R;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class ActivityDishDetails extends AppCompatActivity implements RatingDialogListener, View.OnClickListener
{
    private ImageView m_DishImg;
    private TextView m_DishPrice,m_DishDescription;
    private CollapsingToolbarLayout m_CollapsingToolbarLayout;
    private RatingBar m_RatingBar;
    private String m_DishId;
    private float m_Rating;
    private Dish m_CurrentDish;

    private FirebaseDatabase m_Database;
    private DatabaseReference m_FeedbackRef;
    private FirebaseAuth m_Auth;
    private AnalyticsManager m_AnalyticsManager = AnalyticsManager.GetInstance();

    @Override
    protected void onCreate(Bundle i_SavedInstanceState)
    {
        super.onCreate(i_SavedInstanceState);
        setContentView(R.layout.activity_dish_details);
        setFirebaseData();
        setContentViewFields();

        if(getIntent() != null)
        {
            m_DishId = getIntent().getStringExtra("DishId");
        }
        if(!m_DishId.isEmpty())
        {
            loadDishDetails(m_DishId);
            setDishRating(m_DishId);
        }else {
            Toast.makeText(ActivityDishDetails.this,
                    "ERROR: There is no instance in database",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View i_View)
    {
        int id = i_View.getId();

        if (id == R.id.btn_add_to_cart)
        {
            startCartActivity();
        }
        else if (id == R.id.btn_rating)
        {
            if(CommonUser.isAnonymous())
            {
                Toast.makeText(ActivityDishDetails.this,
                        "Please sign in first", Toast.LENGTH_SHORT).show();
            }else {
                showRatingDialog();
            }
        }
        else if (id == R.id.btn_show_comments)
        {
            m_AnalyticsManager.SetUserProperty("Feedback_Readers",CommonUser.getContext());
            startCommentsActivity();
        }
    }

    @Override
    public void onPositiveButtonClicked(int i_RateMark, @NotNull String i_Comments)
    {   //Get rating and upload to firebase
        String userName, userEmail, displayedName, userId;
        FirebaseUser currentUser = m_Auth.getCurrentUser();

        if(currentUser != null)
        {
            m_AnalyticsManager.TrackDishRatingEvent(m_CurrentDish);
            userId = currentUser.getUid();
            userName = currentUser.getDisplayName();
            userEmail = currentUser.getEmail();

            if(userName != null)
            {
                displayedName = userName;
            }else{
                displayedName = userEmail;
            }

            final Feedback feedback = new Feedback(displayedName,
                    m_DishId,
                    i_RateMark,
                    i_Comments);
            m_FeedbackRef.push().setValue(feedback).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(ActivityDishDetails.this,
                            "Thank you for your feedback", Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Toast.makeText(ActivityDishDetails.this,
                    "ERROR: There is no instance in database", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNegativeButtonClicked() { }

    private void setFirebaseData()
    {
        m_Database = FirebaseDatabase.getInstance();
        m_FeedbackRef = m_Database.getReference("Feedback");
        m_Auth = FirebaseAuth.getInstance();
    }

    private void setContentViewFields()
    {
        findViewById(R.id.btn_show_comments) .setOnClickListener(this);
        findViewById(R.id.btn_rating).setOnClickListener(this);
        findViewById(R.id.btn_add_to_cart).setOnClickListener(this);

        m_RatingBar = findViewById(R.id.rating_bar_big);
        m_DishDescription = findViewById(R.id.txt_dish_description);
        m_DishPrice = findViewById(R.id.txt_dish_details_price);
        m_DishImg = findViewById(R.id.img_dish_details);
        m_CollapsingToolbarLayout = findViewById(R.id.layout_collapsing);
        m_CollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        m_CollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
   }

    private void loadDishDetails(String i_DishId)
    {
        m_Database.getReference("Dishes").child(i_DishId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                m_CurrentDish = dataSnapshot.getValue(Dish.class);
                Picasso.get().load(m_CurrentDish.getImageLink()).into(m_DishImg);
                m_CollapsingToolbarLayout.setTitle(m_CurrentDish.getName());
                m_DishPrice.setText(String.format("%s", m_CurrentDish.getPrice()));
                //mDishName.setText(mCurrentDish.getName());
                m_DishDescription.setText(m_CurrentDish.getDescription());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void setDishRating(String i_DishId)
    {
        Query dishRating = m_FeedbackRef.orderByChild("dishId").equalTo(i_DishId);
        dishRating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                int count = 0;
                float sum = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Feedback item = snapshot.getValue(Feedback.class);
                    sum += Objects.requireNonNull(item).getRateMark();
                    count++;
                }
                if (count != 0)
                {
                    m_Rating = (sum / count);
                    m_RatingBar.setRating(m_Rating);
                }

                m_Database.getReference("Dishes").child(m_DishId).child("rating").setValue(m_Rating * -1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(ActivityDishDetails.this, ">>>>", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRatingDialog()
    {
        if(CommonUser.isAnonymous())
        {
            Toast.makeText(ActivityDishDetails.this,
                    "Please sign up first", Toast.LENGTH_SHORT).show();
        }
        else {
            new AppRatingDialog.Builder()
                    .setNegativeButtonText("Cancel")
                    .setPositiveButtonText("Submit")
                    .setNoteDescriptions(Arrays.asList("BAD", "NOT GOOD", "NOT BAD", "GOOD", "DELICIOUS"))
                    .setDefaultRating(1)
                    .setTitle("LEAVE YOUR FEEDBACK")
                    .setTitleTextColor(R.color.colorPrimaryDark)
                    .setDescription("Please rate this dish")
                    .setDescriptionTextColor(R.color.ocean)
                    .setHint("Write your comment here...")
                    .setHintTextColor(R.color.colorPrimaryDark)
                    .setCommentBackgroundColor(R.color.oceanDark)
                    .setCommentTextColor(R.color.colorAccent)
                    .setWindowAnimation(R.style.RatingDialogFadeAnim)
                    .create(ActivityDishDetails.this).show();
        }
    }

    private void startCartActivity()
    {
        if(CommonUser.isAnonymous())
        {
            Toast.makeText(ActivityDishDetails.this,
                    "Please sign in first", Toast.LENGTH_SHORT).show();
        }
        else{
            m_AnalyticsManager.TrackPurchaseEvent(m_CurrentDish);
            m_AnalyticsManager.SetUserProperty("Purchasers",CommonUser.getContext());
            Intent CartIntent = new Intent(ActivityDishDetails.this, ActivityCart.class);
            CartIntent.putExtra("DishId", m_DishId);
            startActivity(CartIntent);
        }
    }

    private void startCommentsActivity()
    {
        Intent CommentsIntent = new Intent(ActivityDishDetails.this, ActivityComments.class);
        CommentsIntent.putExtra("DishId", m_DishId);
        startActivity(CommentsIntent);
    }
}
