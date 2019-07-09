package com.itad.autorepaircloud.utils;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Base64Helper {

    public static String encode(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        return encode(baos.toByteArray());
    }

    public static String encode(byte[] bytes) {
        String encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);
        return encodedImage;
    }
}