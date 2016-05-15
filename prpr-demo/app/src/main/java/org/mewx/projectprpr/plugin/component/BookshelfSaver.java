package org.mewx.projectprpr.plugin.component;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MewX on 05/11/2016.
 * An item in bookshelf.
 */
@SuppressWarnings("unused")
public class BookshelfSaver {

    public enum BOOK_TYPE {
        NETNOVEL,
        LOCAL_BOOK
    }

    @NonNull
    private BOOK_TYPE type;
    @Nullable
    private String dataSourceTag; // if local, null
    @NonNull
    private NovelInfo novelInfo;
    @Nullable
    private ArrayList<VolumeInfo> listVolumeInfo; // if local, null

    public BookshelfSaver(@NonNull BOOK_TYPE type, @Nullable String dataSourceTag, @NonNull NovelInfo novelInfo, @Nullable ArrayList<VolumeInfo> listVolumeInfo) {
        // path: .. / plug-in's tag / novelInfo's tag
        this.type = type;
        this.dataSourceTag = dataSourceTag;
        this.novelInfo = novelInfo;
        this.listVolumeInfo = listVolumeInfo;
    }

    @NonNull
    public BOOK_TYPE getType() {
        return type;
    }

    public void setType(@NonNull BOOK_TYPE type) {
        this.type = type;
    }

    @Nullable
    public String getDataSourceTag() {
        return dataSourceTag;
    }

    public void setDataSourceTag(@Nullable String dataSourceTag) {
        this.dataSourceTag = dataSourceTag;
    }

    @NonNull
    public NovelInfo getNovelInfo() {
        return novelInfo;
    }

    public void setNovelInfo(@NonNull NovelInfo novelInfo) {
        this.novelInfo = novelInfo;
    }

    @Nullable
    public ArrayList<VolumeInfo> getListVolumeInfo() {
        return listVolumeInfo;
    }

    public void setListVolumeInfo(@NonNull ArrayList<VolumeInfo> listVolumeInfo) {
        this.listVolumeInfo = listVolumeInfo;
    }
}
