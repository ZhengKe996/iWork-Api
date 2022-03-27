package top.fanzhengke.emos.service;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author coco
 * @description 针对表【tb_checkin(签到表)】的数据库操作Service
 * @createDate 2022-03-12 21:19:07
 */
public interface TbCheckinService {
    public String validCanCheckIn(int userId, String date);

    public void checkin(HashMap param);

    public HashMap searchTodayCheckin(Integer userId);

    public long searchCheckinDays(Integer userId);

    public ArrayList<HashMap> searchWeekCheckin(HashMap param);

    public ArrayList<HashMap> searchMonthCheckin(HashMap param);

}
