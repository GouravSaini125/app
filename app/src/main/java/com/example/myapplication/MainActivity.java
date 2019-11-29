package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth mAuth;
    EditText editTextEmail, editTextPassword;
    ProgressBar progressBar;
//    SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//------------------------------------------
//        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
//            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        }
//------------------------------------------

//        sp = getSharedPreferences("login",MODE_PRIVATE);
//        if (sp.getBoolean("logged",false)){
//            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        }

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.text2).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);
        progressBar = findViewById(R.id.loading);
    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Invalid Email");
            editTextEmail.requestFocus();
            return;
        }
        if(password.length()<6){
            editTextPassword.setError("Password must be of 6 digits.");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
//                    sp.edit().putBoolean("logged",true).apply();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(!user.isEmailVerified()){
                        finish();
                        startActivity(new Intent(MainActivity.this,verify.class));
                    }
                    else {
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()){
            finish();
            startActivity(new Intent(this,ProfileActivity.class));
        }
        if(mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().isEmailVerified()){
            finish();
            startActivity(new Intent(this,verify.class));
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.text2:
                startActivity(new Intent(this, SignUp.class));
                break;
            case R.id.login:
                userLogin();
                break;
        }
    }
}
