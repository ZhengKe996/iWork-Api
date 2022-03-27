package top.fanzhengke.emos.service.impl;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import top.fanzhengke.emos.db.mapper.TbMeetingMapper;
import top.fanzhengke.emos.db.mapper.TbUserMapper;
import top.fanzhengke.emos.db.pojo.TbMeeting;
import top.fanzhengke.emos.exception.EmosException;
import top.fanzhengke.emos.service.TbMeetingService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author coco
 * @description 针对表【tb_meeting(会议表)】的数据库操作Service实现
 * @createDate 2022-03-12 21:19:07
 */
@Slf4j
@Service
public class TbMeetingServiceImpl implements TbMeetingService {

    @Autowired
    private TbMeetingMapper meetingMapper;

    @Override
    public void insertMeeting(TbMeeting entity) {
        // 保存数据
        Integer row = meetingMapper.insertMeeting(entity);
        if (row != 1) {
            throw new EmosException("会议添加失败");
        }

        //开启审批工作流
    }

    /**
     * 分页查询会议列表
     *
     * @param param
     * @return
     */
    @Override
    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param) {
        ArrayList<HashMap> list = meetingMapper.searchMyMeetingListByPage(param);
        String date = null;
        ArrayList resultList = new ArrayList();
        HashMap resultMap = null;
        JSONArray array = null;
        for (HashMap map : list) {
            String temp = map.get("date").toString();
            if (!temp.equals(date)) {
                date = temp;
                resultMap = new HashMap();
                resultMap.put("date", date);
                array = new JSONArray();
                resultMap.put("list", array);
                resultList.add(resultMap);
            }
            array.put(map);
        }
        return resultList;
    }

    /**
     * 查询会议详细信息
     *
     * @param id
     * @return
     */
    @Override
    public HashMap searchMeetingById(Integer id) {
        HashMap map = meetingMapper.searchMeetingById(id);
        ArrayList<HashMap> list = meetingMapper.searchMeetingMembers(id);
        map.put("members", list);
        return map;
    }

    /**
     * 编辑会议详细信息
     *
     * @param param
     */
    @Override
    public void updateMeetingInfo(HashMap param) {
        Integer row = meetingMapper.updateMeetingInfo(param);
        if (row != 1) {
            throw new EmosException("会议更新失败");
        }
    }

    /**
     * 删除会议
     *
     * @param id
     */
    @Override
    public void deleteMeetingById(Integer id) {
        HashMap meeting = meetingMapper.searchMeetingById(id);

        long start = (long) meeting.get("START");
        long now = System.currentTimeMillis();

        if (start - now <= 1200000) {
            throw new EmosException("距离会议开始不足20分钟，不能删除会议");
        }
        int row = meetingMapper.deleteMeetingById(id);
        if (row != 1) {
            throw new EmosException("会议删除失败");
        }
    }

    @Override
    public Long searchRoomIdByUUID(String uuid) {
        return null;
    }

    /**
     * 查询当月的会议信息
     * @param param
     * @return
     */
    @Override
    public List<String> searchUserMeetingInMonth(HashMap param) {
        return meetingMapper.searchUserMeetingInMonth(param);
    }

    /**
     * 查询未审批的会议列表
     *
     * @param param
     * @return
     */
    @Override
    public ArrayList<HashMap> searchExamineMeetingList(HashMap param) {
        return meetingMapper.searchExamineMeetingList(param);
    }

    /**
     * 更新审批状态
     *
     * @param param
     */
    @Override
    public void updateExamineMeeting(HashMap param) {
        Integer row = meetingMapper.updateExamineMeeting(param);
        if (row != 1) {
            throw new EmosException("更新审批状态失败");
        }
    }
}
