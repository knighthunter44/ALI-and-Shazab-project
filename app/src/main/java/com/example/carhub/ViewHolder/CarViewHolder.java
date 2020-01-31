package com.example.carhub.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carhub.Interface.ItemClickListner;
import com.example.carhub.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView carImage;

    public TextView carName, carModel, carPrice , carBodyColor;

    private ItemClickListner itemClickListner;

    public CarViewHolder(@NonNull View itemView) {
        super(itemView);
        carName = itemView.findViewById(R.id.car_name);
        carModel = itemView.findViewById(R.id.car_Model);
        carPrice = itemView.findViewById(R.id.car_price);
        carBodyColor = itemView.findViewById(R.id.car_bodycolor);
        carImage = itemView.findViewById(R.id.car_image);
        itemView.setOnClickListener(this);
    }
    public void setItemClickListener(ItemClickListner itemClickListener) {
        this.itemClickListner = itemClickListener;
    }
    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v, getAdapterPosition(), false);

    }
}
