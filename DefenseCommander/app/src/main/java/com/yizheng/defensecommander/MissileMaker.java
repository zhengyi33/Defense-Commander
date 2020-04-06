package com.yizheng.defensecommander;

import android.animation.AnimatorSet;

import java.util.ArrayList;

import static com.yizheng.defensecommander.Interceptor.INTERCEPTOR_BLAST;

public class MissileMaker implements Runnable{

    private MainActivity mainActivity;
    private boolean isRunning;
    private ArrayList<Missile> activeMissiles = new ArrayList<>();
    private int screenWidth, screenHeight;

    private static long delay = 5000;

    private int missilesPerLevel = 10;
    private int count = 0;

    private int current_level = 1;

    private boolean firstMissile = true;

    MissileMaker(MainActivity mainActivity, int screenWidth, int screenHeight){
        this.mainActivity = mainActivity;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void run() {
        setRunning(true);
        try {
            Thread.sleep((long) (delay * 0.5));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (isRunning) {


            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    makeMissile();

                    if (firstMissile) {
                        firstMissile = false;
                        mainActivity.removeTitleView();
                    }
                }
            });



            if( ++count > missilesPerLevel){
                incrementLevel();
                count = 0;
            }
            long sleepTime = getSleepTime();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!activeMissiles.isEmpty()){
            for (Missile m : activeMissiles){
                m.removeImageAtEnd();
            }
            activeMissiles.clear();
        }
    }

    //extra
    int mSize(){
        return activeMissiles.size();
    }

    void makeMissile(){
        long missileTime = (long) ((delay * 0.5) + (Math.random() * delay));
        Missile missile = new Missile(screenWidth, screenHeight, missileTime, mainActivity);
        activeMissiles.add(missile);
        SoundPlayer.getInstance().start("launch_missile");
        missile.start();
    }

    private long getSleepTime() {
        double r = Math.random();
        if (r<0.1){
            return 1;
        } else if (r<0.2){
            return delay/2;
        } else {
            return delay;
        }
    }

    private void incrementLevel() {

        mainActivity.setLevel(++current_level);

        delay -= 500;
        if (delay<=0){
            delay = 1;
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void setRunning(boolean running) {
        isRunning = running;
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);
        for (Missile m : temp) {
            m.stop();
        }
    }

    void applyInterceptorBlast(Interceptor interceptor, int id) {
        float x1 = interceptor.getX();
        float y1 = interceptor.getY();

        ArrayList<Missile> gone = new ArrayList<>();
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);

        for (Missile m : temp){
            float x2 = (int) (m.getX() + (0.5 * m.getWidth()));
            float y2 = (int) (m.getY() + (0.5 * m.getHeight()));

            float dist = (float) (Utilities.distance(x1, y1, x2, y2));
            if (dist < INTERCEPTOR_BLAST) {
                mainActivity.incrementScore();
                SoundPlayer.getInstance().start("interceptor_hit_missile");
                m.interceptorBlast(x2, y2);
                gone.add(m);
            }
        }

        for (Missile m : gone){
            activeMissiles.remove(m);
        }
    }

    public void removeMissile(Missile m) {
        activeMissiles.remove(m);
    }
}
