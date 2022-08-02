package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.messenger.model.UsersAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity implements TextWatcher {
    private EditText edit_name,edit_email,edit_password;
    private Button btn_sign_up;
    private String name,email,pass;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference curDocumentReference;
    private ProgressBar progressBar;
    private ImageView imageView_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.log));
        }
        inti();

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    edit_email.setError("Please enter a valid email ");
                    edit_email.requestFocus();
                    return;
                }

                if (pass.length()<6){
                    edit_password.setError("6 char required...");
                    edit_password.requestFocus();
                    return;
                }
                createNewAccount(email,pass,name);
            }
        });

        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void createNewAccount(String email, String pass, String name) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                curDocumentReference=db.document("users/"+mAuth.getUid());
                UsersAccount usersAccount=new UsersAccount(name,"");
                curDocumentReference.set(usersAccount);
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void inti() {
        edit_name=findViewById(R.id.edit_name);
        edit_email=findViewById(R.id.edit_email);
        edit_password=findViewById(R.id.edit_password);
        btn_sign_up=findViewById(R.id.btn_sign_up);
        progressBar=findViewById(R.id.progress_bar_load_up);


        edit_name.addTextChangedListener(SignUpActivity.this);
        edit_email.addTextChangedListener(SignUpActivity.this);
        edit_password.addTextChangedListener(SignUpActivity.this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        imageView_back=findViewById(R.id.img_back_sign);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        name=edit_name.getText().toString().trim();
        email=edit_email.getText().toString().trim();
        pass=edit_password.getText().toString().trim();
        btn_sign_up.setEnabled(!name.isEmpty()&& !email.isEmpty() && !pass.isEmpty());
    }
    @Override
    public void afterTextChanged(Editable editable) {

    }
}