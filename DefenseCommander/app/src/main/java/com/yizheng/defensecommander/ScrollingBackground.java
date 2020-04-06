package com.yizheng.defensecommander;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static com.yizheng.defensecommander.MainActivity.screenHeight;
import static com.yizheng.defensecommander.MainActivity.screenWidth;


class ScrollingBackground {
    private Context context;
    private ViewGroup layout;
    private ImageView backImageA;
    private ImageView backImageB;
    private long duration;
    private int resId;

    //extra
    private AnimatorSet alphaSet = new AnimatorSet();

    ScrollingBackground(Context context, ViewGroup layout, int resId, long duration) {

        this.context = context;
        this.layout = layout;
        this.resId = resId;
        this.duration = duration;

        setupBackground();
    }

    private void setupBackground() {
        backImageA = new ImageView(context);
        backImageB = new ImageView(context);

        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(screenWidth + getBarHeight(), screenHeight);
        backImageA.setLayoutParams(params);
        backImageB.setLayoutParams(params);

        layout.addView(backImageA);
        layout.addView(backImageB);

        //Bitmap backBitmapA = BitmapFactory.decodeResource(context.getResources(), resId);
        //Bitmap backBitmapB = BitmapFactory.decodeResource(context.getResources(), resId);

        //backImageA.setImageBitmap(backBitmapA);
        //backImageB.setImageBitmap(backBitmapB);
        backImageA.setImageResource(resId);
        backImageB.setImageResource(resId);


        backImageA.setScaleType(ImageView.ScaleType.FIT_XY);
        backImageB.setScaleType(ImageView.ScaleType.FIT_XY);

        backImageA.setZ(-1);
        backImageB.setZ(-1);

        //extra
        ObjectAnimator alpha = ObjectAnimator.ofFloat(backImageA, "alpha", 0.9f, 0.25f);
        alpha.setDuration(duration);
        alpha.setRepeatMode(ValueAnimator.REVERSE);
        alpha.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator alpha2 = ObjectAnimator.ofFloat(backImageB, "alpha", 0.9f, 0.25f);
        alpha2.setDuration(duration);
        alpha2.setRepeatMode(ValueAnimator.REVERSE);
        alpha2.setRepeatCount(ValueAnimator.INFINITE);
        alphaSet.playTogether(alpha, alpha2);


        animateBack();
    }

    private void animateBack() {

        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                float width = screenWidth + getBarHeight();

                float a_translationX = width * progress;
                float b_translationX = width * progress - width;

                backImageA.setTranslationX(a_translationX);
                backImageB.setTranslationX(b_translationX);

                //Log.d(TAG, "onAnimationUpdate: A " + translationX + "   B " + (translationX - width));
                //Log.d(TAG, "onAnimationUpdate: A " + backImageA.getY() + "   B " + backImageB.getY());

            }
        });
        animator.start();
        alphaSet.start();
    }


    private int getBarHeight() {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
