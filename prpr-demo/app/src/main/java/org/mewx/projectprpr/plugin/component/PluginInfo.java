package org.mewx.projectprpr.plugin.component;

/**
 * Created by MewX on 04/17/2016.
 * This class save the plug-in's information.
 */
public class PluginInfo {
    public enum PluginType {
        BUILTIN,
        DEX,
        LUA
    }

    private String className;
    private PluginType type;
    private String path; // if is "BUILTIN", ignore this

    public PluginInfo(String className, PluginType type, String path) {
        this.className = className;
        this.type = type;
        this.path = path;
    }

    public String getClassName() {
        return className;
    }

    public PluginType getType() {
        return type;
    }

    public String getPath() {
        return path;
    }
}
