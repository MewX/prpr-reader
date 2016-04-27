package org.mewx.projectprpr.activity.adapter;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

@SuppressWarnings("unused")
public class PluginCenterItem {
    @NonNull private String centerName;
    @DrawableRes private int backgroundId;

    public PluginCenterItem(@NonNull String name, @DrawableRes int backgroundId) {
        this.centerName = name;
        this.backgroundId = backgroundId;
    }

    @NonNull
    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(@NonNull String centerName) {
        this.centerName = centerName;
    }

    public int getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(@DrawableRes int backgroundId) {
        this.backgroundId = backgroundId;
    }
}
