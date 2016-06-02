package org.mewx.projectprpr.plugin.component;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.mewx.projectprpr.global.G;
import org.mewx.projectprpr.toolkit.FileTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MewX on 4/11/2016.
 * This class handles the whole novel content file.
 * TODO: need to rewrite to support large file.
 * TODO: support multimedia!!!
 */
@SuppressWarnings("unused")
public class NovelContent {
    private static final String TAG_TEXT = "T";
    private static final char TAG_TEXT_CHAR = 'T';
    private static final String TAG_IMAGE_URL = "I";
    private static final char TAG_IMAGE_URL_CHAR = 'I';

    @NonNull private String filePath = ""; // full path, and authorized
    @NonNull private ArrayList<NovelContentLine> loadedContent = new ArrayList<>();

    public NovelContent() {
    }

    public NovelContent(@NonNull NovelContent nc) {
        addToNovelContentAndSaveFile(nc);
    }

    public NovelContent(@NonNull List<NovelContentLine> content) {
        addToNovelContentAndSaveFile(content);
    }

    public NovelContent(@NonNull String filePath) {
        this.filePath = filePath;
        loadFile();
    }

    public void setFileName(@NonNull String filePath) {
        this.filePath = filePath;

        // intelligent decision
        if(loadedContent.size() == 0) loadFile();
        else saveFile();
    }

    private void loadFile() {
        // TODO: need to rewrite to support large file.
        if(!TextUtils.isEmpty(filePath) && FileTool.existFile(filePath)) {
            String line;
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        new FileInputStream(filePath), Charset.forName(G.STANDARD_CHARSET)));

                while ((line = br.readLine()) != null) {
                    if(line.length() == 0) continue;
                    switch (line.charAt(0)) {
                        case TAG_IMAGE_URL_CHAR:
                            // transferred meaning: IMAGE_URL
                            loadedContent.add(
                                    new NovelContentLine(NovelContentLine.TYPE.IMAGE_URL, line.substring(1)));
                            break;
                        case TAG_TEXT_CHAR:
                        default:
                            // transferred meaning: TEXT
                            loadedContent.add(
                                    new NovelContentLine(NovelContentLine.TYPE.TEXT, line.substring(1)));
                            break;
                    }
                }

                br.close();
            } catch (Exception ok) {
                // file not found an just skip (FileNotFoundException, IOException)
                ok.printStackTrace();
            }
        }
    }

    private void saveFile() {
        // TODO: need to rewrite to support large file.
        if(!TextUtils.isEmpty(filePath)) {
            String line;
            try {
                File file = new File(filePath);
                file.createNewFile(); // create file
                OutputStreamWriter osw = new OutputStreamWriter(
                        new FileOutputStream(file), Charset.forName(G.STANDARD_CHARSET));

                for (NovelContentLine ncl : loadedContent) {
                    if (ncl.content.length() == 0) {
                        osw.write(TAG_TEXT); // empty line
                    } else {
                        switch (ncl.type) {
                            case TEXT:
                                osw.write(TAG_TEXT + ncl.content);
                                break;
                            case IMAGE_URL:
                                osw.write(TAG_IMAGE_URL + ncl.content);
                                break;
                        }
                    }
                    osw.write("\n"); // line break
                }

                osw.close();
            } catch (Exception ok) {
                // file not found an just skip (FileNotFoundException, IOException)
                ok.printStackTrace();
            }
        }
    }

    public void setNovelContent(List<NovelContentLine> content) {
        removeAllContent();
        addToNovelContentAndSaveFile(content);
    }

    public void addToNovelContentAndSaveFile(NovelContentLine content) {
        loadedContent.add(content);
        saveFile(); // TODO: save delta file
    }

    public void addToNovelContentAndSaveFile(List<NovelContentLine> content) {
        loadedContent.addAll(content);
        saveFile();
    }

    public void addToNovelContentAndSaveFile(NovelContent nc) {
        for(int i = 0; i < nc.getContentLineCount(); i ++) {
            loadedContent.add(nc.getContentLine(i));
        }
        saveFile();
    }

    public int getContentLineCount() {
        return loadedContent.size(); // TODO: this should handle the large file
    }

    public void removeContentAt(int idx) {
        if (0 <= idx && idx < loadedContent.size()) {
            loadedContent.remove(idx);
        }
    }

    public void removeAllContent() {
        loadedContent.clear();
    }

    public NovelContentLine getContentLine(int i) {
        return loadedContent.get(i);
    }
}
