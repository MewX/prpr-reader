package org.mewx.projectprpr.global;

import org.mewx.projectprpr.plugin.component.PluginInfo;

import java.io.File;

/**
 * This class stores all the global values.
 * Static for all classes to call.
 */
@SuppressWarnings("unused")
public class YBL {
    // enumerables
    public enum VERSION_TYPE_ENUM {
        TEST,
        PUBLISH
    }

    // global constant strings
    public static final String PLUGIN_PACKAGE = "org.mewx.projectprpr.plugin.builtin";
    public static final String PROJECT_FOLDER = "prpr";
    public static final String PROJECT_FOLDER_CACHE = PROJECT_FOLDER + File.separator + "cache";
    public static final String PROJECT_FOLDER_DOWNLOAD = PROJECT_FOLDER + File.separator + "downloads";
    private static final String PROJECT_FOLDER_NETNOVEL = PROJECT_FOLDER_DOWNLOAD + File.separator + "netnovel";

    public static final String STANDARD_CHARSET = "UTF-8";

    public static String getFileFullPath(String folder, String fileName) {
        // still do a check procedure
        return folder.charAt(folder.length() - 1) == File.separatorChar ?
                folder + fileName : folder + File.separator + fileName;
    }

    public static String getProjectFolderNetnovel(String dataSourceTag) {
        // get folder path, without back-leading separator.
        return PROJECT_FOLDER_NETNOVEL + File.separator + dataSourceTag;
    }

    // global constants
    public static final VERSION_TYPE_ENUM VERSION_TYPE = VERSION_TYPE_ENUM.TEST;
    public static final PluginInfo[] BUILTIN_PLUGIN = {new PluginInfo("XsDmzj", PluginInfo.PluginType.BUILTIN, "")};


    // global common variables
    private static boolean skipSplashScreen = false;
    public static boolean getSkipSplashScreen() {
        return skipSplashScreen;
    }
    public static void setSkipSplashScreen(boolean skip) {
        skipSplashScreen = skip;
    }

}
