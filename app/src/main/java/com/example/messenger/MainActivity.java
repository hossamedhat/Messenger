package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.fragment.ChatFragment;
import com.example.messenger.fragment.MoreFragment;
import com.example.messenger.fragment.PeopleFragment;
import com.example.messenger.glide.GlideApp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private FirebaseAuth mAuth;
    private ChatFragment chatFragment;
    private PeopleFragment peopleFragment;
    private MoreFragment moreFragment;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firestoreInstance;
    private DocumentReference dcReference;
    private CircleImageView imageView;
    private long backPressedTime;
    private Toast back;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) ;
        }else{
            getWindow().setStatusBarColor(Color.WHITE);
        }
        inti();


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        setFragment(chatFragment);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });




    }

    private void getImgUser(){

        if (mAuth.getCurrentUser()!=null){
            dcReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        String pathImage=documentSnapshot.getString("profileImage");

                        if (!pathImage.isEmpty()){
                            GlideApp.with(MainActivity.this).load(firebaseStorage.getReference(pathImage)).placeholder(R.drawable.ic_baseline_person_pin_24).into(imageView);
                        }else {
                            GlideApp.with(MainActivity.this).load(R.drawable.user_img).into(imageView);
                        }

                    }else{
                        Toast.makeText(MainActivity.this, "No such document", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Error: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getImgUser();
    }

    private void inti() {
        mAuth = FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference(mAuth.getCurrentUser().getUid());
        firestoreInstance=FirebaseFirestore.getInstance();
        dcReference=firestoreInstance.document("users/"+mAuth.getCurrentUser().getUid());
        chatFragment=new ChatFragment();
        peopleFragment=new PeopleFragment();
        moreFragment=new MoreFragment();
        bottomNavigationView=findViewById(R.id.bottom_nav);
        toolbar=findViewById(R.id.tool_bar);
        imageView=findViewById(R.id.img_profile);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.item_chat:
                setFragment(chatFragment);
                return true;
            case R.id.item_people:
                setFragment(peopleFragment);
                return true;

            case R.id.item_more:
                setFragment(moreFragment);
                return true;

            default:
                return false;
        }

    }

    private void setFragment(Fragment fragment) {
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.replace(R.id.coordinator_layout,fragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000>System.currentTimeMillis()){
            back.cancel();
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            super.onBackPressed();
            return;
        }else {
            back= Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_LONG);
            back.show();
        }
        backPressedTime=System.currentTimeMillis();

    }
}