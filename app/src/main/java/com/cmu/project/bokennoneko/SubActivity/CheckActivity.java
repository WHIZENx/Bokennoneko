package com.cmu.project.bokennoneko.SubActivity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cmu.project.bokennoneko.MainActivity;
import com.cmu.project.bokennoneko.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CheckActivity extends AppCompatActivity {

    CircleImageView img_done;

    String userid, username, img_url;

    EditText name;

    Button btn_done;

    DatabaseReference reference;

    ProgressBar progressBar;

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

        progressBar.setVisibility(View.INVISIBLE);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_done.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                if(!name.getText().toString().equals("")) {
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                    HashMap<String, String> hashMap = new HashMap<>();
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
}
