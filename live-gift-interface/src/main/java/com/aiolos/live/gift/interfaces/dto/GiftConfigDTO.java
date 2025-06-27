package com.aiolos.live.gift.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GiftConfigDTO {

    private Long id;

    @Schema(description = "礼物名称")
    private String giftName;

    @Schema(description = "虚拟货币价格")
    private Integer price;

    @Schema(description = "状态, 0: 无效, 1: 有效")
    private Boolean status;

    @Schema(description = "礼物封面")
    private String cover;

    @Schema(description = "svga资源地址")
    private String svgaUrl;
}
