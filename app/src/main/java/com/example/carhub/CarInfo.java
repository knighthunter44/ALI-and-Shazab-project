package com.example.carhub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class CarInfo extends AppCompatActivity {

    private TextView carName, carModel, carColor, carPrice, carDescription;
    private ImageView carImage;
    private String cName, cModel, cColor, cPrice, cDescription,cImage,menuID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);

        carName = findViewById(R.id.info_car_name);
        carModel = findViewById(R.id.info_car_model);
        carColor = findViewById(R.id.info_car_color);
        carPrice = findViewById(R.id.info_car_price);
        carDescription = findViewById(R.id.info_car_discription);
        carImage = (ImageView) findViewById(R.id.info_car_image);

        if (getIntent() != null) {
            cImage = getIntent().getStringExtra("Image");
            cName = getIntent().getStringExtra("Name");
            cModel = getIntent().getStringExtra("Model");
            cColor = getIntent().getStringExtra("Color");
            cPrice = getIntent().getStringExtra("Price");
            menuID=getIntent().getStringExtra("MenuID");
            cDescription = getIntent().getStringExtra("Description");

            carName.setText(cName);
            carModel.setText("Model:  "+cModel);
            carColor.setText("Body Color:  "+cColor);
            carPrice.setText("Rs: "+cPrice);
            carDescription.setText("Description: "+cDescription);
           // Picasso.with(getBaseContext()).load(cImage).into(carImage);
            Picasso.with(getBaseContext()).load(cImage).into(carImage);


        }
    }
}
