package com.demo.netty.service;


import com.demo.netty.entity.dao.MsgWsContent;
import com.demo.netty.entity.dto.WsMsgDto;

public interface MsgWsContentService {
    Integer deleteByPrimaryKey(Long id);

    Integer insertSelective(MsgWsContent record);

    MsgWsContent selectByPrimaryKey(Long id);

    Integer updateByPrimaryKeySelective(MsgWsContent record);

    Long addWsContent(WsMsgDto wsMsgDto);
}