package top.fanzhengke.emos.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.fanzhengke.emos.common.util.Result;
import top.fanzhengke.emos.config.shiro.JwtUtil;
import top.fanzhengke.emos.controller.form.*;
import top.fanzhengke.emos.db.pojo.TbMeeting;
import top.fanzhengke.emos.exception.EmosException;
import top.fanzhengke.emos.service.TbMeetingService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@Api("会议模块网络接口")
@RequestMapping("/meeting")
public class MeetingController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TbMeetingService meetingService;

    @ApiOperation("查询会议列表分页数据")
    @PostMapping("/searchMyMeetingListByPage")
    public Result searchMyMeetingListByPage(@Valid @RequestBody SearchMyMeetingListByPageForm form, @RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);
        Integer page = form.getPage();
        Integer length = form.getLength();
        long start = (page - 1) * length;
        HashMap map = new HashMap();
        map.put("userId", userId);
        map.put("start", start);
        map.put("length", length);
        ArrayList list = meetingService.searchMyMeetingListByPage(map);
        return Result.ok().put("result", list);
    }

    @ApiOperation("添加会议")
    @PostMapping("/insertMeeting")
    @RequiresPermissions(value = {"ROOT", "MEETING:INSERT"}, logical = Logical.OR)
    public Result insertMeeting(@Valid @RequestBody InsertMeetingForm form, @RequestHeader("token") String token) {
        if (form.getType() == 2 && (form.getPlace() == null || form.getPlace().length() == 0)) {
            throw new EmosException("错误: 线下会议地点不能为空");
        }

        long timeStart = form.getStart();
        long timeEnd = form.getEnd();
        if (timeEnd < timeStart) {
            throw new EmosException("错误: 结束时间必须大于开始时间");
        }
        if (!JSONUtil.isJsonArray(form.getMembers())) {
            throw new EmosException("错误: members不是JSON数组");
        }
        TbMeeting entity = new TbMeeting();
        entity.setUuid(UUID.randomUUID().toString(true));
        entity.setTitle(form.getTitle());
        entity.setCreatorId((long) jwtUtil.getUserId(token));
        entity.setDate(form.getDate());
        entity.setPlace(form.getPlace());
        entity.setStart(form.getStart());
        entity.setEnd(form.getEnd());
        entity.setType((short) form.getType());
        entity.setMembers(form.getMembers());
        entity.setDesc(form.getDesc());
        entity.setStatus(3);
        meetingService.insertMeeting(entity);
        return Result.ok().put("result", "success");
    }

    @ApiOperation("根据ID查询会议")
    @PostMapping("/searchMeetingById")
    @RequiresPermissions(value = {"ROOT", "MEETING:SELECT"}, logical = Logical.OR)
    public Result searchMeetingById(@Valid @RequestBody SearchMeetingByIdFrom form) {
        HashMap map = meetingService.searchMeetingById(form.getId());
        return Result.ok().put("result", map);
    }

    @ApiOperation("更新会议")
    @PostMapping("/updateMeetingInfo")
    @RequiresPermissions(value = {"ROOT", "MEETING:UPDATE"}, logical = Logical.OR)
    public Result updateMeetingInfo(@Valid @RequestBody UpdateMeetingInfoForm form) {
        if (form.getType() == 2 && (form.getPlace() == null || form.getPlace().length() == 0)) {
            throw new EmosException("错误: 线下会议地点不能为空");
        }
        long timeStart = form.getStart();
        long timeEnd = form.getEnd();
        if (timeEnd < timeStart) {
            throw new EmosException("错误: 结束时间必须大于开始时间");
        }
        if (!JSONUtil.isJsonArray(form.getMembers())) {
            throw new EmosException("错误: members不是JSON数组");
        }
        HashMap param = new HashMap();
        param.put("title", form.getTitle());
        param.put("date", form.getDate());
        param.put("place", form.getPlace());
        param.put("start", form.getStart());
        param.put("end", form.getEnd());
        param.put("type", form.getType());
        param.put("members", form.getMembers());
        param.put("desc", form.getDesc());
        param.put("id", form.getId());
        param.put("instanceId", form.getInstanceId());
        param.put("status", 1);
        meetingService.updateMeetingInfo(param);
        return Result.ok().put("result", "success");
    }

    @ApiOperation("根据ID删除会议")
    @PostMapping("/deleteMeetingById")
    @RequiresPermissions(value = {"ROOT", "MEETING:DELETE"}, logical = Logical.OR)
    public Result deleteMeetingById(@Valid @RequestBody DeleteMeetingByIdForm form) {
        meetingService.deleteMeetingById(form.getId());
        return Result.ok().put("result", "success");
    }

    @ApiOperation("查询未审批列表分页数据")
    @PostMapping("/searchExamineMeetingList")
    public Result searchExamineMeetingList(@Valid @RequestBody SearchExamineMeetingForm form, @RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);
        Integer page = form.getPage();
        Integer length = form.getLength();
        Integer status = form.getStatus();
        long start = (page - 1) * length;
        HashMap map = new HashMap();
        map.put("userId", userId);
        map.put("start", start);
        map.put("length", length);
        map.put("status", status);

        ArrayList list = meetingService.searchExamineMeetingList(map);
        return Result.ok().put("result", list);
    }

    @ApiOperation("更新会议")
    @PostMapping("/updateExamineMeeting")
    @RequiresPermissions(value = {"ROOT", "MEETING:UPDATE"}, logical = Logical.OR)
    public Result updateExamineMeeting(@Valid @RequestBody UpdateExamineMeetingForm form) {
        HashMap param = new HashMap();
        param.put("id", form.getId());
        param.put("status", form.getStatus());
        meetingService.updateExamineMeeting(param);
        return Result.ok().put("result", "success");
    }

    @ApiOperation("查询某月用户的会议日期列表")
    @PostMapping("/searchUserMeetingInMonth")
    public Result searchUserMeetingInMonth(@Valid @RequestBody SearchUserMeetingInMonthForm form, @RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);
        HashMap param = new HashMap();
        param.put("userId", userId);
        param.put("year", form.getYear());
        param.put("month", form.getMonth());
        List<String> list = meetingService.searchUserMeetingInMonth(param);
        return Result.ok().put("result", list);
    }
}
