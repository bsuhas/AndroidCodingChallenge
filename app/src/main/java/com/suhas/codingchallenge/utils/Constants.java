package com.suhas.codingchallenge.utils;

import android.app.Activity;
import android.os.Environment;

import java.io.File;


public class Constants {

    public static final String APP_DIRECTORY = "GabbarsTalk";
    public static final String CAMERA_OUTPUT_NAME = APP_DIRECTORY + "_";
    private static final String YOUVOXX_TEMPORARY_DIRECTORY = "Temporary Files";

    public static String mCurrentPhotoPath = "";
    public static final int GALLERY_INTENT_REQUEST_CODE = 0x000005;
    public static final int CAMERA_REQUEST = 0x000003;
    public static String STORED_IMAGE_PATH = "/" + "AndroidCodingchallenge";


    public static Boolean isExternalStorageDirectoryExist = null;
    public static boolean isExternalHiddenStorageDirectoryExist = false;
    public static final String HIDE_APP_DIRECTORY = ".AndroidCodingchallenge";

    public static String getTemporaryFilePath(Activity activity) {
        return getFilePath(YOUVOXX_TEMPORARY_DIRECTORY + File.separator, activity);
    }

    public static String getFilePath(Activity activity) {
        String path = YOUVOXX_TEMPORARY_DIRECTORY + File.separator;
        if (isExternalStorageDirectoryExist == null) {
            setIsExternalStorageDirectoryExistFlag(path);
        }

        if (isExternalStorageDirectoryExist != null && isExternalStorageDirectoryExist) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + APP_DIRECTORY + File.separator + path;
        } else if (isExternalHiddenStorageDirectoryExist) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + HIDE_APP_DIRECTORY + File.separator + path;
        }
        return activity.getExternalCacheDir() + File.separator + APP_DIRECTORY + File.separator + path;
    }

    private static String getFilePath(String path, Activity activity) {
        if (isExternalStorageDirectoryExist == null) {
            setIsExternalStorageDirectoryExistFlag(path);
        }

        if (isExternalStorageDirectoryExist != null && isExternalStorageDirectoryExist) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + APP_DIRECTORY + File.separator + path;
        } else if (isExternalHiddenStorageDirectoryExist) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + HIDE_APP_DIRECTORY + File.separator + path;
        }
        return activity.getExternalCacheDir() + File.separator + APP_DIRECTORY + File.separator + path;
    }

    private static void setIsExternalStorageDirectoryExistFlag(String path) {
        File mydir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + APP_DIRECTORY + File.separator + path);
        if (!mydir.exists()) {
            isExternalStorageDirectoryExist = mydir.mkdirs();
        }
        if (isExternalStorageDirectoryExist != null && !isExternalStorageDirectoryExist) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + HIDE_APP_DIRECTORY + File.separator + path;
            mydir = new File(path);
            if (!mydir.exists()) {
                isExternalHiddenStorageDirectoryExist = mydir.mkdirs();
            }
        }
    }
}
