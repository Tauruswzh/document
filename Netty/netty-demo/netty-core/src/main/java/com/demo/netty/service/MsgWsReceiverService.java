package com.demo.netty.service;


import com.demo.netty.entity.dao.MsgWsReceiver;
import com.demo.netty.entity.dto.MsgWsQueryDto;
import com.demo.netty.entity.vo.WsMsgVo;

import java.util.List;

public interface MsgWsReceiverService {
    Integer deleteByPrimaryKey(Long id);

    Integer insertSelective(MsgWsReceiver record);

    MsgWsReceiver selectByPrimaryKey(Long id);

    Integer updateByPrimaryKeySelective(MsgWsReceiver record);

    List<WsMsgVo> queryList(MsgWsQueryDto queryDto);

    Integer unreadCount(Long userId);
}