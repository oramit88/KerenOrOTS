package il.ac.shenkar.kerenor.tasksapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.Constants;
import il.ac.shenkar.kerenor.tasksapp.R;

/**
 * SplashScreen.java - a simple class that control the splash screen when entering the app
 * and its animation (along with the res/anim folder)
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class SplashScreen extends Activity {

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Animation anim = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.alpha);
        anim.reset();

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layout_splash);
        relativeLayout.clearAnimation();
        relativeLayout.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.translate);
        anim.reset();
        ImageView logo = (ImageView) findViewById(R.id.logo_image_splash);
        logo.clearAnimation();
        logo.startAnimation(anim);

        /* New Handler to start the login activity and close this Splash-Screen after a few seconds */
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the login activity */
                Intent mainIntent = new Intent(SplashScreen.this, LoginActivity.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, Constants.SPLASH_DISPLAY_LENGTH); /** Duration of wait **/
    }

}





