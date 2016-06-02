package org.mewx.projectprpr.global;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.mewx.projectprpr.MyApp;
import org.mewx.projectprpr.plugin.NovelDataSourceBasic;
import org.mewx.projectprpr.plugin.component.PluginInfo;
import org.mewx.projectprpr.toolkit.FileTool;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dalvik.system.DexClassLoader;

/**
 * Created by MewX on 05/15/2016.
 * Manage all the data source plugins.
 *
 * Files in: PROJECT_FOLDER_PLUGIN/org.mewx.prpr.xxxx(.dex)
 */
public class DataSourcePluginManager {
    private static final String TAG = DataSourcePluginManager.class.getSimpleName();
    private static final String OPTIMIZED_DEX_FOLDER = "outdex";

    private static ArrayList<PluginInfo> pluginListLocal = new ArrayList<>();
    private static List<PluginInfo> pluginListCloud = new ArrayList<>();

    public static void loadAllLocalDataSourcePlugin() {
        pluginListLocal = new ArrayList<>(Arrays.asList(G.BUILTIN_PLUGIN));

        // load local plugins, local plugin can overwrite built-in!!!
        final File optimizedDexOutputPath = MyApp.getContext().getDir(OPTIMIZED_DEX_FOLDER, 0);
        String[] pluginList = FileTool.getFileList(G.getStoragePath(G.PROJECT_FOLDER_PLUGIN));

        LocalPlugin:
        for (String pluginFileName : pluginList) {
            String fullPath = G.getStoragePath(G.PROJECT_FOLDER_PLUGIN + File.separator + pluginFileName);
            Log.e(TAG, "Full path: " + fullPath);
            DexClassLoader dexClassLoader = new DexClassLoader(fullPath, optimizedDexOutputPath.getAbsolutePath(), null, MyApp.getContext().getClassLoader());
            Class libProviderClass = null;
            try {
                libProviderClass = dexClassLoader.loadClass(pluginFileName.replace(".dex", ""));
                if (!(libProviderClass.newInstance() instanceof NovelDataSourceBasic)) continue;
                Log.e(TAG, pluginFileName + " is ok!" + ((NovelDataSourceBasic)libProviderClass.newInstance()).getName());

//                // 遍历类里所有方法
//                Method[] methods = libProviderClazz.getDeclaredMethods();
//                for (int i = 0; i < methods.length; i++) {
//                    Log.e(TAG, methods[i].toString());
//                }
//                Method start = libProviderClazz.getDeclaredMethod("func");// 获取方法
//                start.setAccessible(true);// 把方法设为public，让外部可以调用
//                String string = (String) start.invoke(libProviderClazz.newInstance());// 调用方法并获取返回值
//                Toast.makeText(this, string, Toast.LENGTH_LONG).show();

                // check if in pluginListLocal, if true, replace it
                PluginInfo pluginInfo = new PluginInfo(pluginFileName.replace(".dex", ""), PluginInfo.PluginType.DEX, fullPath);
                for(int i = 0; i < pluginListLocal.size(); i ++) {
                    if (pluginListLocal.get(i).getType() == PluginInfo.PluginType.BUILTIN
                            && pluginListLocal.get(i).getClassName().equals(pluginInfo.getClassName())) {
                        // replace this record
                        pluginListLocal.set(i, pluginInfo);
                        continue LocalPlugin;
                    }
                }

                // add to back
                pluginListLocal.add(pluginInfo);
            } catch (Exception exception) {
                // Handle exception gracefully here.
                exception.printStackTrace();
            }
        }
    }

    public static void loadAllCloudDataSourcePluginAsync() {
        // todo loadAllCloudDataSourcePluginAsync
    }

    @Nullable
    public static NovelDataSourceBasic loadDataSourcePluginClassByTag(@NonNull String dataSourceTag) {
        for (PluginInfo pi : pluginListLocal) {
            NovelDataSourceBasic novelDataSourceBasic = loadDataSourcePluginClassByInfo(pi);
            if (novelDataSourceBasic != null && novelDataSourceBasic.getTag().equals(dataSourceTag)) {
                return novelDataSourceBasic;
            }
        }
        return null;
    }

    @Nullable
    public static NovelDataSourceBasic loadDataSourcePluginClassByName(@NonNull String name) {
        for (PluginInfo pi : pluginListLocal) {
            if (pi.getClassName().equals(name)) {
                return loadDataSourcePluginClassByInfo(pi);
            }
        }
        return null;
    }

    @Nullable
    public static NovelDataSourceBasic loadDataSourcePluginClassById(int index) {
        if (0 <= index && index < pluginListLocal.size()) {
            return loadDataSourcePluginClassByInfo(pluginListLocal.get(index));
        }
        return null;
    }

    @Nullable
    public static NovelDataSourceBasic loadDataSourcePluginClassByInfo(PluginInfo pluginInfo) {
        NovelDataSourceBasic novelDataSourceBasic = null;
        try {
            switch (pluginInfo.getType()) {
                case BUILTIN:
                    novelDataSourceBasic = ((NovelDataSourceBasic) Class.forName(G.PLUGIN_PACKAGE + "." + pluginInfo.getClassName()).newInstance());
                    break;

                case DEX:
                    final File optimizedDexOutputPath = MyApp.getContext().getDir(OPTIMIZED_DEX_FOLDER, 0);
                    DexClassLoader dexClassLoader = new DexClassLoader(pluginInfo.getPath(), optimizedDexOutputPath.getAbsolutePath(), null, MyApp.getContext().getClassLoader());
                    Class libProviderClass = dexClassLoader.loadClass(pluginInfo.getClassName());
                    novelDataSourceBasic = (NovelDataSourceBasic) libProviderClass.newInstance();
                    break;

                case LUA:
                    // todo
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return novelDataSourceBasic;
    }

    public static boolean checkDataSourcePluginAvailable(@NonNull String dataSourceTag) {
        // todo: need version code to judge
        // do this job only in local plugin list
        try {
            return loadDataSourcePluginClassByTag(dataSourceTag) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getLocalDataSourcePluginCount() {
        return pluginListLocal.size();
    }

    @Nullable
    public static PluginInfo getLocalDataSourcePluginInfoById(int id) {
        return pluginListLocal.get(id);
    }
}
