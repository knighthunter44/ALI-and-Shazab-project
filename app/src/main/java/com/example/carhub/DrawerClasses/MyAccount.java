package com.example.carhub.DrawerClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.example.carhub.Common.Common;
import com.example.carhub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyAccount extends AppCompatActivity {

    EditText name, phone_number, edt_email, password, useAddress;

    DatabaseReference databaseReference_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        name=(EditText) findViewById(R.id.pro_edtName);

        phone_number= (EditText) findViewById(R.id.pro_phoneNum);

        edt_email= (EditText) findViewById(R.id.pro_email);

        password= (EditText) findViewById(R.id.pro_password);

        useAddress =(EditText) findViewById(R.id.pro_address);

        databaseReference_user= FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference_user.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String nameuser = dataSnapshot.child("name").getValue().toString();

                String phonenumber= dataSnapshot.child("phoneNumber").getValue().toString();

                String email = dataSnapshot.child("email").getValue().toString();

                String address = dataSnapshot.child("address").getValue().toString();

                name.setText(nameuser);

                phone_number.setText(phonenumber);

                edt_email.setText(email);

                useAddress.setText(address);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
