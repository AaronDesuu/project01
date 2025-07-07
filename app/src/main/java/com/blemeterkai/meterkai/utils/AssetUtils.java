package com.blemeterkai.meterkai.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AssetUtils {

    private static final String TAG = "AssetUtils";

    /**
     * Copies an entire subdirectory from assets to a subdirectory in internal storage.
     * Only copies files, does not recursively copy subdirectories within the assetSubdirectory.
     * Skips copying if the destination file already exists.
     */
    public static void copyAssetsToInternalStorage(Context context, String assetSubdirectory, String internalStorageSubdirectory) {
        AssetManager assetManager = context.getAssets();
        String[] files;
        try {
            files = assetManager.list(assetSubdirectory);
            if (files == null || files.length == 0) {
                Log.w(TAG, "No files found in assets/" + assetSubdirectory);
                return;
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get asset file list from: " + assetSubdirectory, e);
            return;
        }

        File internalDir = new File(context.getFilesDir(), internalStorageSubdirectory);
        if (!internalDir.exists()) {
            if (!internalDir.mkdirs()) {
                Log.e(TAG, "Failed to create directory: " + internalDir.getAbsolutePath());
                return;
            }
        }

        for (String filename : files) {
            // This simple check works if you don't have further subdirectories to copy from assetSubdirectory
            if (filename.contains(".")) { // Basic check for a file extension (assumes not a dir)
                copySingleAssetFile(context, assetSubdirectory + File.separator + filename, internalDir, filename, false);
            } else {
                Log.d(TAG, "Skipping directory found in assets: " + filename);
            }
        }
    }

    /**
     * Copies a single file from the assets folder to a specified destination directory.
     *
     * @param context         The application context.
     * @param assetPathAndName Path to the file in assets (e.g., "csv/login.csv").
     * @param targetDirectory The directory where the file should be copied.
     * @param targetFilename  The name for the copied file in the target directory.
     * @param overwrite       If true, overwrites the destination file if it already exists.
     * @return true if the file was copied successfully, false otherwise.
     */
    public static boolean copyFileFromAssets(Context context, String assetPathAndName, File targetDirectory, String targetFilename, boolean overwrite) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;

        try {
            if (!targetDirectory.exists()) {
                if (!targetDirectory.mkdirs()) {
                    Log.e(TAG, "Failed to create target directory: " + targetDirectory.getAbsolutePath());
                    return false;
                }
            }

            File outFile = new File(targetDirectory, targetFilename);

            if (!overwrite && outFile.exists()) {
                Log.d(TAG, "File " + outFile.getAbsolutePath() + " already exists. Skipping overwrite.");
                return true; // Or false if "not copied now" is the desired semantic
            }

            in = assetManager.open(assetPathAndName);
            out = new FileOutputStream(outFile);
            copyFileStream(in, out);
            Log.i(TAG, "Copied asset '" + assetPathAndName + "' to " + outFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Failed to copy asset file: " + assetPathAndName, e);
            return false;
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing streams for " + assetPathAndName, e);
            }
        }
    }

    // Helper method used by both public copy methods
    private static void copyFileStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }


    // --- Convenience wrapper for common use case in CsvUtils (no overwrite) ---
    public static boolean copyFileFromAssets(Context context, String assetPathAndName, File targetDirectory, String targetFilename) {
        return copyFileFromAssets(context, assetPathAndName, targetDirectory, targetFilename, false);
    }


    // --- Adjusted internal helper for copyAssetsToInternalStorage to use the more robust copyFileFromAssets ---
    // This makes copyAssetsToInternalStorage more consistent.
    private static void copySingleAssetFile(Context context, String assetPathAndName, File targetDirectory, String targetFilename, boolean overwrite) {
        // We can directly call the public method now
        copyFileFromAssets(context, assetPathAndName, targetDirectory, targetFilename, overwrite);
    }
}