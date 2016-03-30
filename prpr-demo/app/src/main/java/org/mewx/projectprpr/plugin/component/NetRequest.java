package org.mewx.projectprpr.plugin.component;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * This class packs the request infomation.
 * From "POST", "GET", etc.
 */
public class NetRequest {
    enum REQUEST_TYPE {
        GET, POST
    }

    private REQUEST_TYPE type;
    private @NonNull String url;
    private @NonNull String args;

    public NetRequest(REQUEST_TYPE requestType, @Nullable String url, @Nullable ContentValues args) {
        // save values
        this.type = requestType;
        this.url = url == null ? "" : url;

        // make request args
        if(args != null) {
            StringBuilder params = new StringBuilder("");
            for (String key : args.keySet())
                params.append("&").append(key).append("=").append(args.get(key)); // now, like "&a=1&b=1&c=1"
            this.args = params.toString();
            if(this.args.length() > 1)
                this.args = this.args.substring(1); // remove the first "&"
        }
        else this.args = ""; // make default value
    }

    public REQUEST_TYPE getType() {
        return type;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    @NonNull
    public String getArgs() {
        return args;
    }
}
