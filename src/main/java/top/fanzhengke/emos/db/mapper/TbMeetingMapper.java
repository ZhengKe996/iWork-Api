package top.fanzhengke.emos.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.fanzhengke.emos.db.pojo.TbMeeting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author coco
 * @description 针对表【tb_meeting(会议表)】的数据库操作Mapper
 * @createDate 2022-03-12 21:19:07
 * @Entity top.fanzhengke.emoswxapi.db.pojo.TbMeeting
 */
@Mapper
public interface TbMeetingMapper {

    public Integer insertMeeting(TbMeeting entity);

    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param);

    public boolean searchMeetingMembersInSameDept(String uuid);

    public Integer updateMeetingInstanceId(HashMap map);

    public HashMap searchMeetingById(Integer id);

    public ArrayList<HashMap> searchMeetingMembers(Integer id);

    public Integer updateMeetingInfo(HashMap param);

    public Integer deleteMeetingById(Integer id);

    public List<String> searchUserMeetingInMonth(HashMap param);

    public ArrayList<HashMap> searchExamineMeetingList(HashMap param);

    public Integer updateExamineMeeting(HashMap param);
}
