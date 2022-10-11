package com.demo.netty.service.impl;

import com.demo.netty.entity.constant.RedisConstant;
import com.demo.netty.entity.dao.MsgWsContent;
import com.demo.netty.entity.dao.MsgWsReceiver;
import com.demo.netty.entity.dto.RedisMessage;
import com.demo.netty.entity.dto.WsMsgDto;
import com.demo.netty.entity.enums.MsgReadEnum;
import com.demo.netty.entity.enums.WsActionEnum;
import com.demo.netty.mapper.MsgWsContentMapper;
import com.demo.netty.mapper.MsgWsReceiverMapper;
import com.demo.netty.service.MsgWsContentService;
import com.demo.netty.util.BeanCopierUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MsgWsContentServiceImpl implements MsgWsContentService {

    private final MsgWsContentMapper msgWsContentMapper;
    private final MsgWsReceiverMapper msgWsReceiverMapper;
    private final RedisTemplate<String, RedisMessage> redisMessageRedisTemplate;

    @Override
    public Integer deleteByPrimaryKey(Long id) {
        return msgWsContentMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Integer insertSelective(MsgWsContent record) {
        return msgWsContentMapper.insertSelective(record);
    }

    @Override
    public MsgWsContent selectByPrimaryKey(Long id) {
        return msgWsContentMapper.selectByPrimaryKey(id);
    }

    @Override
    public Integer updateByPrimaryKeySelective(MsgWsContent record) {
        return msgWsContentMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public Long addWsContent(WsMsgDto wsMsgDto) {
        // 添加消息内容
        MsgWsContent content = new MsgWsContent();
        BeanCopierUtil.copy(wsMsgDto, content);
        msgWsContentMapper.insertSelective(content);
        // 添加消息接收者信息
        List<Long> receivers = wsMsgDto.getReceivers();
        List<MsgWsReceiver> receiverList = msgWsReceiver(receivers, content.getId());
        msgWsReceiverMapper.insertBatch(receiverList);
        // 发送消息到redis
        webSocketChat(wsMsgDto, content.getId());
        return content.getId();
    }

    private List<MsgWsReceiver> msgWsReceiver(List<Long> receivers, Long contentId) {
        List<MsgWsReceiver> receiverList = new ArrayList<>();
        for (Long receiver : receivers) {
            MsgWsReceiver wsReceiver = new MsgWsReceiver();
            wsReceiver.setContentId(contentId);
            wsReceiver.setReceiver(receiver);

            wsReceiver.setIsRead(MsgReadEnum.UNREAD.code());
            receiverList.add(wsReceiver);
        }
        return receiverList;
    }

    /**
     * 发送消息到redis
     *
     * @param wsMsgDto  webSocket消息传输实体
     * @param contentId 内容ID
     */
    @Async
    public void webSocketChat(WsMsgDto wsMsgDto, Long contentId) {
        try {
            log.info("--> 睡眠2秒");
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            log.error("发送消息到redis，睡眠失败，{},{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
        List<Long> receivers = wsMsgDto.getReceivers();
        for (Long receiver : receivers) {
            RedisMessage message = new RedisMessage();
            message.setMsgId(contentId);
            message.setTopic(wsMsgDto.getTopic());
            message.setReceiver(receiver);
            message.setSender(wsMsgDto.getSender());
            message.setAction(WsActionEnum.NOTICE.type());
            // 发送数据到redis的频道
            redisMessageRedisTemplate.convertAndSend(RedisConstant.REDIS_TOPIC_KEY, message);
        }
    }
}