package top.fanzhengke.emos;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.fanzhengke.emos.db.mapper.SysConfigMapper;
import top.fanzhengke.emos.db.pojo.MessageEntity;
import top.fanzhengke.emos.db.pojo.MessageRefEntity;
import top.fanzhengke.emos.db.pojo.SysConfig;
import top.fanzhengke.emos.db.pojo.TbMeeting;
import top.fanzhengke.emos.service.MessageService;
import top.fanzhengke.emos.service.TbMeetingService;

import java.util.Date;
import java.util.List;

@SpringBootTest
class EmosWxApiApplicationTests {
    @Autowired
    private MessageService messageService;

    @Autowired
    private TbMeetingService meetingService;

    @Test
    void contextLoads() {
        for (int i = 1; i <= 100; i++) {
            MessageEntity message = new MessageEntity();
            message.setUuid(IdUtil.simpleUUID());
            message.setSenderId(0);
            message.setSenderName("系统消息");
            message.setMsg("这是第" + i + "条测试消息");
            message.setSendTime(new Date());
            String id = messageService.insertMessage(message);

            MessageRefEntity ref = new MessageRefEntity();
            ref.setMessageId(id);
            ref.setReceiverId(25); //接收人ID
            ref.setLastFlag(true);
            ref.setReadFlag(false);
            messageService.insertRef(ref);
        }
    }

    @Test
    void createMeetingData() {
        for (int i = 1; i <= 30; i++) {
            TbMeeting meeting = new TbMeeting();
            meeting.setId((long) i);
            meeting.setUuid(IdUtil.simpleUUID());
            meeting.setTitle("测试会议" + i);
            meeting.setCreatorId(3L); //ROOT用户ID
            meeting.setDate(DateUtil.today());
            meeting.setPlace("线上会议室");
            meeting.setStart(1648389893000L);
            meeting.setEnd(1648393493000L);
            meeting.setType((short) 1);
            meeting.setMembers("[3]");
            meeting.setDesc("会议研讨Emos项目上线测试");
            meeting.setInstanceId(IdUtil.simpleUUID());
            meeting.setStatus(3);
            meetingService.insertMeeting(meeting);
        }
    }


}
