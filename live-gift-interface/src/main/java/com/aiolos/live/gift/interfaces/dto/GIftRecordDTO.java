package com.aiolos.live.gift.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GIftRecordDTO {

    private Long id;

    @Schema(description = "送礼用户id")
    private Long userId;

    @Schema(description = "主播id")
    private Long streamerId;

    @Schema(description = "礼物id")
    private Long giftId;

    @Schema(description = "礼物来源")
    private Integer source;
}
