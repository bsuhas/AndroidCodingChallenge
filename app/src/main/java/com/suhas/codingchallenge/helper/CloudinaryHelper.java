package com.suhas.codingchallenge.helper;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.UploadRequest;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.policy.TimeWindow;
import com.cloudinary.android.policy.UploadPolicy;
import com.cloudinary.android.preprocess.BitmapEncoder;
import com.cloudinary.android.preprocess.ImagePreprocessChain;

import java.util.Map;

public class CloudinaryHelper {
    public static void uploadResource(Activity activity , Uri uri, boolean preprocess) {
       /* UploadRequest request = MediaManager.get().upload(uri)
                .unsigned("android_sample")
                .constrain(TimeWindow.getDefault())
                .option("resource_type", "auto")
                .maxFileSize(100 * 1024 * 1024) // max 100mb
                .policy(MediaManager.get().getGlobalUploadPolicy().newBuilder().maxRetries(2).build());
        if (preprocess) {
            // scale down images above 2000 width/height, and re-encode as webp with 80 quality to save bandwidth
            request.preprocess(ImagePreprocessChain.limitDimensionsChain(2000, 2000)
                    .saveWith(new BitmapEncoder(BitmapEncoder.Format.WEBP, 80)));

        }
*/

/*  MediaManager.get().upload(uri)
        .unsigned("default-preset").dispatch();*/


        String requestId = MediaManager.get().upload(uri).unsigned("default-preset").callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                // your code here
            }
            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                // example code starts here
                Double progress = (double) bytes/totalBytes;
                // post progress to app UI (e.g. progress bar, notification)
                // example code ends here
            }
            @Override
            public void onSuccess(String requestId, Map resultData) {
                // your code
                Log.e("resultData",resultData.toString());
            }
            @Override
            public void onError(String requestId, ErrorInfo error) {
                // your code here
            }
            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                // your code here
            }})
                .dispatch();



//        return request.dispatch(activity);
    }


}
