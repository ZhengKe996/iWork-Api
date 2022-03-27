package top.fanzhengke.emos.db.mapper;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import top.fanzhengke.emos.db.pojo.MessageRefEntity;

import java.util.Date;

@Repository
public class MessageRefMapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 往message_ref中插入一条数据
     *
     * @param entity
     * @return
     */
    public String insert(MessageRefEntity entity) {
        return mongoTemplate.save(entity).get_id();
    }

    /**
     * 查询用户未读消息的总数
     *
     * @param userId
     * @return
     */
    public long searchUnreadCount(Integer userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("readFlag").is(false).and("receiverId").is(userId));
        long count = mongoTemplate.count(query, MessageRefEntity.class);
        return count;
    }

    /**
     * 查询用户新接收到的消息
     *
     * @param userId
     * @return
     */
    public long searchLastCount(Integer userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("lastFlag").is(true).and("receiverId").is(userId));
        Update update = new Update();
        update.set("lastFlag", false);
        UpdateResult result = mongoTemplate.updateMulti(query, update, "message_ref");
        long rows = result.getModifiedCount();
        return rows;
    }

    /**
     * 更新某条消息的状态
     *
     * @param id
     * @return
     */
    public long updateUnreadMessage(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("readFlag", true);
        UpdateResult result = mongoTemplate.updateFirst(query, update, "message_ref");
        long rows = result.getModifiedCount();
        return rows;
    }

    /**
     * 根据id 删除ref记录
     *
     * @param id
     * @return
     */
    public long deleteMessageRefById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        DeleteResult result = mongoTemplate.remove(query, "message_ref");
        long rows = result.getDeletedCount();
        return rows;
    }

    /**
     * 删除某人所有的ref消息
     *
     * @param userId
     * @return
     */
    public long deleteUserMessageRef(Integer userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("receiverId").is(userId));
        DeleteResult result = mongoTemplate.remove(query, "message_ref");
        long rows = result.getDeletedCount();
        return rows;
    }

}
