package top.fanzhengke.emos.db.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author coco
* @description 针对表【tb_holidays(节假日表)】的数据库操作Mapper
* @createDate 2022-03-12 21:19:07
* @Entity top.fanzhengke.emoswxapi.db.pojo.TbHolidays
*/
@Mapper
public interface TbHolidaysMapper  {
    public Integer searchTodayIsHolidays();
    public ArrayList<String> searchHolidaysInRange(HashMap param);

}
