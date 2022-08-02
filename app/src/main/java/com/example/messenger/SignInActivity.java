package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity implements TextWatcher {
    private Button btn_create,btn_sign_in;
    private EditText edit_email,edit_password;
    private String email,pass;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.log));
        }



        inti();

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
            }
        });


        btn_sign_in.setOnClickListener(new View.OnClickListener() {
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

                signIn(email,pass);
            }
        });

    }

    private void signIn(String email,String pass){
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Map<String, Object> userFieldMap = new HashMap<String, Object>();
                            userFieldMap.put("token",s);
                            firestore.collection("users")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .update(userFieldMap);
                        }
                    });
                    progressBar.setVisibility(View.INVISIBLE);
                    Intent intent=new Intent(SignInActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(SignInActivity.this, "error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void inti() {
        btn_create=findViewById(R.id.btn_create_account);
        btn_sign_in=findViewById(R.id.btn_log_in);
        edit_email=findViewById(R.id.edit_emial_in);
        edit_password=findViewById(R.id.edit_password_in);
        progressBar=findViewById(R.id.progress_bar_load_in);

        edit_email.addTextChangedListener(SignInActivity.this);
        edit_password.addTextChangedListener(SignInActivity.this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        email=edit_email.getText().toString().trim();
        pass=edit_password.getText().toString().trim();
        btn_sign_in.setEnabled(!email.isEmpty() && !pass.isEmpty());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}