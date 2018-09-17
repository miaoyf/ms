package com.mq.screen.util;

import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;

public class BaiduAudioUtil {
    //设置APPID/AK/SK
    public static final String APP_ID = "11537994";
    public static final String API_KEY = "yyKNx6nOsCDhY3kMnWGvQqM8";
    public static final String SECRET_KEY = "OU1YytmbwTmSTVP84RUyAIi4DexfxTm3";

    public static byte[] tansToAudio(String text) throws Exception {
        // 初始化一个AipSpeech
        AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
        /*client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理
*/
        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        //System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 调用接口
        TtsResponse res = client.synthesis(text, "zh", 1, null);
        return res.getData();
    }
}
