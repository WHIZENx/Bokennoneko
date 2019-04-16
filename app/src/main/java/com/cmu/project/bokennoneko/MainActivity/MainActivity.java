package com.cmu.project.bokennoneko.MainActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.cmu.project.bokennoneko.Game.StartGame;
import com.cmu.project.bokennoneko.Model.Score;
import com.cmu.project.bokennoneko.Model.Users;
import com.cmu.project.bokennoneko.R;
import com.cmu.project.bokennoneko.SubActivity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    Button btn_logout, btn_score;

    FirebaseUser firebaseUser;

    ViewFlipper v_flipper;

    MediaPlayer mp;

    CircleImageView profile_img;
    TextView maxscore, username;

    FirebaseAuth mAuth;
    FirebaseUser cureUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

//        mp = MediaPlayer.create(getApplicationContext(), R.raw.commonground);
//        mp.setLooping(true);
//        mp.start();

        btn_logout = findViewById(R.id.btn_logout);
        btn_score = findViewById(R.id.btn_score);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        v_flipper = findViewById(R.id.viewflipper);

        profile_img = findViewById(R.id.profile_img);
        maxscore = findViewById(R.id.maxscore);
        username = findViewById(R.id.username);

        mAuth = FirebaseAuth.getInstance();
        cureUser = mAuth.getCurrentUser();

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        btn_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScoreActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        int images[] = {R.drawable.bg1, R.drawable.bg2, R.drawable.bg3};

        for (int image: images) {
            setImageInFlip(image);
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(cureUser.getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                Glide.with(getApplicationContext()).load(users.getImageURL()).into(profile_img);
                username.setText(users.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref = FirebaseDatabase.getInstance().getReference("Scores").child(cureUser.getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Score score = dataSnapshot.getValue(Score.class);
                if (!dataSnapshot.exists()) {
                    maxscore.setText("Max Score: " + 0);
                } else {
                    maxscore.setText("Max Score: " + score.getMaxscore());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, StartGame.class);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(MainActivity.this, R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
//        mp.stop();
//        mp.release();
    }

    private void setImageInFlip(int imgUrl) {
        ImageView image = new ImageView(getApplicationContext());
        image.setBackgroundResource(imgUrl);
        v_flipper.addView(image);
        v_flipper.setFlipInterval(4000);
        v_flipper.setAutoStart(true);

        v_flipper.setInAnimation(this, R.anim.fade_in);
        v_flipper.setOutAnimation(this, R.anim.fade_out);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mp.stop();
//        mp.release();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        PowerManager mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//
//        if (!mPowerManager.isScreenOn()) {
//            if (mp != null && mp.isPlaying())
//                mp.stop();
//        }
//    }
}
