package com.example.iot.asrdemo;

import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.speech.EventListener;
import com.show.api.ShowApiRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements EventListener{

    private Button btn;
    private Button stopBtn;
    private TextView textView;
    private TextView tv_result;
    private EventManager asr;
//    private Handler mHandler;

    private boolean enableOffline = true; // 测试离线命令词，需要改成true

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mHandler=new Handler();

        btn = findViewById(R.id.btn_start);
        stopBtn = findViewById(R.id.btn_stop);
        textView = findViewById(R.id.textView);
        tv_result = findViewById(R.id.tv_result);

        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this); //  EventListener 中 onEvent方法
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                start();
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stop();
            }
        });
        if (enableOffline) {
            loadOfflineEngine(); //测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        if (enableOffline) {
            unloadOfflineEngine(); //测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }
    }

    /**
     * 测试参数填在这里
     */
    private void start() {
        tv_result.setText(" ");
        textView.setText(" ");
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START; // 替换成测试的event

//        params.put(SpeechConstant.PID,15361);//百度语义解析请开启
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.VAD,SpeechConstant.VAD_DNN);
        if (enableOffline){
            params.put(SpeechConstant.DECODER, 2);
        }
//         params.put(SpeechConstant.NLU, "enable");//百度语义解析请开启
         params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0);
         params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
         params.put(SpeechConstant.PROP ,20000);
        String json = null; //可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(event, json, null, 0, 0);
        Log.e("MainActivity", "输入语音结果："+json );
    }

    private void stop() {
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
    }

    private void loadOfflineEngine() {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(SpeechConstant.DECODER, 2);
        params.put(SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH, "assets://baidu_speech_grammar.bsg");
        asr.send(SpeechConstant.ASR_KWS_LOAD_ENGINE, new JSONObject(params).toString(), null, 0, 0);
    }

    private void unloadOfflineEngine() {
        asr.send(SpeechConstant.ASR_KWS_UNLOAD_ENGINE, null, null, 0, 0); //
    }

    //   EventListener  回调方法
    /*
    OnEvent中， name是输出事件名，
    params该事件的参数，
    (data,offset, length) 语义结果的内容，三者一起组成额外数据。
    如回调的音频数据，从data[offset] 开始至data[offset + length] 结束，长度为length。
     */
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {

        if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)){
            // 引擎就绪，可以说话，一般在收到此事件后通过UI通知用户可以说话了
            Toast.makeText(this, "可以说话啦！", Toast.LENGTH_SHORT).show();
        }
        if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_EXIT)){
            // 识别结束
            Toast.makeText(this, "识别结束！", Toast.LENGTH_SHORT).show();

        }
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)){
            //识别结果
            if(params.contains("\"final_result\"")){
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(params);
                    String best_result = jsonObject.getString("best_result");
                    Log.e("MainActivity", "best_result:"+best_result );
                    tv_result.setText(best_result);
                    String fenci = LtpCloud(best_result);
//                    Log.e("MainActivity", "分词结果 ："+fenci );
                    textView.setText(fenci);

                    ControlDevice cd = new ControlDevice();
                    cd.match(fenci);
                    cd.post();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

/*
对结果进行分词，返回结果是个String
 */
    private String LtpCloud(String text){
        HttpConnectLtp htp = new HttpConnectLtp(text);
        return htp.sendGet1(text);

    }
}
