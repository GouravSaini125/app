package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.auth.internal.IdTokenListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.InternalTokenResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity{

    private static final int CHOOSE_IMAGE = 101;
    TextView email;
    Button btn,save;
    ImageView imageView;
    EditText editText;
    Uri uriImage;
    ProgressBar pbar;
    String imageUrl;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        email = findViewById(R.id.email);
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getEmail();
        email.setText(userid);

        mAuth = FirebaseAuth.getInstance();

        btn = findViewById(R.id.logout);
        save = findViewById(R.id.save);
        imageView = findViewById(R.id.pic);
        editText = findViewById(R.id.name);
        pbar = findViewById(R.id.pbar);

        loadDetails();

//        Log.d("iPath",vText.getText().toString());

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                showImageChooser();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){

                saveUserInfo();

            }
        });
    }

    private void loadDetails() {

        if(user.getPhotoUrl() != null){
            Glide.with(this).load(user.getPhotoUrl().toString()).into(imageView);
        }
        if(user.getDisplayName() != null){
            editText.setText(user.getDisplayName());
        }

    }

    private void saveUserInfo() {

        String name = editText.getText().toString();

        if(name.isEmpty()){
            editText.setError("Required");
            editText.requestFocus();
            return;
        }

        if(user != null && imageUrl != null){

            pbar.setVisibility(View.VISIBLE);

            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse(imageUrl))
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    pbar.setVisibility(View.GONE);
                    if (task.isSuccessful()){
                        Toast.makeText(ProfileActivity.this,"Profile Updated",
                                Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(ProfileActivity.this,
                                task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null){

            uriImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriImage);
                imageView.setImageBitmap(bitmap);
                uploadImage();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseAuth.getInstance().getCurrentUser().reload();
//        user = FirebaseAuth.getInstance().getCurrentUser();
//
//        if(user.isEmailVerified()){
//            vText.setText("Email is Verified!");
//            vBtn.setVisibility(View.GONE);
//        }
//
//    }

    private void uploadImage() {

        final StorageReference imageRef =
                FirebaseStorage.getInstance().getReference("pics/"+user.getUid()+".jpg");

        if(uriImage != null){
            pbar.setVisibility(View.VISIBLE);
            imageRef.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    pbar.setVisibility(View.GONE);
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrl = uri.toString();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    pbar.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this,e.getMessage(),
                            Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    private void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select profile pic")
                , CHOOSE_IMAGE);
    }

}
