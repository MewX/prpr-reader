package org.mewx.projectprpr.plugin.builtin;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mewx.projectprpr.global.YBL;
import org.mewx.projectprpr.plugin.NovelDataSourceBasic;
import org.mewx.projectprpr.plugin.component.ChapterInfo;
import org.mewx.projectprpr.plugin.component.NetRequest;
import org.mewx.projectprpr.plugin.component.NovelContent;
import org.mewx.projectprpr.plugin.component.NovelContentLine;
import org.mewx.projectprpr.plugin.component.NovelInfo;
import org.mewx.projectprpr.plugin.component.PageNumBetween;
import org.mewx.projectprpr.plugin.component.VolumeInfo;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is built-in plug-in support for xs.dmzj.com.
 * Created by MewX on 3/31/2016.
 *
 * q.dmzj.com/update_1.shtml (max: update_5.shtml)
 * q.dmzj.com/tags/js/maoxian.js
 * Tags:
 * 冒险 maoxian
 * 搞笑 gaoxiao
 * 格斗 gedou
 * 科幻 kehuan
 * 爱情 aiqing
 * 侦探 zhentan
 * 魔法 mofa
 * 神鬼 shengui
 * 校园 xiaoyuan
 * 恐怖 kongbu
 * 其它 qita
 * 连载 lianzaizhong
 * 完结 yiwanjie
 *
 */
public class XsDmzj extends NovelDataSourceBasic {
    private static final String imgBaseUrl = "http://xs.dmzj.com";

    private final String[] categoriesShowName = {
            "冒险",
            "搞笑",
            "格斗",
            "科幻",
            "爱情",
            "侦探",
            "魔法",
            "神鬼",
            "校园",
            "恐怖",
            "其它",
            "连载",
            "完结"
    };
    private final String[] categoriesRefId = {
            "maoxian",
            "gaoxiao",
            "gedou",
            "kehuan",
            "aiqing",
            "zhentan",
            "mofa",
            "shengui",
            "xiaoyuan",
            "kongbu",
            "qita",
            "lianzaizhong",
            "yiwanjie"
    };
    private int[] categoriesPage = new int[categoriesRefId.length]; // set when used, start from 1
    private String novelContentTagTemp = "";
    private NovelContent novelContentSaveTemp;

    public XsDmzj() {
        // init values
        name = "动漫之家·轻小说";
        author = "MewX";
        url = "http://q.dmzj.com";
        versionCode = 1;
        releaseTime = "2016/04/18";
        logoUrl = "http://xs.dmzj.com/images/xs_logo_dmzj.png";
    }

    @NonNull
    @Override
    public String[] getCategories() {
        return categoriesShowName; // the caller gets & refers category by "ShowName"
    }

    @Override
    public boolean judgeIs404(String pageContent) {
        return pageContent.contains("<title>页面找不到</title>");
    }

    @Override
    public NetRequest getMainListRequest(int pageNum) {
        return new NetRequest(NetRequest.REQUEST_TYPE.GET, "q.dmzj.com/update_" + pageNum + ".shtml", null);
    }

    @Override
    public List<NovelInfo> parseMainListRequestResult(String pageContent) {
        /*
        <li class="clearfix section1">
        <a href="/1686/index.shtml" rel="external" class="pic">
            <img src="http://xs.dmzj.com/img/webpic/23/151117xianshangyouxil.jpg" alt="线上游戏的老婆不可能是女生？" />
            <div class="con">
                <h3>线上游戏的老婆不可能是女生？</h3>
                <p>
                    听猫芝居
                </p>
                <p>
                    搞笑/爱情
                </p>
                <p>
                    连载中
                </p>
                <p>
                    2016/4/19 14:31
                </p>
            </div>
        </a>
        <a href="/1686/7738/59936.shtml" title="第八卷-序章 盾之传说" class="tool">
            <span class="io1"></span>
            <span class="h">第八卷-序章 盾之传说</span>
        </a>
        </li>
        */
        List<NovelInfo> list = new ArrayList<>();
        String regex = "clearfix section1.+?href=\"/(\\d.+?)/index.shtml.+?<img src=\"(.+?)\".+?<h3>(.*?)</h3>.*?<p>\\s*?(.*?)\\s*?</p>.*?<p>\\s*?(.*?)\\s*?</p>.*?<p>\\s*?(.*?)\\s*?</p>.*?<p>\\s*?(.*?)\\s*?</p>";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(pageContent);
        while (matcher.find()) {
            ContentValues cv = new ContentValues();
            cv.put("type", matcher.group(5));
            cv.put("status", matcher.group(6));
            cv.put("update", matcher.group(7));
            list.add(new NovelInfo(matcher.group(1), matcher.group(3), matcher.group(4), matcher.group(2), cv));
        }
        return list;
    }

