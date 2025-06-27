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
 * 礼物配置表
 * </p>
 *
 * @author Aiolos
 * @since 2025-06-26
 */
@Getter
@Setter
@TableName("living_gift_record")
public class LivingGiftRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 送礼用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 主播id
     */
    @TableField("streamer_id")
    private Long streamerId;

    /**
     * 礼物id
     */
    @TableField("gift_id")
    private Long giftId;

    /**
     * 礼物来源
     */
    @TableField("source")
    private Integer source;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    public static final String ID = "id";

    public static final String USER_ID = "user_id";

    public static final String STREAMER_ID = "streamer_id";

    public static final String GIFT_ID = "gift_id";

    public static final String SOURCE = "source";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";
}
