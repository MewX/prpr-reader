package org.mewx.projectprpr.reader.loader;

import android.support.annotation.NonNull;

import org.mewx.projectprpr.plugin.component.NovelContent;
import org.mewx.projectprpr.plugin.component.NovelContentLine;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by MewX on 05/16/2016.
 * Loader for utf8 txt file.
 */
public class ReaderFormatLoaderTxtUtf8 extends ReaderFormatLoaderBasic {
    private static final String TAG = ReaderFormatLoaderTxtUtf8.class.getSimpleName();

    public ReaderFormatLoaderTxtUtf8(@NonNull String fullPath) {
        // read full book
        NovelContent nc = new NovelContent();
        try {
            FileInputStream fis = new FileInputStream(fullPath);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            String line = null;
            while ((line = br.readLine()) != null) {
                nc.addToNovelContentAndSaveFile(new NovelContentLine(NovelContentLine.TYPE.TEXT, line));
            }

            br.close();
            isr.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // this is a must
        setNovelContent(nc);
    }
}
