package org.mewx.projectprpr.plugin.component;

import android.support.annotation.NonNull;

/**
 * Created by MewX on 4/9/2016.
 * Store chapter info only.
 */
@SuppressWarnings("unused")
public class ChapterInfo {
    private @NonNull String title;
    private @NonNull String chapterTag; // TODO: when save, need to translate string, e.g. '/' -> '_'

    /**
     * default construct
     * @param chapterTag this is a must to be set
     */
    public ChapterInfo(@NonNull String chapterTag) {
        this("", chapterTag);
    }

    public ChapterInfo(@NonNull String title, @NonNull String chapterTag) {
        this.title = title;
        this.chapterTag = chapterTag;
    }

    public @NonNull String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public @NonNull String getChapterTag() {
        return chapterTag;
    }

    public void setChapterTag(@NonNull String chapterTag) {
        this.chapterTag = chapterTag;
    }
}
