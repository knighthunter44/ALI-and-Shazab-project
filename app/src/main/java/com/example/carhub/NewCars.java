package com.example.carhub;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.carhub.DrawerClasses.MyAccount;
import com.example.carhub.Interface.ItemClickListner;
import com.example.carhub.Model.C_newcars;
import com.example.carhub.Model.Cars;
import com.example.carhub.Model.Category;
import com.example.carhub.ViewHolder.CarViewHolder;
import com.example.carhub.ViewHolder.NewCarsViewHolder;
import com.example.carhub.ViewHolder.UCarViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewCars extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SpaceNavigationView spaceNavigationView;
    FirebaseDatabase database;
    DatabaseReference category_newcars;
    RecyclerView rv_newcar;
    RecyclerView.LayoutManager layoutManager_usecar;

    private Uri imgUri;
    private final int PICK_IMAGE_REQUEST =1;
    private ImageView brandImageView;
    private EditText brandName;

    //firebase
    public StorageReference storageRef;
    public DatabaseReference databaseRef;
    StorageTask uploadTask;

    //search func
    FirebaseRecyclerAdapter<C_newcars, NewCarsViewHolder> adapter_newcars;
   MaterialSearchBar materialSearchNew;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cars);

        materialSearchNew = findViewById(R.id.searchNewCar);
        //init firebase
        database = FirebaseDatabase.getInstance();
        category_newcars = database.getReference("C_New_cars");
        storageRef=FirebaseStorage.getInstance().getReference("NewCars");
        databaseRef=FirebaseDatabase.getInstance().getReference("C_New_cars");

        //Search
       materialSearchNew.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
           @Override
           public void onSearchStateChanged(boolean enabled) {
               if(!enabled)
                   rv_newcar.setAdapter(adapter_newcars);
           }

           @Override
           public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
           }

           @Override
           public void onButtonClicked(int buttonCode) {

           }
       });

        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);

        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_home_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_settings_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.newcars));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.oldcars));
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent addcars = new Intent(NewCars.this, AddCars.class);
                startActivity(addcars);
                spaceNavigationView.setCentreButtonSelectable(true);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                if (itemIndex == 0) {
                    Intent newcars = new Intent(NewCars.this, Home.class);
                    startActivity(newcars);
                } else if (itemIndex == 1) {
                    Intent setting = new Intent(NewCars.this, Settings.class);
                    startActivity(setting);
                } else if (itemIndex == 2) {

                } else if (itemIndex == 3) {
                    Intent usedcars = new Intent(NewCars.this, UseCars.class);
                    startActivity(usedcars);

                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                if (itemIndex == 0) {
                    Intent newcars = new Intent(NewCars.this, Home.class);
                    startActivity(newcars);
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_newcar);
        navigationView.setNavigationItemSelectedListener(this);

        //load listcars
        rv_newcar = (RecyclerView) findViewById(R.id.recycler_newCar);
        rv_newcar.setHasFixedSize(true);
        layoutManager_usecar = new LinearLayoutManager(this);
        rv_newcar.setLayoutManager(layoutManager_usecar);

        load_newcars();

    }

    private void startSearch(CharSequence text) {
        adapter_newcars = new FirebaseRecyclerAdapter<C_newcars, NewCarsViewHolder>(C_newcars.class,R.layout.list_usecars,NewCarsViewHolder.class,category_newcars.orderByChild("cname").equalTo(text.toString())) {
            @Override
            protected void populateViewHolder(NewCarsViewHolder viewHolder, C_newcars c_newcars, int i) {
                viewHolder.text_Newcars.setText(c_newcars.getCname());
                Picasso.with(getBaseContext()).load(c_newcars.getCimage()).into(viewHolder.image_Newcars);
                viewHolder.setItemClickListner_newcars(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent carlist = new Intent(NewCars.this,CarList.class);
                        carlist.putExtra("CategoryId",adapter_newcars.getRef(position).getKey());
                            startActivity(carlist);
                    }
                });
            }
        };
        rv_newcar.setAdapter(adapter_newcars);
    }


    private void load_newcars() {
        adapter_newcars = new FirebaseRecyclerAdapter<C_newcars, NewCarsViewHolder>(C_newcars.class, R.layout.list_usecars, NewCarsViewHolder.class, category_newcars) {
            @Override
            protected void populateViewHolder(NewCarsViewHolder viewHolder, C_newcars model, int position) {
                viewHolder.text_Newcars.setText(model.getCname());
                Picasso.with(getBaseContext()).load(model.getCimage()).into(viewHolder.image_Newcars);
                viewHolder.setItemClickListner_newcars(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent carlist = new Intent(NewCars.this, CarList.class);
                        carlist.putExtra("CategoryId", adapter_newcars.getRef(position).getKey());
                        startActivity(carlist);
                    }
                });
            }
        };
        rv_newcar.setAdapter(adapter_newcars);

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
        getMenuInflater().inflate(R.menu.new_cars, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_category) {
            addCategory();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addCategory() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Category");


        LayoutInflater inflater = this.getLayoutInflater();
        View addCategory_view = inflater.inflate(R.layout.add_category, null);
        alertDialog.setView(addCategory_view);
        alertDialog.setIcon(R.drawable.ic_add_circle_black_24dp);
        //init
        final MaterialEditText edtBrand = (MaterialEditText) addCategory_view.findViewById(R.id.edt_brandName);
        brandName=edtBrand;
        final ImageView brandImage = (ImageView) addCategory_view.findViewById(R.id.iv_category_pic);
        brandImageView = brandImage;
        brandImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseImage();
            }
        });

        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(uploadTask !=null && uploadTask.isInProgress()){
                    Toast.makeText(NewCars.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
                }
            else {
                        uploadImage();

                }
            }
        });
        alertDialog.show();
    }

    private void uploadImage() {
        if(imgUri != null){
            final StorageReference fileRef = storageRef.child(System.currentTimeMillis()+"."+getFileExtension(imgUri));
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            uploadTask= fileRef.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {
                               progressDialog.dismiss();
                               String url = uri.toString();

                               Toast.makeText(NewCars.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
                               C_newcars c_newcars = new C_newcars(brandName.getText().toString().trim(),url);

                               String uploadId = databaseRef.push().getKey();
                               databaseRef.child(uploadId).setValue(c_newcars);
                           }
                       });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(NewCars.this, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) +progress + "%");
                        }
                    });

        }
        else
            Toast.makeText(this, "No File select", Toast.LENGTH_SHORT).show();
    }

    private void showChooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();
            Picasso.with(NewCars.this).load(imgUri).into(brandImageView);
        }
    }



    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Account) {
            Intent myAccount = new Intent(NewCars.this , MyAccount.class);
            startActivity(myAccount);

        } else if (id == R.id.post) {
            Intent post= new Intent(NewCars.this , AddCars.class);
            startActivity(post);
        } else if (id == R.id.nav_newcar) {
            Intent newcar= new Intent(NewCars.this , NewCars.class);
            startActivity(newcar);
        } else if (id == R.id.nav_oldcar) {
            Intent useCar= new Intent(NewCars.this , UseCars.class);
            startActivity(useCar);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();

            Intent logut = new Intent(NewCars.this,Login.class);
            logut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logut);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
