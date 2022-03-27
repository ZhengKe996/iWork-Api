package top.fanzhengke.emos.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.fanzhengke.emos.db.pojo.TbCheckin;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author coco
 * @description 针对表【tb_checkin(签到表)】的数据库操作Mapper
 * @createDate 2022-03-12 21:19:07
 * @Entity top.fanzhengke.emoswxapi.db.pojo.TbCheckin
 */
@Mapper
public interface TbCheckinMapper {
    /**
     * 查询当日考勤状态
     *
     * @param param
     * @return
     */
    public Integer haveCheckin(HashMap param);

    /**
     * 保存签到记录
     *
     * @param checkin
     */
    public void insert(TbCheckin checkin);

    /**
     * 查询当日签到信息
     *
     * @param userId
     * @return
     */
    public HashMap searchTodayCheckin(int userId);

    /**
     * 查询签到天数
     *
     * @param userId
     * @return
     */
    public long searchCheckinDays(int userId);

    /**
     * 查询近期签到状态
     *
     * @param param
     * @return
     */
    public ArrayList<HashMap> searchWeekCheckin(HashMap param);


}
