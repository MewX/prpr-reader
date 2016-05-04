package org.mewx.projectprpr.reader.loader;

import android.graphics.Bitmap;

import org.mewx.projectprpr.plugin.component.NovelContentLine;

/**
 * Created by MewX on 2016/4/29.
 * Parent of all loaders.
 */
public abstract class ReaderFormatLoader {
    // public abstract void initLoader(String srcPath);

    public abstract void setChapterName(String name); // set chapter name

    public abstract String getChapterName(); // get chapter name

    public abstract boolean hasNext(int wordIndex); // word in current line

    public abstract boolean hasPrevious(int wordIndex); // word in current line

    public abstract NovelContentLine.TYPE getNextType(); // next is {index}, nullable (index keep)

    public abstract String getNextAsString(); // index ++

    public abstract Bitmap getNextAsBitmap(); // index ++

    public abstract NovelContentLine.TYPE getCurrentType(); //

    public abstract String getCurrentAsString(); // index keep

    public abstract Bitmap getCurrentAsBitmap(); // index keep

    public abstract NovelContentLine.TYPE getPreviousType(); // nullable (index keep)

    public abstract String getPreviousAsString(); // index --

    public abstract Bitmap getPreviousAsBitmap(); // index --

    public abstract int getStringLength(int n);

    public abstract int getElementCount();

    public abstract int getCurrentIndex(); // from 0, to {Count - 1}

    public abstract void setCurrentIndex(int i); // set a index, should optimize for the same or relation lines

    public abstract void closeLoader();

}
