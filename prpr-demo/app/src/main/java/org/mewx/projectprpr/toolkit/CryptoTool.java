package org.mewx.projectprpr.toolkit;

import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by MewX on 05/14/2016.
 * Contains the basic encryption methods, and hash methods.
 */
@SuppressWarnings("unused")
public class CryptoTool {
    private static final String DEFAULT_DIGEST_ALGORITHM = "SHA-1";

    static public String base64Encode(byte[] b) {
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    static public String base64Encode(String s) {
        try {
            return base64Encode(s.getBytes("UTF-8")).trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    static public byte[] base64Decode(String s) {
        try {
            byte[] b;
            b = Base64.decode(s, Base64.DEFAULT);
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    static public String base64DecodeString(String s, String charset) {
        try {
            return new String(base64Decode(s), charset); // UTF-8
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static public String hashMessageDigest(String msg) throws NoSuchAlgorithmException{
        final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        MessageDigest messageDigest = MessageDigest.getInstance(DEFAULT_DIGEST_ALGORITHM);
        messageDigest.update(msg.getBytes());

        // convert to
        byte[] hash = messageDigest.digest();
        StringBuilder buf = new StringBuilder(hash.length * 2);
        for (int j = 0; j < hash.length; j++) {
            buf.append(HEX_DIGITS[(hash[j] << 4) & 0x0f]);
            buf.append(HEX_DIGITS[hash[j] & 0x0f]);
        }
        for (byte c : hash) {
            buf.append(HEX_DIGITS[(c << 4) & 0x0f]);
            buf.append(HEX_DIGITS[c & 0x0f]);
        }
        return buf.toString();
    }
}
