package com.mq.screen.manager;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.mq.screen.dao.MeetingDao;

@Service
public class MeetingManagerImpl implements MeetingManager {
	@Autowired
	private MeetingDao meetingDao;
	
	@Value("${meeting_issue.report_unit}")
	private String report_unit;
	@Value("${meeting_issue.report_issue}")
	private String report_issue;
	@Value("${meeting_issue.report_person}")
	private String report_person;
	@Value("${meeting_issue.report_post}")
	private String report_post;
	@Value("${meeting_issue.report_tel_num}")
	private String report_tel_num;
	@Value("${meeting_issue.contact_member}")
	private String contact_member;
	@Value("${meeting_issue.contact_phone}")
	private String contact_phone;
	@Value("${meeting.meeting_room}")
	private String meeting_room;
	@Value("${meeting.meeting_time}")
	private String meeting_time;
	@Value("${meeting.meeting_num}")
	private String meeting_num;
	@Value("${meeting.meeting_type}")
	private String meeting_type;
	@Value("${meeting.meeting_title}")
	private String meeting_title;
	
	@Override
	public Map<String, Object> getMeetingFromOA(String meetingTableName,Long meetingId) throws Exception {
		return meetingDao.getMeetingFromOA(meetingTableName,meetingId);
	}

	@Override
	public void saveMeetingissue(List<Map<String, Object>> meetingissuesFromOA, Map<String, Object> meetingFromOA)
			throws Exception {
		for(Map<String, Object> map : meetingissuesFromOA) {
			Long issueId = 0L;
			Object id = map.get("ID");
			if(id instanceof BigDecimal) {
				BigDecimal meetingIdBD = (BigDecimal) map.get("ID");
				issueId = meetingIdBD.longValue();
			} else {
				issueId = (Long)id;
			}
			Map<String,Object> issues = meetingDao.getMeetingissueById(issueId);
			if(issues == null) {
				List<Object> paramList = new ArrayList<Object>();
				paramList.add(issueId);//id
				paramList.add(map.get("formmain_id"));//meeting_id
				paramList.add(map.get("sort"));//sort_index
				paramList.add(map.get(report_unit));//report_unit
				paramList.add(map.get(report_issue));//report_issue
				paramList.add(map.get(report_person));//report_person
				paramList.add(map.get(report_post));//report_post
				paramList.add(map.get(report_tel_num));//report_tel_num
				paramList.add(map.get(contact_member));//contact_member
				paramList.add(map.get(contact_phone));//contact_phone
				paramList.add(meetingFromOA.get(meeting_room));//meeting_room
				Object meetingTime = meetingFromOA.get(meeting_time);//meeting_time
				if(meetingTime != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					paramList.add(sdf.format(meetingTime));//会议时间
				}else {
					paramList.add("");
				}
				meetingDao.saveMeetingissue(paramList);
			}
		}
	}

	@Override
	public void saveMeeting(Map<String, Object> meetingFromOA) throws Exception {
		Long meetingId = 0L;
		Object id = meetingFromOA.get("ID");
		if(id instanceof BigDecimal) {
			BigDecimal meetingIdBD = (BigDecimal) id;
			meetingId = meetingIdBD.longValue();
		} else {
			meetingId = (Long)id;
		}
		Map<String,Object> meeting = meetingDao.getMeetingById(meetingId);
		if(meeting == null) {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(meetingId);//id
			paramList.add(meetingFromOA.get(meeting_title));//meeting_title
			String enumId = (String) meetingFromOA.get(meeting_type);//meeting_type
			Map<String, Object> enumMap = meetingDao.getMeetingType(Long.valueOf(enumId));
			String meetingType = "";
			if(enumMap != null) {
				meetingType = (String) enumMap.get("showvalue");
			}
			paramList.add(meetingType);
			String meetingTimeStr = "";
			Object meetingTime = meetingFromOA.get(meeting_time);//meeting_time
			if(meetingTime != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				meetingTimeStr = sdf.format(meetingTime);
			}
			paramList.add(meetingTimeStr);
			paramList.add(meetingFromOA.get(meeting_room));//meeting_room
			paramList.add(meetingFromOA.get(meeting_num));//meeting_num
			meetingDao.saveMeeting(paramList);
		}
	}

	@Override
	public List<Map<String, Object>> getMeetingissueFromOA(String meetingissueTableName,Long meetingId) throws Exception {
		return meetingDao.getMeetingissueFromOA(meetingissueTableName,meetingId);
	}

	@Override
	public Map<String,Object> getMeetingById(Long meetingId) throws Exception {
		return meetingDao.getMeetingById(meetingId);
	}

	@Override
	public List<Map<String, Object>> getMeetingissueByMeetingId(Long meetingId) throws Exception {
		return meetingDao.getMeetingissueByMeetingId(meetingId);
	}

	@Override
	public void updateMeetingIssueOrder(Long issueId, Integer index) throws Exception {
		meetingDao.updateMeetingIssueOrder(issueId, index);
	}

	@Override
	public void updateMeetingIssueState(Long issueId, int state) throws Exception {
		meetingDao.updateMeetingIssueState(issueId, state);
	}

	@Override
	public void readScreen(Long issueId) throws Exception {
		Map<String,Map<String,Object>> readMap = new HashMap<String,Map<String,Object>>();
		Map<String,Object> issues = meetingDao.getMeetingissueById(issueId);
		//当前议题、下个议题
		if(issues != null) {
			Long meetingId = (Long) issues.get("meeting_id");
			List<Map<String, Object>> issueList = meetingDao.getMeetingissueByMeetingId(meetingId);
			if(issueList != null && !issueList.isEmpty()) {
				readMap.put("current", issueList.get(0));
				if(issueList.size() > 1) {
					readMap.put("next", issueList.get(1));
				}
			}
			//上个议题
			List<Map<String,Object>> oldIssues = meetingDao.getIssueByMeetingIdAndState(meetingId,0);
			if(oldIssues != null && !oldIssues.isEmpty()) {
				readMap.put("pre", oldIssues.get(0));
			}
		}
		
		//读屏幕
		System.out.println("readScreen=" + JSON.toJSONString(readMap));
	}

	@Override
	public Map<String, Object> getMeetingissueById(Long issueId) throws Exception {
		return meetingDao.getMeetingissueById(issueId);
	}

	@Override
	public List<Map<String, Object>> getIssueByMeetingIdAndState(Long meetingId, int issueState) throws Exception {
		return meetingDao.getIssueByMeetingIdAndState(meetingId, issueState);
	}

	@Override
	public List getFinishedMeeting() throws Exception {
		List meetingIds = new ArrayList<Long>();
		List<Map<String, Object>> meetingList = meetingDao.getFinishedMeeting();
		for(Map<String,Object> map : meetingList) {
			meetingIds.add(map.get("id"));
		}
		return meetingIds;
	}
}
