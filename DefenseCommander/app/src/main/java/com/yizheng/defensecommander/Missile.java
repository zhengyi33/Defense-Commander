package com.yizheng.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

class Missile {

    private static final String TAG = "Missile";
    static int count = 0;
    int id = 0;

    private MainActivity mainActivity;
    private ImageView imageView;
    private AnimatorSet aSet = new AnimatorSet();
    private int screenHeight;
    private int screenWidth;
    private long screenTime;
    private boolean hit = false;

    //private int endX;//////////

    final static int MISSILE_BLAST = 250;

    Missile(int screenWidth, int screenHeight, long screenTime, final MainActivity mainActivity){

        count++;
        id = count;

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenTime = screenTime;
        this.mainActivity = mainActivity;

//        mainActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
                imageView = new ImageView(mainActivity);

                imageView.setImageResource(R.drawable.missile);
                float startX = (float) ((Math.random()*screenWidth));
                float endX = (float) (Math.random()*screenWidth);

                float startY = -100;
                float endY = Missile.this.screenHeight;
                double offset = imageView.getDrawable().getIntrinsicWidth()*0.5;
                startX -= offset;
                startY -= offset;
                float a = Utilities.calculateAngle(startX, startY, endX, endY);
                imageView.setX(startX);
                imageView.setY(startY);
                imageView.setZ(-10);
                imageView.setRotation(a);
                mainActivity.getLayout().addView(imageView);

                ObjectAnimator xAnim = ObjectAnimator.ofFloat(imageView, "x", startX, endX);
                xAnim.setInterpolator(new LinearInterpolator());
                xAnim.setDuration(screenTime);

                ObjectAnimator yAnim = ObjectAnimator.ofFloat(imageView, "y", startY, endY);
                yAnim.setInterpolator(new LinearInterpolator());
                yAnim.setDuration(screenTime);

                xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if (imageView.getY() > screenHeight*0.85){

                            Log.d(TAG, "onAnimationUpdate: " + id + " update and cancel");

//                            xAnim.cancel();
//                            yAnim.cancel();
                            aSet.cancel();
                            makeGroundBlast(Missile.this.getX()+Missile.this.getWidth()*0.5f, Missile.this.getY()+Missile.this.getHeight()*0.5f);
                            mainActivity.removeMissile(Missile.this);
                            mainActivity.getLayout().removeView(imageView);
                        }
                    }
                });
                aSet.playTogether(xAnim, yAnim);

//            }
//        });


//        imageView.setY(-100);

//        endX = (int) (Math.random()*screenWidth);//////////

//        mainActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.getLayout().addView(imageView);
//            }
//        });
    }

    void start(){
        aSet.start();
    }

//    AnimatorSet setData(final int drawId) {
//        mainActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
////                imageView.setImageResource(drawId);
////                int endX = (int) (Math.random()*screenWidth);
//
////                double offset = imageView.getDrawable().getIntrinsicWidth()*0.5;
////                final float startX = (float) ((Math.random()*screenWidth) - offset);
//
//                endX -= offset;
//
////                final float a = Utilities.calculateAngle(startX, -100, endX, screenHeight);
////                imageView.setX(startX);
////                imageView.setY(-100);
////                imageView.setZ(-10);
////                imageView.setRotation(a);
//            }
//        });
//
//
////        int endX = (int) (Math.random()*screenWidth);
////
////        double offset = imageView.getDrawable().getIntrinsicWidth()*0.5;
////        final float startX = (float) ((Math.random()*screenWidth) - offset);
////
////        endX -= offset;
////
////        final float a = Utilities.calculateAngle(startX, -100, endX, screenHeight);
//
////        mainActivity.runOnUiThread(new Runnable() {
////            @Override
////            public void run() {
////                imageView.setX(startX);
////                imageView.setY(-100);
////                imageView.setZ(-10);
////                imageView.setRotation(a);
////            }
////        });
//
//
////        ObjectAnimator xAnim = ObjectAnimator.ofFloat(imageView, "x", endX);
////        xAnim.setInterpolator(new LinearInterpolator());
////        xAnim.setDuration(screenTime);
//
////        ObjectAnimator yAnim = ObjectAnimator.ofFloat(imageView, "y", -100, screenHeight);
////        yAnim.setInterpolator(new LinearInterpolator());
////        yAnim.setDuration(screenTime);
//
//
////        xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
////            @Override
////            public void onAnimationUpdate(ValueAnimator animation) {
////                if (imageView.getY() > screenHeight*0.85){
////                    mainActivity.runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            xAnim.cancel();
////                            yAnim.cancel();
////                            makeGroundBlast(Missile.this.getX()+Missile.this.getWidth()*0.5f, Missile.this.getY()+Missile.this.getHeight()*0.5f);
////                            mainActivity.removeMissile(Missile.this);
////                            mainActivity.getLayout().removeView(imageView);
////                        }
////                    });
////
////
////                }
////            }
////        });
//
//        xAnim.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationCancel(Animator animation) {
//                super.onAnimationCancel(animation);
//                Log.d(TAG, "onAnimationCancel: missile explosion id: "+id+" cancel");
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationCancel(animation);
//                Log.d(TAG, "onAnimationEndxanim: missile explosion id: "+id+" end");
//            }
//        });
//
//        aSet.playTogether(xAnim, yAnim);
//        return aSet;
//    }

    void makeGroundBlast(final float x, final float y){
        ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.explode);
        float offset = (float) (iv.getDrawable().getIntrinsicWidth()*0.5);
        //x -= offset;
        //y -= offset;
        iv.setX(x-offset);
        iv.setY(y-offset);
        iv.setZ(-15);
        mainActivity.getLayout().addView(iv);

        Log.d(TAG, "makeGroundBlast: missile explosion id: "+id+" added to view");

        ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
//        mainActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                alpha.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        mainActivity.getLayout().removeView(iv);
//                    }
//                });
//                alpha.start();
//            }
//        });
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(TAG, "onAnimationEnd: missile explosion id: "+id+" removed from view");
                mainActivity.getLayout().removeView(iv);
            }
        });
        alpha.start();
        mainActivity.applyMissileBlast(x, y);
    }

    void stop() {
        aSet.cancel();
    }

    float getX() {
        return imageView.getX();
    }

    float getY() {
        return imageView.getY();
    }

    float getWidth() {
        return imageView.getWidth();
    }

    float getHeight() {
        return imageView.getHeight();
    }

    void interceptorBlast(float x, float y) {
        mainActivity.getLayout().removeView(imageView);
//        mainActivity.removeMissile(this);
        final ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.explode);

        int w = imageView.getDrawable().getIntrinsicWidth();
        int offset = (int) (w * 0.5);

        iv.setX(x - offset);
        iv.setY(y - offset);

        aSet.cancel();

        mainActivity.getLayout().addView(iv);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);

        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(iv);//////////
            }
        });

        alpha.start();
    }

    void removeImageAtEnd(){
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.getLayout().removeView(imageView);
            }
        });

    }

//    ImageView getImageView() {
//        return imageView;
//    }
}
