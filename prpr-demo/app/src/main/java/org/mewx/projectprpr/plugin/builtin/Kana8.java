package org.mewx.projectprpr.plugin.builtin;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.text.Html;

import org.mewx.projectprpr.R;
import org.mewx.projectprpr.plugin.NovelDataSourceBasic;
import org.mewx.projectprpr.plugin.component.ChapterInfo;
import org.mewx.projectprpr.plugin.component.NetRequest;
import org.mewx.projectprpr.plugin.component.NovelContent;
import org.mewx.projectprpr.plugin.component.NovelContentLine;
import org.mewx.projectprpr.plugin.component.NovelInfo;
import org.mewx.projectprpr.plugin.component.PageNumBetween;
import org.mewx.projectprpr.plugin.component.VolumeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MewX on 05/16/2016.
 * Support for 8kana.com
 */
public class Kana8 extends NovelDataSourceBasic {

    public Kana8() {
        // init values
        tag = "8kana";
        name = "不可能的世界";
        author = "MewX";
        url = "http://m.8kana.com";
        versionCode = 1;
        releaseTime = "2016/05/16";
        logoUrl = "http://s.8kana.com/img/common/head/logoBig.png";
    }

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
        return new String[0]; // set 0 is okay
    }

    @Override
    public boolean judgeIs404(String pageContent) {
        return false; // default
    }

    @Override
    public NetRequest getMainListRequest(int pageNum) {
        return new NetRequest(NetRequest.REQUEST_TYPE.GET, "http://m.8kana.com/index/ajaxrd?page=" + pageNum, null);
    }

    @Override
    public List<NovelInfo> parseMainListRequestResult(String pageContent) {
        // tag, coverURL, title, author&nbsp;count; short intro
        final String regex = "onclick.+?book/(.+?)\\..+?img.+?src.+?\"(.+?)\".+?bookName.+?>(.+?)<.+?story_info.+?h1.+?>(.+?)<.+?<p.+?>(.+?)<";
        List<NovelInfo> list = new ArrayList<>();
        Matcher mainMatcher = Pattern.compile(regex, Pattern.DOTALL).matcher(pageContent);
        while (mainMatcher.find()) {
            String author = Html.fromHtml(mainMatcher.group(4).trim()).toString();
            int endIdx = author.indexOf(' ');
            author = author.substring(0, endIdx < 0 ? author.length() : endIdx);
            list.add(new NovelInfo(mainMatcher.group(1), mainMatcher.group(3), author, mainMatcher.group(2)));
        }
        return list;
    }

    @Override
    public PageNumBetween getMainListPageNum() {
        return new PageNumBetween(1, 100);
    }

    @Override
    public NetRequest getSpecificListRequest(String categoryName, int pageNum) {
        return null; // todo
    }

    @Override
    public List<NovelInfo> parseSpecificListRequestResult(String categoryName, String pageContent) {
        return null; // todo
    }

    @Override
    public PageNumBetween getSpecificListPageNum(String categoryName) {
        return null; // todo
    }

    @Override
    public NetRequest getNovelInfoRequest(String tag) {
        return new NetRequest(NetRequest.REQUEST_TYPE.GET, "http://m.8kana.com/book/" + tag + ".html", null);
    }

    @Override
    public NovelInfo parseNovelInfo(String content) {
        // Group 1:	http://c.8kana.com/201510/111/05/11105_6a5b4_634_s.jpg	  5271	    54
        // Group 2:	流浪在星辰大海	  5536	     7
        // Group 3:	232话	  6125	     4
        // Group 4:	荆洚晓	  6408	     3
        // Group 5:	59.9万字	  6424	     6
        // Group 6:	11105	  6857	     5
        // Group 7:	     被外星人绑架的中二少年，在太空的挣扎求生之路……
        final String contentRegex = "/header.+?img.+?src.+?\"(.+?)\".+?bookName.+?>(.+?)<.+?<span.*?>(.+?)<.+?<span.+?>(.+?)<.+?<span.*?>(.+?)<.+?rite\\((.+?)\\).+?introduce.+?<dt>(.+?)<";
        Matcher contentMatcher = Pattern.compile(contentRegex, Pattern.DOTALL).matcher(content);
        contentMatcher.find();

        ContentValues cv = new ContentValues();
        cv.put(getNovelInfoElementName(R.string.novel_info_latest_chapter), contentMatcher.group(3));
        cv.put(getNovelInfoElementName(R.string.novel_info_word_count), contentMatcher.group(5));
        cv.put(getNovelInfoElementName(R.string.novel_info_full_intro), contentMatcher.group(7).trim());

        return new NovelInfo(contentMatcher.group(6), contentMatcher.group(2), contentMatcher.group(4), contentMatcher.group(1), cv);
    }

    @Override
    public NetRequest getNovelVolumeRequest(String tag) {
        return null; // left blank
    }

    @Override
    public List<VolumeInfo> parseNovelVolume(String content) {
        final String volumeRegex = "smtext\">(.+?)<.+?</ul>";
        final String chapterRegex = "onclick.+?read/(.+?)\\..+?<span>(.+?)<";

        Matcher volumeMatcher = Pattern.compile(volumeRegex, Pattern.DOTALL).matcher(content);
        List<VolumeInfo> listVolumeInfo = new ArrayList<>();
        while (volumeMatcher.find()) {
            VolumeInfo vi = new VolumeInfo(volumeMatcher.group(1));

            Matcher chapterMatcher = Pattern.compile(chapterRegex, Pattern.DOTALL).matcher(volumeMatcher.group());
            while (chapterMatcher.find()) {
                vi.addToChapterList(new ChapterInfo(chapterMatcher.group(2), chapterMatcher.group(1)));
            }
            listVolumeInfo.add(vi);
        }
        return listVolumeInfo;
    }

    @Override
    public NetRequest getNovelChapterRequest(String tag) {
        return null;
    }

    @Override
    public List<ChapterInfo> parseNovelChapter(String content) {
        return null;
    }

    @Override
    public NetRequest getNovelContentRequest(String tag) {
        return new NetRequest(NetRequest.REQUEST_TYPE.GET, "http://www.8kana.com/read/" + tag + ".html", null);
    }

    @Override
    public NovelContent parseNovelContent(String content) {
        final String contentRegex = "myContent.+?</div";
        final String paragraphRegex = "<p .+?>(.+?)</";

        Matcher contentMatcher = Pattern.compile(contentRegex, Pattern.DOTALL).matcher(content);
        contentMatcher.find();
        content = contentMatcher.group();
        Matcher paragraphMatcher = Pattern.compile(paragraphRegex, Pattern.DOTALL).matcher(content);

        NovelContent nc = new NovelContent();
        while (paragraphMatcher.find()) {
            nc.addToNovelContentAndSaveFile(new NovelContentLine(NovelContentLine.TYPE.TEXT, Html.fromHtml(paragraphMatcher.group(1)).toString().trim()));
        }
        return nc;
    }

    @Override
    public NetRequest[] getSearchRequest(String query) {
        return new NetRequest[0]; // todo
    }

    @Override
    public List<NovelInfo> parseSearchResults(String[] contents) {
        return null; // todo
    }
}
