package org.mewx.projectprpr.plugin.component;

import org.mewx.projectprpr.global.YBL;
import org.mewx.projectprpr.plugin.NovelDataSourceBasic;

/**
 * Generate dataSource class.
 * Created by MewX on 04/27/2016.
 */
public class PlugInTools {
    public static NovelDataSourceBasic generateNovelDataSourceBasic(PluginInfo pluginInfo) {
        try {
            Class<?> aClass = Class.forName(YBL.PLUGIN_PACKAGE + "." + pluginInfo.getClassName());
            return (NovelDataSourceBasic) aClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
