package top.fanzhengke.emos.db.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author coco
 * @description 针对表【tb_workday】的数据库操作Mapper
 * @createDate 2022-03-12 21:19:07
 * @Entity top.fanzhengke.emoswxapi.db.pojo.TbWorkday
 */
@Mapper
public interface TbWorkdayMapper {
    public Integer searchTodayIsWorkday();
    public ArrayList<String> searchWorkdayInRange(HashMap param);
}
