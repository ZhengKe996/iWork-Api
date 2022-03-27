package top.fanzhengke.emos.service;

import top.fanzhengke.emos.db.pojo.MessageEntity;
import top.fanzhengke.emos.db.pojo.MessageRefEntity;

import java.util.HashMap;
import java.util.List;

public interface MessageService {
    public String insertMessage(MessageEntity entity);

    public List<HashMap> searchMessageByPage(Integer userId, long start, Integer length);

    public HashMap searchMessageById(String id);

    public String insertRef(MessageRefEntity entity);

    public long searchUnreadCount(Integer userId);

    public long searchLastCount(Integer userId);

    public long updateUnreadMessage(String id);

    public long deleteMessageRefById(String id);

    public long deleteUserMessageRef(Integer userId);

}
