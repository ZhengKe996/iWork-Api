package top.fanzhengke.emos.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.fanzhengke.emos.db.pojo.TbUser;

import java.util.*;

/**
 * @author coco
 * @description 针对表【tb_user(用户表)】的数据库操作Mapper
 * @createDate 2022-03-12 21:19:07
 * @Entity top.fanzhengke.emoswxapi.db.pojo.TbUser
 */
@Mapper
public interface TbUserMapper {
    public boolean haveRootUser();

    public Integer searchIdByOpenId(String openId);

    public Set<String> searchUserPermissions(Integer userId);

    public Integer insert(Map param);

    public TbUser searchById(Integer userId);

    public HashMap searchNameAndDept(Integer userId);

    public String searchUserHiredate(Integer userId);

    public HashMap searchUserSummary(Integer userId);

    public ArrayList<HashMap> searchUserGroupByDept(String keyword);

    public ArrayList<HashMap> searchMembers(List param);

    public HashMap searchUserInfo(Integer userId);

    public Integer searchDeptManagerId(Integer id);

    public Integer searchGmId();

}
