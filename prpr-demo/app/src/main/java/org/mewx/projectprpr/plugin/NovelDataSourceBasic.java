package org.mewx.projectprpr.plugin;

import android.support.annotation.NonNull;
import android.util.Log;

import org.mewx.projectprpr.plugin.component.NetRequest;
import org.mewx.projectprpr.plugin.component.NovelContent;
import org.mewx.projectprpr.plugin.component.NovelInfo;
import org.mewx.projectprpr.plugin.component.PageNumBetween;
import org.mewx.projectprpr.plugin.component.VolumeInfo;

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

    protected @NonNull String name;
    protected @NonNull String author;
    protected @NonNull String url;
    protected @NonNull Integer versionCode;
    protected @NonNull String releaseTime;
    protected @NonNull String logoUrl; // can be empty string ""

    public NovelDataSourceBasic() {
        // init values
        name = "Default";
        author = "MewX";
        url = "http://mewx.org";
        versionCode = 1;
        releaseTime = "2016/04/12";
        logoUrl = "";
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getAuthor() {
        return author;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    @NonNull
    public Integer getVersionCode() {
        return versionCode;
    }

    @NonNull
    public String getReleaseTime() {
        return releaseTime;
    }

    @NonNull
    public String getLogoUrl() {
        return logoUrl;
    }

    // activities
    public abstract NetRequest[] getUltraRequests(String tag, @NonNull String preRequestContent);
    public abstract void ultraReturn(String tag, byte[][] requestResult); // left empty is okay, after this call parse function

    // get novel categories
    public abstract @NonNull String[] getCategories();

    // get novel list & page
    public abstract boolean judgeIs404(String pageContent); // parent class calls this function

    public abstract NetRequest getMainListRequest(int pageNum);
    public abstract List<NovelInfo> parseMainListRequestResult(String pageContent);
    public abstract PageNumBetween getMainListPageNum();

    public abstract NetRequest getSpecificListRequest(String categoryName, int pageNum);
    public abstract List<NovelInfo> parseSpecificListRequestResult(String categoryName, String pageContent);
    public abstract PageNumBetween getSpecificListPageNum(String categoryName);

    public abstract NetRequest getNovelInfoRequest(String tag); // tag - novel id
    public abstract NovelInfo parseNovelInfo(String content);
    public abstract NetRequest getNovelVolumeRequest(String tag); // TODO: if return null, use request above
    public abstract List<VolumeInfo> parseNovelVolume(String content);

    public abstract NetRequest getNovelContentRequest(String tag); // may too large
    public abstract NovelContent parseNovelContent(String content); // parse html novel, images

    protected void Logv(String msg) {
        Log.v(TAG, msg);
    }

    protected void Loge(String msg) {
        Log.e(TAG, msg);
    }
}
