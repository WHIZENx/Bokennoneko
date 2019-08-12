package com.cmu.project.bokennoneko.SubActivity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cmu.project.bokennoneko.MainActivity.MainActivity;
import com.cmu.project.bokennoneko.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME = 5600; //This is 5.6 seconds
    private static int START_TIME = 500; //This is 0.5 seconds
    private static int START_TIME2 = 1000; //This is 1.0 seconds
    private static int END_TIME = 5600; //This is 5.6 seconds
    boolean locked = false;

    private FirebaseAuth mAuth;

    ImageView logo1, logo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_splash);

        RelativeLayout relativeLayoutMain = (RelativeLayout) findViewById(R.id.rlayout_main);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        relativeLayoutMain.startAnimation(alphaAnimation);

        logo1 = findViewById(R.id.logo1);
        logo2 = findViewById(R.id.logo2);
        logo1.setVisibility(View.INVISIBLE);
        logo2.setVisibility(View.INVISIBLE);

        openApp();
    }

    private void openApp() {

        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = mAuth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!locked) {

                    logo1 = findViewById(R.id.logo1);
                    logo1.setVisibility(View.VISIBLE);
                    logo1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.fade_in_activity));

                }
            }
        }, START_TIME);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!locked) {

                    logo2 = findViewById(R.id.logo2);
                    logo2.setVisibility(View.VISIBLE);
                    logo2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.fade_in_activity));

                }
            }
        }, START_TIME2);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!locked) {

                    logo1 = findViewById(R.id.logo1);
                    logo2 = findViewById(R.id.logo2);
                    logo1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.fade_out_activity));
                    logo2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.fade_out_activity));

                }
            }
        }, END_TIME);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!locked) {

                    if(user != null) {
                        Intent mySuperIntent = new Intent(SplashActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mySuperIntent);
                        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                        finish();
                    } else {
                        Intent mySuperIntent = new Intent(SplashActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mySuperIntent);
                        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                        finish();
                    }
                }
            }
        }, SPLASH_TIME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locked = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        locked = false;
        openApp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locked = true;
        finish();
    }
}
