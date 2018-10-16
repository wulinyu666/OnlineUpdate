package com.zengweicong.uploadapp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class APKVersionCodeUtils {
        /**
         * 获取当前本地apk的版本
         *
         * @param mContext
         * @return
         */
        public static int getVersionCode(Context mContext,String packageName) {
            PackageInfo packageInfo = null;
            try {
                PackageManager pm = mContext.getPackageManager();
                packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return (packageInfo == null ? -1 : packageInfo.versionCode);
        }

        /**
         * 获取版本号名称
         *
         * @param context 上下文
         * @return
         */
        public static String getVerName(Context context,String packagename) {
            String verName = "";
            try {
                verName = context.getPackageManager().
                        getPackageInfo(packagename, 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return verName;
        }
    }

