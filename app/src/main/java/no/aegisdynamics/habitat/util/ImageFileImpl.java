package no.aegisdynamics.habitat.util;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

/**
 * A thin wrapper around Android file APIs to make them more testable and allows the injection of a
 * fake implementation for hermetic UI tests.
 */
public class ImageFileImpl implements ImageFile {

    @VisibleForTesting
    File mImageFile;

    @NonNull
    final Context mContext;

    public ImageFileImpl(@NonNull Context context) {
        mContext = context;
    }
    @Override
    public void create(String name, String extension) throws IOException {

        File storageDir = new File(mContext.getFilesDir(), "images");
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        mImageFile = File.createTempFile(
                name,  /* prefix */
                extension,        /* suffix */
                storageDir      /* directory */
        );
    }

    @Override
    public boolean exists() {
        return null != mImageFile && mImageFile.exists();
    }

    @Override
    public void delete() {
        mImageFile = null;
    }

    @Override
    public String getPath() {
        return Uri.fromFile(mImageFile).toString();
    }


}