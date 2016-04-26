package org.mewx.projectprpr.plugin.component;

import android.support.annotation.Nullable;

/**
 * Created by MewX on 4/9/2016.
 * Page number between a and b, that means [a, b].
 */
@SuppressWarnings("unused")
public class PageNumBetween {
    private int beg, end; // just set and get directly
    private @Nullable Integer current;

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

    public boolean hasNext() {
        return current == null || current < end;
    }

    public int getNext() {
        if (current == null) {
            current = beg;
            return current;
        } else if (current < end) {
            current += 1;
            return current;
        }
        else {
            current = end;
            return end;
        }
    }

    public void setCurrent(int c) {
        if (beg <= c && c <= end)
            this.current = c;
        else
            this.current = beg; // if fail
    }

    public int getCurrent() {
        return current == null ? beg : current;
    }

    public int getBeg() {
        return beg;
    }

    public int getEnd() {
        return end;
    }
}
