package org.mewx.projectprpr.plugin.component;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * This class packs the request infomation.
 * From "POST", "GET", etc.
 * Format: arg=encodedArgument&arg=encodedArgument...
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
            for (String key : args.keySet()) {
                params.append("&").append(key).append("="); // now, like "&a=?&b=?&c=?"
                try {
                    params.append(URLEncoder.encode((String)args.get(key), "UTF-8")); // NEED URL ENCODING
                } catch (UnsupportedEncodingException e) {
                    // append empty string, nothing to do (user input error, so just ignore)
                    e.printStackTrace();
                }
            }
            if (params.length() > 1) params.deleteCharAt(0); // remove the leading "&"
            this.args = params.toString(); // save value
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
