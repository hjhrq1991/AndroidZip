package com.leo618.androidzip;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.leo618.zip.IZipCallback;
import com.leo618.zip.ZipManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * function:压缩解压测试
 *
 * <p>
 * Created by Leo on 2018/1/16.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authPermission();
    }

    private String dir_path        = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aaa";
    private String dir_zip_path    = dir_path + "/zip_files/";
    private String unzip_file_path = dir_path + "/zipFile.zip";
    private File   file1           = new File(dir_path + "/图片.png");
    private File   file2           = new File(dir_path + "/123.mp4");


    public void zip(View view) {
        if (!authPermission()) return;

        ArrayList<File> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);
        ZipManager.zip(files, unzip_file_path, new IZipCallback() {
            @Override
            public void onStart() {
                loadingShow(-1);
            }

            @Override
            public void onProgress(int percentDone) {
                loadingShow(percentDone);
            }

            @Override
            public void onFinish(boolean success, String message) {
                loadingHide();
                toast(success ? "成功" : "失败");
            }
        });
    }

    public void unZip(View view) {
        if (!authPermission()) return;
        ZipManager.unzip(unzip_file_path, dir_zip_path, new IZipCallback() {
            @Override
            public void onStart() {
                loadingShow(-1);
            }

            @Override
            public void onProgress(int percentDone) {
                loadingShow(percentDone);
            }

            @Override
            public void onFinish(boolean success, String message) {
                loadingHide();
                toast(success ? "成功" : "失败");
            }
        });
    }

    ///------Progress Loading
    private ProgressDialog mLoading;

    private void loadingShow(int percent) {
        if (mLoading == null) {
            mLoading=  new ProgressDialog(this);
            mLoading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mLoading.setMax(100);
        }
        if (percent > 0) {
            mLoading.setProgress(percent);
            mLoading.setMessage(percent + "%");
        }
        if (!mLoading.isShowing()) mLoading.show();
    }

    private void loadingHide() {
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
            mLoading = null;
        }
    }

    ///------Toast
    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    ///------Permissions
    private boolean hasPermission = false;

    private boolean authPermission() {
        if (hasPermission) return true;

        XXPermissions.with(MainActivity.this).permission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .unchecked()
                .request(new OnPermissionCallback(){
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        hasPermission = allGranted;
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        OnPermissionCallback.super.onDenied(permissions, doNotAskAgain);
                    }
                });

        return false;
    }
}
