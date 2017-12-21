package com.example.iot.asrdemo;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by zy on 2017/12/14.
 */


public class ControlDevice {

    private String text;
    PrintWriter out = null;
    BufferedReader in = null;
    String result = "";
    boolean param ;
    int deviceId ;

    public void match(String result){

        ArrayList v = new ArrayList();
        ArrayList n = new ArrayList();

        String[] resultarray = result.split("\\s+");
//        System.out.println("分割后的数组："+ Arrays.toString(resultarray));
        Log.e("ControlDevice", "分割后的数组："+ Arrays.toString(resultarray));
        for (int i = 0;i<resultarray.length;i++){

            if(resultarray[i].contains("v")){
                String vstr = resultarray[i];
                String[] varray = vstr.split("_");
                System.out.println("varray:"+ varray[0]);
                v.add(varray[0]);
            }else if(resultarray[i].contains("n")){
                String nstr = resultarray[i];
                String[] narray = nstr.split("_");
                n.add(narray[0]);
            }
        }

//        System.out.println("v[] = "+v);
//        System.out.println("n[] = "+n);
        Log.e("ControlDevice", "动词数组："+v );
        Log.e("ControlDevice", "名词数组："+n );

        if(v.contains("打开")||v.contains("拉开")){
            param = true;
        }else if(v.contains("关闭")||v.contains("拉上")||v.contains("关上")){
            param = false;
        }else{
            Log.e("ControlDevice", "the commands contain error!");
        }


        if(n.contains("灯")){
            deviceId = 1138530;
        }else if(n.contains("窗帘")){
            deviceId = 919653;
        }else if(n.contains("门")){
            deviceId = 544418;
        }else{
            Log.e("ControlDecive", "the device is not exsit!");
        }

        Log.e("ControlDevice", "deviceId:"+deviceId );
        Log.e("ControlDevice", "params:"+param );

    }

    public String post(){

       // int deviceId,boolean param

        try {
            String url = "http://10.108.217.227:8080/api/plugins/rpc/oneway/21c6f6d0-bd45-11e7-b3aa-0bb30bb377bd";
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("Accept", "application/json, text/plain, */*");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            conn.setRequestProperty("Connection","keep-alive");

            conn.setRequestProperty("Content-Type","application/json;charset=UTF-8");
            conn.setRequestProperty("Referer","http://10.108.217.227:8080/dashboards/1deebad0-bea7-11e7-b3aa-0bb30bb377bd");
            conn.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36");
            conn.setRequestProperty("X-Authorization","Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZW5hbnRAdGhpbmdzYm9hcmQub3JnIiwic2NvcGVzIjpbIlRFTkFOVF9BRE1JTiJdLCJ1c2VySWQiOiJhMmIyNTMxMC1iN2VjLTExZTctOGZjMC01NTkyMmI1ZDQ3ZjYiLCJlbmFibGVkIjp0cnVlLCJpc1B1YmxpYyI6ZmFsc2UsInRlbmFudElkIjoiYTJiMTY4YjAtYjdlYy0xMWU3LThmYzAtNTU5MjJiNWQ0N2Y2IiwiY3VzdG9tZXJJZCI6IjEzODE0MDAwLTFkZDItMTFiMi04MDgwLTgwODA4MDgwODA4MCIsImlzcyI6InRoaW5nc2JvYXJkLmlvIiwiaWF0IjoxNTEzMjQyMDU0LCJleHAiOjE1MjIyNDIwNTR9.QznLLeAs_OfE5VLrux1AT86DdgV3CVByer3IMRmPh_RZiZWUHEkZiR7CZmKeaOEg_2uAb6YWbWBlbGHLo02_Ww");
            //post必要设置
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());

            // 发送请求参数
            JSONObject json = new JSONObject();
            json.put("method","setValue_"+deviceId);
            json.put("params",param);
            json.put("timeout",1000);

            Log.e("ControlDevice", "请求的json是：" + json);

//            String jsons = "{\"method\":\"setValue_deviceId\"," +
//                    "\"params\":param," +
//                    "\"timeout\":500}";
            out.print(json);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;

    }
}
