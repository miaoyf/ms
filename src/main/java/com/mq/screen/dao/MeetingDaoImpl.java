package com.mq.screen.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class MeetingDaoImpl implements MeetingDao {

	@Autowired
	@Qualifier("primaryJdbcTemplate")
	protected JdbcTemplate oaJdbcTemplate;

	@Autowired
	@Qualifier("secondaryJdbcTemplate")
	protected JdbcTemplate meetingJdbcTemplate;
	
	@Override
	public void saveMeetingissue(List<Object> paramList) throws Exception {
		if (paramList != null && !paramList.isEmpty()) {
			String sql = "insert into meeting_issues(id,meeting_id,sort_index,report_unit,report_issue,report_person,report_post,report_tel_num,contact_member,contact_phone,meeting_room,meeting_time,state)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			meetingJdbcTemplate.update(sql, paramList.get(0), paramList.get(1), paramList.get(2), paramList.get(3),
					paramList.get(4), paramList.get(5), paramList.get(6), paramList.get(7), paramList.get(8),
					paramList.get(9), paramList.get(10), paramList.get(11),0);
		}
	}

	@Override
	public Map<String, Object> getMeetingFromOA(String meetingTableName,Long meetingId) throws Exception {
		try {
			return oaJdbcTemplate.queryForMap("select * from " + meetingTableName + " where id=" + meetingId);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> getMeetingissueFromOA(String meetingissueTableName,Long meetingId) throws Exception {
		String sql = "select * from " + meetingissueTableName + " where formmain_id=" + meetingId;
		return oaJdbcTemplate.queryForList(sql);
	}

	@Override
	public void saveMeeting(List<Object> paramList) throws Exception {
		if (paramList != null && !paramList.isEmpty()) {
			String sql = "insert into meeting(id,meeting_title,meeting_type,meeting_time,meeting_room,meeting_num) values(?,?,?,?,?,?)";
			meetingJdbcTemplate.update(sql, paramList.get(0), paramList.get(1), paramList.get(2), paramList.get(3),
					paramList.get(4),paramList.get(5));
		}
	}

	@Override
	public Map<String, Object> getMeetingById(Long meetingId) throws Exception {
		try {
			return meetingJdbcTemplate.queryForMap("select * from meeting where id=" + meetingId);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Map<String, Object> getMeetingissueById(Long issueId) throws Exception {
		try {
			return meetingJdbcTemplate.queryForMap("select * from meeting_issues where id=" + issueId);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> getMeetingissueByMeetingId(Long meetingId) {
		return meetingJdbcTemplate.queryForList(
				"select * from meeting_issues where meeting_id=" + meetingId + " and state=0 order by sort_index");
	}

	@Override
	public void updateMeetingIssueOrder(Long issueId, Integer index) throws Exception {
		meetingJdbcTemplate.update("update meeting_issues set sort_index=" + index + " where id=" + issueId);
	}

	@Override
	public void updateMeetingIssueState(Long issueId, int state) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		meetingJdbcTemplate.update("update meeting_issues set state=" + state + ",update_time='"
				+ sdf.format(new Date()) + "' where id=" + issueId);
	}

	@Override
	public List<Map<String, Object>> getIssueByMeetingIdAndState(Long meetingId, int issueState) throws Exception {
		return meetingJdbcTemplate.queryForList("select * from meeting_issues where meeting_id=" + meetingId
				+ " and state=1 order by update_time desc");
	}

	@Override
	public Map<String, Object> getMeetingType(Long enumId) throws Exception {
		try {
			return oaJdbcTemplate.queryForMap("select showvalue from ctp_enum_item where id=" + enumId);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> getFinishedMeeting() throws Exception {
		return meetingJdbcTemplate.queryForList("select id from meeting where id not in (select mi.meeting_id from meeting_issues mi where mi.state = 0)");
	}

}
