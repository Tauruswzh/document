package com.demo.netty.controller;

import com.demo.netty.entity.dao.MsgWsContent;
import com.demo.netty.entity.dto.WsMsgDto;
import com.demo.netty.service.MsgWsContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/msg/ws/content")
public class MsgWsContentController {

    private final MsgWsContentService wsContentService;

    /**
     * 新增websocket消息
     *
     * @param wsMsgDto webSocket消息传输实体
     * @return java.lang.Long
     */
    @PostMapping(value = "/add")
    public Long addWsContent(@RequestBody WsMsgDto wsMsgDto) {
        return wsContentService.addWsContent(wsMsgDto);
    }

    /**
     * 根据ID查询
     *
     * @param msgId 消息ID
     * @return com.health.message.entity.dao.websocket.MsgWsContent
     */
    @GetMapping(value = "/getById")
    public MsgWsContent getById(@RequestParam("msgId") Long msgId) {
        return wsContentService.selectByPrimaryKey(msgId);
    }
}