    @Override
    public PageNumBetween getMainListPageNum() {
        return new PageNumBetween(1, 5);
    }

    private int getCategoryIdByName(String name, String caller) {
        int id = 0;
        for(; id < categoriesShowName.length; id ++) {
            if(categoriesShowName[id].equals(name)) break;
        }
        if(id >= categoriesShowName.length) {
            Loge("[categoryName: " + name + "] not found in function [" + caller + "], set default.");
            id = 0;
        }
        return id;
    }

    @Override
    public NetRequest getSpecificListRequest(String categoryName, int pageNum) {
        return new NetRequest(NetRequest.REQUEST_TYPE.GET, "http://q.dmzj.com/tags/js/"
                + categoriesRefId[getCategoryIdByName(categoryName, "getSpecificListRequest")] + ".js", null);
    }

    @Override
    public List<NovelInfo> parseSpecificListRequestResult(String categoryName, String pageContent) {
        /*
        RAW:
        {"author":"\u3061\u3085\u30fc\u3070\u3061\u3070\u3061\u3053","image_url":"..\/img\/webpic
        \/18\/jsbqsn3578l.jpg","full_name":"\u91d1\u5c5e\u7403\u68d2\u5c11\u5973","lnovel_name":
        "\u91d1\u5c5e\u7403\u68d2\u5c11\u5973","fullc_name":"\u7b2c\u4e00\u5377","last_chapter_name"
        :"\u7b2c\u4e00\u5377","lnovel_url":"\/2085\/index.shtml","last_chapter_url":"\/2085\/7613\/
        59918.shtml","m_image_url":"http:\/\/xs.dmzj.com\/img\/webpic\/18\/jsbqsn3578l.jpg","m_intro":
        "5\u6708\u4e2d\u7684\u9177\u70ed\u7684\u4e00\u5929\uff0c\u5728\u8f66\u7ad9\u9047\u89c1\u4e86
        \u4e00\u4e2a\u4e16\u754c\u4e0a\u7b2c\u4e00\u53ef\u7231\u7684\u5c11\u5973\u3002\u5728\u7b2c
        \u4e8c\u5929\uff0c...","status":"[<span class=\"red1_font12\">\u5b8c<\/span>]"}

        PARSED:
        {
            "author": "ちゅーばちばちこ",
            "image_url": "../img/webpic/18/jsbqsn3578l.jpg",
            "full_name": "金属球棒少女",
            "lnovel_name": "金属球棒少女",
            "fullc_name": "第一卷",
            "last_chapter_name": "第一卷",
            "lnovel_url": "/2085/index.shtml",
            "last_chapter_url": "/2085/7613/59918.shtml",
            "m_image_url": "http://xs.dmzj.com/img/webpic/18/jsbqsn3578l.jpg",
            "m_intro": "5月中的酷热的一天，在车站遇见了一个世界上第一可爱的少女。在第二天，...",
            "status": "[<span class=\"red1_font12\">完</span>]"
        }

        {
            "author": "小太刀右京",
            "image_url": "../img/webpic/29/cskys.jpg",
            "full_name": "超时空要塞-边界",
            "lnovel_name": "超时空要塞-边界",
            "fullc_name": "短篇小说
        ",
            "last_chapter_name": "短篇小说
        ",
            "lnovel_url": "/50/index.shtml",
            "last_chapter_url": "/50/183/1095.shtml",
            "m_image_url": "http://xs.dmzj.com/img/webpic/29/cskys.jpg",
            "m_intro": "内容简介：公元2059年。因与巨大的外星人之间的星际战争而频临灭亡的...",
            "status": ""
        }
        */
        int id = getCategoryIdByName(categoryName, "parseSpecificListRequestResult");
        List<NovelInfo> list = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(pageContent.substring(pageContent.indexOf('['), pageContent.lastIndexOf(']') + 1));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                String TAG = Pattern.compile("/(\\d*?)/", Pattern.DOTALL).matcher(jsonObject.getString("lnovel_url")).group(1);
                ContentValues cv = new ContentValues();
                cv.put("latest_chapter", jsonObject.getString("last_chapter_name").trim());
                cv.put("short_intro", jsonObject.getString("m_intro").trim());
                if(!TextUtils.isEmpty(jsonObject.getString("status").trim()))
                    cv.put("status", jsonObject.getString("status").trim());

                list.add(new NovelInfo(TAG, jsonObject.getString("full_name").trim(),
                        jsonObject.getString("author").trim(), jsonObject.getString("m_image_url").trim(), cv));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        categoriesPage[id] = 1; // set total pageCount
        return list;
    }

