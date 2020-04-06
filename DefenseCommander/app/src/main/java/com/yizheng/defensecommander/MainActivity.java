package com.yizheng.defensecommander;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import static com.yizheng.defensecommander.Interceptor.INTERCEPTOR_BLAST;
import static com.yizheng.defensecommander.Missile.MISSILE_BLAST;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final String TAG = "MainActivity";

    private ConstraintLayout layout;
    private TextView score, level;
    static int screenHeight, screenWidth;
    private GestureDetectorCompat mDetector;

    private ArrayList<Base> activeBases = new ArrayList<>();
    private int numBases = 3;

    private MissileMaker missileMaker;

    private Integer scoreValue = 0;
    private Integer levelValue = 1;

    //private ImageView gameover;
    private ImageView titleImage;

    //extra
    ArrayList<Interceptor> interceptors = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.layout);
        score = findViewById(R.id.score);
        level = findViewById(R.id.level);
        //gameover = findViewById(R.id.imageView);
        //gameover.setVisibility(View.INVISIBLE);

        setupFullScreen();
        getScreenDimensions();

        mDetector = new GestureDetectorCompat(this, this);

        doTitle();

        SoundPlayer.getInstance().setupSound(this, "background", R.raw.background,true);
        SoundPlayer.getInstance().setupSound(this, "base_blast", R.raw.base_blast,false);
        SoundPlayer.getInstance().setupSound(this, "interceptor_blast", R.raw.interceptor_blast,false);
        SoundPlayer.getInstance().setupSound(this, "interceptor_hit_missile", R.raw.interceptor_hit_missile,false);
        SoundPlayer.getInstance().setupSound(this, "launch_interceptor", R.raw.launch_interceptor,false);
        SoundPlayer.getInstance().setupSound(this, "launch_missile", R.raw.launch_missile,false);

        new ScrollingBackground(this,
                layout, R.drawable.clouds, 10000);

        makeBases();

        missileMaker = new MissileMaker(this, screenWidth, screenHeight);
        new Thread(missileMaker).start();

    }

    private void doTitle() {
        titleImage = new ImageView(this);
        titleImage.setImageResource(R.drawable.title);
        float centerX = (float) screenWidth/2;
        float centerY = (float) screenHeight/2;
        int offsetH = (int) (titleImage.getDrawable().getIntrinsicHeight()*0.5);
        int offsetW = (int) (titleImage.getDrawable().getIntrinsicWidth()*0.5);
        titleImage.setX(centerX-offsetW);
        titleImage.setY(centerY-offsetH);
        layout.addView(titleImage);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(titleImage, "alpha", 0.0f, 1.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.start();
    }

    public ConstraintLayout getLayout() {
        return layout;
    }

    public void makeBases() {
        for (int i=1; i<=numBases; i++){
            int resId = R.drawable.base;
            float x = (float)screenWidth*i/(numBases+1);
            Base base = new Base(screenHeight, screenWidth, this, x, resId);
            activeBases.add(base);
        }
    }

//    static void doSound(String id){
//
////        while (checkNotReady()) {
////            try {
////                Thread.sleep(1);
////            } catch (Exception e){
////                e.printStackTrace();
////            }
////        }
//
////        if (checkNotReady())
////            return;
//
//        SoundPlayer.getInstance().start(id);
//    }

//    private boolean checkNotReady() {
//        if (SoundPlayer.loadCount != SoundPlayer.doneCount) {
//            String msg = String.format(Locale.getDefault(),
//                    "Sound loading not complete (%d of %d),\n" +
//                            "Please try again in a moment",
//                    SoundPlayer.doneCount, SoundPlayer.loadCount);
//            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
//            return true;
//        }
//        return false;
//    }

    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }


    private void setupFullScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDetector.onTouchEvent(event)){
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        handleTap(e.getX(), e.getY());
        return false;
    }

    private void handleTap(float x, float y) {
        if (!activeBases.isEmpty()) {
            double minDis = Double.MAX_VALUE;
            Base launchBase = activeBases.get(0);
            for (Base b : activeBases) {
                Double dis;
                if ((dis = Utilities.distance(x, y, b.getX(), b.getY())) < minDis) {
                    minDis = dis;
                    launchBase = b;
                }
            }
            launchInterceptor(launchBase, x, y);
        }
    }

    private void launchInterceptor(Base launchBase, float x, float y) {
//        //extra
//        Log.d(TAG, "launchInterceptor: "+missileMaker.mSize());
//        if (missileMaker.mSize() >= 3){
//            Log.d(TAG, "launchInterceptor: "+missileMaker.mSize());
//            return;
//        }

        if (interceptors.size() >= 3){
            return;
        }

        Interceptor i = new Interceptor(this, launchBase.getX(), x, y);

        //extra
        interceptors.add(i);

        SoundPlayer.getInstance().start("launch_interceptor");
        i.launch();
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    void setLevel(final int current_level) {
        this.levelValue = current_level;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                level.setText(String.format(Locale.getDefault(), "Level: %d", current_level));
            }
        });

    }

    //extra
    void reduceInterceptorCount(Interceptor i){
        interceptors.remove(i);
    }

    public void applyInterceptorBlast(Interceptor interceptor, int id) {
        missileMaker.applyInterceptorBlast(interceptor, id);
    }

    //extra
    void applyInterceptorBlastForBase(Interceptor i){
        if (!activeBases.isEmpty()) {
            float x1 = i.getX();
            float y1 = i.getY();

            ArrayList<Base> gone = new ArrayList<>();
            ArrayList<Base> temp = new ArrayList<>(activeBases);

            for (Base b : temp) {
                float x2 = (int) (b.getX());
                float y2 = (int) (b.getY());

                float dist = (float) (Utilities.distance(x1, y1, x2, y2));
                if (dist < INTERCEPTOR_BLAST) {

                    SoundPlayer.getInstance().start("base_blast");
                    b.interceptorBlast();
                    gone.add(b);
                }
            }

            for (Base b : gone) {
                activeBases.remove(b);
            }
            if (activeBases.isEmpty()){
                endGame();
            }
        }
//        else {
//            endGame();
//        }
    }

    public void incrementScore() {
        scoreValue++;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                score.setText(String.format(Locale.getDefault(), "%d", scoreValue));
            }
        });
    }

    public void removeMissile(Missile m) {
        missileMaker.removeMissile(m);
    }

    void applyMissileBlast(float x, float y){
        if (!activeBases.isEmpty()) {
            ArrayList<Base> temp = new ArrayList<>(activeBases);
            double minDis = Double.MAX_VALUE;
            Base closestBase = temp.get(0);
            for (Base b : temp) {
                double dis;
                if ((dis = Utilities.distance(b.getX(), b.getY(), x, y)) < minDis) {
                    closestBase = b;
                    minDis = dis;
                }
            }
            if (Utilities.distance(closestBase.getX(), closestBase.getY(), x, y) < MISSILE_BLAST) {
                Log.d(TAG, "applyMissileBlast: distance: "+Utilities.distance(closestBase.getX(), closestBase.getY(), x, y)+" x: "+x+" y: "+y);
                activeBases.remove(closestBase);
                closestBase.destruct();
                if (activeBases.isEmpty()){
                    endGame();
                }

            }
        }
        else {
            endGame();
        }
    }

    void endGame(){
        SoundPlayer.getInstance().stop("background");
        missileMaker.setRunning(false);
        //gameover.setVisibility(View.VISIBLE);

        Intent i = new Intent(this, ScoreActivity.class);
        i.putExtra("score", scoreValue);
        i.putExtra("level", levelValue);
        startActivity(i);

        finish();//////////
    }


    public void removeTitleView() {
        layout.removeView(titleImage);
    }
}
