package top.fanzhengke.emos.db.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import lombok.Data;

/**
 * 会议表
 *
 * @TableName tb_meeting
 */
@Data
public class TbMeeting implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * UUID
     */
    private String uuid;

    /**
     * 会议题目
     */
    private String title;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 日期
     */
    private String date;

    /**
     * 开会地点
     */
    private String place;

    /**
     * 开始时间
     */
    private long start;

    /**
     * 结束时间
     */
    private long end;

    /**
     * 会议类型（1在线会议，2线下会议）
     */
    private Short type;

    /**
     * 参与者
     */
    private Object members;

    /**
     * 会议内容
     */
    private String desc;

    /**
     * 工作流实例ID
     */
    private String instanceId;

    /**
     * 状态（1未开始，2进行中，3已结束）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbMeeting tbMeeting = (TbMeeting) o;
        return start == tbMeeting.start && end == tbMeeting.end && id.equals(tbMeeting.id) && uuid.equals(tbMeeting.uuid) && title.equals(tbMeeting.title) && creatorId.equals(tbMeeting.creatorId) && date.equals(tbMeeting.date) && place.equals(tbMeeting.place) && type.equals(tbMeeting.type) && members.equals(tbMeeting.members) && desc.equals(tbMeeting.desc) && instanceId.equals(tbMeeting.instanceId) && status.equals(tbMeeting.status) && createTime.equals(tbMeeting.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, title, creatorId, date, place, start, end, type, members, desc, instanceId, status, createTime);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", uuid=").append(uuid);
        sb.append(", title=").append(title);
        sb.append(", creatorId=").append(creatorId);
        sb.append(", date=").append(date);
        sb.append(", place=").append(place);
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", type=").append(type);
        sb.append(", members=").append(members);
        sb.append(", desc=").append(desc);
        sb.append(", instanceId=").append(instanceId);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}