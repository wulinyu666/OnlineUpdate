package com.zengweicong.uploadapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by wulinyu on 2017/7/3.
 */

public class MyAdapter extends BaseAdapter {
    Intent intent ;
    private LayoutInflater mInflater = null;
    private List<ProjectBean>  listpb;
    private Context mContext;
    String TAG = "ApkUpdate";
    String versionNameLocal ;
    Integer versionCodeLocal;
    tools t = new tools();


    public MyAdapter(Context context,List<ProjectBean>  listpb) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.listpb = listpb;
    }
    public void refreshData(List<ProjectBean>  listpb){
        this.listpb = listpb;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listpb.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void cliclItem(int position){
        Log.d(TAG,"listview 点击");
        new  AlertDialog.Builder(mContext)
                .setTitle("更新信息" )
                .setMessage(listpb.get(position).description)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }
                ).show();
    }

    public boolean isEnabled(int position) {
        versionCodeLocal = APKVersionCodeUtils.getVersionCode(mContext,listpb.get(position).packageName);
        versionNameLocal = APKVersionCodeUtils.getVerName(mContext,listpb.get(position).packageName);
        String uploadPackageName = listpb.get(position).packageName;
        Integer uploadVersionCode = listpb.get(position).versionCode;
        String uploadlink = listpb.get(position).link;
        String uploadVersionName = listpb.get(position).versionName;
        boolean isEnabled;
        if (uploadlink!=null&&uploadPackageName!=null && uploadVersionCode!=null&&(uploadVersionCode>versionCodeLocal ||versionCodeLocal==null)) {//这里给上条件，根据业务需求设置，这项是否可点击
            isEnabled=true;//
        } else {
            isEnabled=false;
        }
        return isEnabled;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
       if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.title = (TextView)convertView.findViewById(R.id.db_info);
            holder.viewBtn = (Button) convertView.findViewById(R.id.download);
            holder.itemLayout=convertView.findViewById(R.id.list_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cliclItem(position);
            }
        });

        holder.title.setText(listpb.get(position).project);
        Log.d(TAG,listpb.get(position).project + listpb.get(position).link);
        holder.viewBtn.setTag(listpb.get(position).link);
        Log.d(TAG,"文件名" +listpb.get(position).getVersionname());
        String subPath = holder.title.getText() + listpb.get(position).getVersionname();
        if(!isEnabled(position)){
            holder.viewBtn.setText("已是最新");
            holder.viewBtn.setEnabled(false);
            holder.viewBtn.getBackground().setColorFilter(Color.parseColor("#dcdcdc"), PorterDuff.Mode.SRC_OVER);
        }
        else if (t.fileIsExists("/sdcard/download/" + subPath +".apk")){
            holder.viewBtn.getBackground().clearColorFilter();
            holder.viewBtn.setText("下载完成");
            holder.viewBtn.setEnabled(false);
        }
        else{
            holder.viewBtn.setText("下载");
            holder.viewBtn.setEnabled(true);
            holder.viewBtn.getBackground().clearColorFilter();
        }
        holder.viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.viewBtn.setText("下载中");
                holder.viewBtn.setEnabled(false);
                intent= new Intent(mContext.getApplicationContext(), MyService.class);
                intent.putExtra("project",listpb.get(position).project);
                intent.putExtra("link",listpb.get(position).link);
                intent.putExtra("versionname",listpb.get(position).versionName);
                mContext.startService(intent);
            }
        });
        return convertView;
    }
    static class ViewHolder {
        LinearLayout itemLayout;
        TextView title;
        Button viewBtn;
    }

    public void setListpb(List<ProjectBean>  listpb) {
        this.listpb = listpb;
    }
        //判断文件是否存在
}
