package org.mewx.projectprpr.reader.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;

import org.mewx.projectprpr.MyApp;
import org.mewx.projectprpr.R;
import org.mewx.projectprpr.activity.ViewImageDetailActivity;
import org.mewx.projectprpr.global.G;
import org.mewx.projectprpr.plugin.component.NovelContentLine;
import org.mewx.projectprpr.reader.loader.ReaderFormatLoader;
import org.mewx.projectprpr.reader.setting.ReaderSettingBasic;
import org.mewx.projectprpr.toolkit.FigureTool;
import org.mewx.projectprpr.toolkit.FileTool;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by MewX on 2015/7/8.
 *
 * Implement whole view of page, and use full screen page size.
 *
 * Default Elements:
 *  - Top: ChapterTitle, WIFI/DC
 *  - Bot: Battery, Paragraph/All, CurrentTime
 *
 * Click Elements:
 *  - Top: NovelTitle
 *  - Bot: ToolBar
 */
public class ReaderPageViewBasic extends View {
    private static final String TAG = ReaderPageViewBasic.class.getSimpleName();

    // enum
    public enum LOADING_DIRECTION {
        FORWARDS, // go to next page
        CURRENT, // get this page
        BACKWARDS // go to previous page
    }

    // class
    List<NovelContentLine> lineInfoList;

    private class BitmapInfo {
        int idxLineInfo;
        int width, height;
        int x_beg, y_beg;
        Bitmap bm;
    }

    List<BitmapInfo> bitmapInfoList;

    // core variables
    static private boolean inDayMode = true;
    static private String sampleText = "轻";
    static private ReaderFormatLoader mLoader;
    static private ReaderSettingBasic mSetting;
    static private int pxLineDistance, pxParagraphDistance, pxParagraphEdgeDistance, pxPageEdgeDistance, pxWidgetHeight;
    static private Point screenSize;
    private Point textAreaSize;
    static private Typeface typeface;
    static private TextPaint textPaint, widgetTextPaint;
    static private int fontHeight, widgetFontHeihgt;
    private int lineCount;

    // background
    static private Bitmap bmBackgroundYellow, bmTextureYellow[];
    static private BitmapDrawable bmdBackground;
    static private Random random = new Random();
    static private boolean isBackgroundSet = false;

    // vars
    private int firstLineIndex;
    private int firstWordIndex;
    private int lastLineIndex;
    private int lastWordIndex; // last paragraph's last word's index

    // view components (battery, page number, etc.)

    static public boolean getInDayMode() {
        return inDayMode;
    }

    static public boolean switchDayMode() {
        inDayMode = !inDayMode;
        return inDayMode;
    }

