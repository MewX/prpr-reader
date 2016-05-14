package org.mewx.projectprpr.plugin.component;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.mewx.projectprpr.global.YBL;
import org.mewx.projectprpr.toolkit.FileTool;

import java.io.BufferedReader;
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
    @NonNull private String filePath = ""; // full path, and authorized
    @NonNull private ArrayList<NovelContentLine> loadedContent = new ArrayList<>();

    public NovelContent() {
    }

    public NovelContent(@NonNull List<NovelContentLine> content) {
        addToNovelContent(content);
    }

    public NovelContent(@NonNull String filePath) {
        this.filePath = filePath;
        loadFile();
    }

    public void setFileName(@NonNull String filePath) {
        this.filePath = filePath;

        // intelligent decision
        if(loadedContent.size() == 0) loadFile();
        else  saveFile();
    }

    private void loadFile() {
        // TODO: need to rewrite to support large file.
        if(!TextUtils.isEmpty(filePath) && FileTool.existFile(filePath)) {
            String line;
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        new FileInputStream(filePath), Charset.forName(YBL.STANDARD_CHARSET)));

                while ((line = br.readLine()) != null) {
                    if(line.length() == 0) continue;
                    switch (line.charAt(0)) {
                        case 'I':
                            // transferred meaning: IMAGE_URL
                            addToNovelContent(
                                    new NovelContentLine(NovelContentLine.TYPE.IMAGE_URL, line.substring(1)));
                            break;
                        case 'T':
                        default:
                            // transferred meaning: TEXT
                            addToNovelContent(
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
            // delete file first
            FileTool.deleteFile(filePath);

            String line;
            try {
                OutputStreamWriter osw = new OutputStreamWriter(
                        new FileOutputStream(filePath), Charset.forName(YBL.STANDARD_CHARSET));

                for (NovelContentLine ncl : loadedContent) {
                    if (ncl.content.length() == 0) {
                        osw.write("T");
                    } else {
                        switch (ncl.type) {
                            case TEXT:
                                osw.write("T" + ncl.content);
                                break;
                            case IMAGE_URL:
                                osw.write("I");
                        }
                    }
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
        addToNovelContent(content);
    }

    public void addToNovelContent(NovelContentLine content) {
        loadedContent.add(content);
        saveFile(); // TODO: delta file
    }

    public void addToNovelContent(List<NovelContentLine> content) {
        loadedContent.addAll(content);
        saveFile();
    }

    public void addToNovelContent(NovelContent nc) {
        for(int i = 0; i < nc.getContentLineCount(); i ++) {
            addToNovelContent(nc.getContentLine(i));
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
