package com.yizheng.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Base {

    private static final String TAG = "Base";

    private MainActivity mainActivity;
    private ImageView imageView;
    private int screenHeight;
    private int screenWidth;
    private boolean hit = false;
    private int resId;



    Base(final int screenHeight, int screenWidth, final MainActivity mainActivity, float x, final int resId) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.mainActivity = mainActivity;
        this.imageView = new ImageView(mainActivity);

        imageView.setX(x);

        mainActivity.getLayout().addView(imageView);
        imageView.setImageResource(resId);
        imageView.setY(screenHeight - imageView.getDrawable().getIntrinsicHeight());

    }



    float getX() {
        //return imageView.getX() + 0.5f*imageView.getDrawable().getIntrinsicWidth();
        return imageView.getX() + 0.5f * imageView.getWidth();
    }

    float getY() {
        return imageView.getY() + 0.5f * imageView.getHeight();
    }

    void destruct() {

        Log.d(TAG, "destruct: " + getX());

        float x = imageView.getX();
        float y = imageView.getY();
        SoundPlayer.getInstance().start("base_blast");
        mainActivity.getLayout().removeView(imageView);
        ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.blast);
        float offset = (float) (iv.getDrawable().getIntrinsicWidth() * 0.5);
        iv.setX(x - offset);
        iv.setY(y - offset);
        mainActivity.getLayout().addView(iv);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(iv);
            }
        });
        alpha.start();
    }

    //extra
    void interceptorBlast(){
        destruct();
    }
}
