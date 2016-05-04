package org.mewx.projectprpr.reader.slider.base;

import android.view.MotionEvent;

import org.mewx.projectprpr.reader.slider.SlidingAdapter;
import org.mewx.projectprpr.reader.slider.SlidingLayout;

/**
 * Created by xuzb on 1/16/15.
 */
public interface Slider {
    public void init(SlidingLayout slidingLayout);
    public void resetFromAdapter(SlidingAdapter adapter);
    public boolean onTouchEvent(MotionEvent event);
    public void computeScroll();
    public void slideNext();
    public void slidePrevious();
}
