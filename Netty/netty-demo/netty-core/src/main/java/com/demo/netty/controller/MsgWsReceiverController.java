package com.demo.netty.controller;

import com.demo.netty.entity.dao.MsgWsReceiver;
import com.demo.netty.entity.dto.MsgWsQueryDto;
import com.demo.netty.entity.dto.MsgWsReceiverDto;
import com.demo.netty.entity.dto.PageDto;
import com.demo.netty.entity.vo.WsMsgVo;
import com.demo.netty.service.MsgWsReceiverService;
import com.demo.netty.util.BeanCopierUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/msg/ws/receiver")
public class MsgWsReceiverController {

    private final MsgWsReceiverService wsReceiverService;

    /**
     * 修改消息
     *
     * @param receiver websocket消息接收者实体
     * @return java.lang.Long
     */
    @PutMapping(value = "/modify")
    public Integer updateWsReceiver(@RequestBody MsgWsReceiverDto receiver) {
        MsgWsReceiver msgWsReceiver = new MsgWsReceiver();
        BeanCopierUtil.copy(receiver, msgWsReceiver);
        return wsReceiverService.updateByPrimaryKeySelective(msgWsReceiver);
    }

    /**
     * 未读消息数量
     *
     * @param userId 用户ID
     * @return java.lang.Long
     */
    @GetMapping(value = "/unreadCount")
    public Integer unreadCount(@NotNull(message = "用户ID不能为空") @RequestParam("userId") Long userId) {
        return wsReceiverService.unreadCount(userId);
    }

    /**
     * 分页查询消息
     *
     * @param pageDto 分页参数
     * @return com.github.pagehelper.PageInfo<com.health.message.entity.vo.websocket.WsMsgVo>
     */
    @PostMapping(value = "/page")
    public PageInfo<WsMsgVo> page(@Valid @RequestBody PageDto<MsgWsQueryDto> pageDto) {
        PageHelper.startPage(pageDto.getPageNum(), pageDto.getPageSize());
        List<WsMsgVo> list = wsReceiverService.queryList(pageDto.getData());
        return new PageInfo<>(list);
    }
}
