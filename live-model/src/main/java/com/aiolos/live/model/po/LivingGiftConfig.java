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
@TableName("living_gift_config")
public class LivingGiftConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 礼物名称
     */
    @TableField("gift_name")
    private String giftName;

    /**
     * 虚拟货币价格
     */
    @TableField("price")
    private Integer price;

    /**
     * 状态, 0: 无效, 1: 有效
     */
    @TableField("status")
    private Boolean status;

    /**
     * 礼物封面
     */
    @TableField("cover")
    private String cover;

    /**
     * svga资源地址
     */
    @TableField("svga_url")
    private String svgaUrl;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    public static final String ID = "id";

    public static final String GIFT_NAME = "gift_name";

    public static final String PRICE = "price";

    public static final String STATUS = "status";

    public static final String COVER = "cover";

    public static final String SVGA_URL = "svga_url";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";
}
