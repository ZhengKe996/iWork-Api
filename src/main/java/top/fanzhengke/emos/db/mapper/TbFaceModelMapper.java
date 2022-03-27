package top.fanzhengke.emos.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.fanzhengke.emos.db.pojo.TbFaceModel;

/**
 * @author coco
 * @description 针对表【tb_face_model】的数据库操作Mapper
 * @createDate 2022-03-12 21:19:07
 * @Entity top.fanzhengke.emoswxapi.db.pojo.TbFaceModel
 */
@Mapper
public interface TbFaceModelMapper {
    /**
     * 查询员工人脸数据
     *
     * @param userId
     * @return
     */
    public String searchFaceModel(Integer userId);

    /**
     * 新增员工人脸数据
     *
     * @param faceModel
     */
    public void insert(TbFaceModel faceModel);

    /**
     * 删除员工人脸数据
     *
     * @param userId
     * @return
     */
    public int deleteFaceModel(Integer userId);
}
