package com.example.android_dump_mp4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.os.Environment;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String NV21 = ".NV21";
    private List<String>mNv21ImagesPaths = new ArrayList<>();
    private AvcEncoderOnSynchronous mNv21toH264Encoder = null;
    private int mNv21Width = 1280;
    private int mNv21Height = 720;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (false == init()) {
            return;
        }
        loadImages();
    }
    public boolean init() {
        if (null == mNv21toH264Encoder) {
            try {
                mNv21toH264Encoder = new AvcEncoderOnSynchronous(
                        mNv21Width, mNv21Height, 30,
                        mNv21Width * mNv21Height * 5, Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/11.mp4");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                return false;
            }
        }
        else {
            return true;
        }
    }
    public void loadImages() {
        try {
            String[] fileNames = getAssets().list("");
            Log.d(TAG, "file size:" + fileNames.length);
            for (String fileName : fileNames) {
                Log.d(TAG, "scan file:" + fileName);
                if (true == fileName.contains(NV21)) {
                    mNv21ImagesPaths.add(fileName);
                    Log.d(TAG, "scan NV21 file:" + fileName);
                }
            }
            for (String fileName : mNv21ImagesPaths) {
                InputStream is = getAssets().open(fileName);
                if (is != null) {
                    Log.d(TAG, fileName + " open success!");
                }
                else {
                    Log.d(TAG, fileName + " open failed!");
                    continue;
                }
                byte[] imageData = new byte[is.available()];
                int bytes = is.read(imageData, 0, imageData.length);
                Log.d(TAG, fileName + " read size:" + bytes);
                is.close();
                mNv21toH264Encoder.offerEncoder(imageData);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        finally {
            mNv21toH264Encoder.close();
            mNv21toH264Encoder = null;
        }
    }
}