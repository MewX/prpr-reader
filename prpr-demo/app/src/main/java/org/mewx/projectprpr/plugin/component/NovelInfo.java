package org.mewx.projectprpr.plugin.component;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * Created by MewX on 4/9/2016.
 * General info, and additional info stored in ContentValues.
 * Judge if empty, not display.
 *
 * InfoPairs Sample:
 * 文库 小学馆
 * 连载状态 已完成
 * 最后更新 2012-11-02
 */
@SuppressWarnings("unused")
public class NovelInfo implements Serializable {
    @NonNull private String title; // may be id? hash?
    @NonNull private String dataSource = ""; // TODO: set by parent
    @NonNull private String bookTag; // id in data source, just an identifier
    @NonNull private String author = "";
    @NonNull private String coverUrl = ""; // if has none, set empty
    @NonNull private ContentValues infoPairs = new ContentValues();

    public NovelInfo(@NonNull String bookTag, @NonNull String title) {
        this.bookTag = bookTag;
        this.title = title;
    }

    public NovelInfo(@NonNull String bookTag, @NonNull String title, @NonNull String author, @NonNull String coverUrl) {
        this(bookTag, title);
        this.author = author;
        this.coverUrl = coverUrl;
    }

    public NovelInfo(@NonNull String bookTag, @NonNull String title, @NonNull  String author, @NonNull String coverUrl, @Nullable ContentValues cv) {
        this(bookTag, title, author, coverUrl);
        if (cv != null)
            this.infoPairs = cv;
    }

    @NonNull
    public String getBookTag() {
        return bookTag;
    }

    public void setBookTag(@NonNull String bookTag) {
        this.bookTag = bookTag;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getDataSource() {
        return dataSource;
    }

    @NonNull
    public String getAuthor() {
        return author;
    }

    public void setAuthor(@NonNull String author) {
        this.author = author;
    }

    /**
     * This function should be called by parent class, so user can just ignore it.
     * @param dataSource a typical string to represent a specific data source website.
     */
    public void setDataSource(@NonNull String dataSource) {
        this.dataSource = dataSource;
    }

    @NonNull
    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(@NonNull String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public ContentValues getInfoPairs() {
        return infoPairs;
    }

    public void setInfoPairs(ContentValues infoPairs) {
        this.infoPairs = infoPairs;
    }
}
