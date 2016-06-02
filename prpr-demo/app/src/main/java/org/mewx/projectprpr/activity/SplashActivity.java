package org.mewx.projectprpr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import org.mewx.projectprpr.R;
import org.mewx.projectprpr.global.G;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends Activity {
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private static CountDownTimer activityDeadCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        findViewById(R.id.splash_image).setOnClickListener(new OnClickSkipSplashScreen());

        if(G.VERSION_TYPE == G.VERSION_TYPE_ENUM.TEST || G.getSkipSplashScreen()) {
            endActivityInstantly();
        } else {
            activityDeadCounter = new CountDownTimer(AUTO_HIDE_DELAY_MILLIS, 100) {
                @Override
                public void onTick ( long millisUntilFinished){
                    // Animation can be here.
                }

                @Override
                public void onFinish () {
                    // time-up, and jump
                    endActivity();
                }
            }.start();
        }
    }

    private void endActivity() {
        activityDeadCounter.cancel();
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.hold); // fade in animation
        finish(); // destroy itself
    }

    private void endActivityInstantly() {
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0); // no animation
        finish(); // destroy itself
    }

    @Override
    public void onBackPressed() {
        // hijack this event, and do nothing
    }


    /**
     * The OnClickListener to finish this activity itself, and jump to main activity.
     * Design for saving time, but time consuming tasi must have been done before finish:
     *     Guide viewpages;
     *     Upgrading database;
     *     etc.
     */
    class OnClickSkipSplashScreen implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO: time consuming task judgement

            // just end this activity
            endActivity();
        }
    }

}
