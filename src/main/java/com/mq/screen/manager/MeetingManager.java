package com.mq.screen.manager;

import java.util.List;
import java.util.Map;

public interface MeetingManager {
	/**
	 * 从OA查询会议
	 * @param meetingTableName 
	 * @param meetingId
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> getMeetingFromOA(String meetingTableName, Long meetingId) throws Exception;

	/**
	 * 保存到会议议题
	 * @param meetingissuesFromOA 从OA中取的会议议题
	 * @param meetingFromOA 从OA中取的会议
	 */
	void saveMeetingissue(List<Map<String, Object>> meetingissuesFromOA, Map<String, Object> meetingFromOA) throws Exception;
	
	/**
	 * 保存到会议
	 * @param meetinOA
	 */
	void saveMeeting(Map<String, Object> meetinOA) throws Exception;
	
	/**
	 *  从OA查询会议议题
	 * @param meetingissueTableName 
	 * @param meetingId
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> getMeetingissueFromOA(String meetingissueTableName, Long meetingId) throws Exception;
	
	/**
	 *  查询会议记录
	 * @param meetingId
	 * @return
	 * @throws Exception
	 */
	Map<String,Object> getMeetingById(Long meetingId) throws Exception;

	/**
	 *  查询议题列表
	 * @param meetingId 会议ID
	 * @return
	 */
	List<Map<String, Object>> getMeetingissueByMeetingId(Long meetingId) throws Exception;

	/**
	 *  更新议题排序号
	 * @param issueId
	 * @param index
	 * @throws Exception
	 */
	void updateMeetingIssueOrder(Long issueId, Integer index) throws Exception;

	/**
	 *  更新议题状态
	 * @param issueId 议题ID
	 * @param state 议题状态
	 * @throws Exception
	 */
	void updateMeetingIssueState(Long issueId, int state) throws Exception;

	/**
	 * 读屏幕
	 * @param issueId
	 * @throws Exception
	 */
	void readScreen(Long issueId) throws Exception;
	
	/**
	 * 查询会议议题
	 * @param issueId
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> getMeetingissueById(Long issueId) throws Exception;
	
	/**
	 *  根据会议ID和议题状态查询议题列表
	 * @param meetingId 会议ID
	 * @param issueState 状态
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> getIssueByMeetingIdAndState(Long meetingId, int issueState) throws Exception;

	/**
	 * 查询已开会议
	 * @return
	 * @throws Exception
	 */
	List getFinishedMeeting() throws Exception;
}
