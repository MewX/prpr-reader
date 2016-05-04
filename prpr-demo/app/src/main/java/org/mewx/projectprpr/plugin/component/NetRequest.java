package org.mewx.projectprpr.plugin.component;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.mewx.projectprpr.global.YBL;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.CacheControl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * This class packs the request infomation.
 * From "POST", "GET", etc.
 * Format: arg=encodedArgument&arg=encodedArgument...
 */
public class NetRequest {
    private static final String TAG = NetRequest.class.getSimpleName();

    public enum REQUEST_TYPE {
        GET,
        POST,
        SOCKET,
        ULTRA_REQUEST // this should call the ultra functions in NovelDataSourceBasic, URL is TAG
    }

    private REQUEST_TYPE type;
    @NonNull private String url;
    @NonNull private String args;

    public NetRequest(REQUEST_TYPE requestType, @Nullable String url, @Nullable ContentValues args) {
        // save values
        this.type = requestType;
        this.url = url == null ? "" : url;
        if(!this.url.contains("http"))
            this.url = "http://" + this.url; // must start with "http://" or "https://"

        // make request args
        if(args != null) {
            StringBuilder params = new StringBuilder("");
            for (String key : args.keySet()) {
                if (key.length() == 0) continue; // for safe
                params.append("&").append(key).append("="); // now, like "&a=?&b=?&c=?"
                try {
                    params.append(URLEncoder.encode(args.get(key).toString(), "UTF-8")); // NEED URL ENCODING
                } catch (UnsupportedEncodingException e) {
                    // append empty string, nothing to do (user input error, so just ignore)
                    e.printStackTrace();
                }
            }
            if (params.length() > 1) params.deleteCharAt(0); // remove the leading "&"
            this.args = params.toString(); // save value
        } else {
            this.args = ""; // make default value
        }
    }

    public boolean isEmptyArg() {
        return TextUtils.isEmpty(args);
    }

    public String getFullGetUrl() {
        return isEmptyArg() ? url : url + "?" + args;
    }

    @Nullable
    public Request getOkHttpRequest(String charset) throws IOException {
        Request request;
        
        switch (type) {
            case GET:
                request = new Request.Builder()
                        .url(getFullGetUrl())
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .addHeader("User-Agent", YBL.USER_AGENT)
                        .build();
                return request;

            case POST:
                request = new Request.Builder()
                        .url(getFullGetUrl())
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .post(RequestBody.create(MediaType.parse("text/plain; charset=" + charset), args))
                        .addHeader("User-Agent", YBL.USER_AGENT)
                        .build();
                return request; //YBL.globalOkHttpClient3.newCall(request).execute();

            default:
                Log.e(TAG, "Unsupported request type: " + type.toString() + "; " + url + "; " + args);
                return null;
        }
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
