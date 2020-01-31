package com.example.carhub;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;

import com.example.carhub.DrawerClasses.MyAccount;
import com.example.carhub.Model.C_newcars;
import com.example.carhub.ViewHolder.NewCarsViewHolder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.carhub.Interface.ItemClickListner;
import com.example.carhub.Model.Category;
import com.example.carhub.ViewHolder.UCarViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class UseCars extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SpaceNavigationView spaceNavigationView;
    FirebaseDatabase databaseUseCar;
    DatabaseReference category;
    RecyclerView recyclerView_usecar;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, UCarViewHolder> myAdapter;
    //category add
    private Uri imgUri;
    private final int PICK_IMAGE_REQUEST = 71;
    private ImageView brandImageView;
    private EditText brandName;
    //firebase
    public StorageReference storageRef;
    public DatabaseReference databaseRef;
    StorageTask uploadTask;

    FirebaseRecyclerAdapter<Category, UCarViewHolder> adapter_usedcars;
    MaterialSearchBar materialSearchUsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_cars);

        materialSearchUsed = findViewById(R.id.searchUsedCar);

        //init
        databaseUseCar = FirebaseDatabase.getInstance();
        category = databaseUseCar.getReference("Category");
        storageRef = FirebaseStorage.getInstance().getReference("UseCars");
        databaseRef = FirebaseDatabase.getInstance().getReference("Category");

        //search
        materialSearchUsed.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(enabled)
                recyclerView_usecar.setAdapter(adapter_usedcars);
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
                Intent addcars = new Intent(UseCars.this, AddCars.class);
                startActivity(addcars);
                spaceNavigationView.setCentreButtonSelectable(true);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                if (itemIndex == 0) {
                    Intent newcars = new Intent(UseCars.this, Home.class);
                    startActivity(newcars);
                } else if (itemIndex == 1) {
                    Intent setting = new Intent(UseCars.this, Settings.class);
                    startActivity(setting);
                } else if (itemIndex == 2) {
                    Intent newcars = new Intent(UseCars.this, NewCars.class);
                    startActivity(newcars);

                } else if (itemIndex == 3) {

                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                if (itemIndex == 0) {
                    Intent newcars = new Intent(UseCars.this, Home.class);
                    startActivity(newcars);
                } else if (itemIndex == 1) {
                    Intent setting = new Intent(UseCars.this, Settings.class);
                    startActivity(setting);
                } else if (itemIndex == 2) {
                    Intent newcars = new Intent(UseCars.this, NewCars.class);
                    startActivity(newcars);
                }

            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Use Cars");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_usecar);
        navigationView.setNavigationItemSelectedListener(this);


        //load list of car

        recyclerView_usecar = (RecyclerView) findViewById(R.id.recycler_useCar);
        recyclerView_usecar.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_usecar.setLayoutManager(layoutManager);

        loadlist();

    }

    private void startSearch(CharSequence text) {
        adapter_usedcars = new FirebaseRecyclerAdapter<Category, UCarViewHolder>(Category.class,R.layout.list_usecars,UCarViewHolder.class,category.orderByChild("name").equalTo(text.toString())) {
            @Override
            protected void populateViewHolder(UCarViewHolder uCarViewHolder, Category category, int i) {
                uCarViewHolder.textUsecarName.setText(category.getName());
                Picasso.with(getBaseContext()).load(category.getImage()).into(uCarViewHolder.iv_Usecar);
                uCarViewHolder.setCarclickListener(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent carlist = new Intent(UseCars.this , CarList.class);
                        carlist.putExtra("CategoryId",adapter_usedcars.getRef(position).getKey());
                            startActivity(carlist);
                    }
                });
            }

        };
        recyclerView_usecar.setAdapter(adapter_usedcars);
    }

    private void loadlist() {
        myAdapter = new FirebaseRecyclerAdapter<Category, UCarViewHolder>(Category.class, R.layout.list_usecars, UCarViewHolder.class, category) {
            @Override
            protected void populateViewHolder(UCarViewHolder viewHolder, Category model, int position) {
                viewHolder.textUsecarName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.iv_Usecar);
                viewHolder.setCarclickListener(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent carlist = new Intent(UseCars.this, CarList.class);
                        carlist.putExtra("CategoryId", myAdapter.getRef(position).getKey());
                        startActivity(carlist);
                    }
                });
            }
        };
        recyclerView_usecar.setAdapter(myAdapter);
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
        getMenuInflater().inflate(R.menu.use_cars, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.add_category) {
            addUseCategory();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addUseCategory() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Category");
        LayoutInflater inflater = this.getLayoutInflater();
        View addCategory_view = inflater.inflate(R.layout.add_category, null);
        alertDialog.setView(addCategory_view);
        alertDialog.setIcon(R.drawable.ic_add_circle_black_24dp);
        final MaterialEditText edtBrand = (MaterialEditText) addCategory_view.findViewById(R.id.edt_brandName);
        brandName = edtBrand;
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
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(UseCars.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();

                }
            }
        });
        alertDialog.show();
    }

    private void uploadImage() {
        if (imgUri != null) {
            final StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(imgUri));
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            uploadTask = fileRef.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    progressDialog.dismiss();
                                    String url = uri.toString();

                                    Toast.makeText(UseCars.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
                                    Category category = new Category(brandName.getText().toString().trim(), url);

                                    String uploadId = databaseRef.push().getKey();
                                    databaseRef.child(uploadId).setValue(category);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UseCars.this, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) +progress + "%");
                        }
                    });

        } else
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
            Picasso.with(UseCars.this).load(imgUri).into(brandImageView);
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
            Intent myAccount = new Intent(UseCars.this , MyAccount.class);
            startActivity(myAccount);

        } else if (id == R.id.post) {
            Intent post= new Intent(UseCars.this , AddCars.class);
            startActivity(post);
        } else if (id == R.id.nav_newcar) {
            Intent newcar= new Intent(UseCars.this , NewCars.class);
            startActivity(newcar);
        } else if (id == R.id.nav_oldcar) {
            Intent useCar= new Intent(UseCars.this , UseCars.class);
            startActivity(useCar);
        } else if (id == R.id.nav_logout) {
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
