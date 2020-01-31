package com.example.carhub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carhub.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class Registration extends AppCompatActivity {
    final Pattern NAME_PATTERN = Pattern.compile("[^\\s][a-zA-Z\\s]+", Pattern.CASE_INSENSITIVE);
    final Pattern PHONE_PATTERN = Pattern.compile("^((\\+92)|(0092)|(03))\\d{3}-{0,1}\\d{7}$|^\\d{11}$|^\\d{4}-\\d{7}$");
    EditText name, phone_number, edt_email, password,address;
    Button register;
    TextView alreadyAcc;
    private CircleImageView profileImage;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference table_user = database.getReference("user");

    final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        name = (EditText) findViewById(R.id.edt_name);
        password = (EditText) findViewById(R.id.edt_password);
        phone_number = (EditText) findViewById(R.id.et_phonenumber);
        edt_email = (EditText) findViewById(R.id.edt_email);
        address = (EditText) findViewById(R.id.edt_address);
        register = (Button) findViewById(R.id.b_registration);
        alreadyAcc = (TextView) findViewById(R.id.tv_backsignIn);
        alreadyAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alreadyAcc = new Intent(Registration.this, Login.class);
                startActivity(alreadyAcc);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("")) {
                    name.setError("Enter your name");
                    name.requestFocus();
                } else if (name.getText().toString().length() < 6) {
                    name.setError("Name too short");
                    name.requestFocus();
                } else if (!NAME_PATTERN.matcher(name.getText().toString()).matches()) {
                    name.setError("Enter correct name");
                    name.requestFocus();
                } else if (phone_number.getText().toString().equals("")) {
                    phone_number.setError("Please Enter valid phone no");
                    phone_number.requestFocus();
                } else if (phone_number.getText().toString().length() < 11) {
                    phone_number.setError("Phone number must 11 digits ");
                    phone_number.requestFocus();
                } else if (!PHONE_PATTERN.matcher(phone_number.getText().toString()).matches()) {
                    phone_number.setError("Enter correct phone no");
                    phone_number.requestFocus();
                } else if (edt_email.getText().toString().equals("")) {
                    edt_email.setError("Please Enter email address");
                    edt_email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(edt_email.getText().toString()).matches()) {
                    edt_email.setError("Enter a valid email address");
                    edt_email.requestFocus();
                } else if (password.getText().toString().equals("")) {
                    password.setError("Enter the password");
                    password.requestFocus();
                } else if (password.getText().toString().length() < 8) {
                    password.setError("Password too weak");
                    password.requestFocus();
                }
                else if (address.getText().toString().equals("")) {
                    address.setError("Enter your address");
                    address.requestFocus();
                } else if (address.getText().toString().length() < 15) {
                    address.setError("Valid address");
                    address.requestFocus();
                }

                else {
                    //loadData();
                    registerUser(edt_email.getText().toString(), password.getText().toString());
                }
            }
        });
    }

    public void registerUser(final String email, String password){
        final ProgressDialog progressDialog = new ProgressDialog(Registration.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user= new User(
                            name.getText().toString(),
                            phone_number.getText().toString(),
                            edt_email.getText().toString(),
                            address.getText().toString()
                    );

                    FirebaseDatabase.getInstance().getReference("user")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(Registration.this, "Error loading user data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    startActivity(new Intent(Registration.this, Login.class));
                }else{
                    Toast.makeText(Registration.this, "Registration Failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }



}

