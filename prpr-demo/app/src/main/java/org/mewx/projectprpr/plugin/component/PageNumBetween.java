package org.mewx.projectprpr.plugin.component;

/**
 * Created by MewX on 4/9/2016.
 * Page number between a and b, that means [a, b].
 */
public class PageNumBetween {
    public int beg, end; // just set and get directly

    public PageNumBetween(int beg) {
        this(beg, beg);
    }

    public PageNumBetween(int beg, int end) {
        if(beg < end) {
            this.beg = beg;
            this.end = end;
        }
        else {
            this.end = beg;
            this.beg = end;
        }
    }
}
