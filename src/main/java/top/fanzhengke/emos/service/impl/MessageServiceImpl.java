package top.fanzhengke.emos.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.fanzhengke.emos.db.mapper.MessageMapper;
import top.fanzhengke.emos.db.mapper.MessageRefMapper;
import top.fanzhengke.emos.db.pojo.MessageEntity;
import top.fanzhengke.emos.db.pojo.MessageRefEntity;
import top.fanzhengke.emos.service.MessageService;

import java.util.HashMap;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageRefMapper messageRefMapper;

    /**
     * 新增消息
     *
     * @param entity
     * @return
     */
    @Override
    public String insertMessage(MessageEntity entity) {
        return messageMapper.insert(entity);
    }

    /**
     * 分页查询消息
     *
     * @param userId
     * @param start
     * @param length
     * @return
     */
    @Override
    public List<HashMap> searchMessageByPage(Integer userId, long start, Integer length) {
        return messageMapper.searchMessageByPage(userId, start, length);
    }

    /**
     * 根据id查询消息
     *
     * @param id
     * @return
     */
    @Override
    public HashMap searchMessageById(String id) {
        return messageMapper.searchMessageById(id);
    }

    /**
     * 插入消息的ref
     *
     * @param entity
     * @return
     */
    @Override
    public String insertRef(MessageRefEntity entity) {
        return messageRefMapper.insert(entity);

    }

    /**
     * 查询未读消息数
     *
     * @param userId
     * @return
     */
    @Override
    public long searchUnreadCount(Integer userId) {
        return messageRefMapper.searchUnreadCount(userId);
    }

    /**
     * 查询新消息数
     *
     * @param userId
     * @return
     */
    @Override
    public long searchLastCount(Integer userId) {
        return messageRefMapper.searchLastCount(userId);
    }

    /**
     * 更新未读消息状态
     *
     * @param id
     * @return
     */
    @Override
    public long updateUnreadMessage(String id) {
        return messageRefMapper.updateUnreadMessage(id);
    }

    /**
     * 根据id删除对应消息的ref
     *
     * @param id
     * @return
     */
    @Override
    public long deleteMessageRefById(String id) {
        return messageRefMapper.deleteMessageRefById(id);
    }

    /**
     * 根据用户id 删除该用户所有的ref消息
     *
     * @param userId
     * @return
     */
    @Override
    public long deleteUserMessageRef(Integer userId) {
        return messageRefMapper.deleteUserMessageRef(userId);
    }
}
