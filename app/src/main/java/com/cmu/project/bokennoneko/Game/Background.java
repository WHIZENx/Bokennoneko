package com.cmu.project.bokennoneko.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class Background {

    Bitmap bitmap;
    Bitmap bitmapReversed;

    int width;
    int height;
    boolean reversedFirst;
    float speed;

    int xClip;
    int startY;
    int endY;

    public Background(Context context, int screenWidth, int screenHeight, String bitmapName, int sY, int eY, float s){

        // Make a resource id out of the string of the file name
        int resID = context.getResources().getIdentifier(bitmapName,
                "drawable", context.getPackageName());

        // Load the bitmap using the id
        bitmap = BitmapFactory.decodeResource(context.getResources(), resID);

        // Which version of background (reversed or regular)
        // is currently drawn first (on left)
        reversedFirst = false;

        //Initialise animation variables.

        // Where to clip the bitmaps
        // Starting at the first pixel
        xClip = 0;

        //Position the background vertically
        startY = sY * (screenHeight / 100);
        endY = eY * (screenHeight / 100);
        speed = s;

        // Create the bitmap
        bitmap = Bitmap.createScaledBitmap(bitmap, screenWidth,
                (endY - startY)
                , true);

        // Save the width and height for later use
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        //Create a mirror image of the background (horizontal flip)
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        bitmapReversed = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

    }

    public void update(long fps){

        // Move the clipping position and reverse if necessary
        xClip -= speed/fps;
        if (xClip >= width) {
            xClip = 0;
            reversedFirst = !reversedFirst;
        } else if (xClip <= 0) {
            xClip = width;
            reversedFirst = !reversedFirst;

        }
    }

    public int getHeight(Context context, String bitmapName) {

        // Make a resource id out of the string of the file name
        int resID = context.getResources().getIdentifier(bitmapName,
                "drawable", context.getPackageName());

        // Load the bitmap using the id
        bitmap = BitmapFactory.decodeResource(context.getResources(), resID);

        return bitmap.getHeight();
    }
}
