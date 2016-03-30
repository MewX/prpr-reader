package org.mewx.projectprpr.global;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.mewx.projectprpr.toolkit.LruBitmapCache;

/**
 * For singleton mode "volley" access.
 * Created by MewX on 3/31/2016.
 */
@SuppressWarnings("unused")
public class VolleyController {
    private static final String TAG = VolleyController.class.getSimpleName();

    private static VolleyController mInstance;

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private Context mContext;


    private VolleyController(Context context) {
        mContext = context;
    }

    public static VolleyController getInstance(Context context) {
        if (mInstance == null) {
            synchronized(VolleyController.class) {
                if (mInstance == null)
                    mInstance = new VolleyController(context);
            }
        }
        return mInstance;
    }

    /**
     * get the requestQueue object, if null then create one.
     * @return requestQueue object
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            synchronized(VolleyController.class) {
                if (requestQueue == null)
                    requestQueue = Volley.newRequestQueue(mContext);
            }
        }
        return requestQueue;
    }

    public ImageLoader getImageLoader(){
        getRequestQueue();
        if(imageLoader == null)
            imageLoader = new ImageLoader(requestQueue, new LruBitmapCache());
        return imageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, @Nullable String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag); // may use default TAG
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        addToRequestQueue(req, TAG);
    }

    /**
     * Cancel request by tag of request object.
     * @param tag request object's tag
     */
    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