    @Override
    public PageNumBetween getSpecificListPageNum(String categoryName) {
        return new PageNumBetween(1, categoriesPage[getCategoryIdByName(categoryName, "getSpecificListPageNum")]);
    }

    @Override
    public NetRequest getNovelInfoRequest(String tag) {
        return new NetRequest(NetRequest.REQUEST_TYPE.GET, "http://q.dmzj.com/" + tag + "/index.shtml", null);
    }

    @Override
    public NovelInfo parseNovelInfo(String content) {
        // id(tag), coverUrl, title, author, category, status, lastUpdate
        final String regex = "page_url = '/(.+?)/index.+?class=\"main\".+?src=\"(.+?)\".*?<h3>(.+?)</.+?作者：(.+?)</.+?类型：(.+?)</.+?状态：(.+?)</.+?更新：(.+?)</p>.+?小说介绍：.+?<p>(.+?)</p>";
        Matcher infoMatcher = Pattern.compile(regex, Pattern.DOTALL).matcher(content);

        ContentValues cv = new ContentValues();
        cv.put("category", infoMatcher.group(5));
        cv.put("status", infoMatcher.group(6));
        cv.put("last_update", infoMatcher.group(7));
        cv.put("full_intro", infoMatcher.group(8));

        return new NovelInfo(infoMatcher.group(1), infoMatcher.group(3), infoMatcher.group(4),
                infoMatcher.group(2), cv);
    }

    @Override
    public NetRequest getNovelVolumeRequest(String tag) {
        // use the last request result
        return null;
    }

    @Override
    public List<VolumeInfo> parseNovelVolume(String content) {
        final String fullIntroRegex = "detail_block.*?<p>(.+?)</p>";
        final String volumeRegex = "chapnamesub\">(.+?)<.+?(volume_list|</script>)"; // get group(1)
        final String chapterRegex = "chapter_list\\[\\d\\]\\[.+?href=\"(.+?)\".+?>(.+?)<";

        // parse volumes and chapters
        List<VolumeInfo> list = new ArrayList<>();
        content = Pattern.compile(fullIntroRegex, Pattern.DOTALL).matcher(content).group(1); // get volumes
        Matcher volumeMatcher = Pattern.compile(volumeRegex, Pattern.DOTALL).matcher(content);
        while (volumeMatcher.find()) {
            String tempVolume = volumeMatcher.group(); // get whole matched string
            Matcher chapterMatcher = Pattern.compile(chapterRegex, Pattern.DOTALL).matcher(tempVolume);

            VolumeInfo vi = new VolumeInfo(volumeMatcher.group(1), volumeMatcher.group(1));
            while (chapterMatcher.find()) {
                ChapterInfo ci = new ChapterInfo(chapterMatcher.group(2), chapterMatcher.group(1));
                vi.addToChapterList(ci);
            }
            list.add(vi);
        }
        return list;
    }

    @Override
    public NetRequest getNovelContentRequest(String tag) {
        // url is tag, this is a pre-request
        return new NetRequest(NetRequest.REQUEST_TYPE.ULTRA_REQUEST, this.url + tag, null);
    }

    @Override
    public NovelContent parseNovelContent(String content) {
        // no necessary to use "content"
        return novelContentSaveTemp;
    }

