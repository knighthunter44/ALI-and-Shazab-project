package com.example.carhub;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.carhub.Functions.AddCarsFunctions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddCars extends AppCompatActivity {

    private CardView imageUpload;
    Context ctx;
    private ImageView imageView;

    private Button cashBtn, leaseBtn, postButton;


    private EditText carName, carModel, carColor, carPrice, carDescriptions, carUnfrontPrice, monthlyInstallment, pendingInstallment, leasdDescriptions;

    View viewCash, viewLease;
    private Uri filepath;
    private final int PICK_IMAGE_REQUEST = 71;
    //firebase
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    public FirebaseDatabase firebaseDatabase;
    public DatabaseReference databaseReference;
    ValueEventListener listener;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinerDataList;
    ArrayList<String> spinerDataList1;
    RadioGroup b_group;

    StorageTask uploadTask;


    Spinner spin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cars);


        b_group = findViewById(R.id.radioGroup);
        spin = findViewById(R.id.categories);


        //init
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("Post");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("CarList");

        imageView = findViewById(R.id.iv_select_pic);
        imageUpload = findViewById(R.id.cv_upload_photos);
        cashBtn = findViewById(R.id.Cash_button);
        leaseBtn = findViewById(R.id.Leased_button);
        postButton = findViewById(R.id.bt_post_button);
        carName = findViewById(R.id.edt_car_name);
        carModel = findViewById(R.id.edt_car_model);
        carColor = findViewById(R.id.edt_car_bodycolor);
        carPrice = findViewById(R.id.price);
        carDescriptions = findViewById(R.id.carDescription);
        carUnfrontPrice = findViewById(R.id.unfront_Price);
        monthlyInstallment = findViewById(R.id.monthly_installment);
        pendingInstallment = findViewById(R.id.pending_installment);
        leasdDescriptions = findViewById(R.id.Description_leased);

        cashBtn.setBackgroundResource(R.drawable.act_transaction_button);
        //views
        viewCash = findViewById(R.id.cash);
        viewLease = findViewById(R.id.leased);
        //visible

        spinerDataList = new ArrayList<>();
        spinerDataList1 = new ArrayList<>();


        b_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final int checkbtn = b_group.getCheckedRadioButtonId();
                if (checkbtn == R.id.radio_oldCars) {
                    adapter = new ArrayAdapter<String>(AddCars.this, android.R.layout.simple_spinner_dropdown_item, spinerDataList);
                    Log.d("asd", String.valueOf(spinerDataList));
                    spin.setAdapter(adapter);
                    retrieveData();
                    adapter.clear();
                } else if (checkbtn == R.id.radio_newCars) {
                    if (checkbtn == R.id.radio_newCars) {
                        adapter = new ArrayAdapter<String>(AddCars.this, android.R.layout.simple_spinner_dropdown_item, spinerDataList1);
                        spin.setAdapter(adapter);
                        retrieveDataNew();
                        adapter.clear();

                    }


                }

            }
        });


        findViewById(R.id.cash).setVisibility(View.VISIBLE);
        findViewById(R.id.cash).setVisibility(View.VISIBLE);


        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage();
            }
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress()) {

                    Toast.makeText(AddCars.this, "upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    String name = carName.getText().toString();
                    String model = carModel.getText().toString();
                    String color = carColor.getText().toString();
                    //spiner

                    //Leased Button Details
                    String lDescription = "" + leasdDescriptions.getText().toString();
                    String lUnfrontPrice = "" + carUnfrontPrice.getText().toString();
                    String mInstallment = "" + monthlyInstallment.getText().toString();
                    String pInstallment = "" + pendingInstallment.getText().toString();

                    //Cash Button Details
                    String cDescription = "" + carDescriptions.getText().toString();
                    String cPrice = "" + carPrice.getText().toString();

                    AddCarsFunctions carsFunctions = new AddCarsFunctions(AddCars.this, filepath, storageReference, uploadTask, databaseReference);
                    carsFunctions.uploadImage(name, model, color, lDescription, lUnfrontPrice, mInstallment, pInstallment, cDescription, cPrice);
                }

            }
        });
        cashBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                cashBtn.setBackgroundResource(R.drawable.act_transaction_button);
                leaseBtn.setBackgroundResource(R.drawable.transaction_button);
                viewCash.setVisibility(View.VISIBLE);

                viewLease.setVisibility(View.GONE);

            }
        });

        leaseBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                leaseBtn.setBackgroundResource(R.drawable.act_transaction_button);
                cashBtn.setBackgroundResource(R.drawable.transaction_button);
                viewLease.setVisibility(View.VISIBLE);


                viewCash.setVisibility(View.GONE);

            }
        });
    }

    // spiner for Old cars
    private void retrieveData() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Category");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //                    spinerDataList.add(data.getKey());
                    spinerDataList.add((String) data.child("name").getValue());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // spiner for new cars
    private void retrieveDataNew() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("C_New_cars");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    spinerDataList1.add((String) data.child("cname").getValue());

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filepath = data.getData();
            Picasso.with(AddCars.this).load(filepath).into(imageView);
        }
    }

}

