package org.mewx.projectprpr.activity.adapter;

/**
 * Created by MewX on 04/17/2016.
 * Store the basic info of data source, which are displaying in recycler view.
 */
public class DataSourceItem {
    private String displayName;
    private String websiteDomain;
    private int versionCode;
    private String logoUrl;

    public DataSourceItem(String name, String domain, int versionCode, String logoUrl) {
        this.displayName = name;
        this.websiteDomain = domain;
        this.versionCode = versionCode;
        this.logoUrl = logoUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getWebsiteDomain() {
        return websiteDomain;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}
