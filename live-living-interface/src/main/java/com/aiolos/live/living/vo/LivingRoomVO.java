package com.aiolos.live.living.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class LivingRoomVO implements Serializable {

    private Long roomId;
    private Long userId;
    private String nickName;
    private String avatar;
    
    @Schema(description = "roomId")
    private Long id;

    @Schema(description = "主播id")
    private Long streamerId;

    @Schema(description = "状态, 0: 无效, 1: 有效")
    private Boolean status;
    
    @Schema(description = "直播间类型, 1: 常规直播间, 2: PK房")
    private Integer type;

    @Schema(description = "直播间名称")
    private String roomName;

    @Schema(description = "封面图片")
    private String cover;

    @Schema(description = "观看数")
    private Integer watchCount;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "开播时间")
    private LocalDateTime startTime;
}
