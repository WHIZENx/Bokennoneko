package com.cmu.project.bokennoneko.SubActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.cmu.project.bokennoneko.R;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME = 4500; //This is 3 seconds

    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        img = findViewById(R.id.img);

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(img, "scaleX", 0.0f, 1.0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(img, "scaleY", 0.0f, 1.0f);
        scaleDownX.setDuration(1500);
        scaleDownY.setDuration(1500);

        ObjectAnimator moveUpY = ObjectAnimator.ofFloat(img, "translationY", -100);
        moveUpY.setDuration(1500);

        AnimatorSet scaleDown = new AnimatorSet();
        AnimatorSet moveUp = new AnimatorSet();

        scaleDown.play(scaleDownX).with(scaleDownY);
        moveUp.play(moveUpY);

        scaleDown.start();
        moveUp.start();

        //Code to start timer and take action after the timer ends
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do any action here. Now we are moving to next page
                Intent mySuperIntent = new Intent(SplashActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(SplashActivity.this, R.anim.fade_in, R.anim.fade_out);
                startActivity(mySuperIntent, options.toBundle());
                /* This 'finish()' is for exiting the app when back button pressed
                 *  from Home page which is ActivityHome
                 */
                finish();
            }
        }, SPLASH_TIME);
    }
}
