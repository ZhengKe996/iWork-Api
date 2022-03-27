package top.fanzhengke.emos.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import top.fanzhengke.emos.db.mapper.TbDeptMapper;
import top.fanzhengke.emos.db.mapper.TbUserMapper;
import top.fanzhengke.emos.db.pojo.MessageEntity;
import top.fanzhengke.emos.db.pojo.TbUser;
import top.fanzhengke.emos.exception.EmosException;
import top.fanzhengke.emos.service.TbUserService;
import org.springframework.stereotype.Service;
import top.fanzhengke.emos.service.impl.task.MessageTask;

import java.util.*;

/**
 * @author coco
 * @description 针对表【tb_user(用户表)】的数据库操作Service实现
 * @createDate 2022-03-12 21:19:07
 */
@Slf4j
@Service
@Scope("prototype")
public class TbUserServiceImpl implements TbUserService {
    @Value("${wx.app-id}")
    private String appId;

    @Value("${wx.app-secret}")
    private String appSecret;

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private TbDeptMapper deptMapper;

    @Autowired
    private MessageTask messageTask;

    private String getOpenId(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        HashMap map = new HashMap();
        map.put("appid", appId);
        map.put("secret", appSecret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String response = HttpUtil.post(url, map);
        JSONObject json = JSONUtil.parseObj(response);
        String openId = json.getStr("openid");
        if (openId == null || openId.length() == 0) {
            throw new RuntimeException("临时登陆凭证错误");
        }
        return openId;
    }

    /**
     * 用户注册,判断邀请码是否为超级管理员 且当超级管理员用户不存在时新增超级管理员
     *
     * @param registerCode
     * @param code
     * @param nickname
     * @param photo
     * @return
     */
    @Override
    public Integer registerUser(String registerCode, String code, String nickname, String photo) {
        if (registerCode.equals("000000")) {
            boolean bool = userMapper.haveRootUser();
            if (!bool) {
                String openId = getOpenId(code);
                HashMap param = new HashMap();
                param.put("openId", openId);
                param.put("nickname", nickname);
                param.put("photo", photo);
                param.put("role", "[0]");
                param.put("status", 1);
                param.put("createTime", new Date());
                param.put("root", true);
                userMapper.insert(param);
                Integer id = userMapper.searchIdByOpenId(openId);

                MessageEntity entity = new MessageEntity();
                entity.setSenderId(0);
                entity.setSenderName("系统消息");
                entity.setUuid(IdUtil.simpleUUID());
                entity.setMsg("欢迎您注册成为超级管理员，请及时更新你的个人信息。");
                entity.setSendTime(new Date());
                messageTask.sendAsync(id + "", entity);
                return id;
            } else {
                throw new EmosException("无法绑定超级管理员账号");
            }
        } else {
            System.out.println("我是注册普通用户");
            HashMap param = new HashMap();
            String openId = getOpenId(code);
            param.put("openId", openId);
            param.put("nickname", nickname);
            param.put("photo", photo);
            param.put("role", "[3]");
            param.put("status", 1);
            param.put("createTime", new Date());
            param.put("root", false);

            userMapper.insert(param);
            Integer id = userMapper.searchIdByOpenId(openId);
            return id;
        }
    }

    /**
     * 根据用户ID查询用户的权限列表
     *
     * @param userId
     * @return
     */
    @Override
    public Set<String> searchUserPermissions(int userId) {
        return userMapper.searchUserPermissions(userId);
    }

    /**
     * 用户登录操作 根据openId凭证判断用户是否为单位员工
     *
     * @param code
     * @return
     */
    @Override
    public Integer login(String code) {
        String openId = getOpenId(code);
        Integer id = userMapper.searchIdByOpenId(openId);
        if (id == null) {
            throw new EmosException("帐户不存在");
        }
        return id;
    }

    /**
     * 返回用户信息对象
     *
     * @param userId
     * @return
     */
    @Override
    public TbUser searchById(int userId) {
        return userMapper.searchById(userId);
    }

    /**
     * 查询用户入职日期
     *
     * @param userId
     * @return
     */
    @Override
    public String searchUserHiredate(Integer userId) {
        return userMapper.searchUserHiredate(userId);
    }

    /**
     * 返回用户个人信息
     *
     * @param userId
     * @return
     */
    @Override
    public HashMap searchUserSummary(Integer userId) {
        return userMapper.searchUserSummary(userId);
    }

    /**
     * 返回成员列表
     *
     * @param keyword
     * @return
     */
    @Override
    public ArrayList<HashMap> searchUserGroupByDept(String keyword) {
        ArrayList<HashMap> deptList = deptMapper.searchDeptMembers(keyword);
        ArrayList<HashMap> userList = userMapper.searchUserGroupByDept(keyword);
        for (HashMap deptMap : deptList) {
            long deptId = (Long) deptMap.get("id");
            ArrayList members = new ArrayList();
            for (HashMap userMap : userList) {
                long id = (Long) userMap.get("deptId");
                if (deptId == id) {
                    members.add(userMap);
                }
            }
            deptMap.put("members", members);
        }
        return deptList;
    }

    /**
     * 查询会议参会人员
     *
     * @param param
     * @return
     */
    @Override
    public ArrayList<HashMap> searchMembers(List param) {
        return userMapper.searchMembers(param);
    }

}


