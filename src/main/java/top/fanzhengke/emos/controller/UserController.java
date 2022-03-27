package top.fanzhengke.emos.controller;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.fanzhengke.emos.common.util.Result;
import top.fanzhengke.emos.config.shiro.JwtUtil;
import top.fanzhengke.emos.controller.form.LoginForm;
import top.fanzhengke.emos.controller.form.RegisterForm;
import top.fanzhengke.emos.controller.form.SearchMembersForm;
import top.fanzhengke.emos.controller.form.SearchUserGroupByDeptForm;
import top.fanzhengke.emos.exception.EmosException;
import top.fanzhengke.emos.service.TbUserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Api(value = "用户模块Web接口")
@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private TbUserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${emos.jwt.cache-expire}")
    private Integer cacheExpire;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result register(@Valid @RequestBody RegisterForm form) {
        Integer id = userService.registerUser(form.getRegisterCode(), form.getCode(), form.getNickname(), form.getPhoto());
        String token = jwtUtil.createToken(id);
        Set<String> permsSet = userService.searchUserPermissions(id);
        saveCacheToken(token, id);
        return Result.ok("用户注册成功").put("token", token).put("permission", permsSet);
    }

    @ApiOperation("登陆系统")
    @PostMapping("/login")
    public Result login(@Valid @RequestBody LoginForm form) {
        Integer id = userService.login(form.getCode());
        String token = jwtUtil.createToken(id);
        saveCacheToken(token, id);
        Set<String> permsSet = userService.searchUserPermissions(id);
        return Result.ok("登陆成功").put("token", token).put("permission", permsSet);
    }

    @ApiOperation("查询用户摘要信息")
    @GetMapping("/searchUserSummary")
    public Result searchUserSummary(@RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);
        HashMap map = userService.searchUserSummary(userId);
        return Result.ok().put("result", map);
    }

    @ApiOperation("查询员工列表，按照部门分组排列")
    @PostMapping("/searchUserGroupByDept")
    @RequiresPermissions(value = {"ROOT", "EMPLOYEE:SELECT"}, logical = Logical.OR)
    public Result searchUserGroupByDept(@Valid @RequestBody SearchUserGroupByDeptForm form) {
        ArrayList<HashMap> list = userService.searchUserGroupByDept(form.getKeyword());
        return Result.ok().put("result", list);
    }

    @ApiOperation("查询会议参会成员")
    @PostMapping("/searchMembers")
    @RequiresPermissions(value = {"ROOT", "MEETING:INSERT", "MEETING:UPDATE"}, logical = Logical.OR)
    public Result searchMembers(@Valid @RequestBody SearchMembersForm form) {
        if (!JSONUtil.isJsonArray(form.getMembers())) {
            throw new EmosException("错误: members不是JSON数组");
        }
        List param = JSONUtil.parseArray(form.getMembers()).toList(Integer.class);
        ArrayList list = userService.searchMembers(param);
        return Result.ok().put("result", list);
    }

    /**
     * 往Redis中缓存Token
     *
     * @param token
     * @param userId
     */
    private void saveCacheToken(String token, int userId) {
        redisTemplate.opsForValue().set(token, userId + "", cacheExpire, TimeUnit.DAYS);
    }
}