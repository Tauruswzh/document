package com.demo.netty.mapper;


import com.demo.netty.entity.dto.MsgWsQueryDto;
import com.demo.netty.entity.dao.MsgWsReceiver;
import com.demo.netty.entity.vo.WsMsgVo;

import java.util.List;

public interface MsgWsReceiverMapper {
    /**
     * 通过主键删除<br>
     * <p>
     * TABLE： msg_ws_receiver<br>
     *
     * @mbg.generated DATE: 2021-01-27 17:16
     */
    Integer deleteByPrimaryKey(Long id);

    /**
     * 添加数据到<br>
     * <p>
     * TABLE： msg_ws_receiver<br>
     *
     * @mbg.generated DATE: 2021-01-27 17:16
     */
    Integer insertSelective(MsgWsReceiver record);

    /**
     * 批量插入
     *
     * @param receivers 接受者
     * @return Integer
     */
    Integer insertBatch(List<MsgWsReceiver> receivers);

    /**
     * 通过主键查询<br>
     * <p>
     * TABLE： msg_ws_receiver<br>
     *
     * @mbg.generated DATE: 2021-01-27 17:16
     */
    MsgWsReceiver selectByPrimaryKey(Long id);

    /**
     * 通过主键更新<br>
     * <p>
     * TABLE： msg_ws_receiver<br>
     *
     * @mbg.generated DATE: 2021-01-27 17:16
     */
    Integer updateByPrimaryKeySelective(MsgWsReceiver record);

    /**
     * 根据条件查询
     *
     * @param queryDto 查询条件
     * @return java.util.List<com.health.message.entity.vo.websocket.WsMsgVo>
     */
    List<WsMsgVo> queryList(MsgWsQueryDto queryDto);

    /**
     * 未读消息数量
     *
     * @param userId 用户ID
     * @return java.lang.Integer
     */
    Integer unreadCount(Long userId);
}
