package com.cmu.project.bokennoneko.MainActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    ImageView  btn_logout, btn_score, btn_exit;

    FirebaseUser firebaseUser;

    ViewFlipper v_flipper;

    MediaPlayer mp;

    CircleImageView profile_img;
    TextView maxscore, username;

    FirebaseAuth mAuth;
    FirebaseUser cureUser;

    GifImageView cat, cat_angry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

//        mp = MediaPlayer.create(getApplicationContext(), R.raw.commonground);
//        mp.setLooping(true);
//        mp.start();

        btn_logout = findViewById(R.id.btn_logout);
        btn_score = findViewById(R.id.btn_score);
        btn_exit = findViewById(R.id.btn_exit);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        v_flipper = findViewById(R.id.viewflipper);

        profile_img = findViewById(R.id.profile_img);
        maxscore = findViewById(R.id.maxscore);
        username = findViewById(R.id.username);

        cat = findViewById(R.id.cat);
        cat_angry = findViewById(R.id.cat_angry);

        mAuth = FirebaseAuth.getInstance();
        cureUser = mAuth.getCurrentUser();

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                finish();
            }
        });

        btn_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScoreActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                finish();
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
                profile_img.setVisibility(View.VISIBLE);
                username.setVisibility(View.VISIBLE);
                maxscore.setVisibility(View.VISIBLE);
                btn_logout.setVisibility(View.VISIBLE);
                cat_angry.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Animation
        Animation animUpDown;

        // load the animation
        animUpDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.up_down);

        // start the animation
        cat.startAnimation(animUpDown);
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, StartGame.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
        finish();
//        mp.stop();
//        mp.release();
    }

    private void setImageInFlip(int imgUrl) {
        ImageView image = new ImageView(getApplicationContext());
        image.setBackgroundResource(imgUrl);
        v_flipper.addView(image);
        v_flipper.setFlipInterval(5000);
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
