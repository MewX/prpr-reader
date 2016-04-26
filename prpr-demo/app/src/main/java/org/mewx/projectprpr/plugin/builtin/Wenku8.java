package org.mewx.projectprpr.plugin.builtin;

import android.support.annotation.NonNull;

import org.mewx.projectprpr.plugin.NovelDataSourceBasic;
import org.mewx.projectprpr.plugin.component.ChapterInfo;
import org.mewx.projectprpr.plugin.component.NetRequest;
import org.mewx.projectprpr.plugin.component.NovelContent;
import org.mewx.projectprpr.plugin.component.NovelInfo;
import org.mewx.projectprpr.plugin.component.PageNumBetween;
import org.mewx.projectprpr.plugin.component.VolumeInfo;

import java.util.List;

/**
 * This is built-in plug-in support for wenku8.com, and it's need to be logged in to access novel.
 * Created by MewX on 3/31/2016.
 */
public class Wenku8 extends NovelDataSourceBasic {

    @Override
    public NetRequest[] getUltraRequests(String tag, @NonNull String preRequestContent) {
        return new NetRequest[0];
    }

    @Override
    public void ultraReturn(String tag, byte[][] requestResult) {

    }

    @NonNull
    @Override
    public String[] getCategories() {
        return new String[0];
    }

    @Override
    public boolean judgeIs404(String pageContent) {
        return false;
    }

    @Override
    public NetRequest getMainListRequest(int pageNum) {
        return null;
    }

    @Override
    public List<NovelInfo> parseMainListRequestResult(String pageContent) {
        return null;
    }

    @Override
    public PageNumBetween getMainListPageNum() {
        return null;
    }

    @Override
    public NetRequest getSpecificListRequest(String categoryName, int pageNum) {
        return null;
    }

    @Override
    public List<NovelInfo> parseSpecificListRequestResult(String categoryName, String pageContent) {
        return null;
    }

    @Override
    public PageNumBetween getSpecificListPageNum(String categoryName) {
        return null;
    }

    @Override
    public NetRequest getNovelInfoRequest(String tag) {
        return null;
    }

    @Override
    public NovelInfo parseNovelInfo(String content) {
        return null;
    }

    @Override
    public NetRequest getNovelVolumeRequest(String tag) {
        return null;
    }

    @Override
    public List<VolumeInfo> parseNovelVolume(String content) {
        return null;
    }

    @Override
    public NetRequest getNovelContentRequest(String tag) {
        return null;
    }

    @Override
    public NovelContent parseNovelContent(String content) {
        return null;
    }

    @Override
    public NetRequest[] getSearchRequest(String query) {
        return new NetRequest[0];
    }

    @Override
    public List<NovelInfo> parseSearchResults(String[] contents) {
        return null;
    }
}
