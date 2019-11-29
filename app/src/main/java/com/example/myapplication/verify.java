package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class verify extends AppCompatActivity {

    TextView vText,email;
    Button vBtn,login;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        vBtn = findViewById(R.id.vBtn);
        vText = findViewById(R.id.vText);
        login = findViewById(R.id.login);
        email = findViewById(R.id.email);

        String userid = user.getEmail();
        email.setText(userid);

        if(user.isEmailVerified()){
            vText.setText("Email is Verified!");
            vBtn.setVisibility(View.GONE);
        }else{
            vBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.sendEmailVerification().addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    vText.setText("Verification Email Sent. Click on the link " +
                                            "provided in email and sign in to your account.");
                                    vBtn.setVisibility(View.GONE);
                                }
                            });
                }
            });
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(verify.this,MainActivity.class));
            }
        });

    }


}
