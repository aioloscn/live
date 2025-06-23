package com.aiolos.live.im.provider.controller;

import com.aiolos.live.im.provider.service.ImService;
import com.aiolos.live.im.provider.vo.ImConfigVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/im")
@Tag(name = "IM服务")
public class ImController {
    
    @Autowired
    private ImService imService;
    
    @PostMapping("/get-im-config")
    public ImConfigVO getImConfig() {
        return imService.getImConfig();
    }
}
