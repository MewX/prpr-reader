package org.mewx.projectprpr.plugin.component;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by MewX on 4/9/2016.
 * Contain chapters.
 */
@SuppressWarnings("unused")
public class VolumeInfo {
    private @NonNull String title;
    private @NonNull String volumeTag;
    private @NonNull ArrayList<ChapterInfo> chapterList = new ArrayList<>();

    public VolumeInfo(@NonNull String volumeTag) {
        this("", volumeTag);
    }

    public VolumeInfo(@NonNull String title, @NonNull String volumeTag) {
        this.title = title;
        this.volumeTag = volumeTag;
    }

    public int getChapterListSize() {
        return chapterList.size();
    }

    public ChapterInfo getChapterByListIndex(int i) {
        return chapterList.get(i);
    }

    public void addToChapterList(ChapterInfo[] chapterInfoArray) {
        chapterList.addAll(Arrays.asList(chapterInfoArray));
    }

    public void addTochapterList(List<ChapterInfo> chapterInfoList) {
        chapterList.addAll(chapterInfoList);
    }

    public void addToChapterList(ChapterInfo chapterInfo) {
        chapterList.add(chapterInfo);
    }

    public void clearChapterList() {
        chapterList.clear();
    }

    public @NonNull String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public @NonNull String getVolumeTag() {
        return volumeTag;
    }

    public void setVolumeTag(@NonNull String volumeTag) {
        this.volumeTag = volumeTag;
    }

}
