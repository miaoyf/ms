package com.mq.screen.service;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mq.screen.bo.Meeting;
import com.mq.screen.bo.MeetingBO;
import com.mq.screen.util.ReadUtil;
import com.mq.screen.util.ResponseUtil;

@RestController
@RequestMapping("/screen")
public class ScreenService {

	CloseableHttpClient https = HttpClients.createDefault();
	
	private static MeetingBO meetingBO = new MeetingBO();
	
	@RequestMapping(value = "/queue", method = RequestMethod.GET)
	public @ResponseBody Object getQRCode() {
		if(meetingBO.isFresh()) {
			meetingBO.setFresh(false);
			return ResponseUtil.successData(JSON.toJSONString(meetingBO));
		}
		return ResponseUtil.success();
	}
	
	private void doCallMember(MeetingBO meetingBO) {
		Meeting current = meetingBO.getCurrent();
		if(current != null) {
			String curr = "请，" + current.getUserName() + "，到" + current.getRoom() + "，参加" + current.getSubject() + "会议。";
			ReadUtil.reading(curr);
		}
		Meeting next = meetingBO.getNext();
		if(next != null) {
			String nextMember = "下一个议题，" + next.getSubject() + "，请" + next.getUserName() + "，做好准备。";
			ReadUtil.reading(nextMember);
		}
	}
	
	@RequestMapping(value = "/readScreen", method = RequestMethod.GET)
	public @ResponseBody Object readScreen() {
		for(int i=0;i<2;i++) {
			
			doCallMember(meetingBO);//读取屏幕内容
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		return ResponseUtil.success();
	
	}
	
	
	@RequestMapping(value = "/show")
	public @ResponseBody Object show(@RequestParam("param")String param) {
		if(param != null && !"".equals(param)) {
			JSONObject jsonObj = JSON.parseObject(param);
			if(jsonObj != null) {
				JSONObject currentObj = jsonObj.getJSONObject("current");
				if(currentObj != null) {
					//设置当前会议
					meetingBO.setCurrent(currentObj.toJavaObject(Meeting.class));
					meetingBO.setFresh(true);
					//设置前一个
					JSONObject preObj = jsonObj.getJSONObject("pre");
					if(preObj != null) {
						meetingBO.setPre(preObj.toJavaObject(Meeting.class));
					}
					//设置下一个
					JSONObject nextObj = jsonObj.getJSONObject("next");
					if(nextObj != null) {
						meetingBO.setNext(nextObj.toJavaObject(Meeting.class));
					}
				}
			}
		}
		return ResponseUtil.success();
	}
	
}
