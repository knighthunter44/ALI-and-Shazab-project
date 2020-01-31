package com.example.carhub.Functions;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.carhub.AddCars;
import com.example.carhub.Model.Cars;
import com.example.carhub.Model.CategorySpinner;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class AddCarsFunctions {

    Context ctx;
    Uri filepath;
    StorageReference storageReference;
    StorageTask uploadTask;
    DatabaseReference databaseReference;
    Spinner spin;


    public AddCarsFunctions(Context ctx, Spinner spin) {
        this.ctx = ctx;
        this.spin = spin;
    }

    public AddCarsFunctions(AddCars ctx, Uri filepath, StorageReference storageReference, StorageTask uploadTask, DatabaseReference databaseReference){
        this.ctx = ctx;
        this.filepath = filepath;
        this.storageReference = storageReference;
        this.uploadTask = uploadTask;
        this.databaseReference = databaseReference;

    }

    public void uploadImage(final String name, final String model, final String color, final String lDescription, final String lUnfrontPrice, final String mInstallment
            , final String pInstallment, final String cDescription, final String cPrice) {

        if (filepath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(ctx);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(filepath));

            uploadTask = fileRef.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    progressDialog.dismiss();
                                    String url = uri.toString();

                                    Toast.makeText(ctx, "Upload Successfully", Toast.LENGTH_SHORT).show();
                                    Cars cars = new Cars(url, name, model, cPrice, color," ", cDescription, lDescription, lUnfrontPrice,
                                            mInstallment, pInstallment);

                                    String uploadId = databaseReference.push().getKey();
                                    databaseReference.child(uploadId).setValue(cars);

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ctx, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) +progress + "%");
                        }

                    });
        } else {
            Toast.makeText(ctx, "No File Selected...", Toast.LENGTH_SHORT).show();
        }

    }

    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = ctx.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }


    }

