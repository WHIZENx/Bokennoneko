package com.cmu.project.bokennoneko.Game;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.View;

import com.cmu.project.bokennoneko.MainActivity.MainActivity;
import com.cmu.project.bokennoneko.MainActivity.ScoreActivity;

public class StartGame extends Activity {

    private GameView gameview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();

        // Load the resolution into a Point object
        Point resolution = new Point();
        display.getSize(resolution);

        //set the view for our game
        gameview = new GameView(this, resolution.x, resolution.y);

        // Make our GameView the view for the Activity
        setContentView(gameview);
    }

    // If the Activity is paused make sure to pause our thread
    @Override
    protected void onPause() {
        super.onPause();
        gameview.pause();
    }

    // If the Activity is resumed make sure to resume our thread
    @Override
    protected void onResume() {
        super.onResume();
        gameview.resume();
    }

    @Override
    public void onBackPressed() {
    }
}
