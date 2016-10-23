package com.jolpai.doctorsdairy;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.facebook.stetho.Stetho;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by User on 10/11/2016.
 */

public class App extends Application {


    public static String TAG="TAG";
    public static String SDK_VERSION;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"App");
        Log.e(TAG, currentTime());
        Stetho.initializeWithDefaults(this);
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP){
            SDK_VERSION="v21";
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }

    public static Animation blinkAnim(){
        Animation anim = new AlphaAnimation(0.8f, 1.0f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        return anim;
    }

    public static String currentTime(){

        return DateFormat.getDateTimeInstance().format(new Date());
    }
}