    /**
     * Set view static variables, before first onDraw()
     *
     * @param wrl loader
     * @param wrs setting
     */
    static public void setViewComponents(ReaderFormatLoader wrl, ReaderSettingBasic wrs, boolean forceMode) {
        mLoader = wrl;
        mSetting = wrs;
        pxLineDistance = FigureTool.dip2px(MyApp.getContext(), mSetting.getLineDistance());
        pxParagraphDistance = FigureTool.dip2px(MyApp.getContext(), mSetting.getParagraphDistance());
        pxParagraphEdgeDistance = FigureTool.dip2px(MyApp.getContext(), mSetting.getParagraphEdgeDistance());
        pxPageEdgeDistance = FigureTool.dip2px(MyApp.getContext(), mSetting.getPageEdgeDistance());
        pxWidgetHeight = FigureTool.dip2px(MyApp.getContext(), mSetting.widgetHeight);

        // calc general var
        try {
            if (mSetting.getUseCustomFont())
                typeface = Typeface.createFromFile(mSetting.getCustomFontPath()); // custom font
            else
                typeface = null;
        } catch (Exception e) {
            Toast.makeText(MyApp.getContext(), e.toString() + "\n可能的原因有：字体文件不在内置SD卡；内存太小字体太大，请使用简体中文字体，而不是CJK或GBK，谢谢，此功能为试验性功能；", Toast.LENGTH_SHORT).show();
        }
        textPaint = new TextPaint();
        textPaint.setColor(getInDayMode() ? mSetting.fontColorDark : mSetting.fontColorLight);
        textPaint.setTextSize(FigureTool.sp2px(MyApp.getContext(), (float) mSetting.getFontSize()));
        if (typeface != null) textPaint.setTypeface(typeface);
        textPaint.setAntiAlias(true);
        fontHeight = (int) textPaint.measureText(sampleText); //(int) textPaint.getTextSize(); // in "px"
        widgetTextPaint = new TextPaint();
        widgetTextPaint.setColor(getInDayMode() ? mSetting.fontColorDark : mSetting.fontColorLight);
        widgetTextPaint.setTextSize(FigureTool.sp2px(MyApp.getContext(), (float) mSetting.widgetTextSize));
        widgetTextPaint.setAntiAlias(true);
        widgetFontHeihgt = (int) textPaint.measureText(sampleText);

        // load bitmap
        if (forceMode || !isBackgroundSet) {
            screenSize = FigureTool.getRealScreenSize(MyApp.getContext());
            if (Build.VERSION.SDK_INT < 19) {
                screenSize.y -= FigureTool.getStatusBarHeightValue(MyApp.getContext());
            }

            if (mSetting.getPageBackgroundType() == ReaderSettingBasic.PAGE_BACKGROUND_TYPE.CUSTOM) {
                try {
                    bmBackgroundYellow = BitmapFactory.decodeFile(mSetting.getPageBackgrounCustomPath());
                } catch (OutOfMemoryError oome) {
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        bmBackgroundYellow = BitmapFactory.decodeFile(mSetting.getPageBackgrounCustomPath(), options);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
                bmdBackground = null;
            }
            if (mSetting.getPageBackgroundType() == ReaderSettingBasic.PAGE_BACKGROUND_TYPE.SYSTEM_DEFAULT || bmBackgroundYellow == null) {
                // use system default
                bmBackgroundYellow = BitmapFactory.decodeResource(MyApp.getContext().getResources(), R.drawable.reader_bg_yellow_edge);
                bmTextureYellow = new Bitmap[3];
                bmTextureYellow[0] = BitmapFactory.decodeResource(MyApp.getContext().getResources(), R.drawable.reader_bg_yellow1);
                bmTextureYellow[1] = BitmapFactory.decodeResource(MyApp.getContext().getResources(), R.drawable.reader_bg_yellow2);
                bmTextureYellow[2] = BitmapFactory.decodeResource(MyApp.getContext().getResources(), R.drawable.reader_bg_yellow3);

                bmdBackground = new BitmapDrawable(MyApp.getContext().getResources(), bmTextureYellow[random.nextInt(bmTextureYellow.length)]);
                bmdBackground.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                bmdBackground.setBounds(0, 0, screenSize.x, screenSize.y);
            }
            isBackgroundSet = true;
        }
    }

    /**
     * Reset text color, to fit day/night mode.
     * If textPaint is null, then do nothing.
     */
    static public void resetTextColor() {
        textPaint.setColor(getInDayMode() ? mSetting.fontColorDark : mSetting.fontColorLight);
        widgetTextPaint.setColor(getInDayMode() ? mSetting.fontColorDark : mSetting.fontColorLight);
    }

    /**
     * This function init the view class。
     * Notice: (-1, -1), (-1, 0), (0, -1) means first page.
     *
     * @param context          current context, should be WenkuReaderActivity
     * @param lineIndex        if FORWARDS, this is the last index of last page;
     *                         if CURRENT, this is the first index of this page;
     *                         if BACKWARDS, this is the first index of last page;
     * @param directionForward get next or get previous
     */
    public ReaderPageViewBasic(Context context, int lineIndex, int wordIndex, LOADING_DIRECTION directionForward) {
        super(context);
        Log.e("MewX", "-- view: construct my");
        lineInfoList = new ArrayList<>();
        bitmapInfoList = new ArrayList<>();
        mLoader.setCurrentIndex(lineIndex);

        // get environmental vars, use actual layout size
        textAreaSize = new Point(screenSize.x - 2 * (pxPageEdgeDistance + pxParagraphEdgeDistance),
                screenSize.y - 2 * (pxPageEdgeDistance + pxWidgetHeight));
        if (Build.VERSION.SDK_INT < 19) textAreaSize.y = textAreaSize.y + pxWidgetHeight;

        // save vars, calc all ints
        switch (directionForward) {
            case FORWARDS:
                if (wordIndex + 1 < mLoader.getCurrentAsString().length()) {
                    firstLineIndex = lineIndex;
                    if (lineIndex == 0 && wordIndex == 0)
                        firstWordIndex = 0;
                    else
                        firstWordIndex = wordIndex + 1;
                } else if (lineIndex + 1 < mLoader.getElementCount()) {
                    firstLineIndex = lineIndex + 1;
                    firstWordIndex = 0;
                } else {
                    Log.e("MewX", "-- view: end construct A, just return");
                    return;
                }
                mLoader.setCurrentIndex(firstLineIndex);
                calcFromFirst();
                break;

            case CURRENT:
                firstLineIndex = lineIndex;
                firstWordIndex = wordIndex;
                mLoader.setCurrentIndex(firstLineIndex);
                calcFromFirst();
                break;

            case BACKWARDS:
                // fit first and last
                if (wordIndex > 0) {
                    lastLineIndex = lineIndex;
                    lastWordIndex = wordIndex - 1;
                } else if (lineIndex > 0) {
                    lastLineIndex = lineIndex - 1;
                    lastWordIndex = mLoader.getStringLength(lastLineIndex) - 1;
                }

                // firstLineIndex firstWordIndex; and last values changeable
                mLoader.setCurrentIndex(lastLineIndex);
                calcFromLast();
                break;
        }

        for (NovelContentLine li : lineInfoList)
            Log.e("MewX", "get: " + li.content);

    }

    /**
     * Calc page from first to last.
     * firstLineIndex & firstWordIndex set.
     */
    private void calcFromFirst() {
        int widthSum = 0;
        int heightSum = fontHeight;
        String tempText = "";

        Log.e("MewX", "firstLineIndex = " + firstLineIndex + "; firstWordIndex = " + firstWordIndex);
        for (int curLineIndex = firstLineIndex, curWordIndex = firstWordIndex; curLineIndex < mLoader.getElementCount(); ) {
            // init paragraph head vars
            if (curWordIndex == 0 && mLoader.getCurrentType() == NovelContentLine.TYPE.TEXT) {
                // leading space
                widthSum = 2 * fontHeight;
                tempText = "　　";
            } else if (mLoader.getCurrentType() == NovelContentLine.TYPE.IMAGE_URL) {
                if (lineInfoList.size() != 0) {
                    // end a page first
                    lastLineIndex = mLoader.getCurrentIndex() - 1;
                    mLoader.setCurrentIndex(lastLineIndex);
                    lastWordIndex = mLoader.getCurrentAsString().length() - 1;
                    break;
                }

                // one image on page
                lastLineIndex = firstLineIndex = mLoader.getCurrentIndex();
                firstWordIndex = 0;
                lastWordIndex = mLoader.getCurrentAsString().length() - 1;
                NovelContentLine li = new NovelContentLine();
                li.type = NovelContentLine.TYPE.IMAGE_URL;
                li.content = mLoader.getCurrentAsString();
                lineInfoList.add(li);
                break;
            }

            // get a record of line
            if (TextUtils.isEmpty(mLoader.getCurrentAsString())) {
                Log.e("MewX", "empty string! in " + curLineIndex + "(" + curWordIndex + ")");
                curWordIndex = 0;
                if (curLineIndex >= mLoader.getElementCount()) {
                    // out of bounds
                    break;
                }
                mLoader.setCurrentIndex(++curLineIndex);
            }

            NovelContentLine.TYPE type = mLoader.getCurrentType();
            String temp = mLoader.getCurrentAsString().charAt(curWordIndex) + ""; // TODO: fix out of index exception
            int tempWidth = (int) textPaint.measureText(temp);

            // Line full?
            if (widthSum + tempWidth > textAreaSize.x) {
                // wrap line, save line
                NovelContentLine li = new NovelContentLine();
                li.type = NovelContentLine.TYPE.TEXT;
                li.content = tempText;
                lineInfoList.add(li);
                heightSum += pxLineDistance;

                // change vars for next line
                if (heightSum + fontHeight > textAreaSize.y) {
                    // reverse one index
                    if (curWordIndex > 0) {
                        lastLineIndex = curLineIndex;
                        lastWordIndex = curWordIndex - 1;
                    } else if (curLineIndex > 0) {
                        mLoader.setCurrentIndex(--curLineIndex);
                        lastLineIndex = curLineIndex;
                        lastWordIndex = mLoader.getCurrentAsString().length() - 1;
                    } else {
                        lastLineIndex = lastWordIndex = 0;
                    }
                    break; // height overflow
                }

                // height acceptable
                tempText = temp;
                widthSum = tempWidth;
                heightSum += fontHeight;
            } else {
                tempText = tempText + temp;
                widthSum += tempWidth;
            }

            // String end?
            if (curWordIndex + 1 >= mLoader.getCurrentAsString().length()) {
                // next paragraph, wrap line
                NovelContentLine li = new NovelContentLine();
                li.type = NovelContentLine.TYPE.TEXT;
                li.content = tempText;
                lineInfoList.add(li);
                heightSum += pxParagraphDistance;

                // height not acceptable
                if (heightSum + fontHeight > textAreaSize.y) {
                    lastLineIndex = mLoader.getCurrentIndex();
                    lastWordIndex = mLoader.getCurrentAsString().length() - 1;
                    break; // height overflow
                }

                // height acceptable
                heightSum += fontHeight;
                widthSum = 0;
                tempText = "";
                curWordIndex = 0;
                if (curLineIndex + 1 >= mLoader.getElementCount()) {
                    // out of bounds
                    lastLineIndex = curLineIndex;
                    lastWordIndex = mLoader.getCurrentAsString().length() - 1;
                    break;
                }
                mLoader.setCurrentIndex(++curLineIndex);
            } else {
                curWordIndex++;
            }
        }
    }

    /**
     * Calc page from last to first
     * lastLineIndex & lastWordIndex set.
     */
    private void calcFromLast() {

        int heightSum = 0;
        boolean isFirst = true;
        mLoader.setCurrentIndex(lastLineIndex);

        LineLoop:
        for (int curLineIndex = lastLineIndex, curWordIndex = lastWordIndex; curLineIndex >= 0; ) {
            // calc curLine to curWord(contained), make a String list
            NovelContentLine.TYPE curType = mLoader.getCurrentType();
            String curString = mLoader.getCurrentAsString();

            // special to image
            if (curType == NovelContentLine.TYPE.IMAGE_URL && lineInfoList.size() != 0) {
                Log.e("MewX", "jump 1");
                firstLineIndex = curLineIndex + 1;
                firstWordIndex = 0;
                mLoader.setCurrentIndex(firstLineIndex);
                lineInfoList = new ArrayList<>();
                calcFromFirst();
                break;
            } else if (curType == NovelContentLine.TYPE.IMAGE_URL) {
                // one image on page
                lastLineIndex = firstLineIndex = mLoader.getCurrentIndex();
                firstWordIndex = 0;
                lastWordIndex = mLoader.getCurrentAsString().length() - 1;
                NovelContentLine li = new NovelContentLine();
                li.type = NovelContentLine.TYPE.IMAGE_URL;
                li.content = mLoader.getCurrentAsString();
                lineInfoList.add(li);
                break;
            }

            int tempWidth = 0;
            List<NovelContentLine> curList = new ArrayList<>();
            String temp = "";
            for (int i = 0; i < curString.length(); ) {
                if (i == 0) {
                    tempWidth += fontHeight + fontHeight;
                    temp = "　　";
                }

                String c = curString.charAt(i) + "";
                int width = (int) textPaint.measureText(c);
                if (tempWidth + width > textAreaSize.x) {
                    // save line to next
                    NovelContentLine li = new NovelContentLine();
                    li.type = NovelContentLine.TYPE.TEXT;
                    li.content = temp;
                    curList.add(li);

                    // fit needs
                    if (i >= curWordIndex) break;

                    // goto next round
                    tempWidth = 0;
                    temp = "";
                    continue;
                } else {
                    temp = temp + c;
                    tempWidth += width;
                    i++;
                }

                // string end
                if (i == curString.length()) {
                    NovelContentLine li = new NovelContentLine();
                    li.type = NovelContentLine.TYPE.TEXT;
                    li.content = temp;
                    curList.add(li);
                }
            }

            // reverse to add to lineInfoList, full to break, image to do calcFromFirst then break
            for (int i = curList.size() - 1; i >= 0; i--) {
                if (isFirst)
                    isFirst = false;
                else if (i == curList.size() - 1)
                    heightSum += pxParagraphDistance;
                else
                    heightSum += pxLineDistance;

                heightSum += fontHeight;
                if (heightSum > textAreaSize.y) {
                    // calc first index
                    int indexCount = -2;
                    for (int j = 0; j <= i; j++) indexCount += curList.get(j).content.length();
                    firstLineIndex = curLineIndex;
                    firstWordIndex = indexCount + 1;

                    // out of index
                    if (firstWordIndex + 1 >= curString.length()) {
                        firstLineIndex = curLineIndex + 1;
                        firstWordIndex = 0;
                    }
                    break LineLoop;
                }
                lineInfoList.add(0, curList.get(i));
            }
            for (NovelContentLine li : lineInfoList)
                Log.e("MewX", "full: " + li.content);

            // not full to continue, set curWord as last index of the string
            if (curLineIndex - 1 >= 0) {
                mLoader.setCurrentIndex(--curLineIndex);
                curWordIndex = mLoader.getCurrentAsString().length();
            } else {
                Log.e("MewX", "jump 2");
                firstLineIndex = 0;
                firstWordIndex = 0;
                mLoader.setCurrentIndex(firstLineIndex);
                lineInfoList = new ArrayList<>();
                calcFromFirst();
                break;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawLine(0.0f, 0.0f, 320.0f, 320.0f, new Paint()); // px
        if (mSetting == null || mLoader == null) return;
        Log.e("MewX", "onDraw()");

        // draw background
        if (getInDayMode()) {
            // day
            if (bmdBackground != null)
                bmdBackground.draw(canvas);
            if (bmBackgroundYellow.getWidth() != screenSize.x || bmBackgroundYellow.getHeight() != screenSize.y)
                bmBackgroundYellow = Bitmap.createScaledBitmap(bmBackgroundYellow, screenSize.x, screenSize.y, true);
            canvas.drawBitmap(bmBackgroundYellow, 0, 0, null);

        } else {
            // night
            Paint paintBackground = new Paint();
            paintBackground.setColor(mSetting.bgColorDark);
            canvas.drawRect(0, 0, screenSize.x, screenSize.y, paintBackground);
        }
//        Paint paintBackground = new Paint();
//        paintBackground.setColor(mSetting.inDayMode ? mSetting.bgColorLight : mSetting.bgColorDark);
//        canvas.drawRect(0, 0, screenSize.x, screenSize.y, paintBackground);

        // draw divider
//        Paint paintDivider = new Paint();
//        paintDivider.setColor(getContext().getResources().getColor(mSetting.inDayMode ? R.color.dlgDividerColor : R.color.reader_default_text_light));
//        canvas.drawLine(1, 1, 1, screenSize.y - 1, paintDivider);
//        canvas.drawLine(screenSize.x - 1, 1, screenSize.x - 1, screenSize.y - 1, paintDivider);

        // draw widgets
        canvas.drawText(mLoader.getChapterName(), pxPageEdgeDistance, screenSize.y - pxPageEdgeDistance, widgetTextPaint);
        String percentage = "( " + (lastLineIndex + 1) * 100 / mLoader.getElementCount() + "% )";
        int tempWidth = (int) widgetTextPaint.measureText(percentage);
        canvas.drawText(percentage, screenSize.x - pxPageEdgeDistance - tempWidth, screenSize.y - pxPageEdgeDistance, widgetTextPaint);

        // draw text on average in page and line
        int heightSum = fontHeight + pxPageEdgeDistance + pxWidgetHeight;
        if (Build.VERSION.SDK_INT < 19) heightSum -= pxWidgetHeight;
        for (int i = 0; i < lineInfoList.size(); i++) {
            NovelContentLine li = lineInfoList.get(i);
            if (i != 0) {
                if (li.content.length() > 2 && li.content.substring(0, 2).equals("　　")) {
                    heightSum += pxParagraphDistance;
                } else {
                    heightSum += pxLineDistance;
                }
            }

            Log.e("MewX", "draw: " + li.content);
            if (li.type == NovelContentLine.TYPE.TEXT) {
                canvas.drawText(li.content, (float) (pxPageEdgeDistance + pxParagraphEdgeDistance), (float) heightSum, textPaint);
                heightSum += fontHeight;
            } else if (li.type == NovelContentLine.TYPE.IMAGE_URL) {
                if (bitmapInfoList != null) {
                    int foundIndex = -1;
                    for (BitmapInfo bi : bitmapInfoList) {
                        if (bi.idxLineInfo == i) {
                            foundIndex = bitmapInfoList.indexOf(bi);
                            break;
                        }
                    }

                    if (foundIndex == -1) {
                        // not found, new load task

                        canvas.drawText("正在加载图片：" + li.content.split("/")[li.content.split("/").length-1], (float) (pxPageEdgeDistance + pxParagraphEdgeDistance), (float) heightSum, textPaint);
                        BitmapInfo bitmapInfo = new BitmapInfo();
                        bitmapInfo.idxLineInfo = i;
                        bitmapInfo.x_beg = pxPageEdgeDistance + pxParagraphEdgeDistance;
                        bitmapInfo.y_beg = pxPageEdgeDistance + pxWidgetHeight;
                        if (Build.VERSION.SDK_INT < 19) bitmapInfo.y_beg -= pxWidgetHeight;
                        bitmapInfo.height = textAreaSize.y;
                        bitmapInfo.width = textAreaSize.x;
                        bitmapInfoList.add(0, bitmapInfo);

//                        AsyncLoadImage ali = new AsyncLoadImage();
//                        ali.execute(bitmapInfoList.get(0));
                        AsyncLoadBitmap(bitmapInfoList.get(0));
                    } else {
                        if (bitmapInfoList.get(foundIndex).bm == null) {
                            canvas.drawText("正在加载图片：" + li.content.substring(21, li.content.length()), (float) (pxPageEdgeDistance + pxParagraphEdgeDistance), (float) heightSum, textPaint);
                        } else {
//                            canvas.drawText("Can you see image?", (float) (pxPageEdgeDistance + pxParagraphEdgeDistance), (float) heightSum, textPaint);
                            canvas.drawBitmap(bitmapInfoList.get(foundIndex).bm, bitmapInfoList.get(foundIndex).x_beg, bitmapInfoList.get(foundIndex).y_beg, new Paint());
                        }
                    }
                } else {
                    canvas.drawText("Unexpected array: " + li.content.substring(21, li.content.length()), (float) (pxPageEdgeDistance + pxParagraphEdgeDistance), (float) heightSum, textPaint);
                }
            } else {
                canvas.drawText("（！请先用旧引擎浏览）图片" + li.content.substring(21, li.content.length()), (float) (pxPageEdgeDistance + pxParagraphEdgeDistance), (float) heightSum, textPaint);
            }
        }
    }

    public int getFirstLineIndex() {
        return firstLineIndex;
    }

    public int getFirstWordIndex() {
        return firstWordIndex;
    }

    public int getLastLineIndex() {
        return lastLineIndex;
    }

    public int getLastWordIndex() {
        return lastWordIndex;
    }

    private void AsyncLoadBitmap(final BitmapInfo bitmapInfo) {
        final String imagePath = G.generateImageFileFullPathByURL(lineInfoList.get(bitmapInfo.idxLineInfo).content, G.STANDARD_IMAGE_FORMAT);
        ImageRequest imageRequest;
        if(!FileTool.existFile(imagePath)) {
            imageRequest = ImageRequest.fromUri(lineInfoList.get(bitmapInfo.idxLineInfo).content);
        } else {
            // load from storage, the image is saved when downloading
            imageRequest = ImageRequest.fromUri("file://" + imagePath);
            Log.e("TAG", "file://" + imagePath);
        }

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, this);

        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(Bitmap bitmap) {
                // save file first for next time usage
                if(!FileTool.existFile(imagePath)) {
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(imagePath);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 89, out); // bmp is your Bitmap instance
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (out != null)
                                out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                int width = bitmap.getWidth(), height = bitmap.getHeight();
                if (bitmapInfo.height / (float) bitmapInfo.width > height / (float) width) {
                    // fit width
                    float percentage = (float) height / width;
                    bitmapInfo.height = (int) (bitmapInfo.width * percentage);
                } else {
                    // fit height
                    float percentage = (float) width / height;
                    bitmapInfo.width = (int) (bitmapInfo.height * percentage);
                }

                bitmapInfo.bm = Bitmap.createScaledBitmap(bitmap, bitmapInfo.width, bitmapInfo.height, true);
                ReaderPageViewBasic.this.postInvalidate();
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                Log.e(TAG ,"onFailureImpl");
                // todo: not available in non-UI thread
                // Toast.makeText(MyApp.getContext(),"Failure", Toast.LENGTH_SHORT).show();
            }
        }, CallerThreadExecutor.getInstance());

    }

    public void watchImageDetailed(Activity activity) {
        if (bitmapInfoList == null || bitmapInfoList.size() == 0 || bitmapInfoList.get(0).bm == null) {
            Toast.makeText(getContext(), getResources().getString(R.string.reader_view_image_no_image), Toast.LENGTH_SHORT).show();
        } else {
            String imagePath = "";
            if (lineInfoList.get(bitmapInfoList.get(0).idxLineInfo).content.contains("http")) {
                imagePath = G.generateImageFileFullPathByURL(lineInfoList.get(bitmapInfoList.get(0).idxLineInfo).content, G.STANDARD_IMAGE_FORMAT);
            } else if (lineInfoList.get(bitmapInfoList.get(0).idxLineInfo).content.contains("file")) {
                imagePath = lineInfoList.get(bitmapInfoList.get(0).idxLineInfo).content.replace("file://", "");
            }
            Intent intent = new Intent(activity, ViewImageDetailActivity.class);
            intent.putExtra(ViewImageDetailActivity.PATH_TAG, imagePath);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.hold); // fade in animation
        }
    }
}