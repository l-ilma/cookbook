package com.example.cookbook.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.example.cookbook.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

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

    public static void setImageView(ImageView v, String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            v.setImageResource(R.drawable.baseline_image_24);
        } else {
            v.setImageBitmap(ImageUtils.loadFile(imagePath));
        }
    }

    public static String uploadLocalImageFromUri(ContentResolver resolver, Uri imageUri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, imageUri);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return ImageUtils.createFile(UUID.randomUUID() + ".jpg", bytes);
    }
}
