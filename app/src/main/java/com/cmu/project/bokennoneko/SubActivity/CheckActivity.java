package com.cmu.project.bokennoneko.SubActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cmu.project.bokennoneko.MainActivity.MainActivity;
import com.cmu.project.bokennoneko.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CheckActivity extends AppCompatActivity {

    CircleImageView img_done;

    String userid, username, img_url;

    EditText name;

    Button btn_done;

    DatabaseReference reference;

    ProgressBar progressBar;

    static int PReqCode = 1 ;
    static int REQUESCODE = 1 ;
    Uri pickedImgUri ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_check);

        img_done = findViewById(R.id.img_done);

        name = findViewById(R.id.name);

        btn_done = findViewById(R.id.btn_done);

        progressBar = findViewById(R.id.done_progress);

        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        username = intent.getStringExtra("nameid");
        img_url = intent.getStringExtra("imgurl");

        Glide.with(getApplicationContext()).load(img_url).into(img_done);

        name.setText(username);

        img_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                }
                else
                {
                    openGallery();
                }
            }
        });

        progressBar.setVisibility(View.INVISIBLE);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_done.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                if(!name.getText().toString().equals("")) {

                    if (pickedImgUri != null) {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        final FirebaseUser currentUser = mAuth.getCurrentUser();
                        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
                        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
                        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // image uploaded succesfully
                                // now we can get our image url
                                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(final Uri uri) {
                                        // uri contain user image url
                                        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(username)
                                                .setPhotoUri(uri)
                                                .build();
                                        currentUser.updateProfile(profleUpdate)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            img_url = ""+uri;
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }
                        });
                    }
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", userid);
                    hashMap.put("username", username);
                    hashMap.put("imageURL", img_url);

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                btn_done.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);

                                Intent intent = new Intent(CheckActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                } else {
                    Toast.makeText(CheckActivity.this, "Please type your name!",
                            Toast.LENGTH_SHORT).show();
                    btn_done.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(CheckActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CheckActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(CheckActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();
            }

            else
            {
                ActivityCompat.requestPermissions(CheckActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        }
        else
            openGallery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {
            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            img_done.setImageURI(pickedImgUri);
        }
    }
}
