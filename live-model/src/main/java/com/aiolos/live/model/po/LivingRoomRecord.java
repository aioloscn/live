package com.aiolos.live.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 直播记录表
 * </p>
 *
 * @author Aiolos
 * @since 2025-06-12
 */
@Getter
@Setter
@TableName("living_room_record")
public class LivingRoomRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 主播id
     */
    @TableField("streamer_id")
    private Long streamerId;

    /**
     * 状态, 0: 无效, 1: 有效
     */
    @TableField("status")
    private Boolean status;

    /**
     * 直播间类型, 1: 常规直播间, 2: PK房
     */
    @TableField("type")
    private Integer type;

    /**
     * 直播间名称
     */
    @TableField("room_name")
    private String roomName;

    /**
     * 直播间封面
     */
    @TableField("covert_img")
    private String covertImg;

    /**
     * 观看数量
     */
    @TableField("watch_count")
    private Integer watchCount;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 开播时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 关播时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    public static final String ID = "id";

    public static final String STREAMER_ID = "streamer_id";

    public static final String STATUS = "status";

    public static final String ROOM_NAME = "room_name";

    public static final String COVERT_IMG = "covert_img";

    public static final String WATCH_COUNT = "watch_count";

    public static final String LIKE_COUNT = "like_count";

    public static final String START_TIME = "start_time";

    public static final String END_TIME = "end_time";

    public static final String UPDATE_TIME = "update_time";
}
