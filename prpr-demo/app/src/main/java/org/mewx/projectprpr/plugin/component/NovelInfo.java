package org.mewx.projectprpr.plugin.component;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.mewx.projectprpr.toolkit.CryptoTool;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

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
    public static final String TAG_TITLE = "title";
    public static final String LOCAL_BOOK_PREFIX = "file://";
    public static final String TAG_DATASOURCE = "datasource";
    public static final String TAG_BOOKTAG = "booktag";
    public static final String TAG_AUTHOR = "author";
    public static final String TAG_COVERURL = "coverurl";

    @NonNull private String title; // file name generated automatically or manually set
    @NonNull private String dataSource = ""; // MUST: set by parent, or file path (URI: file://)
    @NonNull private String bookTag; // id in data source, just an identifier (may be id? hash?)
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
        setInfoPairs(cv);
    }

    public boolean isLocal() {
        return dataSource.contains(LOCAL_BOOK_PREFIX);
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
        if (dataSource.contains(LOCAL_BOOK_PREFIX)) {
            // local book
            int separatorIndex = dataSource.lastIndexOf('/') < 0 ? dataSource.lastIndexOf('\\') : dataSource.lastIndexOf('/');
            this.bookTag = this.title = dataSource.substring(separatorIndex + 1);
        }
    }

    @NonNull
    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(@NonNull String coverUrl) {
        this.coverUrl = coverUrl;
    }

    @NonNull
    public ContentValues getInfoPairs() {
        return infoPairs;
    }

    public void setInfoPairs(@Nullable ContentValues infoPairs) {
        this.infoPairs.clear();
        if(infoPairs != null)
            this.infoPairs = infoPairs;
    }

    public void addToInfoPairs(@NonNull String key, @NonNull String value) {
        this.infoPairs.put(key, value);
    }
}
