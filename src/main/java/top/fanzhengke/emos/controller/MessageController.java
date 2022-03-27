package top.fanzhengke.emos.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.fanzhengke.emos.common.util.Result;
import top.fanzhengke.emos.config.shiro.JwtUtil;
import top.fanzhengke.emos.controller.form.DeleteMessageRefByIdForm;
import top.fanzhengke.emos.controller.form.SearchMessageByIdForm;
import top.fanzhengke.emos.controller.form.SearchMessageByPageForm;
import top.fanzhengke.emos.controller.form.UpdateUnreadMessageForm;
import top.fanzhengke.emos.service.MessageService;
import top.fanzhengke.emos.service.impl.task.MessageTask;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@Api("消息模块网络接口")
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageTask messageTask;

    @ApiOperation("获取分页消息列表")
    @PostMapping("/searchMessageByPage")
    public Result searchMessageByPage(@Valid @RequestBody SearchMessageByPageForm form, @RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);
        Integer page = form.getPage();
        Integer length = form.getLength();
        long start = (page - 1) * length;
        List<HashMap> list = messageService.searchMessageByPage(userId, start, length);
        return Result.ok().put("result", list);
    }

    @ApiOperation("根据ID查询消息")
    @PostMapping("/searchMessageById")
    public Result searchMessageById(@Valid @RequestBody SearchMessageByIdForm form) {
        HashMap map = messageService.searchMessageById(form.getId());
        return Result.ok().put("result", map);
    }

    @ApiOperation("未读消息更新成已读消息")
    @PostMapping("/updateUnreadMessage")
    public Result updateUnreadMessage(@Valid @RequestBody UpdateUnreadMessageForm form) {
        long rows = messageService.updateUnreadMessage(form.getId());
        return Result.ok().put("result", rows == 1 ? true : false);
    }

    @ApiOperation("删除消息")
    @PostMapping("/deleteMessageRefById")
    public Result deleteMessageRefById(@Valid @RequestBody DeleteMessageRefByIdForm form) {
        long rows = messageService.deleteMessageRefById(form.getId());
        return Result.ok().put("result", rows == 1 ? true : false);
    }

    @ApiOperation("刷新用户消息")
    @GetMapping("/refreshMessage")
    public Result refreshMessage(@RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);
        // 异步接收消息
        messageTask.receiveAsync(userId + "");

        // 查询接收了多少条消息
        long lastRows = messageService.searchLastCount(userId);

        // 查询未读数据
        long unreadRows = messageService.searchUnreadCount(userId);

        return Result.ok().put("lastRows", lastRows).put("unreadRows", unreadRows);
    }
}
