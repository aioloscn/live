package com.aiolos.live.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO implements java.io.Serializable {

    private Long userId;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "真实姓名")
    private String trueName;

    @Schema(description = "性别")
    private Byte sex;

    @Schema(description = "出生时间")
    private LocalDateTime bornTime;

    @Schema(description = "工作地")
    private Integer workCity;

    @Schema(description = "出生地")
    private Integer bornCity;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
