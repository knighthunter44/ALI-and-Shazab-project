package com.example.carhub;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carhub.Common.Common;
import com.example.carhub.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Map;
import java.util.Set;

public class Login extends AppCompatActivity {
    EditText edt_password, email;
    Button login;
    TextView tv_create_acc, tv_forgot_pass;
    FirebaseDatabase database;
    DatabaseReference table_user;
    FirebaseAuth.AuthStateListener mFirebaseAuthState;
    FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        email = (EditText) findViewById(R.id.edt_email);
        edt_password = (EditText) findViewById(R.id.edt_loginpassword);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuthState = new FirebaseAuth.AuthStateListener() {


            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    Toast.makeText(Login.this, "You are logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            }
        };

        tv_forgot_pass = findViewById(R.id.tv_Forgot);
        tv_forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPassDialog();
            }
        });
        login = (Button) findViewById(R.id.b_login);
        tv_create_acc = (TextView) findViewById(R.id.tv_register);
        tv_create_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(Login.this, Registration.class);
                startActivity(signUp);
            }
        });

        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("user");


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().equals("")) {
                    email.setError("Enter your email");
                    email.requestFocus();
                } else if (edt_password.getText().toString().equals("")) {
                    edt_password.setError("Enter your password");
                    edt_password.requestFocus();
                } else {
                    loginAuthentication(email.getText().toString(), edt_password.getText().toString());
                }
            }
        });
    }

    private void loginAuthentication(String email, String password) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(Login.this, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void showForgotPassDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Forgot Password");
        alertDialog.setMessage("Enter Your email");
        LayoutInflater inflater = this.getLayoutInflater();
        final Context context = inflater.getContext();
        final LayoutInflater inflater1 = LayoutInflater.from(context);
        View forgot_view = inflater1.inflate(R.layout.forgot_pass_layout, null);
        final EditText forgotPass = (EditText) forgot_view.findViewById(R.id.email_forgot);
        final FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();

        alertDialog.setView(forgot_view);
        alertDialog.setIcon(R.drawable.ic_forgot_icon);
        alertDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebaseAuth.sendPasswordResetEmail(forgotPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Password sent to your email", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthState);
    }


}
