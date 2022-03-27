package top.fanzhengke.emos.service;

import top.fanzhengke.emos.db.pojo.TbMeeting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
* @author coco
* @description 针对表【tb_meeting(会议表)】的数据库操作Service
* @createDate 2022-03-12 21:19:07
*/
public interface TbMeetingService  {
    public void insertMeeting(TbMeeting entity);

    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param);

    public HashMap searchMeetingById(Integer id);

    public void updateMeetingInfo(HashMap param);

    public void deleteMeetingById(Integer id);

    public Long searchRoomIdByUUID(String uuid);

    public List<String> searchUserMeetingInMonth(HashMap param);

    public ArrayList<HashMap> searchExamineMeetingList(HashMap param);

    public void updateExamineMeeting(HashMap param);

}
