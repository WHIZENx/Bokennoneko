package com.cmu.project.bokennoneko.Game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.cmu.project.bokennoneko.MainActivity.MainActivity;
import com.cmu.project.bokennoneko.Model.Score;
import com.cmu.project.bokennoneko.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    ArrayList<Background> backgrounds;

    private volatile boolean running;
    private Thread gameThread = null;

    // For drawing
    private Paint paint;
    private Canvas canvas;
    private Rect rect;
    private SurfaceHolder ourHolder;

    // Holds a reference to the Activity
    Context context;

    // Control the fps
    long fps =60;

    // Screen resolution
    int screenWidth;
    int screenHeight;

    Bitmap[] cats;

    Bitmap fish;
    Bitmap bomb;
    Bitmap pause;

    Bitmap catdead;
    Bitmap home;
    Bitmap restart;

    Bitmap health1, health2, health3;

    Paint scorePaint = new Paint();

    int catFrame = 0;
    int velocity = 0, gravity = 1;

    int catX, catY;

    boolean gameState = false;
    boolean pregameState = false;
    boolean gameOver = false;
    boolean addfish = false;

    Random random;
    int numberofBombs = 8;
    int fishX;
    int fishY;

    int[] bombX = new int[numberofBombs];
    int[] bombY = new int[numberofBombs];

    int tubeVelocity = 5;

    int maxY;
    int score = 0;
    int speedGame = 1;
    int addbomb = 0;

    DatabaseReference ref;
    FirebaseAuth mAuth;
    FirebaseUser curUser;

    int centerWidth, centerHeight;

    int health1X, health1Y;
    int health2X, health2Y;
    int health3X, health3Y;

    GameView(Context context, int screenWidth, int screenHeight) {
        super(context);

        this.context = context;
        if (Build.VERSION.SDK_INT < 16) {
            ((Activity)getContext()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else{
            ((Activity)getContext()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            View decorView = ((Activity)getContext()).getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();

        ref = FirebaseDatabase.getInstance().getReference("Scores").child(curUser.getUid());

        // Initialize our drawing objects
        ourHolder = getHolder();
        paint = new Paint();
        rect = new Rect(0,0,screenWidth,screenHeight);

        // Initialize our array list
        backgrounds = new ArrayList<>();

        //load the background data into the Background objects and
        // place them in our GameObject arraylist

        backgrounds.add(new Background(
                this.context,
                screenWidth,
                screenHeight,
                "bg1",  0, 110, 70));

        backgrounds.add(new Background(
                this.context,
                screenWidth,
                screenHeight,
                "bg2",  0, 110, 70));

        backgrounds.add(new Background(
                this.context,
                screenWidth,
                screenHeight,
                "bg3",  0, 110, 70));

        backgrounds.add(new Background(
                this.context,
                screenWidth,
                screenHeight,
                "bg4",  0, 110, 0));

        // Add more sources here
        fish = BitmapFactory.decodeResource(getResources(), R.drawable.fish);
        bomb = BitmapFactory.decodeResource(getResources(), R.drawable.bomb);
        pause = BitmapFactory.decodeResource(getResources(), R.drawable.pause);

        catdead = BitmapFactory.decodeResource(getResources(), R.drawable.catdead);
        home = BitmapFactory.decodeResource(getResources(), R.drawable.home);
        restart = BitmapFactory.decodeResource(getResources(), R.drawable.restart);

        cats = new Bitmap[4];
        cats[0] = BitmapFactory.decodeResource(getResources(), R.drawable.cat1);
        cats[1] = BitmapFactory.decodeResource(getResources(), R.drawable.cat2);
        cats[2] = BitmapFactory.decodeResource(getResources(), R.drawable.cat3);
        cats[3] = BitmapFactory.decodeResource(getResources(), R.drawable.cat4);

        health1 = BitmapFactory.decodeResource(getResources(), R.drawable.cat2);
        health2 = BitmapFactory.decodeResource(getResources(), R.drawable.cat1);
        health3 = BitmapFactory.decodeResource(getResources(), R.drawable.cat4);

        catX = cats[0].getWidth();
        catY = -cats[0].getHeight();
        maxY = (screenHeight / 100 * 55) - cats[0].getHeight();
        centerWidth = screenWidth / 2;
        centerHeight = screenHeight / 2;

        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(80);
        Typeface plain = Typeface.createFromAsset(getContext().getAssets(), "fonts/ARCADECLASSIC.TTF");
        Typeface bold = Typeface.create(plain, Typeface.NORMAL);
        scorePaint.setTypeface(bold);
        scorePaint.getTextBounds("Score: "+score, 0, ("Score: "+score).length(), rect);
        health1X = rect.width() + 300;
        health1Y = 30;
        health2X = rect.width() + 300 + health1.getWidth() + 10;
        health2Y = 30;
        health3X = rect.width() + 300 + health1.getWidth()*2 +10;
        health3Y = 30;

        random = new Random();
        fishX = screenWidth + fish.getWidth();
        fishY = random.nextInt(screenHeight - fish.getHeight());
        for(int i=0;i<numberofBombs;i++){
            bombX[i] = screenWidth + random.nextInt(1000);
            bombY[i] = random.nextInt(screenHeight - bomb.getHeight());
        }
    }

    @Override
    public void run() {

        while (running) {
            long startFrameTime = System.currentTimeMillis();

            update();

            draw();

            // Calculate the fps this frame
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }

    }

    public boolean CheckHitItem(int x, int y) {

        if (catX <= x && x <= (catX + cats[0].getWidth()) && catY <= y && y <= (catY + cats[0].getHeight())) {
            return true;
        }
        return false;
    }

    public boolean Checkclickitem(int scalex, int scaley, int width, int height, int x, int y) {

        if (scalex <= x && x <= (scalex + width) && scaley <= y && y <= (scaley + height)) {
            return true;
        }
        return false;
    }

    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent e) {
            longtouch = true;
        }
    });

    boolean longtouch = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();

        gestureDetector.onTouchEvent(event);
        if (action == MotionEvent.ACTION_DOWN) {
            if(!gameState) {
                pregameState = true;
                velocity = -20;
            }
            if (!gameOver) {
                longtouch = true;
            } else {
                if (Checkclickitem(centerWidth - home.getWidth()/2,
                        centerHeight + 100,
                        home.getWidth(),
                        home.getHeight(),
                        x, y)) {
                    Intent intent = new Intent(getContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getContext().startActivity(intent );
                    ((Activity)getContext()).finish();
                }
                if (Checkclickitem(centerWidth - restart.getWidth()/2,
                        centerHeight + 100 + home.getHeight() + 50,
                        restart.getWidth(),
                        restart.getHeight(),
                        x, y)) {
                    Intent intent = new Intent(getContext(), StartGame.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getContext().startActivity(intent );
                    ((Activity)getContext()).finish();
                }
            }
        }
        if (action == MotionEvent.ACTION_UP) {
            longtouch = false;
        }

        return true;
    }

    private void update() {
        // Update all the background positions
        for (Background bg : backgrounds) {
            bg.update(fps);
        }

    }

    int ck = 0;
    int c_bg = 0;
    int keep_c = score;
    int add_bomb = score;
    int health = 3;
    private void draw() {

        if (ourHolder.getSurface().isValid()) {
            //First we lock the area of memory we will be drawing to
            canvas = ourHolder.lockCanvas();

            if(!gameOver) {
                // Draw the background parallax
                if (gameState) {
                    if (score == keep_c + 250) {
                        c_bg += 1;
                        keep_c = score;
                    }
                }
                drawBackground(c_bg % 3);

                // Draw the score of the game
                canvas.drawText("Score: " + score, 50, 100, scorePaint);

                canvas.drawBitmap(pause, screenWidth - pause.getWidth() - 50, 50, paint);

                if (pregameState || gameState) {
                    if (catFrame == 0) {
                        catFrame = 1;
                    } else if (catFrame == 1) {
                        catFrame = 2;
                    } else if (catFrame == 2) {
                        catFrame = 3;
                    } else if (catFrame == 3) {
                        catFrame = 0;
                    }
                }

                // Draw the foreground parallax
                if (pregameState) {
                    if (catY >= screenHeight / 2 - cats[0].getHeight() / 2) {
                        catY = screenHeight / 2 - cats[0].getHeight() / 2;
                        gameState = true;
                        pregameState = false;
                    } else {
                        catY += 30;
                    }
                }
                if (gameState) {

                    if (score == add_bomb + 100) {
                        addbomb += 1;
                        add_bomb = score;
                    }

                    speedGame += 1;
                    if (speedGame == 500) {
                        tubeVelocity += 1;
                        speedGame = 0;
                    }
                    ck += 1;
                    if (ck == 20) {
                        score += 1;
                        ck = 0;
                    }
                    // Mode: Free Fly
                    if (!longtouch) {
                        velocity += gravity;
                        catY += velocity;
                    } else {
                        catY -= 40;
                        velocity = 0;
                    }
                    if (catY <= 0) {
                        catY = 0;
                        velocity = 0;
                    }
                    if (catY >= screenHeight - cats[0].getHeight()) {
                        catY = screenHeight - cats[0].getHeight();
                    }
                    if (health != 3) {
                        if (!addfish) {
                            int predict = random.nextInt(1000);

                            if (predict == 1) {
                                fishX = screenWidth + fish.getWidth();
                                fishY = random.nextInt(screenHeight - fish.getHeight());
                                addfish = true;
                            }
                        }
                        if (addfish) {
                            fishX -= tubeVelocity;
                            if (fishX < -fish.getWidth()) {
                                addfish = false;
                            }
                            canvas.drawBitmap(fish, fishX, fishY, paint);
                        }
                    }
                    for (int i = 0; i < numberofBombs; i++) {
                        bombX[i] -= tubeVelocity;
                        if (bombX[i] < -bomb.getWidth()) {
                            bombX[i] = screenWidth + random.nextInt(1000);
                            bombY[i] = random.nextInt(screenHeight - bomb.getHeight());
                        }
                        if (CheckHitItem(fishX, fishY)) {
                            fishX = -1000;
                            fishY = -1000;
                            if (health < 3) {
                                if (health == 1) {
                                    health2X = rect.width() + 300 + health1.getWidth() + 10;
                                    health2Y = 30;
                                } else if (health == 2) {
                                    health3X = rect.width() + 300 + health1.getWidth()*2 +10;
                                    health3Y = 30;
                                }
                                health += 1;
                            }
                        }
                        if (CheckHitItem(bombX[i], bombY[i])) {
                            bombX[i] = -1000;
                            bombY[i] = -1000;
                            if (health > 0) {
                                if (health == 3) {
                                    health3X = -1000;
                                    health3Y = -1000;
                                } else if (health == 2) {
                                    health2X = -1000;
                                    health2Y = -1000;
                                } else {
                                    health1X = -1000;
                                    health1Y = -1000;
                                }
                                health -= 1;
                            }
                            if (health == 0) {
                                gameOver = true;
                                gameState = false;
                            }
                            if (gameOver) {
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Score scores = dataSnapshot.getValue(Score.class);
                                        if (dataSnapshot.exists()) {
                                            if (scores.getMaxscore() < score) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("id", curUser.getUid());
                                                hashMap.put("maxscore", score);
                                                ref.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("id", curUser.getUid());
                                            hashMap.put("maxscore", score);
                                            ref.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                        canvas.drawBitmap(bomb, bombX[i], bombY[i], paint);
                    }
                }

                canvas.drawBitmap(cats[catFrame], catX, catY, paint);

                canvas.drawBitmap(health1, health1X, health1Y, paint);
                canvas.drawBitmap(health2, health2X, health2Y, paint);
                canvas.drawBitmap(health3, health3X, health3Y, paint);
            } else {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                drawBackground(3);

                scorePaint.getTextBounds("Score: "+score, 0, ("Score: "+score).length(), rect);
                canvas.drawBitmap(catdead, centerWidth - catdead.getWidth()/2, centerHeight - catdead.getHeight()*2, paint);

                scorePaint.setColor(Color.BLACK);
                scorePaint.setTextSize(200);
                canvas.drawText("Score: "+score, centerWidth - rect.width()/2,
                        centerHeight ,
                        scorePaint);

                canvas.drawBitmap(home, centerWidth - home.getWidth()/2, centerHeight + 100, paint);
                canvas.drawBitmap(restart, centerWidth - restart.getWidth()/2, centerHeight + 100 + home.getHeight() + 50, paint);
            }

            // Unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawBackground(int position) {

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
            canvas.drawBitmap(bg.bitmap, fromRect1, toRect1, paint);
            canvas.drawBitmap(bg.bitmapReversed, fromRect2, toRect2, paint);
        } else {
            canvas.drawBitmap(bg.bitmap, fromRect2, toRect2, paint);
            canvas.drawBitmap(bg.bitmapReversed, fromRect1, toRect1, paint);
        }

    }

    // Clean up our thread if the game is stopped
    public void pause() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    // Make a new thread and start it
    // Execution moves to our run method
    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
