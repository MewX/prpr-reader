package org.mewx.projectprpr.plugin.component;

import android.support.annotation.NonNull;

/**
 * Created by MewX on 4/12/2016.
 * Stores the novel content line element.
 */
@SuppressWarnings("unused")
public class NovelContentLine {
    public enum TYPE {
        TEXT,
        IMAGE_URL
    }

    @NonNull public TYPE type = TYPE.TEXT;
    @NonNull public String content = "";

    public NovelContentLine() {

    }

    public NovelContentLine(@NonNull TYPE type, @NonNull String content) {
        this.type = type;
        this.content = content;
    }
}