    @Override
    public NetRequest[] getUltraRequests(String tag, @NonNull String preRequestContent) {
        // preRequestContent is from getNovelContentRequest's ULTRA_REQUEST
        /*
        <p>【第十章】　前往地上&hellip;&hellip;（二）</p><p>
        </p><p>
        【Robot=105/Human=102】</p><p>
        </p><p>
        只有一条路可走。</p><p>
        摇篮的电池只能再维持二十二个小时，失去白雪公主的我们，也没有其他剩余电力了。既然如此，只有一条路可走。</p><p>
        &mdash;&mdash;唯有朝地面前进。</p><p>
        基本方针不变，无论如何，目标就是地面上的发电厂。我重新组织起散落在不同地点的村民，为每一组指派新领队，指示大家重新出发。如果可能，随时与其他组村民会合。若途中失去通讯，就在各自判断之下继续前进，以地表为目标&mdash;&mdash;迅速做出作战指示，我们重新振作出发。</p><p>
        第一个难关是&ldquo;产道&rdquo;。</p><p>
        &mdash;&mdash;小心谨慎，小心谨慎&hellip;&hellip;</p><p>
        */
//        // content, pages_mata
//        String contentRegex = "chapter_contents_first\">.+?<p>(.+?)</div>.+?jump_select(.+?)</select>";
//        String pagerRegex = "option value=\"(.*?)\""; // start from 2. (1 is "跳转到")
//
//        Matcher contentMatcher = Pattern.compile(contentRegex, Pattern.DOTALL).matcher(preRequestContent);
//        novelContentSaveTemp = purifyNovelContent(contentMatcher.group(1));
//
//        List<NetRequest> requestList = new ArrayList<>();
//        String pager = contentMatcher.group(2);
//        Matcher pagerMatcher = Pattern.compile(pagerRegex, Pattern.DOTALL).matcher(pager);
//        int count = 0;
//        while (pagerMatcher.find()) {
//            count += 1;
//            if(count > 2) {
//                // skip: <option value="">跳转到</option>
//                // skip: <option value="1">第1页</option>
//                // start from page 2
//                requestList.add(new NetRequest(NetRequest.REQUEST_TYPE.GET, tag.replace("\\.shtml", "_" + pagerMatcher.group(1) + ".shtml"), null));
//            }
//        }
//
//        return (NetRequest[]) requestList.toArray();

        // title, pageCount, content
        final String contentRegex = "tit\">(.+?)</.+?/共(\\d+?)页.+?\">(.+?)</div>";
        Matcher contentMatcher = Pattern.compile(contentRegex, Pattern.DOTALL).matcher(preRequestContent);
        novelContentSaveTemp = purifyNovelContent(contentMatcher.group(3));
        novelContentTagTemp = Html.fromHtml(contentMatcher.group(1)).toString();

        int pageCount = Integer.valueOf(contentMatcher.group(2));
        if(pageCount < 2) return new NetRequest[0];
        else {
            NetRequest[] temp = new NetRequest[pageCount - 2];
            for(int i = 2; i <= pageCount; i ++) {
                temp[i - 1] = new NetRequest(NetRequest.REQUEST_TYPE.GET, tag.replace("\\.shtml", "_" + i + ".shtml"), null);
            }
            return temp;
        }

    }

    @Override
    public void ultraReturn(String tag, byte[][] requestResult) {
        if (novelContentSaveTemp == null) novelContentSaveTemp = new NovelContent();

        for (byte[] temp : requestResult) {
            try {
                novelContentSaveTemp.addToNovelContent(purifyNovelContent(new String(temp, YBL.STANDARD_CHARSET)));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        // then parent call parseNovelContent
    }

    private NovelContent purifyNovelContent(String content) {
        // TODO: remove html tags, and transform characters
        final String paragraphRegex = "p>(.*?)</p";
        final String imageSearch = "<img";
        final String imageRegex = "src=\".*?(/img.+?)\"";


        NovelContent nc = new NovelContent();
        Matcher paraMatcher = Pattern.compile(paragraphRegex, Pattern.DOTALL).matcher(content);

        while (paraMatcher.find()) {
            String temp = paraMatcher.group(1).trim();
            if (temp.contains(imageSearch)) {
                // this is an image
                nc.addToNovelContent(new NovelContentLine(NovelContentLine.TYPE.IMAGE_URL,
                        imgBaseUrl + Pattern.compile(imageRegex, Pattern.DOTALL).matcher(temp).group(1)));
            }
            else {
                // process as text
                nc.addToNovelContent(new NovelContentLine(NovelContentLine.TYPE.TEXT, purifyHtmlToDisplayable(temp)));
            }
        }

        return nc;
    }

    private String purifyHtmlToDisplayable(String text) {
        return Html.fromHtml(text).toString();
    }
}
