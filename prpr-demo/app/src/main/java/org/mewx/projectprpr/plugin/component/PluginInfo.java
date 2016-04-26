package org.mewx.projectprpr.plugin.component;

import java.io.Serializable;

/**
 * Created by MewX on 04/17/2016.
 * This class save the plug-in's information.
 * Should be pass between activities.
 */
public class PluginInfo implements Serializable {
    public enum PluginType {
        BUILTIN,
        DEX,
        LUA
    }

    private String className;
    private PluginType type;
    private String path; // if is "BUILTIN", ignore this; if path contains "http", need download first

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
