package com.suhas.codingchallenge.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by SUHAS on 09/05/2016.
 */
public class CameraUtils {
    public static final int CAMERA_REQUEST = 0x000003;
    public static String STORED_IMAGE_PATH = "/AndroidCodingChallenge";
    public static String mCurrentPhotoPath = "";
    public static Bitmap sImageThumbBitmap;
    public static Bitmap sImageActualBitmap;
    public static final String sPassImageURI = "PassedImageURI";
    public static final int ACTIVITY_REQUEST_CODE = 30;

    public static String compressImage(String imageUri, Activity activity) {

        String filePath = getRealPathFromURI(imageUri, activity);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
//        float maxHeight = 416.0f;
//        float maxWidth = 312.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));


        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);

            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);

            } else if (orientation == 3) {
                matrix.postRotate(180);

            } else if (orientation == 8) {
                matrix.postRotate(270);

            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filename = getFilename(activity);
        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
//            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;
    }

    private static String getRealPathFromURI(String contentURI, Activity activity) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = activity.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public static String getSDCardPathForStoreImages(Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath();
    }

    public static String getFilename(Context context) {
        File file = new File(CameraUtils.getSDCardPathForStoreImages(context), CameraUtils.STORED_IMAGE_PATH + "/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
      /*  if (resizedBitmap.getByteCount() > 31961088)
            getResizedBitmap(resizedBitmap,THUMBNAIL_SIZE, THUMBNAIL_SIZE);*/
        return resizedBitmap;
    }

    public static Bitmap getOriententionBitmap(String filePath) {
        Bitmap myBitmap = null;
        try {
            File f = new File(filePath);
            ExifInterface exif = new ExifInterface(f.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }

            Matrix mat = new Matrix();
            mat.postRotate(angle);

            Bitmap bmp1 = BitmapFactory.decodeStream(new FileInputStream(f), null, null);
            myBitmap = Bitmap.createBitmap(bmp1, 0, 0, bmp1.getWidth(), bmp1.getHeight(), mat, true);

            if (myBitmap.getByteCount() > 31961088) {
                myBitmap = CameraUtils.getResizedBitmap(myBitmap, myBitmap.getWidth() / 2, myBitmap.getHeight() / 2);
            }

        } catch (IOException e) {
            Log.w("TAG", "-- Error in setting image");
        } catch (OutOfMemoryError oom) {
            Log.w("TAG", "-- OOM Error in setting image");
        }
        return myBitmap;
    }

    public static String getImageEncodedData(String path) {
        File f = new File(path);
        Bitmap imageBitmap = null;
        try {
            imageBitmap = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //added for testing base 64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] imageBytes = baos.toByteArray();
        String mImageEncodedString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return mImageEncodedString;
    }

    public static String startCameraIntent(Activity activity) {
        String fileName = Constants.CAMERA_OUTPUT_NAME + System.currentTimeMillis() + ".mp4";
        File filePath = new File(Constants.getFilePath(activity));
        if (!filePath.exists()) {
            if (!filePath.mkdirs()) {
                return null;
            }
        }
        File file = new File(Constants.getTemporaryFilePath(activity) + fileName);
        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        final Uri outputUri;
        if (Build.VERSION.SDK_INT < 24) {
            outputUri = Uri.fromFile(file);
        } else {
            outputUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", file);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        activity.startActivityForResult(cameraIntent, ACTIVITY_REQUEST_CODE);
        return file.getAbsolutePath();
    }
}
