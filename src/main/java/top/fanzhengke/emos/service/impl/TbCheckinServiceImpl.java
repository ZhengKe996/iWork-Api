package top.fanzhengke.emos.service.impl;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import top.fanzhengke.emos.config.SystemConstants;
import top.fanzhengke.emos.db.mapper.*;
import top.fanzhengke.emos.db.pojo.TbCheckin;
import top.fanzhengke.emos.exception.EmosException;
import top.fanzhengke.emos.service.TbCheckinService;
import org.springframework.stereotype.Service;
import top.fanzhengke.emos.service.impl.task.EmailTask;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author coco
 * @description 针对表【tb_checkin(签到表)】的数据库操作Service实现
 * @createDate 2022-03-12 21:19:07
 */
@Slf4j
@Service
@Scope("prototype")
public class TbCheckinServiceImpl implements TbCheckinService {
    @Autowired
    private SystemConstants systemConstants;

    @Autowired
    private TbHolidaysMapper holidaysMapper;

    @Autowired
    private TbWorkdayMapper workdayMapper;

    @Autowired
    private TbCheckinMapper checkinMapper;

    @Autowired
    private TbCityMapper cityMapper;

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private EmailTask emailTask;


    @Value("${emos.email.hr}")
    private String hrEmail;

    /**
     * 判断用户当日的签到状态
     *
     * @param userId
     * @param date
     * @return
     */
    @Override
    public String validCanCheckIn(int userId, String date) {
        boolean bool_1 = holidaysMapper.searchTodayIsHolidays() != null ? true : false;
        boolean bool_2 = workdayMapper.searchTodayIsWorkday() != null ? true : false;
        String type = "工作日";
        if (DateUtil.date().isWeekend()) {
            type = "节假日";
        }
        if (bool_1) {
            type = "节假日";
        } else if (bool_2) {
            type = "工作日";
        }

        if (type.equals("节假日")) {
            return "节假日不需要考勤";
        } else {
            DateTime now = DateUtil.date();
            String start = DateUtil.today() + " " + systemConstants.attendanceStartTime;
            String end = DateUtil.today() + " " + systemConstants.attendanceEndTime;
            DateTime attendanceStart = DateUtil.parse(start);
            DateTime attendanceEnd = DateUtil.parse(end);
            if (now.isBefore(attendanceStart)) {
                return "没到上班考勤开始时间";
            } else if (now.isAfter(attendanceEnd)) {
                return "超过了上班考勤结束时间";
            } else {
                HashMap map = new HashMap();
                map.put("userId", userId);
                map.put("date", date);
                map.put("start", start);
                map.put("end", end);
                boolean bool = checkinMapper.haveCheckin(map) != null ? true : false;
                return bool ? "今日已经考勤，不用重复考勤" : "可以考勤";
            }
        }
    }

    /**
     * 用户签到方法
     *
     * @param param
     */
    @Override
    public void checkin(HashMap param) {
        // 获取当前系统时间
        Date dNow = DateUtil.date();

        // 获取上班签到时间
        Date dStart = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceTime);

