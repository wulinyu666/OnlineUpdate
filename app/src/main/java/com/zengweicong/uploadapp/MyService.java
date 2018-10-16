package com.zengweicong.uploadapp;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

/**
 * Created by zengweicong on 2018-5-14.
 */

public class MyService extends Service {
    String project;
    String link;
    String versionname;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                project=intent.getStringExtra("project");
                link=intent.getStringExtra("link");
                versionname = intent.getStringExtra("versionname");
                downloadAPK(link);
            }
        });
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("xxxx" ,"onDestroy");
    }
    private void downloadAPK(String dowloadUrl) {
         DownloadManager dManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
         Uri uri = Uri.parse(dowloadUrl);
         File file=new File("/sdcard/download/"+ project + versionname +".apk");
         if(file.exists()) {
             file.delete();
         }
         DownloadManager.Request request = new DownloadManager.Request(uri);
        // 设置下载路径和文件名
         request.setDestinationInExternalPublicDir("download", project  + versionname +".apk");
         request.setAllowedNetworkTypes(request.NETWORK_WIFI);
        //设置通知栏标题
         request.setTitle("下载");
         request.setDescription("app新版本下载");
         request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
         request.setMimeType("application/vnd.android.package-archive");
         // 设置为可被媒体扫描器找到
         request.allowScanningByMediaScanner();
         // 设置为可见和可管理
         request.setVisibleInDownloadsUi(true);
         long refernece = dManager.enqueue(request);
         // 把当前下载的ID保存起来
         SharedPreferences sPreferences = getSharedPreferences("downloadcomplete", 0);
         sPreferences.edit().putLong("refernece", refernece).commit();
    }

}
