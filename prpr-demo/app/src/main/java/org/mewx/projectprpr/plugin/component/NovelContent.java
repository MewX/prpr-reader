package org.mewx.projectprpr.plugin.component;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.mewx.projectprpr.global.YBL;
import org.mewx.projectprpr.toolkit.FileTool;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Created by MewX on 4/11/2016.
 * This class handles the whole novel content file.
 * TODO: need to rewrite to support large file.
 * TODO: support multimedia!!!
 */
@SuppressWarnings("unused")
public class NovelContent {
    private @NonNull String filePath = ""; // full path, and authorized
    private StringBuilder content;

    public NovelContent() {
        this("", "");
    }

    public NovelContent(@NonNull String filePath, String content) {
        setFileName(filePath);
        this.content = new StringBuilder(content);
    }

    public void setFileName(@NonNull String filePath) {
        this.filePath = filePath;

        // intelligent decision
        if(content.length() == 0) loadFile();
        else  saveFile();
    }

    private void loadFile() {
        // TODO: need to rewrite to support large file.
        if(!TextUtils.isEmpty(filePath)) {
            try {
                content.append(new String(FileTool.loadFile(filePath), YBL.STANDARD_CHARSET));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile() {
        // TODO: need to rewrite to support large file.
        if(!TextUtils.isEmpty(filePath)) {
            FileTool.saveFile(filePath, this.content.toString(), true);
        }
    }

    public void saveToNovel(String content) {
        this.content = new StringBuilder(content);
        saveFile();
    }

    public void appendToNovel(String content) {
        this.content.append(content);
        saveFile(); // TODO: delta file
    }

    public String readNext() {
        return content.toString();
    }

    public boolean hasNext() {
        return false;
    }
}
