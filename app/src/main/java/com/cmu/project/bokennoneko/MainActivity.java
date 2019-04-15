package com.cmu.project.bokennoneko;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.cmu.project.bokennoneko.Game.StartGame;
import com.cmu.project.bokennoneko.SubActivity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button btn_logout;

    FirebaseUser firebaseUser;

    ViewFlipper v_flipper;

    MediaPlayer mp;

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

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        v_flipper = findViewById(R.id.viewflipper);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        int images[] = {R.drawable.bg1, R.drawable.bg2, R.drawable.bg3};

        for (int image: images) {
            setImageInFlip(image);
        }
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, StartGame.class);
        startActivity(intent);
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
