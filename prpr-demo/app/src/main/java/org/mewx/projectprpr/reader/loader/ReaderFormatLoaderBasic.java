package org.mewx.projectprpr.reader.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.mewx.projectprpr.plugin.component.NovelContent;
import org.mewx.projectprpr.plugin.component.NovelContentLine;

import java.util.List;

/**
 * Created by MewX on 2015/7/8.
 *
 * Raw data loader. Need async call!
 */
public class ReaderFormatLoaderBasic extends ReaderFormatLoader {

    private int currentIndex = 0;
    private NovelContent nc;
    public String chapterName = "";

    public ReaderFormatLoaderBasic(NovelContent nc) {
        this.nc = nc;
    }

    @Override
    public void setChapterName(String name) {
        chapterName = name;
    }

    @Override
    public String getChapterName() {
        return chapterName;
    }

    @Override
    public boolean hasNext(int wordIndex) {
        if(currentIndex < nc.getContentLineCount() && currentIndex >= 0) {
            // size legal
            if(currentIndex + 1 < nc.getContentLineCount()) {
                // remain one more
                return true;
            }
            else {
                 // last one
                if(nc.getContentLine(currentIndex).type == NovelContentLine.TYPE.TEXT && wordIndex + 1 < nc.getContentLine(currentIndex).content.length()) {
                    // text but not last word
                    return true;
                }
                else if(nc.getContentLine(currentIndex).type != NovelContentLine.TYPE.TEXT && wordIndex == 0) {
                    // image
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasPrevious(int wordIndex) {
        if(currentIndex < nc.getContentLineCount() && currentIndex >= 0) {
            // size legal
            if(currentIndex - 1 >= 0) {
                // one more ahead
                return true;
            }
            else {
                // first one
                if(nc.getContentLine(currentIndex).type == NovelContentLine.TYPE.TEXT && wordIndex - 1 >= 0) {
                    // one more word ahead
                    return true;
                }
                else if(nc.getContentLine(currentIndex).type != NovelContentLine.TYPE.TEXT && wordIndex == nc.getContentLine(currentIndex).content.length() - 1)
                    // image previous use index last
                    return true;
            }
        }
        return false;
    }

    @Override
    public NovelContentLine.TYPE getNextType() {
        // nullable
        if(currentIndex + 1 < nc.getContentLineCount() && currentIndex >= 0) {
            if(currentIndex != nc.getContentLineCount() - 1)
                return nc.getContentLine(currentIndex + 1).type;
        }
        return null;
    }

    @Override
    public String getNextAsString() {
        if(currentIndex + 1 < nc.getContentLineCount() && currentIndex >= 0) {
            currentIndex ++;
            return nc.getContentLine(currentIndex).content;
        }
        return null;
    }

    @Override
    public Bitmap getNextAsBitmap() {
        // Async get bitmap from local or internet
        Bitmap bm = null;
        if(currentIndex + 1 < nc.getContentLineCount() && currentIndex >= 0) {
            currentIndex++;
            if(nc.getContentLine(currentIndex).content.contains("http")) {
                bm = getBitmapByUrl(nc.getContentLine(currentIndex).content);
            } else {
                // todo: for local file
            }
        }
        return bm;
    }

    @Override
    public NovelContentLine.TYPE getCurrentType() {
        // nullable
        if(currentIndex < nc.getContentLineCount() && currentIndex >= 0) {
            return nc.getContentLine(currentIndex).type;
        }
        return null;
    }

    @Override
    public String getCurrentAsString() {
        if(currentIndex < nc.getContentLineCount() && currentIndex >= 0) {
            return nc.getContentLine(currentIndex).content;
        }
        return null;
    }

    @Override
    public Bitmap getCurrentAsBitmap() {
        // Async get bitmap from local or internet
        Bitmap bm = null;
        if(currentIndex < nc.getContentLineCount() && currentIndex >= 0) {
            if(nc.getContentLine(currentIndex).content.contains("http")) {
                bm = getBitmapByUrl(nc.getContentLine(currentIndex).content);
            } else {
                // todo: for local file
            }
        }
        return null;
    }

    @Override
    public NovelContentLine.TYPE getPreviousType() {
        // nullable
        if(currentIndex < nc.getContentLineCount() && currentIndex - 1 >= 0) {
            if(currentIndex != 0)
                return nc.getContentLine(currentIndex - 1).type;
        }
        return null;
    }

    @Override
    public String getPreviousAsString() {
        if(currentIndex < nc.getContentLineCount() && currentIndex - 1 >= 0) {
            currentIndex --;
            return nc.getContentLine(currentIndex).content;
        }
        return null;
    }

    @Override
    public Bitmap getPreviousAsBitmap() {
        // Async get bitmap from local or internet
        Bitmap bm = null;
        if(currentIndex < nc.getContentLineCount() && currentIndex - 1 >= 0) {
            currentIndex--;
            if(nc.getContentLine(currentIndex).content.contains("http")) {
                bm = getBitmapByUrl(nc.getContentLine(currentIndex).content);
            } else {
                // todo: for local file
            }
        }
        return null;
    }

    @Override
    public int getStringLength(int n) {
        if(n >= 0 && n < nc.getContentLineCount())
            return nc.getContentLine(n).content.length();
        return 0;
    }

    @Override
    public int getElementCount() {
        return nc.getContentLineCount();
    }

    @Override
    public int getCurrentIndex() {
        return currentIndex;
    }

    @Override
    public void setCurrentIndex(int i) {
        currentIndex = i;
    }

    @Override
    public void closeLoader() {
        nc = null;
    }

    private Bitmap getBitmapByUrl(String url) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).build();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, this);
        CloseableReference<CloseableImage> imageReference = null;
        try {
            imageReference = dataSource.getResult();
            if (imageReference != null) {
                CloseableImage image = imageReference.get();
                return ((CloseableStaticBitmap) image).getUnderlyingBitmap(); // load bitmap
            }
        } finally {
            dataSource.close();
            CloseableReference.closeSafely(imageReference);
        }
        return null;
    }
}
