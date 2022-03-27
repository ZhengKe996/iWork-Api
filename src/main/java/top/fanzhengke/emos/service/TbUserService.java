package top.fanzhengke.emos.service;

import top.fanzhengke.emos.db.pojo.TbUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author coco
 * @description 针对表【tb_user(用户表)】的数据库操作Service
 * @createDate 2022-03-12 21:19:07
 */
public interface TbUserService {
    public Integer registerUser(String registerCode, String code, String nickname, String photo);

    public Set<String> searchUserPermissions(int userId);

    public Integer login(String code);

    public TbUser searchById(int userId);

    public String searchUserHiredate(Integer userId);

    public HashMap searchUserSummary(Integer userId);

    public ArrayList<HashMap> searchUserGroupByDept(String keyword);

    public ArrayList<HashMap> searchMembers(List param);
}
