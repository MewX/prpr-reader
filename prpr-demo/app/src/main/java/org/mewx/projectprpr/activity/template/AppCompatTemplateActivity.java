package org.mewx.projectprpr.activity.template;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.mewx.projectprpr.R;

/**
 * This activity used to init some common components.
 * Created by MewX on 1/18/2016.
 */
public class AppCompatTemplateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set StatusBar Color
        // TODO: should change when theme change? like dark mode?
        if(Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // final float STATUSBAR_ALPHA = 0.15f;

            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            systemBarTintManager.setStatusBarTintEnabled(true);
            systemBarTintManager.setStatusBarTintColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        // TODO: Umeng statistic codes can be added here
    }
}
