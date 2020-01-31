package com.example.carhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import dmax.dialog.SpotsDialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carhub.Common.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity {
   CardView changePassword,logout;
   DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    changePassword =(CardView) findViewById(R.id.cv_change_password);
    logout =(CardView) findViewById(R.id.cv_logout);
    logout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent logout = new Intent(Settings.this , Login.class);
            startActivity(logout);
        }
    });

    changePassword.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            changePassord();
        }
    });
    

    }

    private void changePassord() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Change Password");
        alertDialog.setMessage("Please fill all information");
        LayoutInflater inflater = this.getLayoutInflater();
        View change_view = inflater.inflate(R.layout.change_password, null);

        alertDialog.setView(change_view);
        alertDialog.setIcon(R.drawable.ic_forgot_icon);

        final MaterialEditText password= (MaterialEditText) change_view.findViewById(R.id.edt_passwordchange);
        final MaterialEditText newPassword= (MaterialEditText) change_view.findViewById(R.id.edt_newPassword);
        final MaterialEditText repeatPassword= (MaterialEditText) change_view.findViewById(R.id.edt_repeatPassword);
        alertDialog.setView(change_view);

        //button
        alertDialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                //android.app.AlertDialog waitingDialog = new SpotsDialog(Settings.this);

                if(password.getText().toString().equals(Common.owners.getPassword()))
                {
                    if(newPassword.getText().toString().equals(repeatPassword.getText().toString())){

                        Map<String,Object> passwordUpdate = new HashMap<>();
                        passwordUpdate.put("password",newPassword.getText().toString());
                        DatabaseReference user = FirebaseDatabase.getInstance().getReference("user");
                        user.child(Common.owners.toString()).updateChildren(passwordUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(Settings.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Settings.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else
                    {

                        Toast.makeText(Settings.this, "New Password  doesn't match", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(Settings.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }
}
