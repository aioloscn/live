package com.aiolos.live.common.utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlUtil {

    public static Map<String, String> urlParse(String url) {
        return Arrays.stream(url.split("&"))
                .map(pair -> pair.split("=", 2))    // 最多分割成2个元素，至少有一个元素
                .collect(Collectors.toMap(
                        arr -> URLDecoder.decode(arr[0], StandardCharsets.UTF_8),
                        arr -> arr.length > 1 ? URLDecoder.decode(arr[1], StandardCharsets.UTF_8) : "",
                        (v1, v2) -> v1
                ));
    }
}
