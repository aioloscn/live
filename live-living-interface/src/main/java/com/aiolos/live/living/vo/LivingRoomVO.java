package com.aiolos.live.living.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class LivingRoomVO implements Serializable {

    private Long id;

    @Schema(description = "主播id")
    private Long streamerId;

    @Schema(description = "状态, 0: 无效, 1: 有效")
    private Boolean status;

    @Schema(description = "直播间名称")
    private String roomName;

    @Schema(description = "封面图片")
    private String covertImg;

    @Schema(description = "观看数")
    private Integer watchCount;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "开播时间")
    private LocalDateTime startTime;
}
