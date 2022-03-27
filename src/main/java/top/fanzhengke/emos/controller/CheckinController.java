package top.fanzhengke.emos.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import top.fanzhengke.emos.common.util.Result;
import top.fanzhengke.emos.config.SystemConstants;
import top.fanzhengke.emos.config.shiro.JwtUtil;
import top.fanzhengke.emos.controller.form.CheckinForm;
import top.fanzhengke.emos.controller.form.SearchMonthCheckinForm;
import top.fanzhengke.emos.exception.EmosException;
import top.fanzhengke.emos.service.TbCheckinService;
import top.fanzhengke.emos.service.TbUserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

@Api("签到模块Web接口")
@Slf4j
@RestController
@RequestMapping("/checkin")
public class CheckinController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TbCheckinService checkinService;

    @Autowired
    private TbUserService userService;

    @Autowired
    private SystemConstants systemConstants;


    @ApiOperation("查看用户今天是否可以签到")
    @GetMapping("/validCanCheckIn")
    public Result validCanCheckIn(@RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);
        String result = checkinService.validCanCheckIn(userId, DateUtil.today());
        return Result.ok(result);
    }

    @ApiOperation("签到")
    @PostMapping("/checkin")
    public Result checkin(@Valid @RequestBody CheckinForm form, @RequestHeader("token") String token) {

        Integer userId = jwtUtil.getUserId(token);
        HashMap param = new HashMap();
        param.put("userId", userId);
        param.put("city", form.getCity());
        param.put("district", form.getDistrict());
        param.put("address", form.getAddress());
        param.put("country", form.getCountry());
        param.put("province", form.getProvince());

        checkinService.checkin(param);
        return Result.ok("签到成功");
    }

    @ApiOperation("查询用户当日签到数据")
    @GetMapping("/searchTodayCheckin")
    public Result searchTodayCheckin(@RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);
        HashMap map = checkinService.searchTodayCheckin(userId);
        // 考勤开始时间
        map.put("attendanceTime", systemConstants.attendanceTime);

        // 考勤结束时间
        map.put("closingTime", systemConstants.closingTime);

        // 查询用户签到总天数
        long days = checkinService.searchCheckinDays(userId);
        map.put("checkinDays", days);

        // 查询用户入职日期
        DateTime hiredate = DateUtil.parse(userService.searchUserHiredate(userId));

        // 查询本周考勤开始日期
        DateTime startDate = DateUtil.beginOfWeek(DateUtil.date());

        // 判断考勤日期是否在用户入职时间前
        if (startDate.isBefore(hiredate)) {
            startDate = hiredate;
        }

        // 本周考勤的结束日期
        DateTime endDate = DateUtil.endOfWeek(DateUtil.date());

        HashMap param = new HashMap();
        param.put("startDate", startDate.toString());
        param.put("endDate", endDate.toString());
        param.put("userId", userId);

        // 查询本周考勤记录
        ArrayList<HashMap> list = checkinService.searchWeekCheckin(param);
        map.put("weekCheckin", list);
        return Result.ok().put("result", map);
    }

    @ApiOperation("查询用户某月签到数据")
    @PostMapping("/searchMonthCheckin")
    public Result searchMonthCheckin(@Valid @RequestBody SearchMonthCheckinForm form, @RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);

        // 查询入职日期
        DateTime hiredate = DateUtil.parse(userService.searchUserHiredate(userId));

        // 把月份处理成2位
        String month = form.getMonth() < 10 ? "0" + form.getMonth() : form.getMonth().toString();

        // 某年某月的起始日期
        DateTime startDate = DateUtil.parse(form.getYear() + "-" + month + "-01");
        if (startDate.isBefore(DateUtil.beginOfMonth(hiredate))) {
            throw new EmosException("只能查询入职后的签到数据");
        }

        // 如果查询的是入职的月份 则把入职日期作为查询的起始日期
        if (startDate.isBefore(hiredate)) {
            startDate = hiredate;
        }

        // 获取当月结束日期
        DateTime endDate = DateUtil.endOfMonth(startDate);

        // 封装查询条件
        HashMap param = new HashMap();
        param.put("userId", userId);
        param.put("startDate", startDate.toString());
        param.put("endDate", endDate.toString());
        ArrayList<HashMap> list = checkinService.searchMonthCheckin(param);
        int sumOK = 0, sumLate = 0, sumAbsenteeism = 0;
        for (HashMap<String, String> one : list) {
            String type = one.get("type");
            String status = one.get("status");
            if ("工作日".equals(type)) {
                if ("正常".equals(status)) {
                    sumOK++;
                } else if ("迟到".equals(status)) {
                    sumLate++;
                } else if ("缺勤".equals(status)) {
                    sumAbsenteeism++;
                }
            }
        }
        return Result.ok().put("list", list).put("sumOK", sumOK).put("sumLate", sumLate).put("sumAbsenteeism", sumAbsenteeism);
    }
}
