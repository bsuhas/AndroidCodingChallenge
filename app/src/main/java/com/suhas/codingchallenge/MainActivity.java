package com.suhas.codingchallenge;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.suhas.codingchallenge.dailog.PickMemoryDialog;
import com.suhas.codingchallenge.helper.CloudinaryHelper;
import com.suhas.codingchallenge.utils.CameraUtils;
import com.suhas.codingchallenge.utils.Constants;

import java.io.File;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private int CAMERA_PERMISSION = 454;
    private Activity mActivity;
    private PickMemoryDialog mPickMemoryDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        init();
        removeFileExposer();
    }

    private void removeFileExposer() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION);
                } else {
                    openPickMemoryDialog();
                }
            }
        });
    }

    private void openPickMemoryDialog() {
        mPickMemoryDialog = new PickMemoryDialog(mActivity, mActivity);
        mPickMemoryDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Image from Camera
        if (requestCode == Constants.CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            String compressedPath = CameraUtils.compressImage(Constants.mCurrentPhotoPath, this);

            Log.e("Src", "compressedPath" + compressedPath);
            File src = new File(compressedPath);
            Log.e("Src", src.getAbsolutePath());
            mPickMemoryDialog.dismiss();
//            uploadImage(src);
            Uri uri = Uri.fromFile(src);
            CloudinaryHelper.uploadResource(mActivity,uri,false);
            File fileDelete = new File(Constants.mCurrentPhotoPath);
            if (fileDelete.exists())
                fileDelete.delete();

        }
        //Image from Gallery
        else if (requestCode == Constants.GALLERY_INTENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            try {
                String path = getFilePath(data);
                String compressedPath = CameraUtils.compressImage(path, this);
                File src = new File(compressedPath);
                mPickMemoryDialog.dismiss();
//                uploadImage(src);
                Uri uri = Uri.fromFile(src);
                CloudinaryHelper.uploadResource(mActivity,uri,false);
                mPickMemoryDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openPickMemoryDialog();
            }else {
                Toast.makeText(this, "Please grant permissions ", Toast.LENGTH_LONG).show();
            }
        }

    }

    private String getFilePath(Intent data) {
        String imagePath;
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = mActivity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        imagePath = cursor.getString(columnIndex);
        cursor.close();

        return imagePath;

    }

}
