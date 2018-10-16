package com.zengweicong.uploadapp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    /**
     * @param citiesString    从服务器端得到的JSON字符串数据
     * @return    解析JSON字符串数据，放入List当中
     */
    public static List<String> parseCities(String citiesString)
    {
        List<String> cities = new ArrayList<String>();

        try
        {
            JSONObject jsonObject = new JSONObject(citiesString);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for(int i = 0; i < jsonArray.length(); i++)
            {
                cities.add(jsonArray.getString(i));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return cities;
    }
}