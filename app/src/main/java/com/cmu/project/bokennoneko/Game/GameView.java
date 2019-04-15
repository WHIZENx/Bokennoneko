package com.cmu.project.bokennoneko.Game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;

import com.cmu.project.bokennoneko.R;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {

    ArrayList<Background> backgrounds;

    Handler handler;
    Runnable runnable;

    final int UPDATE_MILLIS = 30;

    Bitmap background;
    Bitmap coin;
    Bitmap bomb;
    Display display;
    Point point;
    int dWidth, dHeight;
    Rect rect;

    Bitmap[] cats;

    int catFrame = 0;
    int velocity = 0, gravity = 10;

    int catX, catY;

    boolean gameState = false;
    boolean onetouch = false;
    int gap = 100;
    int minTubeOffset, maxTubeOffset;
    int numberofTubes = 4;
    int distanceBetteenTube;
    int[] coinX = new int[numberofTubes];
    int[] coinY = new int[numberofTubes];

    int[] bombX = new int[numberofTubes];
    int[] bombY = new int[numberofTubes];

    int tubeVelocity = 50;

    int maxY;

    Random random;

    Paint scorePaint = new Paint();

    int score;

    int getHeight;

    public GameView(Context context) {
        super(context);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        ((Activity)getContext()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.bg1);
        coin = BitmapFactory.decodeResource(getResources(), R.drawable.coin);
        bomb = BitmapFactory.decodeResource(getResources(), R.drawable.bomb);
        display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        point = new Point();
        display.getSize(point);

        dWidth = point.x;
        dHeight = point.y;
        rect = new Rect(0,0,dWidth,dHeight);

        cats = new Bitmap[10];
        cats[0] = BitmapFactory.decodeResource(getResources(), R.drawable.cat1);
        cats[1] = BitmapFactory.decodeResource(getResources(), R.drawable.cat2);
        cats[2] = BitmapFactory.decodeResource(getResources(), R.drawable.cat3);
        cats[3] = BitmapFactory.decodeResource(getResources(), R.drawable.cat4);
        cats[4] = BitmapFactory.decodeResource(getResources(), R.drawable.cat5);
        cats[5] = BitmapFactory.decodeResource(getResources(), R.drawable.cat6);
        cats[6] = BitmapFactory.decodeResource(getResources(), R.drawable.cat7);
        cats[7] = BitmapFactory.decodeResource(getResources(), R.drawable.cat8);
        cats[8] = BitmapFactory.decodeResource(getResources(), R.drawable.cat9);
        cats[9] = BitmapFactory.decodeResource(getResources(), R.drawable.cat10);

        // Initialize our array list
        backgrounds = new ArrayList<>();

        //load the background data into the Background objects and
        // place them in our GameObject arraylist

        backgrounds.add(new Background(
                context,
                dWidth,
                dHeight,
                "skyline",  0, 80, 50));

//        backgrounds.add(new Background(
//                context,
//                dWidth,
//                dHeight,
//                "bg1",  70, 110, 200));

        catX = 0;
        catY = dHeight/2 - cats[0].getHeight()/2;
        catY = dHeight / 100 * 70;
        maxY = catY;

        distanceBetteenTube = dWidth*3/4;
        minTubeOffset = gap/2;
        maxTubeOffset = dHeight - minTubeOffset - gap;

        random = new Random();
        for(int i=0;i<numberofTubes;i++){
            coinX[i] = dWidth + i*distanceBetteenTube;
            coinY[i] = minTubeOffset + random.nextInt(maxTubeOffset - minTubeOffset + 1);

            bombX[i] = dWidth + i*distanceBetteenTube;
            bombY[i] = minTubeOffset + random.nextInt(maxTubeOffset - minTubeOffset + 1);
        }

        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(150);
        scorePaint.setTypeface(Typeface.DEFAULT_BOLD);
        scorePaint.setAntiAlias(true);

        score = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(background,null,rect,null);

        update();

        drawBackground(0, canvas);
//        drawBackground(1, canvas);

        if (!onetouch) {
            if (catFrame == 0) {
                catFrame = 1;
            } else if (catFrame == 1) {
                catFrame = 2;
            } else if (catFrame == 2) {
                catFrame = 3;
            } else if (catFrame == 3) {
                catFrame = 4;
            } else if (catFrame == 4) {
                catFrame = 5;
            } else if (catFrame == 5) {
                catFrame = 6;
            } else if (catFrame == 6) {
                catFrame = 7;
            } else if (catFrame == 7) {
                catFrame = 8;
            } else if (catFrame == 8) {
                catFrame = 9;
            } else if (catFrame == 9) {
                catFrame = 0;
            }
        } else {
            catFrame = 2;
        }

        if (gameState) {
            // Mode: Free Flt
//            if (catY < dHeight - cats[0].getHeight() || velocity < 0) {
//                velocity += gravity;
//                catY += velocity;
//                if (catY <= 0) {
//                    catY = 0;
//                    velocity = 0;
//                    gameState = false;
//                }
//            } else {
//                gameState = false;
//            }
//            if (catY == dWidth/2 - cats[0].getWidth()/2) {
//                gameState = false;
//            }
            // Mode: One Touch to jump over.
            if (catY < maxY || velocity < 0) {
                velocity += gravity;
                catY += velocity;
            } else  {
                catY = maxY;
                onetouch = false;
            }
            for(int i=0;i<numberofTubes;i++) {
                coinX[i] -= tubeVelocity;
                bombX[i] -= tubeVelocity;
                if(coinX[i] < -coin.getWidth()) {
                    coinX[i] += numberofTubes * distanceBetteenTube;
                    coinY[i] = minTubeOffset + random.nextInt(maxTubeOffset - minTubeOffset + 1);
                }
                if(bombX[i] < -bomb.getWidth()) {
                    bombX[i] += numberofTubes * distanceBetteenTube;
                    bombY[i] = minTubeOffset + random.nextInt(maxTubeOffset - minTubeOffset + 1);
                }
                if(hitcoincheck(coinX[i], coinY[i])) {
                    score += 10;
                    coinX[i] = -1000;
                    coinY[i] = -1000;
                }
                if(hitcoincheck(bombX[i], bombY[i])) {
                    gameState = false;
                }
                canvas.drawBitmap(coin, coinX[i], coinY[i], null);
                canvas.drawBitmap(bomb, bombX[i], bombY[i], null);
            }
        }

        canvas.drawBitmap(cats[catFrame],catX, catY,null);
        canvas.drawText("Score: " + score, dWidth/2 - cats[0].getWidth()/2 - 200, 200, scorePaint);

        handler.postDelayed(runnable,UPDATE_MILLIS);
    }

    public boolean hitcoincheck(int x, int y) {

        if (catX < x && x < (catX + cats[0].getWidth()) && catY < y && y < (catY + cats[0].getHeight())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            if (!onetouch) {
                velocity = -100;
                gameState = true;
                onetouch = true;
            }
//            velocity = -100;
//            gameState = true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            Log.i("Touch", "moving: (" + x + ", " + y + ")");
        }

        return true;
    }

    private void drawBackground(int position, Canvas canvas) {

        // Make a copy of the relevant background
        Background bg = backgrounds.get(position);

        // define what portion of images to capture and
        // what coordinates of screen to draw them at

        // For the regular bitmap
        Rect fromRect1 = new Rect(0, 0, bg.width - bg.xClip, bg.height);
        Rect toRect1 = new Rect(bg.xClip, bg.startY, bg.width, bg.endY);

        // For the reversed background
        Rect fromRect2 = new Rect(bg.width - bg.xClip, 0, bg.width, bg.height);
        Rect toRect2 = new Rect(0, bg.startY, bg.xClip, bg.endY);

        //draw the two background bitmaps
        if (!bg.reversedFirst) {
            canvas.drawBitmap(bg.bitmap, fromRect1, toRect1, scorePaint);
            canvas.drawBitmap(bg.bitmapReversed, fromRect2, toRect2, scorePaint);
        } else {
            canvas.drawBitmap(bg.bitmap, fromRect2, toRect2, scorePaint);
            canvas.drawBitmap(bg.bitmapReversed, fromRect1, toRect1, scorePaint);
        }

    }

    private void update() {
        // Update all the background positions
        for (Background bg : backgrounds) {
            bg.update(UPDATE_MILLIS);
        }

    }

}
