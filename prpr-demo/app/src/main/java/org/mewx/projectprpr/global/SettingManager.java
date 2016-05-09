package org.mewx.projectprpr.global;

import android.content.ContentValues;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.mewx.projectprpr.toolkit.FileTool;

/**
 * Created by MewX on 05/08/2016.
 * This for all the settings.
 */
@SuppressWarnings("unused")
public class SettingManager {
    private static final String TAG = SettingManager.class.getSimpleName();

    private static ContentValues allSetting;

    public static void loadAllSetting() {
        allSetting = new ContentValues();
        String h = FileTool.loadFullFileContent(YBL.getStoragePath(YBL.PROJECT_FILE_READER_SETTINGS));
        String[] sets = h.split("\\|\\|\\|\\|");
        for(String set : sets) {
            String[] temp = set.split("::::");
            if(temp.length != 2 || temp[0] == null || temp[0].length() == 0 || temp[1] == null || temp[1].length() == 0) continue;

            allSetting.put(temp[0], temp[1]);
        }

        if(TextUtils.isEmpty(getFromAllSetting(YBL.SettingItemsBasic.version)))
            setToAllSetting(YBL.SettingItemsBasic.version, "1");
    }

    public static void saveAllSetting() {
        if(allSetting == null) loadAllSetting();
        String result = "";
        for( String key : allSetting.keySet() ) {
            if(!result.equals("")) result = result + "||||";
            result = result + key + "::::" + allSetting.getAsString(key);
        }

        FileTool.writeFullFileContent(YBL.getStoragePath(YBL.PROJECT_FILE_READER_SETTINGS), result);
    }

    @Nullable
    public static String getFromAllSetting(YBL.SettingItemsBasic name) {
        if(allSetting == null) loadAllSetting();
        return allSetting.getAsString(name.toString());
    }

    public static void setToAllSetting(YBL.SettingItemsBasic name, String value) {
        if(allSetting == null) loadAllSetting();
        if(name != null && value != null) {
            allSetting.remove(name.toString());
            allSetting.put(name.toString(), value);
            saveAllSetting();
        }
    }
}
