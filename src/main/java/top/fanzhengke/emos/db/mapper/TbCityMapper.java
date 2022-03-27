package top.fanzhengke.emos.db.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * @author coco
 * @description 针对表【tb_city(疫情城市列表)】的数据库操作Mapper
 * @createDate 2022-03-12 21:19:07
 * @Entity top.fanzhengke.emoswxapi.db.pojo.TbCity
 */
@Mapper
public interface TbCityMapper {
    public String searchCode(String city);
}
