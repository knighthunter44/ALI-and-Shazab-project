package com.example.carhub;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

//import com.example.carhub.Adapter.BannerSlider;
import com.example.carhub.DrawerClasses.MyAccount;
import com.example.carhub.Interface.ItemClickListner;
import com.example.carhub.Model.Cars;
import com.example.carhub.ViewHolder.CarViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    SpaceNavigationView spaceNavigationView;

    //user details
    TextView userFullname, userEmail;
    SliderView sliderView;


    // carlist database
    FirebaseDatabase database;
    DatabaseReference carlistRef;
    RecyclerView rv_carList;
    RecyclerView.LayoutManager layoutManager_carlist;
    DatabaseReference databaseReference_user;

    //search functionalities
    FirebaseRecyclerAdapter<Cars, CarViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;
    String categoryId;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //init
        database = FirebaseDatabase.getInstance();
        carlistRef = database.getReference("CarList");

        //search

        materialSearchBar = findViewById(R.id.searchBar);

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

                if (enabled)
                    rv_carList.setAdapter(searchAdapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {


            }
        });


        //bottom buttons
        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);

        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_home_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_settings_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.newcars));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.oldcars));
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent addcars = new Intent(Home.this, AddCars.class);
                startActivity(addcars);
                spaceNavigationView.setCentreButtonSelectable(true);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {

                if (itemIndex == 0) {

                } else if (itemIndex == 1) {
                    Intent setting = new Intent(Home.this, Settings.class);
                    startActivity(setting);
                } else if (itemIndex == 2) {
                    Intent newcars = new Intent(Home.this, NewCars.class);
                    startActivity(newcars);

                } else if (itemIndex == 3) {
                    Intent usedcars = new Intent(Home.this, UseCars.class);
                    startActivity(usedcars);

                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                if (itemIndex == 0) {
                    // Toast.makeText(Home.this, "One time", Toast.LENGTH_SHORT).show();

                } else if (itemIndex == 1) {
                    Intent setting = new Intent(Home.this, Settings.class);
                    startActivity(setting);
                } else if (itemIndex == 2) {
                    Intent newcars = new Intent(Home.this, NewCars.class);
                    startActivity(newcars);
                } else if (itemIndex == 3) {
                    Intent usedcars = new Intent(Home.this, UseCars.class);
                    startActivity(usedcars);
                }

            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        userFullname = headerView.findViewById(R.id.textviewUser);
        userEmail = headerView.findViewById(R.id.tVUserEmail);
        databaseReference_user = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nameuser = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();

                userFullname.setText(nameuser);
                userEmail.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //layout car list
        rv_carList = findViewById(R.id.random_carList);
        rv_carList.setHasFixedSize(true);
        layoutManager_carlist = new GridLayoutManager(this,2);
        rv_carList.setLayoutManager(layoutManager_carlist);

        loadCarlist();

    }

    private void startSearch(CharSequence text) {

        searchAdapter = new FirebaseRecyclerAdapter<Cars, CarViewHolder>(
                Cars.class,
                R.layout.ramdon_carlist,
                CarViewHolder.class,
                carlistRef.orderByChild("name").equalTo(text.toString()))
         {
            @Override
            protected void populateViewHolder(CarViewHolder carViewHolder, Cars cars, int i) {
                carViewHolder.carName.setText(cars.getName());
                carViewHolder.carModel.setText(cars.getModel());
                carViewHolder.carPrice.setText(cars.getPrice());
                Picasso.with(getBaseContext()).load(cars.getImage())
                        .into(carViewHolder.carImage);
                final Cars clickItems = cars;
                carViewHolder.setItemClickListener(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent infoCar = new Intent(Home.this, CarInfo.class);
                        infoCar.putExtra("Name", clickItems.getName());
                        infoCar.putExtra("Model", clickItems.getModel());
                        infoCar.putExtra("Color", clickItems.getBodyColor());
                        infoCar.putExtra("Price", clickItems.getPrice());
                        infoCar.putExtra("Description", clickItems.getDescription());
                        infoCar.putExtra("MenuID", clickItems.getMenuId());
                        infoCar.putExtra("Image", clickItems.getImage());
                        // Toast.makeText(CarList.this, "car hai", Toast.LENGTH_SHORT).show();
                        startActivity(infoCar);
                    }
                });

            }
        };
        rv_carList.setAdapter(searchAdapter);
    }


    //random car list
    private void loadCarlist() {
        FirebaseRecyclerAdapter<Cars, CarViewHolder> adapter = new FirebaseRecyclerAdapter<Cars, CarViewHolder>(Cars.class, R.layout.ramdon_carlist, CarViewHolder.class, carlistRef) {
            @Override
            protected void populateViewHolder(CarViewHolder carViewHolder, Cars cars, int i) {
                carViewHolder.carName.setText(cars.getName());
                carViewHolder.carModel.setText(cars.getModel());
                carViewHolder.carPrice.setText(cars.getPrice());
                Picasso.with(getBaseContext()).load(cars.getImage())
                        .into(carViewHolder.carImage);
                final Cars clickItems = cars;
                carViewHolder.setItemClickListener(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent infoCar = new Intent(Home.this, CarInfo.class);
                        infoCar.putExtra("Name", clickItems.getName());
                        infoCar.putExtra("Model", clickItems.getModel());
                        infoCar.putExtra("Color", clickItems.getBodyColor());
                        infoCar.putExtra("Price", clickItems.getPrice());
                        infoCar.putExtra("Description", clickItems.getDescription());
                        infoCar.putExtra("MenuID", clickItems.getMenuId());
                        infoCar.putExtra("Image", clickItems.getImage());
                        // Toast.makeText(CarList.this, "car hai", Toast.LENGTH_SHORT).show();
                        startActivity(infoCar);

                    }
                });
            }
        };
        rv_carList.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_AccountHome) {
            Intent myAcc = new Intent(Home.this, MyAccount.class);
            startActivity(myAcc);
            //Toast.makeText(this, "my account", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.posthome) {
            startActivity(new Intent(Home.this, AddCars.class));

        } else if (id == R.id.nav_newcarhome) {
            startActivity(new Intent(Home.this,NewCars.class));

        } else if (id == R.id.nav_oldcarhome) {
            startActivity(new Intent(Home.this,UseCars.class));

        } else if (id == R.id.nav_logouthome) {

            Intent logout = new Intent(this,Login.class);

            startActivity(logout);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


}
