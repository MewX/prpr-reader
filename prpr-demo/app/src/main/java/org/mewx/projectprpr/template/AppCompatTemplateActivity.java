package org.mewx.projectprpr.template;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import org.mewx.projectprpr.toolkit.thirdparty.SystemBarTintManager;

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
        SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
        if(Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            systemBarTintManager.setStatusBarTintEnabled(true);
            systemBarTintManager.setStatusBarTintColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        if(Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
            // set navigation bar color (todo temporary solution)
            systemBarTintManager.setNavigationBarTintEnabled(true);
            systemBarTintManager.setNavigationBarTintColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        // TODO: Umeng statistic codes can be added here
    }
}
