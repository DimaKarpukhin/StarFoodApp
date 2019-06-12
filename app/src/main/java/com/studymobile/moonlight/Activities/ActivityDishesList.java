package com.studymobile.moonlight.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.studymobile.moonlight.Analytics.AnalyticsManager;
import com.studymobile.moonlight.Interfaces.ItemClickListener;
import com.studymobile.moonlight.Models.Dish;
import com.studymobile.moonlight.R;
import com.studymobile.moonlight.ViewHolders.ViewHolderDish;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActivityDishesList extends AppCompatActivity implements
        TextWatcher, MaterialSearchBar.OnSearchActionListener,
        NavigationView.OnNavigationItemSelectedListener

{
    private static final String CONTEXT = "#context#";
    private static final String ALL_CATEGORIES = "10";
    private static final String PRICE = "price";
    private static final String RATING = "rating";
    private static final String SPICY = "spicy";
    private static final String NOT_SPICY = "not spicy";
    private static final String VEGGIE = "veggie";
    private static final String DISH_NAME = "name";

    private RecyclerView m_RecyclerView;
    private FirebaseDatabase m_Database;
    private DatabaseReference m_DishesRef;
    private FirebaseRecyclerAdapter<Dish, ViewHolderDish> m_Adapter;

    private String m_Context, m_SavedCategory;
    private List<String> m_SuggestionsList;
    private MaterialSearchBar m_SearchBar;
    private FirebaseRecyclerOptions<Dish> m_Options;
    private NavigationView m_NavigationView;

    private DrawerLayout m_DrawerLayout;

    private AnalyticsManager m_AnalyticsManager = AnalyticsManager.GetInstance();

    @Override
    protected void onCreate(Bundle i_SavedInstanceState)
    {
        super.onCreate(i_SavedInstanceState);
        setContentView(R.layout.activity_dishes_list);
        setFirebaseData();
        setContentViewFields();

        if(getIntent() != null)
        {
            m_Context = getIntent().getStringExtra(CONTEXT);
        }
        if(!m_Context.isEmpty())
        {
            setSearchSuggestions();
            m_Options = buildAdapterOptionsByContext(m_Context,null);
            loadListOfDishes(m_Options);
        }else {
            Toast.makeText(ActivityDishesList.this,
                    "ERROR: There is no instance in database",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(m_Adapter != null)
        {
            m_Adapter.startListening();
        }
        if(m_Adapter != null)
        {
            m_Adapter.startListening();//???????
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
    public void onButtonClicked(int i_Button) {
        switch (i_Button)
        {
            case MaterialSearchBar.BUTTON_NAVIGATION:
                m_DrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case MaterialSearchBar.BUTTON_BACK:
                //mSearchBar.hideSuggestionsList();
                m_SearchBar.disableSearch();
                break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem i_MenuItem)
    {
        int id = i_MenuItem.getItemId();
        if (id == R.id.nav_home)
        {
            startActivity(new Intent(ActivityDishesList.this, ActivityHome.class));
        }else {
            if (id == R.id.nav_by_price) {
                m_Options = buildAdapterOptionsByContext(PRICE, null);

            }
            else if (id == R.id.nav_by_rating)
            {
                m_Options = buildAdapterOptionsByContext(RATING, null);

            }
            else if (id == R.id.nav_veggie)
            {
                m_AnalyticsManager.TrackVeggieFilteringEvent();
                m_Options = buildAdapterOptionsByContext(VEGGIE, null);

            }
            else if (id == R.id.nav_spicy)
            {
                m_AnalyticsManager.TrackSpicyFilteringEvent();
                m_Options = buildAdapterOptionsByContext(SPICY, null);

            }
            else if (id == R.id.nav_not_spicy)
            {
                m_AnalyticsManager.TrackNotSpicyFilteringEvent();
                m_Options = buildAdapterOptionsByContext(NOT_SPICY, null);
            }

            loadListOfDishes(m_Options);
        }

        m_DrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onSearchStateChanged(boolean i_IsEnabled)
    {
        m_SearchBar.hideSuggestionsList();
    }

    @Override
    public void onSearchConfirmed(CharSequence i_Text)
    {
        if (i_Text.toString().isEmpty())
        {
            Toast.makeText(ActivityDishesList.this,
                    "Please enter your choice", Toast.LENGTH_SHORT).show();
        }
        else if (!m_SuggestionsList.contains(i_Text.toString())) {
            Toast.makeText(ActivityDishesList.this,
                    "No dishes found, try another category", Toast.LENGTH_SHORT).show();
        }
        else {
            if (m_Context.equals(DISH_NAME))
            {
                m_Context = m_SavedCategory;
                m_Options = buildAdapterOptionsByContext(m_Context, null);
            }
            else {
                m_SavedCategory = m_Context;
                m_Context = DISH_NAME;
                m_Options = buildAdapterOptionsByContext(m_Context, i_Text.toString());
            }

            m_AnalyticsManager.TrackSearchEvent(i_Text.toString());
            loadListOfDishes(m_Options);
        }
    }

    @Override
    public void onTextChanged(CharSequence i_Text, int i_Start, int i_Before, int i_Count)
    {
        if(m_SearchBar.isSearchEnabled())
        {
            if (m_SuggestionsList.isEmpty())
            {
                setSearchSuggestions();
            }

            List<String> suggestions = new ArrayList<>();
            for (String searchOption : m_SuggestionsList)
            {
                if (!m_SearchBar.getText().isEmpty() && !m_SearchBar.getText().startsWith(" ") &&
                        searchOption.toLowerCase().contains(m_SearchBar.getText().toLowerCase()))
                {
                    suggestions.add(searchOption);
                } else {
                    m_SearchBar.hideSuggestionsList();
                }
            }

            m_SearchBar.setLastSuggestions(suggestions);
            if (!m_SearchBar.getText().isEmpty())
            {
                m_SearchBar.showSuggestionsList();
            }
        } else {
            m_SearchBar.hideSuggestionsList();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence i_Text, int i_Start, int i_Count, int i_After) { }

    @Override
    public void afterTextChanged(Editable i_Text) { }

    private void setFirebaseData()
    {
        m_Database = FirebaseDatabase.getInstance();
        m_DishesRef = m_Database.getReference("Dishes");
    }

    private void setContentViewFields()
    {
        m_SuggestionsList = new ArrayList<>();
        m_SearchBar = findViewById(R.id.search_bar);

        m_SearchBar.setCardViewElevation(10);
        m_SearchBar.setMaxSuggestionCount(5);
        m_SearchBar.addTextChangeListener(this);
        m_SearchBar.setOnSearchActionListener(this);

        m_RecyclerView = findViewById(R.id.recycler_view_dishes);
        m_RecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        m_RecyclerView.setLayoutManager(layoutManager);

        m_DrawerLayout = findViewById(R.id.drawer_layout_dishes_list);
        m_NavigationView = findViewById(R.id.nav_view_search);
        m_NavigationView.setNavigationItemSelectedListener(this);
    }

    private void setSearchSuggestions()
    {
        if(m_Context.equals(ALL_CATEGORIES))
        {
            setSuggestionsForAll();
        }else {
            setSuggestionsByContext();
        }
    }

    private void setSuggestionsForAll()
    {
        m_DishesRef.orderByChild("menuId")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Dish item = snapshot.getValue(Dish.class);
                            m_SuggestionsList.add(Objects.requireNonNull(item).getName());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

        m_SearchBar.setLastSuggestions(m_SuggestionsList);
    }

    private void setSuggestionsByContext()
    {
        m_DishesRef.orderByChild("menuId").equalTo(m_Context)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            Dish item = snapshot.getValue(Dish.class);
                            m_SuggestionsList.add(Objects.requireNonNull(item).getName());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

        m_SearchBar.setLastSuggestions(m_SuggestionsList);
    }

    private FirebaseRecyclerOptions<Dish> buildAdapterOptionsByContext(String i_Context, String i_Key)//String context)
    {
        FirebaseRecyclerOptions<Dish> options;

        if(i_Context.equals(ALL_CATEGORIES)) {
            options = new FirebaseRecyclerOptions
                    .Builder<Dish>()
                    .setQuery(m_DishesRef, Dish.class)
                    .build();
        }
        else if(i_Context.equals(PRICE) || i_Context.equals(RATING)) {
            options = new FirebaseRecyclerOptions
                    .Builder<Dish>()
                    .setQuery(m_DishesRef.orderByChild(i_Context), Dish.class)
                    .build();
        }
        else if(i_Context.equals(SPICY) || i_Context.equals(NOT_SPICY)) {
            options = new FirebaseRecyclerOptions
                    .Builder<Dish>()
                    .setQuery(m_DishesRef.orderByChild("isSpicy").equalTo(i_Context), Dish.class)
                    .build();
        }
        else if(i_Context.equals(VEGGIE)) {
            options = new FirebaseRecyclerOptions
                    .Builder<Dish>()
                    .setQuery(m_DishesRef.orderByChild("isVeggie").equalTo(i_Context), Dish.class)
                    .build();
        }
        else if(i_Context.equals(DISH_NAME)) {
            options = new FirebaseRecyclerOptions
                    .Builder<Dish>()
                    .setQuery(m_DishesRef.orderByChild(i_Context).equalTo(i_Key), Dish.class)
                    .build();
        }
        else {//build options by menuId of the dishes
            options = new FirebaseRecyclerOptions
                    .Builder<Dish>()
                    .setQuery(m_DishesRef.orderByChild("menuId").equalTo(i_Context), Dish.class)
                    .build();
        }

        return options;
    }

    private void loadListOfDishes(FirebaseRecyclerOptions<Dish> i_Options)//String foodCategoryId)
    {
        m_Adapter = new FirebaseRecyclerAdapter<Dish, ViewHolderDish>(i_Options) {
            @NonNull
            @Override
            public ViewHolderDish onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                //Create a new instance of the ViewHolder and use R.layout.item_dish for each item
                View view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.item_dish, viewGroup, false);

                return new ViewHolderDish(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolderDish holder, int position,
                                            @NonNull final Dish model)
            {
                holder.SetDishName(model.getName());
                holder.SetDishPrice(String.format("$ %s",model.getPrice()));
                Picasso.get().load(model.getImageLink()).into(holder.GetDishImage());
                holder.SetItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        m_AnalyticsManager.TrackDishChoiceEvent(model);
                        Toast.makeText(ActivityDishesList.this, "" + model.getName(), Toast.LENGTH_SHORT).show();
                        startDisheDetailsActivity(position);
                    }
                });
            }
        };

        m_Adapter.startListening();
        m_RecyclerView.setAdapter(m_Adapter);
    }

    private void startDisheDetailsActivity(int i_Position)
    {
        Intent DishDetailsIntent = new Intent(ActivityDishesList.this, ActivityDishDetails.class);
        DishDetailsIntent.putExtra("DishId", m_Adapter.getRef(i_Position).getKey());
        startActivity(DishDetailsIntent);
    }
}
