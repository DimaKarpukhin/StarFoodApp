package com.studymobile.moonlight.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.studymobile.moonlight.Analytics.AnalyticsManager;
import com.studymobile.moonlight.Common.CommonUser;
import com.studymobile.moonlight.Interfaces.ItemClickListener;
import com.studymobile.moonlight.Models.FoodCategory;
import com.studymobile.moonlight.R;
import com.studymobile.moonlight.ViewHolders.ViewHolderFoodCategory;

public class ActivityHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener
{
    private static final String CONTEXT = "#context#";
    private static final String ALL_CATEGORIES = "10";
    private static final String PRICE = "price";
    private static final String RATING = "rating";
    private static final String SPICY = "spicy";
    private static final String NOT_SPICY = "not spicy";
    private static final int ALL_POSITIONS = 999999999;
    private static final int SORTING_BY_PRICE = 999999987;
    private static final int SORTING_BY_RATING = 999999876;
    private static final int SPICY_DISHES = 999998765;
    private static final int NOT_SPICY_DISHES = 999987654;

    private FirebaseAuth m_Auth;
    private FirebaseDatabase m_Database;
    private RecyclerView m_RecyclerViewCategories;
    private FirebaseRecyclerAdapter<FoodCategory, ViewHolderFoodCategory> m_Adapter;
    private NavigationView m_NavigationView;

    private AnalyticsManager m_AnalyticsManager = AnalyticsManager.GetInstance();

    @Override
    protected void onCreate(Bundle i_SavedInstanceState)
    {
        super.onCreate(i_SavedInstanceState);
        setContentView(R.layout.activity_home);
        setFirebaseData();
        setContentViewFields();
        updateTitleOfNavigationItem();
        loadDishCategories();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(m_Adapter != null)
        {
            m_Adapter.startListening();
        }
    }

    @Override
    protected void onStop()
    {
        if(m_Adapter != null)
        {
            m_Adapter.stopListening();
        }

        super.onStop();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(m_Adapter != null)
        {
            m_Adapter.startListening();
        }
    }

    @Override
    public void onClick(View i_View)
    {
        int id = i_View.getId();

        if (id == R.id.btn_cart)
        {
            startCartActivity();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem i_Item)
    {
        int id = i_Item.getItemId();

        if (id == R.id.nav_all_dishes)
        {
            m_AnalyticsManager.TrackFoodCategoryChoiceEvent("ALL DISHES");
            startDishesListActivity(ALL_POSITIONS);
        }
        else if (id == R.id.nav_cart)
        {
            startCartActivity();
        }
        else if (id == R.id.nav_sign_out)
        {//nav_sign_out
            logOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu i_Menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    private void setFirebaseData()
    {
        m_Auth = FirebaseAuth.getInstance();
        m_Database = FirebaseDatabase.getInstance();
    }

    private void setContentViewFields()
    {
        //mIsAllPositions = false;
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);
        findViewById(R.id.btn_cart).setOnClickListener(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        m_NavigationView = findViewById(R.id.navigation_view);
        m_NavigationView.setNavigationItemSelectedListener(this);
        View headerView = m_NavigationView.getHeaderView(0);

        TextView userName = headerView.findViewById(R.id.text_current_user);
        userName.setText(CommonUser.getName());

        m_RecyclerViewCategories = findViewById(R.id.recycler_view_dish_categories);
        m_RecyclerViewCategories.setHasFixedSize(true);
        m_RecyclerViewCategories.setLayoutManager(new GridLayoutManager(this,2));
    }

    private void updateTitleOfNavigationItem()
    {
        Menu menu = m_NavigationView.getMenu();
        MenuItem signUpSignOut = menu.findItem(R.id.nav_sign_out);

        if (CommonUser.isAnonymous())
        {
            signUpSignOut.setTitle("Sign in");
        }else {
            signUpSignOut.setTitle("Sign out");
        }
    }

    private void loadDishCategories()
    {
        DatabaseReference databaseRef = m_Database.getReference().child("FoodCategory");
        FirebaseRecyclerOptions<FoodCategory> options = new FirebaseRecyclerOptions
                .Builder<FoodCategory>()
                .setQuery(databaseRef, FoodCategory.class)
                .build();

        m_Adapter = new FirebaseRecyclerAdapter<FoodCategory, ViewHolderFoodCategory>(options) {
            @NonNull
            @Override
            public ViewHolderFoodCategory onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                //Create a new instance of the ViewHolder and use R.layout.item_food_category for each item
                View view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.item_food_category, viewGroup, false);

                return new ViewHolderFoodCategory(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolderFoodCategory holder, int position,
                                            @NonNull final FoodCategory model)
            {
                holder.SetCategoryName(model.getName());
                Picasso.get().load(model.getImageLink()).into(holder.GetCategoryImage());
                holder.SetItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick)
                    {
                        m_AnalyticsManager.TrackFoodCategoryChoiceEvent(model.getName());
                        m_AnalyticsManager.SetUserProperty("Users_By_Food_Category", model.getName());
                        Toast.makeText(ActivityHome.this,""+ model.getName(),Toast.LENGTH_SHORT).show();
                        startDishesListActivity(position);
                    }
                });
            }
        };

        m_Adapter.startListening();
        m_RecyclerViewCategories.setAdapter(m_Adapter);
    }

    private void startCartActivity()
    {
        if(CommonUser.isAnonymous())
        {
            Toast.makeText(ActivityHome.this,
                    "Please sign in first", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent CartIntent = new Intent(ActivityHome.this, ActivityCart.class);
            CartIntent.putExtra("DishId", "");
            startActivity(CartIntent);
        }
    }

    private void logOut()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        m_Auth.signOut();
        GoogleSignIn.getClient(this, gso).signOut();
        LoginManager.getInstance().logOut();

        Toast.makeText(ActivityHome.this,
                "Please wait...", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ActivityHome.this, ActivityLogin.class));
    }

    private void startDishesListActivity(int i_Context)
    {
        Intent DishesListIntent = new Intent(ActivityHome.this, ActivityDishesList.class);
        if(i_Context == ALL_POSITIONS){
            Toast.makeText(ActivityHome.this,"ALL DISHES",Toast.LENGTH_SHORT).show();
            DishesListIntent.putExtra(CONTEXT, ALL_CATEGORIES);
        }
        else if(i_Context == SORTING_BY_PRICE) {
            Toast.makeText(ActivityHome.this,"Sorting dishes by price...",Toast.LENGTH_SHORT).show();
            DishesListIntent.putExtra(CONTEXT, PRICE);
        }
        else if(i_Context == SORTING_BY_RATING) {
            Toast.makeText(ActivityHome.this,"Sorting dishes by rating...",Toast.LENGTH_SHORT).show();
            DishesListIntent.putExtra(CONTEXT, RATING);
        }
        else if(i_Context == SPICY_DISHES) {
            Toast.makeText(ActivityHome.this,"Searching dishes...",Toast.LENGTH_SHORT).show();
            DishesListIntent.putExtra(CONTEXT, SPICY);
        }
        else if(i_Context == NOT_SPICY_DISHES) {
            Toast.makeText(ActivityHome.this,"Searching dishes...",Toast.LENGTH_SHORT).show();
            DishesListIntent.putExtra(CONTEXT, NOT_SPICY);
        }
        else {
            DishesListIntent.putExtra(CONTEXT, m_Adapter.getRef(i_Context).getKey());
        }

        startActivity(DishesListIntent);
    }
}
