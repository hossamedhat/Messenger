package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.messenger.glide.GlideApp;
import com.example.messenger.model.UsersAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.protobuf.Any;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private Button btn_sign_out;
    private Toolbar toolbar;
    private CircleImageView circleImageView;
    private TextView txt_name;
    private static final int im_code=1;
    private Uri uri=null;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firestoreInstance;
    private DocumentReference dcReference;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) ;
        }else{
            getWindow().setStatusBarColor(Color.WHITE);
        }
        inti();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Me");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),im_code);
            }
        });
        getUser();

        btn_sign_out.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mAuth.signOut();
            Intent intent=new Intent(ProfileActivity.this,SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==im_code && resultCode==RESULT_OK){
            progressBar.setVisibility(View.VISIBLE);
            uri=data.getData();
            circleImageView.setImageURI(uri);
            try {
                Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytesStream);
                byte[] bytesOutImg = bytesStream.toByteArray();
                uploadProfileImages(bytesOutImg);
            } catch (IOException e) {
                progressBar.setVisibility(View.INVISIBLE);
                e.printStackTrace();
                Toast.makeText(this, "Error:"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void uploadProfileImages(byte[] bytesOutImg) {
        StorageReference ref = storageReference.child("profilePictures/" + UUID.nameUUIDFromBytes(bytesOutImg));
        ref.putBytes(bytesOutImg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    onSuccessPath(ref.getPath());
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ProfileActivity.this, "Error"+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getUser(){

        if (mAuth.getCurrentUser()!=null){
            dcReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        String name=documentSnapshot.getString("name");
                        String pathImage=documentSnapshot.getString("profileImage");
                        if (!name.isEmpty()){
                            txt_name.setText(name);
                        }
//                        Toast.makeText(ProfileActivity.this, "Path: "+pathImage, Toast.LENGTH_SHORT).show();
                        if (!pathImage.isEmpty()){
                            GlideApp.with(ProfileActivity.this).load(firebaseStorage.getReference(pathImage)).placeholder(R.drawable.ic_baseline_person_pin_24).into(circleImageView);
                        }else {
                            GlideApp.with(ProfileActivity.this).load(R.drawable.user_img).into(circleImageView);
                        }

                    }else{
                        Toast.makeText(ProfileActivity.this, "No such document", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "Error: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void onSuccessPath(String imagePath){
        dcReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String name=documentSnapshot.getString("name");
                    Map<String, Object> userFieldMap = new HashMap<String, Object>();
                    userFieldMap.put("name",name);
                    userFieldMap.put("profileImage",imagePath);
//                    Toast.makeText(getApplicationContext(), "name: "+name+" Path_img: "+imagePath, Toast.LENGTH_SHORT).show();
                    dcReference.update(userFieldMap);
                }else{
                    Toast.makeText(ProfileActivity.this, "No such document", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Error: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void inti() {
        btn_sign_out=findViewById(R.id.btn_out);

        toolbar=findViewById(R.id.toolbar_profile);
        circleImageView=findViewById(R.id.img_user_account);
        txt_name=findViewById(R.id.txt_username);
        progressBar=findViewById(R.id.progress_bar_load_profile);

        mAuth = FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference(mAuth.getCurrentUser().getUid());
        firestoreInstance=FirebaseFirestore.getInstance();
        dcReference=firestoreInstance.document("users/"+mAuth.getCurrentUser().getUid());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return false;
    }


}