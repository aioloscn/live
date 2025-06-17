package com.aiolos.live.enums.exceptions;

import com.aiolos.common.enums.errors.CommonError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LivingRoomExceptionEnum implements CommonError {
    
    START_STREAMING_TYPE_ERROR(21001, "请选择正确的直播类型"),
    START_STREAMING_ERROR(21002, "开启直播失败"),
    ;

    private Integer errCode;
    private String errMsg;

    @Override
    public CommonError setErrMsg(String s) {
        return null;
    }
}
