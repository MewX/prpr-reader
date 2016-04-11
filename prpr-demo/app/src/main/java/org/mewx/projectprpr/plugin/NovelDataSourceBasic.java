package org.mewx.projectprpr.plugin;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.mewx.projectprpr.plugin.component.ChapterInfo;
import org.mewx.projectprpr.plugin.component.NetRequest;
import org.mewx.projectprpr.plugin.component.NovelContent;
import org.mewx.projectprpr.plugin.component.NovelInfo;
import org.mewx.projectprpr.plugin.component.PageNumBetween;

import java.util.List;

/**
 * This class hold a data source in a simple interface. (*.luas)
 *
 * Script sample: ( Lua need to integrate some usefule cunctions, like byteText2Unicode(), etc.
 *
 * name = "动漫之家";
 * author = "MewX";
 * url = "http://xs.dmzj.com/";
 * versionCode = 1;
 * releaseTime = "2016.05.13";
 *
 * func getRecentUpdate() {
 *     // url, regex for novel(name capture)
 *     // regex for categories(name capture, nullable)
 *     // regex for max page num(name capture, nullable), <-- not available in simple mode
 *     return "http://xs.dmzj.com/update_1.shtml",
 *     "<a\shref=\"/({id}\d+?)/index.shtml\"\salt=\"({title}\w+?)\".+?<img\ssrc=\"({cover}.+?)\".+?</p><p>作者：({author}.+?).+?title=\"({chapter}.+?)\".+?<p>({date}.+?)</p>"
 *     "/tags/({tag}\w+?)\.shtml\"><span>({name}.+?)</span>";
 * }
 *
 * func getImageUrl(string geturl) {
 *     // optional
 * }
 *
 * func getBookInfoUrl(string strId) {
 *     return "http://xs.dmzj.com/" + strId + "/index.shtml";
 * }
 *
 * func bookInfoById(int id) {
 *     return url, regex for info, regex for volume, regex for chapter,
 * }
 *
 * func getSearchResult(string codedKeyword) {
 *     // return baseurl, post/get, parameters, regex for result
 *     return "http://s.acg.178.com/lnovelsum/search.php?s=%E7%94%B0%E4%B8%AD&type=0
 * }
 *
 * Created by MewX on 1/19/2016.
 */
@SuppressWarnings("unused")
public abstract class NovelDataSourceBasic {
    private final static String TAG = NovelDataSourceBasic.class.getSimpleName();

    protected @NonNull String name = "Default";
    protected @NonNull String author = "MewX";
    protected @NonNull String url = "http://mewx.org";
    protected @NonNull Integer versionCode = 1;
    protected @NonNull String releaseTime = "2016/04/12";

    // activities
    public final void showInitialActivity(){}; // initial activity
    public final void showNovelListActivity(){}; // novel list (load more callback)
    public final void showNovelDetailActivity(){}; // novel detail, contains chapters
    public final void ultraRequest(String tag, NetRequest[] requests){}; // force requests and let parent callback after all the requests
    public abstract void ultraReturn(String tag, byte[][] requestResult); // left empty is okay

    // get novel categories
    public abstract @NonNull String[] getCategories();

    // get novel list & page
    public abstract boolean judgeIs404(String pageContent); // parent class calls this function

    public abstract NetRequest getMainListRequest(int pageNum);
    public abstract List parseMainListRequestResult(String pageContent);
    public abstract PageNumBetween getMainListPageNum();

    public abstract NetRequest getSpecificListRequest(String categoryName, int pageNum);
    public abstract List parseSpecificListRequestResult(String categoryName, String pageContent);
    public abstract PageNumBetween getSpecificListPageNum(String categoryName);

    public abstract NetRequest getNovelInfoRequest();
    public abstract NovelInfo parseNovelInfo(String content);
    public abstract NetRequest getNovelChapterRequest(); // if return null, use request above
    public abstract List<ChapterInfo> parseNovelChapter();

    public abstract NetRequest getNovelContentRequest(); // may too large
    public abstract NovelContent parseNovelContent(String content); // parse html novel, images

    protected void Logv(String msg) {
        Log.v(TAG, msg);
    }

    protected void Loge(String msg) {
        Log.e(TAG, msg);
    }
}
