package com.zengweicong.uploadapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.zengweicong.uploadapp.HttpUtils.TAG;

public class MainActivity extends AppCompatActivity {
    String TAG = "APKUpdate";
    Spinner spinner;
    String URL = "https://fsaqa.meizu.com/utility/all";
    String resdata = null;
    private ListView mListView;
    static MyAdapter adapter;
    private TextView netState;
    private TextView title;
    List<ProjectBean>  listpb = new ArrayList<>();
    UpdataBroadcastReceiver mBroadcastReceiverp;
    UpdataBroadcastReceiver mBroadcastReceiverd;
    Handler mHandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 0:
                    if(listpb.size()>0){
                        if (adapter == null) {
                            adapter = new MyAdapter(MainActivity.this, listpb);
                        } else {
                            adapter.setListpb(MainActivity.this.listpb);
                        }
                        mListView.setAdapter(adapter);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        getNetData();
        initBroadcast();
   }

    private void initView() {
        mListView = (ListView)findViewById(R.id.list_view);
        netState = (TextView)findViewById(R.id.net_state);
        title = (TextView)findViewById(R.id.title);

        /*mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int positon, long id) {
                Toast.makeText(MainActivity.this,"click",Toast.LENGTH_SHORT).show();
                Log.d(TAG,"listview 点击");
                //在这里面就是执行点击后要进行的操作,这里只是做一个显示
//                                Toast.makeText(MainActivity.this, "您点击的是"+list.get(positon).toString(), 0).show();
                new  AlertDialog.Builder(MainActivity.this)
                        .setTitle("更新信息" )
                        .setMessage(listpb.get(positon).description)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }
                        ).show();
            }
        });*/
    }

    public void getNetData(){
       if(!connection(this)){
           title.setVisibility(View.INVISIBLE);
           mListView.setVisibility(View.INVISIBLE);
           netState.setText("无网络,请检查网络连接");
       }
       else {
           netState.setVisibility(View.INVISIBLE);
           new Thread(){
               @Override
               public void run() {
                   try {
                       resdata = HttpUtils.getRequester(URL);
                       List<String> model = new ArrayList<String>();
                       model = JsonUtil.parseCities(resdata);
                       Log.d(TAG,"model" + model.toString());
                       ProjectBean pb = null;
                       for (int i = 0;i<model.size();i++){
                           JSONObject jsonObject = JSONObject.parseObject(model.get(i));
                           pb = JSONObject.toJavaObject(jsonObject,
                                   ProjectBean.class);
                           Log.d(TAG,"description "+ pb.description);
                           listpb.add(pb);
                       }
                       mHandler.sendEmptyMessage(0);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }  catch (org.json.JSONException e) {
                       e.printStackTrace();
                   }
               }
           }.start();
       }
   }
    public void initBroadcast() {
        mBroadcastReceiverp = new UpdataBroadcastReceiver();
        mBroadcastReceiverd = new UpdataBroadcastReceiver();
        IntentFilter intentFilterpackage = new IntentFilter();
        intentFilterpackage.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilterpackage.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilterpackage.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilterpackage.addDataScheme("package");
        IntentFilter intentFilterdownload = new IntentFilter();
        intentFilterdownload.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(mBroadcastReceiverp,intentFilterdownload);
        registerReceiver(mBroadcastReceiverd,intentFilterpackage);
    }

    private boolean connection(Context context) {
        if (context != null) {
            ConnectivityManager connect = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connect.getActiveNetworkInfo();
            if (info != null ) {
                return info.isConnected();
                //if(info.isAvailable()){//此判断在使用时会有异常，需要处理
            }
        }
        return false;
    }

    public class UpdataBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            Log.d(TAG, "RECEI:" + intent.getAction());
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED) || intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                Log.d(TAG, intent.getDataString());
                listpb.get(0).description = listpb.get(0).description + " ";
                adapter.refreshData(listpb);
            }
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                SharedPreferences sPreferences = context.getSharedPreferences("downloadcomplete", 0);
                long refernece = sPreferences.getLong("refernece", 0);
                if (refernece == myDwonloadID) {
                    try {
                        String serviceString = Context.DOWNLOAD_SERVICE;
                        DownloadManager dManager = (DownloadManager) context.getSystemService(serviceString);
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            //能过FileDescriptor读取contentUri中的输入流
                            //并保存到app外部存储files目录下
                            //用于共享给安装程序用于更新

                            ParcelFileDescriptor parcelFileDescriptor = dManager.openDownloadedFile(myDwonloadID);
                            //                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");

                            InputStream fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                            //                    InputStream fileInputStream = getContentResolver().openInputStream(uri);

                            byte[] buffer = new byte[fileInputStream.available()];
                            int read = fileInputStream.read(buffer);
                            //不设置DownloadManager.Request的DestinationUri
                            String tempFilePath = context.getFilesDir().getPath() + File.separator + "aaa111.apk";
                            //设置request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "app-release.apk");
                            File targetFile = new File(tempFilePath);
                            long length = targetFile.length();
                            OutputStream outStream = new FileOutputStream(targetFile);
                            outStream.write(buffer);
                            fileInputStream.close();
                            outStream.close();

                            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", targetFile);
                            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            install.setDataAndType(apkUri, "application/vnd.android.package-archive");

                        } else {

                            Uri downloadFileUri = dManager.getUriForDownloadedFile(myDwonloadID);
                            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        context.startActivity(install);
                    } catch (FileNotFoundException e){
                        e.printStackTrace();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void refreshListView(){
        if(listpb==null || listpb.size()<=0){
            return;
        }
        listpb.get(0).description = listpb.get(0).description + " ";
        //testList.addAll(listpb);
        adapter.refreshData(listpb);
    }
    @Override
    protected void onResume() {
        super.onResume();
        refreshListView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiverp);
        unregisterReceiver(mBroadcastReceiverd);
    }
}
