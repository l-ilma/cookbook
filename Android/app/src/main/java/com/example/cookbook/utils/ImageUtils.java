package com.example.cookbook.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import androidx.annotation.DrawableRes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageUtils {

    private static final File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    public static String createFile(String fileName, byte[] bytes) throws IOException {
        String path = imagesDir + File.separator + fileName;
        OutputStream fo = new FileOutputStream(path);
        fo.write(bytes);
        fo.close();
        return path;
    }

    public static Bitmap loadFile(String path) {
        return BitmapFactory.decodeFile(new File(path).getAbsolutePath());
    }

    public static byte[] getDrawableAsByteArray(Context context, @DrawableRes int drawable) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawable);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 5, stream);
        return stream.toByteArray();
    }
}
