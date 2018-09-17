package com.mq.screen.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mq.screen.config.ServiceInfoUtil;
import com.mq.screen.manager.MeetingManager;
import com.mq.screen.util.QRCodeUtil;

@Controller
@RequestMapping("/meeting")
public class MeetingController {
	private static final Logger LOG = LoggerFactory.getLogger(MeetingController.class);
	@Autowired
	private MeetingManager meetingManager;
	
	@Value("${table.meeting}")
	private String meetingTableName;
	
	@Value("${table.meeting_issue}")
	private String meetingissueTableName;
	
	@Value("${ip_address}")
	private String ip_address;
	
	@RequestMapping("/beginMeeting/{id}")
    public String beginMeeting(@PathVariable("id") Long meetingId,Model model) {
		String url = ip_address + "/meeting/openMeeting/" + meetingId;
		String base64Img = QRCodeUtil.getQRCode(url);
		model.addAttribute("img_src", "data:image/png;base64,"+base64Img);
        return "meeting/qrCode";
    }
	
	@RequestMapping("/openMeeting/{id}")
    public String openMeeting(@PathVariable("id") Long meetingId,Model model) {
		//保存会议和会议议题
		try {
			//保存会议
			Map<String, Object> meeting = meetingManager.getMeetingFromOA(meetingTableName,meetingId);
			if(meeting != null) {
				meetingManager.saveMeeting(meeting);
				//保存会议议题
				List<Map<String, Object>> meetingIssues = meetingManager.getMeetingissueFromOA(meetingissueTableName,meetingId);
				meetingManager.saveMeetingissue(meetingIssues,meeting);
				//从本地查询会议内容
				Map<String,Object> meetingLoacl = meetingManager.getMeetingById(meetingId);
				model.addAttribute("meeting", meetingLoacl);
				//从本地查询会议议题列表
				model.addAttribute("meetingissues", meetingManager.getMeetingissueByMeetingId(meetingId));
				model.addAttribute("meetingId", meetingId);
			}
		} catch (Exception e) {
			LOG.error("保存会议信息异常", e);
		}
        return "meeting/mobile";
    }
	
	
}
