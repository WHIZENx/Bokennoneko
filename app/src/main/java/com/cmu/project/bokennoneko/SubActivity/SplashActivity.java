package com.cmu.project.bokennoneko.SubActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.cmu.project.bokennoneko.R;

public class SplashActivity extends AppCompatActivity {

    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        img = findViewById(R.id.img);

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(img, "scaleX", 0.1f, 1.0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(img, "scaleY", 0.1f, 1.0f);
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
    }
}
