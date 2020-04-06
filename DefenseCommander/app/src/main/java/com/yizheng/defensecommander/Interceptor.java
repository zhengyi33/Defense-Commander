package com.yizheng.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import static com.yizheng.defensecommander.MainActivity.screenHeight;

public class Interceptor {
    private MainActivity mainActivity;
    private ImageView imageview;
    private ObjectAnimator moveX, moveY;
    private float startX, startY, endX, endY;

    static final int INTERCEPTOR_BLAST = 120;

    Interceptor(final MainActivity mainActivity, float startX, float endX, float endY){
        this.mainActivity = mainActivity;
        this.startX = startX;
        this.endX = endX;
        this.endY = endY;
        imageview = new ImageView(mainActivity);
        imageview.setImageResource(R.drawable.interceptor);
        startY = screenHeight - 70;

        int www = (int) (imageview.getDrawable().getIntrinsicWidth()*0.5);
        this.endX -= www;
        this.endY -= www;

        //float a = calculateAngle(imageview.getX(), imageview.getY(), endX, endY);
        float a = calculateAngle(startX, startY, endX, endY);

        imageview.setX(startX);
        imageview.setY(this.startY);
        imageview.setZ(-10);
        imageview.setRotation(a);
        mainActivity.getLayout().addView(imageview);
        double distance  = Utilities.distance(startX, this.startY, this.endX, this.endY);

        moveX = ObjectAnimator.ofFloat(imageview, "x", startX, this.endX);
        moveX.setInterpolator(new AccelerateInterpolator());
        moveX.setDuration((long) (distance*2));

        moveY = ObjectAnimator.ofFloat(imageview, "y", this.startY, this.endY);
        moveY.setInterpolator(new AccelerateInterpolator());
        moveY.setDuration((long) (distance*2));

        moveX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(imageview);
                mainActivity.reduceInterceptorCount(Interceptor.this);
                makeBlast();
            }
        });
    }

    void makeBlast(){
        SoundPlayer.getInstance().start("interceptor_blast");
        final ImageView explodeView = new ImageView(mainActivity);
        explodeView.setImageResource(R.drawable.i_explode);
        float w = explodeView.getDrawable().getIntrinsicWidth();
        explodeView.setX(this.getX() - (w/2));
        explodeView.setY(this.getY() - (w/2));
        explodeView.setZ(-15);
        mainActivity.getLayout().addView(explodeView);
        final ObjectAnimator alpha = ObjectAnimator.ofFloat(explodeView, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(explodeView);
            }
        });
        alpha.start();

        mainActivity.applyInterceptorBlast(this, imageview.getId());

        //extra
        mainActivity.applyInterceptorBlastForBase(this);
    }

    void launch() {
        moveX.start();
        moveY.start();
    }

    float getX() {
        int xVar = imageview.getWidth() / 2;
        return imageview.getX() + xVar;
    }

    float getY() {
        int yVar = imageview.getHeight() / 2;
        return imageview.getY() + yVar;
    }

    private float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        // Keep angle between 0 and 360
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (180.0f - angle);

    }

}
