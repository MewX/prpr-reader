package org.mewx.projectprpr.global;

import android.os.Environment;

import okhttp3.OkHttpClient;

import org.mewx.projectprpr.MyApp;
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
    public static final String FOLDER_NAME_CACHE = "cache";
    public static final String FOLDER_NAME_DOWNLOAD = "downloads";
    public static final String FOLDER_NAME_IMAGE = "imgs";
    public static final String FOLDER_NAME_NETNOVEL = "netnovel";
    public static final String PROJECT_FOLDER = "prpr";
    public static final String PROJECT_FOLDER_CACHE = PROJECT_FOLDER + File.separator + FOLDER_NAME_CACHE;
    public static final String PROJECT_FOLDER_DOWNLOAD = PROJECT_FOLDER + File.separator + FOLDER_NAME_DOWNLOAD;
    private static final String PROJECT_FOLDER_NETNOVEL = PROJECT_FOLDER_DOWNLOAD + File.separator + FOLDER_NAME_NETNOVEL;

    public static final String STANDARD_CHARSET = "UTF-8";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/14.14295 ProjectPRPR/1.00";

    public static String getStoragePath(String folder) {
        // TODO: set external storage or internal storage
        return Environment.getExternalStorageDirectory() + File.separator + folder;
    }

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
    public static final int IMAGE_CACHE_DISK_SIZE = 100 * 1024 * 1024;
    public static final PluginInfo[] BUILTIN_PLUGIN = {new PluginInfo("XsDmzj", PluginInfo.PluginType.BUILTIN, "")};

    // global common variables
    public static OkHttpClient globalOkHttpClient3;

    private static boolean skipSplashScreen = false;
    public static boolean getSkipSplashScreen() {
        return skipSplashScreen;
    }
    public static void setSkipSplashScreen(boolean skip) {
        skipSplashScreen = skip;
    }

}
