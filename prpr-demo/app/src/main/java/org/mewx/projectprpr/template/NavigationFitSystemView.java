package org.mewx.projectprpr.template;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;

import org.mewx.projectprpr.toolkit.ScreenTool;

/**
 * This view makes the NavigationDrawer fit system view.
 * Created by MewX on 1/19/2016.
 */
public class NavigationFitSystemView extends NavigationView {
    public NavigationFitSystemView(Context context) {
        super(context);
    }

    public NavigationFitSystemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationFitSystemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {

        // TODO: optimize for tablet screen layout
        super.onMeasure(widthSpec, heightSpec- ScreenTool.getCurrentNavigationBarSize(getContext()).y);
    }
}
