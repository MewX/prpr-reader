package org.mewx.projectprpr.global;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.mewx.projectprpr.reader.loader.ReaderFormatLoader;
import org.mewx.projectprpr.reader.loader.ReaderFormatLoaderTxtUtf8;

/**
 * Created by MewX on 05/16/2016.
 * Holds all the reading formats plugins.
 */
public class FormatPluginManager {
    private static final String TAG = FormatPluginManager.class.getSimpleName();
    private static final String FORMAT_TXT = "txt";

    public enum SUPPORTED_FORMAT {
        UNKNOWN,
        TXT_UTF8
    }

    @Nullable
    static public ReaderFormatLoader detectFileAndLoadFormatLoader(@NonNull String fullPath) {
        SUPPORTED_FORMAT format = detectFileFormat(fullPath);
        switch (format) {
            case TXT_UTF8:
                return new ReaderFormatLoaderTxtUtf8(fullPath);

            case UNKNOWN:
            default:
                return null;
        }
    }

    static public SUPPORTED_FORMAT detectFileFormat(@NonNull String fullPath) {
        int separatorIndex = fullPath.lastIndexOf('/') < 0 ? fullPath.lastIndexOf('\\') : fullPath.lastIndexOf('/');
        if(separatorIndex < 0) return SUPPORTED_FORMAT.UNKNOWN;

        // use suffix to detect
        String fileName = fullPath.substring(separatorIndex + 1);
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            String suffix = fileName.substring(dotIndex + 1).trim().toLowerCase();
            if (suffix.equals(FORMAT_TXT))
                return SUPPORTED_FORMAT.TXT_UTF8;
            // add more elseif
            else
                return SUPPORTED_FORMAT.UNKNOWN;

        } else {
            // todo: need to read binary to detect
            Log.e(TAG, "need to read binary to detect");
            return SUPPORTED_FORMAT.UNKNOWN;
        }
    }
}
