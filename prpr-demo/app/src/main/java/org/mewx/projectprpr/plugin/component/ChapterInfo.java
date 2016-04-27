package org.mewx.projectprpr.plugin.component;

import android.support.annotation.NonNull;

/**
 * Created by MewX on 4/9/2016.
 * Store chapter info only.
 */
@SuppressWarnings("unused")
public class ChapterInfo {
    @NonNull private String title;
    @NonNull private String chapterTag; // TODO: when save, need to translate string, e.g. '/' -> '_'

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

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getChapterTag() {
        return chapterTag;
    }

    public void setChapterTag(@NonNull String chapterTag) {
        this.chapterTag = chapterTag;
    }
}
