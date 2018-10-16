package com.zengweicong.uploadapp;

import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class HttpUtils {
//    /**
//     * @param path    请求的服务器URL地址
//     * @param encode    编码格式
//     * @return    将服务器端返回的数据转换成String
//     */
//    public static String sendPostMessage(String path, String encode)
//    {
//        String result = "";
//        HttpClient httpClient = new DefaultHttpClient();
//        try
//        {
//            HttpPost httpPost = new HttpPost(path);
//            HttpResponse httpResponse = httpClient.execute(httpPost);
//            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
//            {
//                HttpEntity httpEntity = httpResponse.getEntity();
//                if(httpEntity != null)
//                {
//                    result = EntityUtils.toString(httpEntity, encode);
//                }
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally
//        {
//            httpClient.getConnectionManager().shutdown();
//        }
//
//        return result;
//    }

    public static final String TAG = "APKUPDATE";
    public static String getRequester(String url) throws IOException, JSONException {
        String data;
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(10, 30, TimeUnit.SECONDS))
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        data = response.body().string();
        Log.i(TAG, "getRequester: " + data);
        return data;
    }

    public String postRequester(String url, String param, String content_type) throws IOException {
        OkHttpClient okHttpClient_post_json = new OkHttpClient();
        RequestBody body =
                RequestBody.create(MediaType.parse(content_type), param);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        String response = okHttpClient_post_json.newCall(request).execute().body().string();
        Log.d(TAG,response);
        return response;
    }


}
