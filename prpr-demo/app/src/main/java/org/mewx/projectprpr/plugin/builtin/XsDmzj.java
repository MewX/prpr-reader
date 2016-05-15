package org.mewx.projectprpr.plugin.builtin;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mewx.projectprpr.R;
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
@SuppressWarnings("unused")
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
        tag = "dmzjqxs";
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
        return new NetRequest(NetRequest.REQUEST_TYPE.GET, url + "/update_" + pageNum + ".shtml", null);
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
            cv.put(getNovelInfoElementName(R.string.novel_info_category), matcher.group(5).trim());
            cv.put(getNovelInfoElementName(R.string.novel_info_status), matcher.group(6).trim());
            cv.put(getNovelInfoElementName(R.string.novel_info_last_update), matcher.group(7).trim());
            list.add(new NovelInfo(matcher.group(1).trim(), matcher.group(3).trim(), matcher.group(4).trim(), matcher.group(2).trim(), cv));
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
        return new NetRequest(NetRequest.REQUEST_TYPE.GET, url + "/tags/js/"
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
                String novelTag = Pattern.compile("/(\\d*?)/", Pattern.DOTALL).matcher(jsonObject.getString("lnovel_url")).group(1);
                ContentValues cv = new ContentValues();
                cv.put(getNovelInfoElementName(R.string.novel_info_latest_chapter), jsonObject.getString("last_chapter_name").trim());
                cv.put(getNovelInfoElementName(R.string.novel_info_short_intro), jsonObject.getString("m_intro").trim());
                if(!TextUtils.isEmpty(jsonObject.getString("status").trim()))
                    cv.put(getNovelInfoElementName(R.string.novel_info_status), jsonObject.getString("status").trim());

                list.add(new NovelInfo(novelTag, jsonObject.getString("full_name").trim(),
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
        return new NetRequest(NetRequest.REQUEST_TYPE.GET, url + "/" + tag + "/index.shtml", null);
    }

    @Override
    public NovelInfo parseNovelInfo(String content) {
        // id(tag), coverUrl, title, author, category, status, lastUpdate
        // "page_url = '/(.+?)/index.+?class="main".+?src="(.+?)".*?<h3>(.+?)</.+?作者：(.+?)</.+?类型：(.+?)</.+?状态：(.+?)</.+?更新：(.+?)</p>.+?小说介绍：.+?<p>(.+?)</p>";
        final String regex = "page_url = '/(.+?)/index.+?class=\"main\".+?src=\"(.+?)\".*?<h3>(.+?)</.+?作者：(.+?)</.+?类型：(.+?)</.+?状态：(.+?)</.+?更新：(.+?)</p>.+?小说介绍：.+?<p>(.+?)</p>";
        Matcher infoMatcher = Pattern.compile(regex, Pattern.DOTALL).matcher(content);
        infoMatcher.find(); // need do this!

        ContentValues cv = new ContentValues();
        cv.put(getNovelInfoElementName(R.string.novel_info_category), infoMatcher.group(5).trim());
        cv.put(getNovelInfoElementName(R.string.novel_info_status), infoMatcher.group(6).trim());
        cv.put(getNovelInfoElementName(R.string.novel_info_last_update), infoMatcher.group(7).trim());
        cv.put(getNovelInfoElementName(R.string.novel_info_full_intro), Html.fromHtml(infoMatcher.group(8).trim()).toString()); // remove </br>

        return new NovelInfo(infoMatcher.group(1).trim(), infoMatcher.group(3).trim(), infoMatcher.group(4).trim(),
                infoMatcher.group(2).trim(), cv);
    }

    @Override
    public NetRequest getNovelVolumeRequest(String tag) {
        // use the last request result
        return null;
    }

    @Override
    public List<VolumeInfo> parseNovelVolume(String content) {
        final String volumeRegex = "chapnamesub\">(.+?)<.+?(volume_list|</script>)"; // get group(1)
        final String chapterRegex = "chapter_list\\[\\d+?\\]\\[.+?href=\"(.+?)\".+?>(.+?)<";

        // parse volumes and chapters
        List<VolumeInfo> list = new ArrayList<>();
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
    public NetRequest getNovelChapterRequest(String tag) {
        return null;
    }

    @Override
    public List<ChapterInfo> parseNovelChapter(String content) {
        return null;
    }

    @Override
    public NetRequest getNovelContentRequest(String tag) {
        // url is tag, this is a pre-request
        return new NetRequest(NetRequest.REQUEST_TYPE.GET, this.url + tag, null);
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

        // title, pageCount, content
        final String contentRegex = "tit\">(.+?)</.+?/共(\\d+?)页.+?\">(.+?)</div>";
        Matcher contentMatcher = Pattern.compile(contentRegex, Pattern.DOTALL).matcher(preRequestContent);
        contentMatcher.find();
        novelContentSaveTemp = purifyNovelContent(contentMatcher.group(3));
        novelContentTagTemp = Html.fromHtml(contentMatcher.group(1)).toString();

        int pageCount = Integer.valueOf(contentMatcher.group(2));
        if(pageCount < 2) return new NetRequest[0];
        else {
            NetRequest[] temp = new NetRequest[pageCount - 1];
            for(int i = 2; i <= pageCount; i ++) {
                temp[i - 2] = new NetRequest(NetRequest.REQUEST_TYPE.GET, url + tag.replaceAll("\\.shtml", "_" + i + ".shtml"), null);
                Loge(temp[i-2].getFullGetUrl());
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
            if (TextUtils.isEmpty(temp)) continue;

            if (temp.contains(imageSearch)) {
                // this is an image
                Matcher matcher = Pattern.compile(imageRegex, Pattern.DOTALL).matcher(temp);
                while (matcher.find()) {
                    nc.addToNovelContent(new NovelContentLine(NovelContentLine.TYPE.IMAGE_URL,
                            imgBaseUrl + matcher.group(1)));
                }
            } else {
                // process as text
                nc.addToNovelContent(new NovelContentLine(NovelContentLine.TYPE.TEXT, purifyHtmlToDisplayable(temp)));
            }
        }
        return nc;
    }

    private String purifyHtmlToDisplayable(String text) {
        return Html.fromHtml(text).toString();
    }

    @Override
    public NetRequest[] getSearchRequest(String query) {
        NetRequest[] requests = new NetRequest[1];
        ContentValues cv = new ContentValues();
        cv.put("s", query);
        requests[0] = new NetRequest(NetRequest.REQUEST_TYPE.GET, "http://s.acg.178.com/lnovelsum/search.php", cv);
        return requests;
    }

    @Override
    public List<NovelInfo> parseSearchResults(String[] contents) {
        List<NovelInfo> list = new ArrayList<>();
        if (contents.length > 0) {
            /*
            var g_search_data =
            [{"author":"\u7530\u4e2d\u7f57\u5bc6\u6b27","image_url":"http:\/\/xs.dmzj.com\/img\/webpic\/16\/0007Y.jpg","lnovel_name":"AURA\u3000\u9b54\u9f99\u9662...","last_chapter_name":"\u5168\u4e00\u5377","lnovel_url":"..\/6\/index.shtml","last_chapter_url":"..\/6\/37\/201.shtml","full_name":"AURA\u3000\u9b54\u9f99\u9662\u5149\u7259\u6700\u540e\u7684\u6218\u6597","fullc_name":"\u5168\u4e00\u5377","types":"\u6821\u56ed","status":"\u5df2\u5b8c\u7ed3","description":"\u5185\u5bb9\u7b80\u4ecb\uff1a\u90a3\u4e00\u5929\u3002\u5fd8\u8bb0\u5e26\u8bfe\u672c\u56de\u5bb6\u7684\u6211\uff0c\u5728\u534a\u591c\u6e9c\u8fdb\u5b66\u6821\uff0c\u7136\u540e\u9047\u89c1\u4e86\u5979\u3002\u90a3\u91cc\u662f\u901a\u5f80\u6559\u5ba4\u7684\u9636\u68af\u8f6c\u89d2\u5904\u3002\n\u5728\u51b0\u51b7\u6708\u5149\u5f62\u6210\u7684\u805a\u5149\u706f\u4e4b\u4e0b\uff0c\u6709\u4e00\u540d\u5c4f\u6c14\u51dd\u795e\u6ce8\u89c6\u7740\u9ed1\u6697\u7684\u5c11\u5973\u3002\u597d\u7f8e\u4e3d\u2014\u2014\u3002\n\u7ad9\u5728\u90a3\u91cc\u7684\u84dd\u8272\u9b54\u5973\uff0c\u91ca\u653e\u7740\u4ee4\u4eba\u4e3a\u4e4b\u503e\u5012\u7684\u6c14\u606f\u3002\u2026\u2026\u4e0d\uff0c\u6162\u7740\uff0c\u8fd9\u53ef\u4e0d\u662f\u5f00\u73a9\u7b11\u7684\u3002\u6211\u505c\u6b62\u4e86\u8fd9\u6837\u7684\u5984\u60f3\u3002\u6211\u6210\u529f\u5728\u5347\u4e0a\u9ad8\u4e2d\u4e4b\u540e\u6539\u5934\u6362\u9762\u4e86\uff01\n\u539f\u672c\u5e94\u8be5\u662f\u8fd9\u6837\u624d\u5bf9\uff0c\u53ef\u662f\u8fd9\u4e2a\u5984\u60f3\u5c11\u5973\u662f\u600e\u4e48\u56de\u4e8b\uff01\n\u201c\u6ca1\u6709\u4efb\u4f55\u9632\u536b\u673a\u5236\u7684\u73b0\u8c61\u754c\u4eba\uff0c\u65e0\u6cd5\u62b5\u79a6\u60c5\u62a5\u4f53\u7684\u5f3a\u5236\u5e72\u6d89\u3002\u201d\n\u201c\u6211\u5b8c\u5168\u542c\u4e0d\u61c2\u4f60\u5728\u8bf4\u4ec0\u4e48\u3002\u201d\u5176\u5b9e\u6211\u5927\u81f4\u80fd\u7406\u89e3\u8fd9\u756a\u8bdd\u7684\u542b\u610f\u3002\n\n\u8f6c\u81ea \u8f7b\u4e4b\u56fd\u5ea6"},
            {"author":"\u7530\u4e2d\u82b3\u6811","image_url":"http:\/\/xs.dmzj.com\/img\/webpic\/13\/mhds.jpg","lnovel_name":"\u6ce2\u7f57\u7684\u6d77\u590d\u4ec7\u8bb0","last_chapter_name":"\u300a\u6ce2\u7f57\u7684\u6d77\u590d\u4ec7...","lnovel_url":"..\/19\/index.shtml","last_chapter_url":"..\/19\/67\/417.shtml","full_name":"\u6ce2\u7f57\u7684\u6d77\u590d\u4ec7\u8bb0","fullc_name":"\u300a\u6ce2\u7f57\u7684\u6d77\u590d\u4ec7\u8bb0\u300b\u53d6\u6750\u540c\u884c\u8bb0","types":"\u5192\u9669","status":"\u5df2\u5b8c\u7ed3","description":"\u4ee5\u4e8c\u5341\u4e8c\u5c81\u7684\u5c0f\u5c0f\u5e74\u7eaa\u5c31\u88ab\u6c49\u8428\u540c\u76df\u7684\u5bcc\u5546\u59d4\u4efb\u4e3a\u8239\u957f\u7684\u5e74\u8f7b\u4eba\u827e\u529b\u514b\u5728\u8d2d\u4e70\u4e86\u6602\u8d35\u7684\u7425\u73c0\u56de\u7a0b\u7684\u8def\u4e0a\uff0c\u906d\u5230\u8239\u5458\u4eec\u7684\u80cc\u53db\uff0c\u88ab\u4e22\u5230\u6d77\u91cc\u53bb\u3002\n\u88ab\u4e00\u4e2a\u81ea\u79f0\u662f\u9b54\u5973\u7684\u8001\u5a46\u5a46\u6551\u52a9\u800c\u6361\u56de\u4e00\u6761\u547d\u7684\u4ed6\u4e3a\u4e86\u89e3\u5f00\u4e8b\u4ef6\u80cc\u540e\u7684\u9634\u8c0b\uff0c\u548c\u6b66\u6280\u9ad8\u8d85\u7684\u9a91\u58eb\u53ca\u9ed1\u732b\u300c\u5c0f\u767d\u300d\u5e76\u80a9\u4f5c\u6218\uff0c\u4e3a\u6d17\u5237\u81ea\u5df1\u7684\u6e05\u767d\u800c\u52c7\u5f80\u76f4\u524d\u3002\n\u827e\u529b\u514b\u548c\u9634\u8c0b\u7684\u9ed1\u5e55\u4e4b\u95f4\u7684\u5bf9\u51b3\u4f1a\u6709\u4ec0\u4e48\u6837\u7684\u7ed3\u679c\uff1f\uff01\n\n\u8f6c\u81ea \u8f7b\u4e4b\u56fd\u5ea6"},
             */
            /*
            {
                "author": "田中罗密欧",
                "image_url": "http://xs.dmzj.com/img/webpic/16/0007Y.jpg",
                "lnovel_name": "AURA　魔龙院...",
                "last_chapter_name": "全一卷",
                "lnovel_url": "../6/index.shtml",
                "last_chapter_url": "../6/37/201.shtml",
                "full_name": "AURA　魔龙院光牙最后的战斗",
                "fullc_name": "全一卷",
                "types": "校园",
                "status": "已完结",
                "description": "内容简介：那一天。忘记带课本回家的我，在半夜溜进学校，然后遇见了她。那里是通往教室的阶梯转角处。
            在冰冷月光形成的聚光灯之下，有一名屏气凝神注视着黑暗的少女。好美丽——。
            站在那里的蓝色魔女，释放着令人为之倾倒的气息。……不，慢着，这可不是开玩笑的。我停止了这样的妄想。我成功在升上高中之后改头换面了！
            原本应该是这样才对，可是这个妄想少女是怎么回事！
            “没有任何防卫机制的现象界人，无法抵禦情报体的强制干涉。”
            “我完全听不懂你在说什么。”其实我大致能理解这番话的含意。

            转自 轻之国度"
            }
             */
            try {
                JSONArray jsonArray = new JSONArray(contents[0].substring(contents[0].indexOf('['), contents[0].lastIndexOf(']') + 1));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                    String TAG = Pattern.compile("/(\\d*?)/", Pattern.DOTALL).matcher(jsonObject.getString("lnovel_url")).group(1);
                    ContentValues cv = new ContentValues();
                    cv.put(getNovelInfoElementName(R.string.novel_info_latest_chapter), jsonObject.getString("last_chapter_name").trim());
                    cv.put(getNovelInfoElementName(R.string.novel_info_short_intro), jsonObject.getString("description").trim());
                    if(!TextUtils.isEmpty(jsonObject.getString("status").trim()))
                        cv.put(getNovelInfoElementName(R.string.novel_info_status), jsonObject.getString("status").trim());

                    list.add(new NovelInfo(TAG, jsonObject.getString("full_name").trim(),
                            jsonObject.getString("author").trim(), jsonObject.getString("image_url").trim(), cv));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
