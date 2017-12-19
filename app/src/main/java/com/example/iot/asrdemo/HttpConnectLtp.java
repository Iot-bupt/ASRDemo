package com.example.iot.asrdemo;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by zy on 2017/12/11.
 */

public class HttpConnectLtp {

    public  String  api_key;
    public  String  pattern;
    public  String  format;
    public  String  text = "";

    public HttpConnectLtp(String txt){
        api_key = "r1p5R7F5Y3jYNpgzkaLZWfLnooLL7N4N7DrfPXCK";//api_key,申请账号后生成，免费申请。
        pattern = "pos";//ws表示只分词，除此还有pos词性标注、ner命名实体识别、dp依存句法分词、srl语义角色标注、all全部
        format = "plain";//指定结果格式类型，plain表示简洁文本格式
        text = txt;

    }

    public  String sendGet1(String text) {
        String result = "";
        BufferedReader in = null;
        try {
            //String urlNameString = url + "?" + param;
            String re = URLEncoder.encode(text,"UTF-8");
            String url = "https://api.ltp-cloud.com/analysis/?"
                    + "api_key=" + api_key + "&"
                    + "text=" + re + "&"
                    + "format=" + format + "&"
                    + "pattern=" + pattern;

            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

//    public static void main(String[] arg){
//       HttpConnectLtp hc= new HttpConnectLtp("打开窗帘");
//       hc.sendGet1("打开窗帘");
//       //System.err.println("res--->"+hc.sendGet1("打开窗帘"));
//    }

}
