package top.fanzhengke.emos.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.fanzhengke.emos.db.pojo.SysConfig;

import java.util.List;

/**
 * @author coco
 * @description 针对表【sys_config】的数据库操作Mapper
 * @createDate 2022-03-12 21:19:07
 * @Entity top.fanzhengke.emoswxapi.db.pojo.SysConfig
 */
@Mapper
public interface SysConfigMapper {
    public List<SysConfig> selectAllParam();
}
