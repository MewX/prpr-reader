package org.mewx.projectprpr.plugin;

import android.support.annotation.Nullable;
import android.util.Log;

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
public abstract class NovelDataSourceSimple {
    private final static String TAG = NovelDataSourceSimple.class.getSimpleName();

    protected String name;
    protected String author;
    protected String url;
    protected int versionCode;
    protected String releaseTime;

    // get novel categories
    public abstract @Nullable String[] getCategories();

    // activities
    public abstract void showInitialActivity(); // initial activity
    public abstract void showNovelListActivity(); // novel list (load more callback)
    public abstract void showNovelDetailActivity(); // novel detail, contains chapters

    // get novel list & page
    //public abstract

    protected void Logv(String msg) {
        Log.v(TAG, msg);
    }

    protected void Loge(String msg) {
        Log.e(TAG, msg);
    }
}
