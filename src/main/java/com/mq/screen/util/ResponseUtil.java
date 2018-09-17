package com.mq.screen.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;

import com.alibaba.fastjson.JSON;

public class ResponseUtil {
	
	public static <T> Map<String, Object> success() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", true);
		return map;
	}

	public static <T> Map<String, Object> success(String message) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", true);
		map.put("msg", message);
		return map;
	}

	public static <T> Map<String, Object> successData(String message) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", true);
		map.put("data", message);
		return map;
	}

	public static <T> Map<String, Object> fail(String message) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", false);
		map.put("message", message);
		return map;
	}
	
	
	/**
     * 返回JsonP或者Json的方法
     * @param callback
     * @param t
     * @return
     */
    public static String getJsonP(String callback,Object t){
    	if(Strings.isNotBlank(callback)){
			return callback + "("+JSON.toJSONString(t)+")";
		}else{
			return JSON.toJSONString(t);
		}
    }
}
