package com.mq.screen.service;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mq.screen.manager.MeetingManager;
import com.mq.screen.util.BaiduAudioUtil;
import com.mq.screen.util.ReadUtil;
import com.mq.screen.util.ResponseUtil;

@RestController
@RequestMapping("/meeting")
public class MeetingService {
	
	private static final Logger LOG = LoggerFactory.getLogger(MeetingService.class);
	@Autowired
	private MeetingManager meetingManager;
	
	private static Map<String,Map<String,Object>> readMap = new HashMap<String,Map<String,Object>>();
	
	@RequestMapping(value = "/issueState/{id}", method = RequestMethod.POST)
	public @ResponseBody Object issueState(@PathVariable("id") Long issueId) {
		try {
			//读屏幕
			readScreen(issueId);
			//更新议题状态为已开
			meetingManager.updateMeetingIssueState(issueId,1);
			return ResponseUtil.success();
		} catch (Exception e) {
			LOG.error("更新议题已开状态异常：", e);
			return ResponseUtil.fail("更新议题已开状态异常");
		}
	}
	
	@RequestMapping(value = "/showScreen", method = RequestMethod.GET)
	public @ResponseBody Object getQRCode() {
		Map<String,Object> flagMap = readMap.get("isNew");
		if(flagMap != null) {
			Boolean isNew = (Boolean) flagMap.get("isNew");
			if(isNew != null && isNew) {
				setFlag(false);
				return ResponseUtil.successData(JSON.toJSONString(readMap));
			}
		}
		return ResponseUtil.success();
	}
	
	@RequestMapping(value = "/loadAudio", method = RequestMethod.GET)
	public void loadAudio(HttpServletRequest request,HttpServletResponse response) {
		try {
			OutputStream outputStream = response.getOutputStream();//获取OutputStream输出流
			byte[] data = BaiduAudioUtil.tansToAudio(getReadText());
			outputStream.write(data);
		} catch (Exception e) {
			LOG.error("音频转换异常：",e);
		}
	}
	
	@RequestMapping(value = "/readScreen", method = RequestMethod.GET)
	public @ResponseBody Object readScreen() {
		for(int i=0;i<2;i++) {
			doCallMember(readMap);//读取屏幕内容
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		return ResponseUtil.success();
	}
	
	/**
	 * 读取屏幕内容
	 * @param readMap
	 */
	private String getReadText() {
		String text = "";
		Map<String,Object> current = readMap.get("current");
		if(current != null) {
			//当前参会人
			text = "请," + current.get("report_unit") + current.get("report_post") + "," + current.get("report_person") + "，到" + current.get("meeting_room") + "，参加" + current.get("report_issue") + "会议.";
			//下一个
			Map<String,Object> next = readMap.get("next");
			if(next != null) {
				String nextMember = "下一个议题," + next.get("report_issue") + ",请" + next.get("report_unit") + next.get("report_post") + "," + next.get("report_person") + "，做好准备。";
				text += "," + nextMember;
			}
		}
		LOG.error("text=" + text);
		return text;
	}
	
	/**
	 * 读取屏幕内容
	 * @param readMap
	 */
	private void doCallMember(Map<String,Map<String,Object>> readMap) {
		Map<String,Object> current = readMap.get("current");
		if(current != null) {
			//当前参会人
			String curr = "请," + current.get("report_unit") + current.get("report_post") + "," + current.get("report_person") + "，到" + current.get("meeting_room") + "，参加" + current.get("report_issue") + "会议.";
			ReadUtil.reading(curr);
			//下一个
			Map<String,Object> next = readMap.get("next");
			if(next != null) {
				String nextMember = "下一个议题," + next.get("report_issue") + ",请" + next.get("report_unit") + next.get("report_post") + "," + next.get("report_person") + "，做好准备。";
				ReadUtil.reading(nextMember);
			}
		}
	}
	
	/**
	 * 读屏幕
	 * @param issueId
	 * @throws Exception
	 */
	private void readScreen(Long issueId) throws Exception {
		Map<String,Object> issues = meetingManager.getMeetingissueById(issueId);
		//当前议题、下个议题
		if(issues != null) {
			//清空当前的屏幕内容
			readMap = new HashMap<String,Map<String,Object>>();
			//组装下一屏信息
			Long meetingId = (Long) issues.get("meeting_id");
			List<Map<String, Object>> issueList = meetingManager.getMeetingissueByMeetingId(meetingId);
			if(issueList != null && !issueList.isEmpty()) {
				readMap.put("current", issueList.get(0));
				if(issueList.size() > 1) {
					readMap.put("next", issueList.get(1));
				}
			}
			//上个议题
			List<Map<String,Object>> oldIssues = meetingManager.getIssueByMeetingIdAndState(meetingId,0);
			if(oldIssues != null && !oldIssues.isEmpty()) {
				readMap.put("pre", oldIssues.get(0));
			}
			//标记有新内容需要读屏
			setFlag(!readMap.isEmpty());
		}
		//读屏幕
		LOG.info("readScreen=" + JSON.toJSONString(readMap));
	}

	private void setFlag(boolean flag) {
		Map<String,Object> flagMap = new HashMap<String, Object>();
		flagMap.put("isNew", flag);
		readMap.put("isNew", flagMap);
	}
	
	@RequestMapping(value = "/sortindex", method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
	public @ResponseBody Object sortindex(@RequestBody String jsonParam) {
		JSONArray orderList = JSON.parseArray(jsonParam);
		for(Object order : orderList) {
			JSONObject issueObj = (JSONObject) order;
			Long issueId = issueObj.getLong("id");
			Integer index = issueObj.getInteger("index");
			try {
				meetingManager.updateMeetingIssueOrder(issueId,index);
			} catch (Exception e) {
				LOG.error("更新排序异常：", e);
			}
		}
		return ResponseUtil.success();
	}
	
	@RequestMapping(value = "/meeting/{meetingId}", method = RequestMethod.GET)
	public @ResponseBody Object meeting(@PathVariable("meetingId") Long meetingId) {
		try {
			Map<String,Object> meeting = meetingManager.getMeetingById(meetingId);
			return ResponseUtil.success(JSON.toJSONString(meeting));
		} catch (Exception e) {
			return ResponseUtil.fail(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/meetingissue/{meetingId}", method = RequestMethod.GET)
	public @ResponseBody Object meetingissue(@PathVariable("meetingId") Long meetingId) {
		try {
			List<Map<String,Object>> meetingIssues = meetingManager.getMeetingissueByMeetingId(meetingId);
			return ResponseUtil.success(JSON.toJSONString(meetingIssues));
		} catch (Exception e) {
			return ResponseUtil.fail(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/finished")
	public @ResponseBody Object finished(@RequestParam("callback") String callback) {
		Map<String,Object> result=new HashMap<String,Object>();
		try {
			List<Long> meetingIds = meetingManager.getFinishedMeeting();
			result.put("result", true);
			result.put("message", meetingIds);
			return ResponseUtil.getJsonP(callback, result);
		} catch (Exception e) {
			result.put("result", false);
			LOG.error("", e);
		}
		return ResponseUtil.getJsonP(callback, result);
	}
	
	
}
