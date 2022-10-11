package com.demo.netty.controller;

import com.demo.netty.entity.dto.WsMsgDto;
import com.demo.netty.service.MsgWsContentService;
import com.demo.netty.util.GsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/send")
public class TestController {

    private final MsgWsContentService wsContentService;

    /**
     * 新增websocket消息
     */
    @PostMapping(value = "/add")
    public Long addWsContent() {
        Map<String, Object> map = new HashMap<>();
        map.put("cureItemCode", "100010001");
        map.put("inspectDeviceCode", "2002000200");
        map.put("historyId", "3003000300");

        List<Long> receivers = new ArrayList<>();
        receivers.add(1L);

        WsMsgDto msgDto = new WsMsgDto();
        msgDto.setSender(2L);
        msgDto.setReceivers(receivers);
        msgDto.setTopic("demo-test1");
        msgDto.setType(1);
        msgDto.setCreateTime(new Date());
        msgDto.setTitle("测试netty");
        msgDto.setContent("第一次测试。。。。。。。。。第一次测试");
        msgDto.setAdditional(GsonUtil.beanToJson(map));
        return wsContentService.addWsContent(msgDto);
    }

}
