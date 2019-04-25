package com.cmu.project.bokennoneko.Game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;
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
    Bitmap[] cats_hurt;
    Bitmap[] click;

    Bitmap fish;
    Bitmap bomb;
    Bitmap pause;

    Bitmap catdead;
    Bitmap home;
    Bitmap restart;
    Bitmap resume;


    Bitmap health1, health2, health3;

    Paint scorePaint = new Paint();
    Paint pausePaint = new Paint();
    Paint overPaint = new Paint();
    Paint catPaint = new Paint();

    int catFrame = 0;
    int clickFrame = 0;
    int velocity = 0, gravity = 1;

    int catX, catY;

    boolean gameState = false;
    boolean pregameState = false;
    boolean gameOver = false;
    boolean pregameOver = false;
    boolean addfish = false;
    boolean gamePause = false;
    boolean pregamePause = false;
    boolean postgamOver = false;

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

    int clickX, clickY;

    boolean hasMenuKey = ViewConfiguration.get(((Activity)getContext())).hasPermanentMenuKey();
    boolean hasBackkey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

    int enviny = 0;

    boolean savescore = false;

    GameView(Context context, int screenWidth, int screenHeight) {
        super(context);

        this.context = context;
        ((Activity)getContext()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (hasBackkey && hasMenuKey) {
            enviny = 102;
        }
        else{
            View decorView = ((Activity)getContext()).getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
            enviny = 110;
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
                "bg1",  0, enviny, 70));

        backgrounds.add(new Background(
                this.context,
                screenWidth,
                screenHeight,
                "bg2",  0, enviny, 70));

        backgrounds.add(new Background(
                this.context,
                screenWidth,
                screenHeight,
                "bg3",  0, enviny, 70));

        backgrounds.add(new Background(
                this.context,
                screenWidth,
                screenHeight,
                "bg4",  0, enviny, 0));

        // Add more sources here
        fish = BitmapFactory.decodeResource(getResources(), R.drawable.fish);
        bomb = BitmapFactory.decodeResource(getResources(), R.drawable.bomb);
        pause = BitmapFactory.decodeResource(getResources(), R.drawable.pause);

        catdead = BitmapFactory.decodeResource(getResources(), R.drawable.catdead);
        home = BitmapFactory.decodeResource(getResources(), R.drawable.home);
        restart = BitmapFactory.decodeResource(getResources(), R.drawable.restart);
        resume = BitmapFactory.decodeResource(getResources(), R.drawable.resume);

        cats = new Bitmap[4];
        cats[0] = BitmapFactory.decodeResource(getResources(), R.drawable.cat1);
        cats[1] = BitmapFactory.decodeResource(getResources(), R.drawable.cat2);
        cats[2] = BitmapFactory.decodeResource(getResources(), R.drawable.cat3);
        cats[3] = BitmapFactory.decodeResource(getResources(), R.drawable.cat4);

        cats_hurt = new Bitmap[4];
        cats_hurt[0] = BitmapFactory.decodeResource(getResources(), R.drawable.cat1_hurt);
        cats_hurt[1] = BitmapFactory.decodeResource(getResources(), R.drawable.cat2_hurt);
        cats_hurt[2] = BitmapFactory.decodeResource(getResources(), R.drawable.cat3_hurt);
        cats_hurt[3] = BitmapFactory.decodeResource(getResources(), R.drawable.cat4_hurt);

        health1 = BitmapFactory.decodeResource(getResources(), R.drawable.cat2);
        health2 = BitmapFactory.decodeResource(getResources(), R.drawable.cat1);
        health3 = BitmapFactory.decodeResource(getResources(), R.drawable.cat4);

        click = new Bitmap[2];
        click[0] = BitmapFactory.decodeResource(getResources(), R.drawable.click1);
        click[1] = BitmapFactory.decodeResource(getResources(), R.drawable.click2);

        catX = cats[0].getWidth();
        catY = -cats[0].getHeight();
        maxY = (screenHeight / 100 * 55) - cats[0].getHeight();
        centerWidth = screenWidth / 2;
        centerHeight = screenHeight / 2;

        clickX = centerWidth - click[0].getWidth()/2;
        clickY = centerHeight - click[0].getHeight()/2;

        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(80);
        Typeface plain = Typeface.createFromAsset(getContext().getAssets(), "fonts/ARCADECLASSIC.TTF");
        Typeface bold = Typeface.create(plain, Typeface.NORMAL);
        scorePaint.setTypeface(bold);
        scorePaint.getTextBounds("Score: "+score, 0, ("Score: "+score).length(), rect);

        pausePaint.setColor(Color.WHITE);
        pausePaint.setTextSize(200);
        pausePaint.setTypeface(bold);

        overPaint.setColor(Color.BLACK);
        overPaint.setTextSize(200);
        overPaint.setTypeface(bold);

        health1X = rect.width() + 300;
        health1Y = 30;
        health2X = rect.width() + 300 + health1.getWidth() + 10;
        health2Y = 30;
        health3X = rect.width() + 300 + health1.getWidth()*2 +10;
        health3Y = 30;

        random = new Random();
        for(int i=0;i<numberofBombs;i++){
            bombX[i] = screenWidth + random.nextInt(1000) + bomb.getHeight();
            bombY[i] = random.nextInt(screenHeight - bomb.getHeight());
        }

        fishX = screenWidth + fish.getWidth();
        fishY = random.nextInt(screenHeight - fish.getHeight());
    }

    @Override
    public void run() {

        while (running) {
            long startFrameTime = System.currentTimeMillis();

            if (!gamePause && !postgamOver) {
                update();
                draw();
            }

            // Calculate the fps this frame
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }

        }

    }

    public boolean CheckHitItem(int scalex, int scaley, int x, int y, int width, int height) {

        if (scalex < x && x < (scalex + cats[0].getWidth()) && scaley < y && y < (scaley + cats[0].getHeight()) ||
                scalex < x + width && x + width < (scalex + cats[0].getWidth()) && scaley < y + height && y + height < (scaley + cats[0].getHeight())){
            return true;
        } else {
            return false;
        }
    }

    public boolean Checkclickitem(int scalex, int scaley, int width, int height, int x, int y) {

        if (scalex <= x && x <= (scalex + width) && scaley <= y && y <= (scaley + height)) {
            return true;
        }
        return false;
    }

    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent e) {
            if(!pregameOver) {
                longtouch = true;
            }
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
            if(!pregameOver) {
                if (!gameState) {
                    if (!Checkclickitem(screenWidth - pause.getWidth() - 20,
                            20,
                            pause.getWidth(),
                            pause.getHeight(),
                            x, y)) {
                        pregameState = true;
                        velocity = -20;
                    }
                } else {
                    if (!gamePause) {
                        if (Checkclickitem(screenWidth - pause.getWidth() - 20,
                                20,
                                pause.getWidth(),
                                pause.getHeight(),
                                x, y)) {
                            pregamePause = true;
                        }
                    } else {
                        if (Checkclickitem(centerWidth - resume.getWidth()/2,
                                centerHeight,
                                resume.getWidth(),
                                resume.getHeight(),
                                x, y)) {
                            pregamePause = false;
                            gamePause =false;
                        }
                        if (Checkclickitem(centerWidth - restart.getWidth()/2,
                                centerHeight + resume.getHeight() + 50,
                                restart.getWidth(),
                                restart.getHeight(),
                                x, y)) {
                            pause();
                            Intent intent = new Intent(getContext(), StartGame.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            getContext().startActivity(intent);
                            ((Activity) getContext()).overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                            ((Activity) getContext()).finish();
                        }
                        if (Checkclickitem(centerWidth - home.getWidth()/2,
                                centerHeight + resume.getHeight() + 50 + home.getHeight() + 50,
                                home.getWidth(),
                                home.getHeight(),
                                x, y)) {
                            pause();
                            Intent intent = new Intent(getContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            getContext().startActivity(intent);
                            ((Activity) getContext()).overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                            ((Activity) getContext()).finish();
                        }
                    }
                }
                if (!gameOver) {
                    longtouch = true;
                }
            } else {
                longtouch = false;
                if (gameOver) {
                    if (Checkclickitem(centerWidth - home.getWidth() / 2,
                            centerHeight + 100,
                            home.getWidth(),
                            home.getHeight(),
                            x, y)) {
                        pause();
                        Intent intent = new Intent(getContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getContext().startActivity(intent);
                        ((Activity) getContext()).overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                        ((Activity) getContext()).finish();
                    }
                    if (Checkclickitem(centerWidth - restart.getWidth() / 2,
                            centerHeight + 100 + home.getHeight() + 50,
                            restart.getWidth(),
                            restart.getHeight(),
                            x, y)) {
                        pause();
                        Intent intent = new Intent(getContext(), StartGame.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getContext().startActivity(intent);
                        ((Activity) getContext()).overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                        ((Activity) getContext()).finish();
                    }
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
    int framerate = 0;
    int savecatY = 0;
    int time_hurt = 0;
    boolean isHurt = false;
    boolean godmode = false;
    int alpha_mode = 0;
    int alpha = 255;
    int time_god_mode = 0;
    private void draw() {

        if (ourHolder.getSurface().isValid()) {
            //First we lock the area of memory we will be drawing to
            canvas = ourHolder.lockCanvas();

//            canvas.drawColor(Color.argb(255,  35, 94, 14));

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

                if(!gameState && !pregameState) {
                    if (framerate <= 20) {
                        clickFrame = 0;
                        framerate += 1;
                    } else if (framerate < 40) {
                        clickFrame = 1;
                        framerate += 1;
                    } else {
                        framerate = 0;
                    }
                    canvas.drawBitmap(click[clickFrame], clickX, clickY, paint);
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

                if (health >= 1) {
                    canvas.drawBitmap(health1, health1X, health1Y, paint);
                }
                if (health >= 2) {
                    canvas.drawBitmap(health2, health2X, health2Y, paint);
                }
                if (health == 3) {
                    canvas.drawBitmap(health3, health3X, health3Y, paint);
                }

                if(pregamePause){
                    paint.setAlpha(100);
                    catPaint.setAlpha(100);
                } else {
                    paint.setAlpha(255);
                    catPaint.setAlpha(255);
                    canvas.drawBitmap(pause, screenWidth - pause.getWidth() - 20, 20, paint);
                }

                if(godmode && health > 0) {
                    if (alpha_mode == 0) {
                        alpha -= 10;
                        if (alpha <= 50) {
                            alpha = 50;
                            alpha_mode = 1;
                        }
                    } else if (alpha_mode == 1) {
                        alpha += 10;
                        if (alpha >= 255) {
                            alpha = 255;
                            alpha_mode = 0;
                        }
                    }
                    catPaint.setAlpha(alpha);

                    time_god_mode += 1;
                    if (time_god_mode == 150) {
                        godmode = false;
                        isHurt = false;
                        time_hurt = 0;
                        time_god_mode = 0;
                    }
                }

                if (pregameState) {
                    if (catFrame == 0) {
                        catFrame = 1;
                    } else if (catFrame == 1) {
                        catFrame = 2;
                    } else if (catFrame == 2) {
                        catFrame = 3;
                    } else if (catFrame == 3) {
                        catFrame = 0;
                    }
                    if (!isHurt || godmode) {
                        canvas.drawBitmap(cats[catFrame], catX, catY, catPaint);
                    } else {
                        canvas.drawBitmap(cats_hurt[catFrame], catX, catY, catPaint);
                    }
                }

                if (gameState) {

                    if (isHurt) {
                        time_hurt += 1;
                        if (time_hurt == 10) {
                            godmode = true;
                        }
                    }

                    if (score == add_bomb + 100) {
                        addbomb += 1;
                        add_bomb = score;
                    }

                    speedGame += 1;
                    if (speedGame == 500) {
                        tubeVelocity += 1;
                        speedGame = 0;
                    }
                    if(!pregameOver) {
                        ck += 1;
                        if (ck == 20) {
                            score += 1;
                            ck = 0;
                        }
                    }
                    // Mode: Free Fly
                    if (!longtouch) {
                        velocity += gravity;
                        catY += velocity;
                    } else {
                        catY -= 20;
                        velocity = 0;
                    }
                    if (catY <= 0) {
                        catY = 0;
                        velocity = 0;
                    }
                    if (!pregameOver) {
                        if (catY > screenHeight - cats[0].getHeight()) {
                            catY = screenHeight - cats[0].getHeight();
                        }
                    } else {
                        if (catY > screenHeight - cats[0].getHeight()) {
                            gameOver = true;
                            gameState = false;
                        }
                    }
                    if (health < 3) {
                        if (!addfish) {
                            int predict = random.nextInt(1000);

                            if (predict == 1) {
                                fishX = screenWidth + fish.getWidth();
                                if (hasBackkey && hasMenuKey) {
                                    fishY = random.nextInt(screenHeight - fish.getHeight());
                                } else {
                                    fishY = random.nextInt(screenHeight);
                                }
                                addfish = true;
                            }
                        }
                        if (addfish) {
                            fishX -= tubeVelocity;
                            if (fishX < -fish.getWidth()) {
                                addfish = false;
                            }
                            if (fishX >= -fish.getWidth()) {
                                canvas.drawBitmap(fish, fishX, fishY, paint);
                            }
                        }
                    }
                    for (int i = 0; i < numberofBombs; i++) {
                        bombX[i] -= tubeVelocity;
                        if (bombX[i] < -bomb.getWidth()) {
                            bombX[i] = screenWidth + random.nextInt(1000) + bomb.getWidth();
                            bombY[i] = random.nextInt(screenHeight - bomb.getHeight());
                        }
                        if (CheckHitItem(catX, catY, fishX, fishY, fish.getWidth(), fish.getHeight()) && !pregameOver) { // Fish
                            fishX = -1000;
                            fishY = -1000;
                            if (health < 3) {
                                health += 1;
                            }
                        }
                        if (CheckHitItem(catX, catY, bombX[i], bombY[i], bomb.getWidth(), bomb.getHeight()) && !pregameOver) {
                            if (!godmode && !isHurt) {
                                if (health > 0) {
                                    isHurt = true;
                                }
                                bombX[i] = -1000;
                                bombY[i] = -1000;
                                if (health > 0) {
                                    health -= 1;
                                }
                                if (health == 0) {
                                    savecatY = catY;
                                    velocity = -20;
                                    pregameOver = true;
                                }
                                if (pregameOver) {
                                    longtouch = false;
                                    if (catY <= savecatY - 20) {
                                        catY -= 20;
                                    } else {
                                        velocity += gravity;
                                        catY += velocity;
                                    }
                                }
                            }
                        }
                        if (bombX[i] >= -bomb.getWidth()) {
                            canvas.drawBitmap(bomb, bombX[i], bombY[i], paint);
                        }
                    }

                    if (catFrame == 0) {
                        catFrame = 1;
                    } else if (catFrame == 1) {
                        catFrame = 2;
                    } else if (catFrame == 2) {
                        catFrame = 3;
                    } else if (catFrame == 3) {
                        catFrame = 0;
                    }
                    if (!isHurt || godmode) {
                        canvas.drawBitmap(cats[catFrame], catX, catY, catPaint);
                    } else {
                        canvas.drawBitmap(cats_hurt[catFrame], catX, catY, catPaint);
                    }

                }

                if(pregamePause){
                    pausePaint.getTextBounds("PAUSE", 0, "PAUSE".length(), rect);
                    canvas.drawText("PAUSE", centerWidth - rect.width()/2,
                            centerHeight - rect.height()*3 ,
                            pausePaint);

                    canvas.drawBitmap(resume, centerWidth - resume.getWidth()/2, centerHeight, pausePaint);
                    canvas.drawBitmap(restart, centerWidth - home.getWidth()/2, centerHeight + resume.getHeight() + 50, pausePaint);
                    canvas.drawBitmap(home, centerWidth - restart.getWidth()/2, centerHeight + resume.getHeight() + 50 + home.getHeight() + 50, pausePaint);
                    pregamePause = false;
                    gamePause = true;
                }

                } else {
                if (!savescore) {
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
                                                savescore = true;
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
                                            savescore = true;
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

                drawBackground(3);

                overPaint.getTextBounds("Score: "+score, 0, ("Score: "+score).length(), rect);
                canvas.drawBitmap(catdead, centerWidth - catdead.getWidth()/2, centerHeight - catdead.getHeight()*2, overPaint);
                canvas.drawText("Score: "+score, centerWidth - rect.width()/2,
                        centerHeight ,
                        overPaint);

                canvas.drawBitmap(home, centerWidth - home.getWidth()/2, centerHeight + 100, overPaint);
                canvas.drawBitmap(restart, centerWidth - restart.getWidth()/2, centerHeight + 100 + home.getHeight() + 50, overPaint);

                postgamOver = true;
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
