package com.example.carhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.carhub.Interface.ItemClickListner;
import com.example.carhub.Model.Cars;
import com.example.carhub.ViewHolder.CarViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.squareup.picasso.Picasso;

public class CarList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference items;

    String categoryId;

    FirebaseRecyclerAdapter<Cars, CarViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);


        //Firebase
        database = FirebaseDatabase.getInstance();
        items = database.getReference("CarList");

        recyclerView = findViewById(R.id.rv_car_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");
        }

        if (!categoryId.isEmpty() && categoryId != null) {
            loadcarItem(categoryId);
        }

    }

    private void loadcarItem(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Cars, CarViewHolder>(Cars.class, R.layout.car_list, CarViewHolder.class, items.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(CarViewHolder viewHolder, Cars model, int i) {
                viewHolder.carName.setText(model.getName());
                viewHolder.carModel.setText(model.getModel());
                viewHolder.carPrice.setText("Rs: " + model.getPrice());
                viewHolder.carBodyColor.setText(model.getBodyColor());

                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.carImage);


                final Cars itemDAO = model;

                viewHolder.setItemClickListener(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent infoCar = new Intent(CarList.this, CarInfo.class);
                        infoCar.putExtra("Name", itemDAO.getName());
                        infoCar.putExtra("Model", itemDAO.getModel());
                        infoCar.putExtra("Color", itemDAO.getBodyColor());
                        infoCar.putExtra("Price", itemDAO.getPrice());
                        infoCar.putExtra("Description", itemDAO.getDescription());
                        infoCar.putExtra("MenuID", itemDAO.getMenuId());
                        infoCar.putExtra("Image", itemDAO.getImage());
                        // Toast.makeText(CarList.this, "car hai", Toast.LENGTH_SHORT).show();
                        startActivity(infoCar);

                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }
}
