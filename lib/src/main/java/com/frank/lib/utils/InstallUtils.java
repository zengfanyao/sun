package com.frank.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.service.DownloadService;

import java.io.File;

public class InstallUtils {

    /**
     * 下载apk并安装
     * @param context
     * @param apkUrl
     */
    public static void updateApk(final Context context, String apkUrl){
        UpdateAppBean updateAppBean = new UpdateAppBean();
        updateAppBean.setApkFileUrl(apkUrl);//设置 apk 的下载地址
        String path = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
            try {
                path = context.getExternalCacheDir().getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(path)) {
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            }
        } else {
            path = context.getCacheDir().getAbsolutePath();
        }
        updateAppBean.setTargetPath(path); //设置apk 的保存路径
        updateAppBean.setHttpManager(new UpdateAppHttpUtil()); //实现网络接口，只实现下载就可以
        UpdateAppManager.download(context, updateAppBean, new DownloadService.DownloadCallback() {
            @Override
            public void onStart() {
                HProgressDialogUtils.showHorizontalProgressDialog((Activity) context, "下载进度", false);
                Log.d("下载", "onStart() called");
            }

            @Override
            public void onProgress(float progress, long totalSize) {
                HProgressDialogUtils.setProgress(Math.round(progress * 100));
                Log.d("下载","onProgress() called with: progress = [" + progress + "], totalSize = [" + totalSize + "]");
            }

            @Override
            public void setMax(long totalSize) {
                Log.d("下载","setMax() called with: totalSize = [" + totalSize + "]");
            }

            @Override
            public boolean onFinish(File file) {
                HProgressDialogUtils.cancel();
                Log.d("下载","onFinish() called with: file = [" + file.getAbsolutePath() + "]");
                return true;
            }

            @Override
            public void onError(String msg) {
                HProgressDialogUtils.cancel();
                Log.e("下载", "onError() called with: msg = [" + msg + "]");
            }

            @Override
            public boolean onInstallAppAndAppOnForeground(File file) {
                Log.d("下载","onInstallAppAndAppOnForeground() called with: file = [" + file + "]");
                return false;
            }
        });
    }
}
