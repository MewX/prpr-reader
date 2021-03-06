package org.mewx.projectprpr.global;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.mewx.projectprpr.plugin.component.BookshelfSaver;
import org.mewx.projectprpr.plugin.component.NovelInfo;
import org.mewx.projectprpr.plugin.component.VolumeInfo;
import org.mewx.projectprpr.toolkit.CryptoTool;
import org.mewx.projectprpr.toolkit.FileTool;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MewX on 05/08/2016.
 * This bookshelf manages all types of local books.
 * Including data sources from Internet, and imported books.
 * They all need a manager.
 *
 * path: dataSourceTag/netnovelTag/files
 * save: dataSourceTag/netnovelTag/_info.prpr
 *       base64_key:base64_value|base64_key:base64_value
 *
 * save: dataSourceTag/netnovelTag/_volume.prpr
 *       (raw serializable)
 *
 * save: /bookshelf.prpr
 *      base64_tag:base64_title|base64_tag:base64_title
 */
public class BookShelfManager {
    private static final String TAG = BookShelfManager.class.getSimpleName();
    public static final String NETNOVEL_INFO_SAVE_FILE_NAME = "_info.prpr"; // in netnovelTag folder
    public static final String NETNOVEL_VOLUME_SAVE_FILE_NAME = "_volume.prpr"; // in netnovelTag folder

    @NonNull
    private static List<BookshelfSaver> bookList = new ArrayList<>();

