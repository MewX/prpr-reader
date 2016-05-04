package org.mewx.projectprpr.reader.setting;

import android.graphics.Color;

import org.mewx.projectprpr.global.YBL;
import org.mewx.projectprpr.toolkit.FigureTool;
import org.mewx.projectprpr.toolkit.FileTool;

/**
 * Created by MewX on 2015/7/8.
 *
 * This is the first version of reader activity setting.
 * New version extends from this setting class.
 */
public class ReaderSettingBasic {
    /**
     * Setting values, containing default value;
     * Setting Class must be defined before reader activity created.
     */

    // enum type
    public enum PAGE_BACKGROUND_TYPE {
        SYSTEM_DEFAULT,
        CUSTOM
    }

    // global settings
    public final int fontColorLight = Color.parseColor("#32414E"); // for dark background (ARGB)
    public final int fontColorDark = Color.parseColor("#444444"); // for light background
    public final int bgColorLight = Color.parseColor("#CFBEB6");
    public final int bgColorDark = Color.parseColor("#090C13");
    public final int widgetHeight = 24; // in "dp"
    public final int widgetTextSize = 12; // in "sp"

    // font setting
    private int fontSize = 18; // in "sp"
    private boolean useCustomFont = false; // Custom font must declare this first!
    private String customFontPath = "";

    // paragraph setting
    private int lineDistance = 16; // in "dp"
    private int paragraphDistance = 20; // in "dp"
    private int paragraghEdgeDistance = 8; // in "dp", text part edge distance (left&right)

    // page setting
    private int pageEdgeDistance = 8; // in "dp", top&right&bottom&left 4 directions distances to screen edge, and paragraph to side view widgets
    private PAGE_BACKGROUND_TYPE pageBackgroundType = PAGE_BACKGROUND_TYPE.SYSTEM_DEFAULT;
    private String pageBackgroundCustomPath = "";


    /**
     * Construct Function
     */
    public ReaderSettingBasic() {
        // font size
        String str = YBL.getFromAllSetting(YBL.SettingItemsBasic.reader_font_size);
        if(str != null && FigureTool.isInteger(str)) {
            int temp = Integer.parseInt(str);
            if(8 <= temp && temp <= 32)
                fontSize = temp;
        }

        // line distance
        str = YBL.getFromAllSetting(YBL.SettingItemsBasic.reader_line_distance);
        if(str != null && FigureTool.isInteger(str)) {
            int temp = Integer.parseInt(str);
            if(0 <= temp && temp <= 32)
                lineDistance = temp;
        }

        // paragraph distance
        str = YBL.getFromAllSetting(YBL.SettingItemsBasic.reader_paragraph_distance);
        if(str != null && FigureTool.isInteger(str)) {
            int temp = Integer.parseInt(str);
            if(0 <= temp && temp <= 48)
                paragraphDistance = temp;
        }

        // paragraph edge distance
        str = YBL.getFromAllSetting(YBL.SettingItemsBasic.reader_paragraph_edge_distance);
        if(str != null && FigureTool.isInteger(str)) {
            int temp = Integer.parseInt(str);
            if(0 <= temp && temp <= 16)
                paragraghEdgeDistance = temp;
        }

        // background path
        str = YBL.getFromAllSetting(YBL.SettingItemsBasic.reader_background_path);
        if(str != null) {
            if(str.equals("0")) {
                pageBackgroundType = PAGE_BACKGROUND_TYPE.SYSTEM_DEFAULT;
            }
            else if(FileTool.existFile(str)) {
                pageBackgroundType = PAGE_BACKGROUND_TYPE.CUSTOM;
                pageBackgroundCustomPath = str;
            }
        }

        // font path
        str = YBL.getFromAllSetting(YBL.SettingItemsBasic.reader_font_path);
        if(str != null) {
            if(str.equals("0")) {
                useCustomFont = false;
            }
            else if(FileTool.existFile(str)) {
                useCustomFont = true;
                customFontPath = str;
            }
        }
    }


    /**
     * gets & sets functions
     */

    public void setFontSize(int s) {
        fontSize = s;
        YBL.setToAllSetting(YBL.SettingItemsBasic.reader_font_size, Integer.toString(s));
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setUseCustomFont(boolean b) {
        if(!b) setCustomFontPath("0");
        useCustomFont = b;
    }

    public boolean getUseCustomFont() {
        return useCustomFont;
    }

    public void setCustomFontPath(String s) {
        // should test file before set this value, allow setting, but not allow use!
        customFontPath = s;
        useCustomFont = !s.equals("0");
        YBL.setToAllSetting(YBL.SettingItemsBasic.reader_font_path, s);
    }

    public String getCustomFontPath() {
        return customFontPath;
    }

    public void setLineDistance(int l) {
        lineDistance = l;
        YBL.setToAllSetting(YBL.SettingItemsBasic.reader_line_distance, Integer.toString(l));
    }

    public int getLineDistance() {
        return lineDistance;
    }

    public void setParagraphDistance(int l) {
        paragraphDistance = l;
        YBL.setToAllSetting(YBL.SettingItemsBasic.reader_paragraph_distance, Integer.toString(l));
    }

    public int getParagraphDistance() {
        return paragraphDistance;
    }

    public void setParagraphEdgeDistance(int l) {
        paragraghEdgeDistance = l;
        YBL.setToAllSetting(YBL.SettingItemsBasic.reader_paragraph_edge_distance, Integer.toString(l));
    }

    public int getParagraphEdgeDistance() {
        return paragraghEdgeDistance;
    }

    public void setPageEdgeDistance(int l) {
        pageEdgeDistance = l;
    }

    public int getPageEdgeDistance() {
        return pageEdgeDistance;
    }

    public void setPageBackgroundType(PAGE_BACKGROUND_TYPE t) {
        if(t == PAGE_BACKGROUND_TYPE.SYSTEM_DEFAULT) setPageBackgroundCustomPath("0");
        pageBackgroundType = t;
    }

    public PAGE_BACKGROUND_TYPE getPageBackgroundType() {
        return pageBackgroundType;
    }

    public void setPageBackgroundCustomPath(String s) {
        pageBackgroundCustomPath = s;
        if(s.equals("0")) pageBackgroundType = PAGE_BACKGROUND_TYPE.SYSTEM_DEFAULT;
        else pageBackgroundType = PAGE_BACKGROUND_TYPE.CUSTOM;
        YBL.setToAllSetting(YBL.SettingItemsBasic.reader_background_path, s);
    }

    public String getPageBackgrounCustomPath() {
        return pageBackgroundCustomPath;
    }
}
