package top.fanzhengke.emos.db.mapper;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import top.fanzhengke.emos.db.pojo.MessageEntity;
import top.fanzhengke.emos.db.pojo.MessageRefEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Repository
public class MessageMapper {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 向MongoDB插入一条数据,需要将北京时间转为格林尼治时间,返回id
     *
     * @param entity
     * @return
     */
    public String insert(MessageEntity entity) {
        Date sendTime = entity.getSendTime();
        sendTime = DateUtil.offset(sendTime, DateField.HOUR, 8);
        entity.setSendTime(sendTime);
        return mongoTemplate.save(entity).get_id();
    }

    /**
     * 分页联合查询
     *
     * @param userId
     * @param start
     * @param length
     * @return
     */
    public List<HashMap> searchMessageByPage(int userId, long start, int length) {
        JSONObject json = new JSONObject();
        json.set("$toString", "$_id");
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.addFields().addField("id").withValue(json).build(),
                Aggregation.lookup("message_ref", "id", "messageId", "ref"),
                Aggregation.match(Criteria.where("ref.receiverId").is(userId)),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "sendTime")),
                Aggregation.skip(start),
                Aggregation.limit(length)
        );
        AggregationResults<HashMap> results = mongoTemplate.aggregate(aggregation, "message", HashMap.class);
        List<HashMap> list = results.getMappedResults();
        list.forEach(one -> {
            List<MessageRefEntity> refList = (List<MessageRefEntity>) one.get("ref");
            MessageRefEntity entity = refList.get(0);
            one.put("readFlag", entity.getReadFlag());
            one.put("refId", entity.get_id());

            // 删除引用字段
            one.remove("ref");

            // 删除集合中的组件列
            one.remove("_id");

            // 时间转换
            Date sendTime = (Date) one.get("sendTime");
            sendTime = DateUtil.offset(sendTime, DateField.HOUR, -8);

            // 对时间进行处理
            String today = DateUtil.today();
            if (today.equals(DateUtil.date(sendTime).toDateStr())) {
                one.put("sendTime", DateUtil.format(sendTime, "HH:mm"));
            } else {
                one.put("sendTime", DateUtil.format(sendTime, "yyyy/MM/dd"));
            }
        });
        return list;
    }


    /**
     * 根据id 查询具体信息内容
     *
     * @param id
     * @return
     */
    public HashMap searchMessageById(String id) {
        HashMap map = mongoTemplate.findById(id, HashMap.class, "message");
        Date sendTime = (Date) map.get("sendTime");
        sendTime = DateUtil.offset(sendTime, DateField.HOUR, -8);
        map.replace("sendTime", DateUtil.format(sendTime, "yyyy-MM-dd HH:mm"));
        return map;
    }

}