    public static void loadAllBook() {
        // clear first
        bookList.clear();

        // load directory structure, which is the NETNOVEL list
        //  datasource folder; novel folder;
        String[] pluginTagList = FileTool.getFolderList(G.getStoragePath(G.PROJECT_FOLDER_NETNOVEL));

        for (String pluginTag : pluginTagList) {
            String[] netnovelList = FileTool.getFolderList(G.getProjectFolderDataSource(pluginTag));

            for (String netnovelTag : netnovelList) {
                // load NovelInfo
                NovelInfo ni = new NovelInfo(netnovelTag, netnovelTag);
                String readNovelInfo = FileTool.loadFullFileContent(G.getProjectFolderNetNovel(pluginTag, netnovelTag)
                        + File.separator + NETNOVEL_INFO_SAVE_FILE_NAME);
                String[] sets = readNovelInfo.split("\\|");
                for(String set : sets) {
                    String[] temp = set.split(":");
                    if(temp.length != 2 || TextUtils.isEmpty(temp[0]) || TextUtils.isEmpty(temp[1])) continue;

                    String key = CryptoTool.base64DecodeString(temp[0], G.STANDARD_CHARSET);
                    String value = CryptoTool.base64DecodeString(temp[1], G.STANDARD_CHARSET);
                    if(TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) continue;

                    switch (key) {
                        case NovelInfo.TAG_TITLE:
                            ni.setTitle(value);
                            break;

                        case NovelInfo.TAG_DATASOURCE:
                            ni.setDataSource(value);
                            break;

                        case NovelInfo.TAG_AUTHOR:
                            ni.setAuthor(value);
                            break;

                        case NovelInfo.TAG_COVERURL:
                            ni.setCoverUrl(value);
                            break;

                        default:
                            Log.e(TAG, "LOAD: " + key + " - " + value);
                            ni.addToInfoPairs(key, value);
                            break;
                    }
                }

                // load volumes
                Serializable readObject = FileTool.loadFullSerializable(G.getProjectFolderNetNovel(pluginTag, netnovelTag)
                        + File.separator + NETNOVEL_VOLUME_SAVE_FILE_NAME);
                if(readObject == null) continue;

                ArrayList<VolumeInfo> listVolume = (ArrayList<VolumeInfo>)readObject;
                bookList.add(new BookshelfSaver(BookshelfSaver.BOOK_TYPE.NETNOVEL, pluginTag, ni, listVolume));
            }
        }

        // load local book
        String readNovelInfo = FileTool.loadFullFileContent(G.getStoragePath(G.PROJECT_FILE_LOCAL_BOOKSHELF));
        if(!TextUtils.isEmpty(readNovelInfo)) {
            String[] sets = readNovelInfo.split("\\|");
            for (String set : sets) {
                String[] temp = set.split(":");
                if (temp.length != 3 || TextUtils.isEmpty(temp[0]) || TextUtils.isEmpty(temp[1]) || TextUtils.isEmpty(temp[2]))
                    continue;

                String tag = CryptoTool.base64DecodeString(temp[0], G.STANDARD_CHARSET);
                String title = CryptoTool.base64DecodeString(temp[1], G.STANDARD_CHARSET);
                String dataSource = CryptoTool.base64DecodeString(temp[2], G.STANDARD_CHARSET);
                if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(title) || TextUtils.isEmpty(dataSource)) continue;

                NovelInfo ni = new NovelInfo(tag, title);
                ni.setDataSource(dataSource);
                bookList.add(new BookshelfSaver(BookshelfSaver.BOOK_TYPE.LOCAL_BOOK, null, ni, null));
            }
        }
    }

    @NonNull
    public static List<BookshelfSaver> getBookList() {
        return bookList;
    }

    public static void removeBook(@Nullable String dataSourceTag, String novelTag) {
        for (int i = 0; i < bookList.size(); i ++) {
            BookshelfSaver bookshelfSaver = bookList.get(i);
            if (dataSourceTag != null && bookshelfSaver.getDataSourceTag() != null && dataSourceTag.equals(bookshelfSaver.getDataSourceTag())
                    || dataSourceTag == null && bookshelfSaver.getDataSourceTag() == null) {
                if (bookshelfSaver.getNovelInfo().getBookTag().equals(novelTag)) {
                    removeBookAt(i); // concurrent exception?
                    break;
                }
            }
        }
    }

    public static void removeBookAt(int i) {
        // remove netnovel
        if (bookList.get(i).getType() == BookshelfSaver.BOOK_TYPE.NETNOVEL) {
            FileTool.deleteFolder(G.getProjectFolderNetNovel(bookList.get(i).getDataSourceTag(), bookList.get(i).getNovelInfo().getBookTag()));

            // todo remove related records
        } else {
            // todo remvoe local book reading records
            // G.removeReadSavesRecordV1(dataSourceBasic.getTag() + novelTag);
        }
        bookList.remove(i);
        saveAllLocalBookList();
    }

    public static void addLocalBookToBookshelf(@NonNull String fullPath) {
        fullPath = fullPath.contains(NovelInfo.LOCAL_BOOK_PREFIX) ? fullPath : NovelInfo.LOCAL_BOOK_PREFIX + fullPath;
        NovelInfo ni = new NovelInfo(fullPath, fullPath);
        ni.setDataSource(fullPath);
        addToBookshelf(new BookshelfSaver(BookshelfSaver.BOOK_TYPE.LOCAL_BOOK, null, ni, null));
    }

    public static void addToBookshelf(BookshelfSaver saver) {
        // path: .. / plug-in's tag / novelInfo's tag
        if (saver.getType() == BookshelfSaver.BOOK_TYPE.NETNOVEL) {
            // save volumes
            if(saver.getListVolumeInfo() != null) {
                FileTool.writeFullSerializable(G.getProjectFolderNetNovel(saver.getDataSourceTag(), saver.getNovelInfo().getBookTag())
                        + File.separator + NETNOVEL_VOLUME_SAVE_FILE_NAME, saver.getListVolumeInfo());
            }

            // save novel info
            StringBuilder fileContent = new StringBuilder(CryptoTool.base64Encode(NovelInfo.TAG_TITLE) + ":" + CryptoTool.base64Encode(saver.getNovelInfo().getTitle())
                    + "|" + CryptoTool.base64Encode(NovelInfo.TAG_DATASOURCE) + ":" + CryptoTool.base64Encode(saver.getNovelInfo().getDataSource())
                    + "|" + CryptoTool.base64Encode(NovelInfo.TAG_AUTHOR) + ":" + CryptoTool.base64Encode(saver.getNovelInfo().getAuthor())
                    + "|" + CryptoTool.base64Encode(NovelInfo.TAG_COVERURL) + ":" + CryptoTool.base64Encode(saver.getNovelInfo().getCoverUrl()));
            for(String key : saver.getNovelInfo().getInfoPairs().keySet()) {
                fileContent.append("|" + CryptoTool.base64Encode(key) + ":" + CryptoTool.base64Encode(saver.getNovelInfo().getInfoPairs().get(key).toString()));
            }
            FileTool.writeFullFileContent(G.getProjectFolderNetNovel(saver.getDataSourceTag(), saver.getNovelInfo().getBookTag())
                    + File.separator + NETNOVEL_INFO_SAVE_FILE_NAME, fileContent.toString());

            // add to bookshelf
            bookList.add(saver);
        } else if (saver.getType() == BookshelfSaver.BOOK_TYPE.LOCAL_BOOK) {
            // save novel book list, save to file
            bookList.add(saver);
            saveAllLocalBookList();
        }

    }

    private static void saveAllLocalBookList() {
        // known information: full path(novel tag), novel title
        StringBuilder fileContent = new StringBuilder();
        for (BookshelfSaver saver : bookList) {
            if (saver.getType() == BookshelfSaver.BOOK_TYPE.LOCAL_BOOK) {
                if(fileContent.length() != 0) fileContent.append("|");
                fileContent.append(CryptoTool.base64Encode(saver.getNovelInfo().getBookTag()) // url, in fact
                        + ":" + CryptoTool.base64Encode(saver.getNovelInfo().getTitle())
                        + ":" + CryptoTool.base64Encode(saver.getNovelInfo().getDataSource()));
            }
        }
        FileTool.writeFullFileContent(G.getStoragePath(G.PROJECT_FILE_LOCAL_BOOKSHELF), fileContent.toString());
    }

    public static boolean inBookshelf(@Nullable String dataSourceTag, @NonNull String novelTag) {
        for (BookshelfSaver saver : bookList) {
            if((saver.getDataSourceTag() != null && dataSourceTag != null && saver.getDataSourceTag().equals(dataSourceTag)
                    || saver.getDataSourceTag() == null && dataSourceTag == null) && saver.getNovelInfo().getBookTag().equals(novelTag))
                return true;
        }
        return false;
    }
}
