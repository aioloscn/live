package com.aiolos.live.living.provider.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class HomePageVO {

    private Long userId;

    @Schema(description = "昵称")
    private String nickName;
    
    @Schema(description = "头像")
    private String avatar;
    
    @Schema(description = "登录状态")
    private Boolean loginStatus = false;
    
    @Schema(description = "是否显示可开播按钮")
    private Boolean showStartLivingBtn = false;
}
