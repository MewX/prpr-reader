package org.mewx.projectprpr.toolkit;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by MewX on 04/18/2016.
 * This is for easy use of volley library.
 */
@SuppressWarnings("unused")
public class VolleyController {
    private static final String TAG = VolleyController.class.getSimpleName();

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    // for global access
    private static VolleyController mInstance;

    private Context mContext;

    private VolleyController(Context context) {
        mContext = context;
    }

    public static VolleyController getInstance(Context context) {
        // for singleton
        if (mInstance == null) {
            synchronized (VolleyController.class) {
                if (mInstance == null) {
                    mInstance = new VolleyController(context);
                }
            }
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            synchronized (VolleyController.class) {
                if (requestQueue == null) {
                    requestQueue = Volley.newRequestQueue(mContext);
                }
            }
        }
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (imageLoader == null) {
            imageLoader = new ImageLoader(requestQueue, new VolleyLruBitmapCache());
        }
        return imageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // if tag is empty, use default TAG
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