        // 获取上班签到结束时间
        Date dEnd = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceEndTime);

        // 判断用户签到状态
        Integer status = 1;
        if (dNow.compareTo(dStart) <= 0) {
            status = 1;
        } else if (dNow.compareTo(dStart) > 0 && dNow.compareTo(dEnd) < 0) {
            status = 2;
        } else {
            throw new EmosException("超出考勤时间段，无法考勤");
        }

        Integer userId = (Integer) param.get("userId");

        // 查询疫情风险等级
        Integer risk = 1;
        String city = (String) param.get("city");
        String district = (String) param.get("district");
        String address = (String) param.get("address");
        String country = (String) param.get("country");
        String province = (String) param.get("province");
        if (!StrUtil.isBlank(city) && !StrUtil.isBlank(district)) {
            String code = cityMapper.searchCode(city);
            try {
                String url = "http://m." + code + ".bendibao.com/news/yqdengji/?qu=" + district;
                Document document = Jsoup.connect(url).get();
                Elements elements = document.getElementsByClass("list-content");
                if (elements.size() > 0) {
                    Element element = elements.get(0);
                    String result = element.select("p:last-child").text();
                    if ("高风险".equals(result) || "中风险".equals(result)) {
                        risk = 2;
                        // 发送告警邮件
                        HashMap<String, String> map = userMapper.searchNameAndDept(userId);
                        String name = map.get("name");
                        String deptName = map.get("dept_name");
                        deptName = deptName != null ? deptName : "";
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setTo(hrEmail);
                        message.setSubject("员工: " + name + "身处高风险疫情地区警告");
                        message.setText(deptName + "员工: " + name + "，" + DateUtil.format(new Date(), "yyyy年MM月dd日") + "处于" + address + ",属于新冠疫情" + result + "风险地区，请及时与该员工联系，核实情况！");
                        emailTask.sendAsync(message);
                    }
                }
            } catch (Exception e) {
                log.error("执行异常: 获取风险等级失败", e);
                throw new EmosException("获取风险等级失败");
            }
        }

        // 保存签到记录
        TbCheckin entity = new TbCheckin();
        entity.setUserId(userId);
        entity.setAddress(address);
        entity.setCountry(country);
        entity.setProvince(province);
        entity.setCity(city);
        entity.setDistrict(district);
        entity.setStatus(status);
        entity.setRisk(risk);
        entity.setDate(DateUtil.today());
        entity.setCreateTime(dNow);
        try {
            checkinMapper.insert(entity);
        } catch (Exception e) {
            throw new EmosException("已签到,请勿重复签到");
        }
    }

    /**
     * 查询用户当日的签到状态
     *
     * @param userId
     * @return
     */
    @Override
    public HashMap searchTodayCheckin(Integer userId) {
        return checkinMapper.searchTodayCheckin(userId);
    }

    /**
     * 查询用户周考勤
     *
     * @param userId
     * @return
     */
    @Override
    public long searchCheckinDays(Integer userId) {
        return checkinMapper.searchCheckinDays(userId);
    }

    @Override
    public ArrayList<HashMap> searchWeekCheckin(HashMap param) {
        // 用户的签到状态
        ArrayList<HashMap> checkinList = checkinMapper.searchWeekCheckin(param);
        // 节假日列表
        ArrayList holidaysList = holidaysMapper.searchHolidaysInRange(param);
        // 工作日列表
        ArrayList workdayList = workdayMapper.searchWorkdayInRange(param);
        // 开始日期
        DateTime startDate = DateUtil.parseDate(param.get("startDate").toString());
        // 结束日期
        DateTime endDate = DateUtil.parseDate(param.get("endDate").toString());
        // 本周的日期对象
        DateRange range = DateUtil.range(startDate, endDate, DateField.DAY_OF_MONTH);
        ArrayList<HashMap> list = new ArrayList<>();

        range.forEach(one -> {
            String date = one.toString("yyyy-MM-dd");
            String type = "工作日";
            if (one.isWeekend()) {
                type = "节假日";
            }
            if (holidaysList != null && holidaysList.contains(date)) {
                type = "节假日";
            } else if (workdayList != null && workdayList.contains(date)) {
                type = "工作日";
            }
            String status = "";
            if (type.equals("工作日") && DateUtil.compare(one, DateUtil.date()) <= 0) {
                status = "缺勤";
                boolean flag = false;
                for (HashMap<String, String> map : checkinList) {
                    if (map.containsValue(date)) {
                        status = map.get("status");
                        flag = true;
                        break;
                    }
                }
                DateTime endTime = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceEndTime);
                String today = DateUtil.today();
                if (date.equals(today) && DateUtil.date().isBefore(endTime) && flag == false) {
                    status = "";
                }
            }
            HashMap map = new HashMap();
            map.put("date", date);
            map.put("status", status);
            map.put("type", type);
            map.put("day", one.dayOfWeekEnum().toChinese("周"));
            list.add(map);
        });
        return list;
    }

    /**
     * 查看用户月考勤
     *
     * @param param
     * @return
     */
    @Override
    public ArrayList<HashMap> searchMonthCheckin(HashMap param) {
        return this.searchWeekCheckin(param);
    }
}
