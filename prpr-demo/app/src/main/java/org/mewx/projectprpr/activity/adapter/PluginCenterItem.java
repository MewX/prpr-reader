package org.mewx.projectprpr.activity.adapter;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

@SuppressWarnings("unused")
public class PluginCenterItem {
    private @NonNull String centerName;
    private @DrawableRes int backgroundId;

    public PluginCenterItem(@NonNull String name, @DrawableRes int backgroundId) {
        this.centerName = name;
        this.backgroundId = backgroundId;
    }

    public @NonNull String getCenterName() {
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
