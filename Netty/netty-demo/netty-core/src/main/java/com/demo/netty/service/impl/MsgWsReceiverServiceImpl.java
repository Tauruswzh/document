package com.demo.netty.service.impl;

import com.demo.netty.entity.dao.MsgWsReceiver;
import com.demo.netty.entity.dto.MsgWsQueryDto;
import com.demo.netty.entity.vo.WsMsgVo;
import com.demo.netty.mapper.MsgWsReceiverMapper;
import com.demo.netty.service.MsgWsReceiverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MsgWsReceiverServiceImpl implements MsgWsReceiverService {

    private final MsgWsReceiverMapper msgWsReceiverMapper;

    @Override
    public Integer deleteByPrimaryKey(Long id) {
        return msgWsReceiverMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Integer insertSelective(MsgWsReceiver record) {
        return msgWsReceiverMapper.insertSelective(record);
    }

    @Override
    public MsgWsReceiver selectByPrimaryKey(Long id) {
        return msgWsReceiverMapper.selectByPrimaryKey(id);
    }

    @Override
    public Integer updateByPrimaryKeySelective(MsgWsReceiver record) {
        return msgWsReceiverMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<WsMsgVo> queryList(MsgWsQueryDto queryDto) {
        return msgWsReceiverMapper.queryList(queryDto);
    }

    @Override
    public Integer unreadCount(Long userId) {
        return msgWsReceiverMapper.unreadCount(userId);
    }
}