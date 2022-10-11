package com.demo.netty.mapper;


import com.demo.netty.entity.dao.MsgWsContent;

public interface MsgWsContentMapper {
    /**
     * 通过主键删除<br>
     *
     * TABLE： msg_ws_content<br>
     *
     * @mbg.generated
     *
     * DATE: 2021-01-27 17:16
     */
    Integer deleteByPrimaryKey(Long id);

    /**
     * 添加数据到<br>
     *
     * TABLE： msg_ws_content<br>
     *
     * @mbg.generated
     *
     * DATE: 2021-01-27 17:16
     */
    Integer insertSelective(MsgWsContent record);

    /**
     * 通过主键查询<br>
     *
     * TABLE： msg_ws_content<br>
     *
     * @mbg.generated
     *
     * DATE: 2021-01-27 17:16
     */
    MsgWsContent selectByPrimaryKey(Long id);

    /**
     * 通过主键更新<br>
     *
     * TABLE： msg_ws_content<br>
     *
     * @mbg.generated
     *
     * DATE: 2021-01-27 17:16
     */
    Integer updateByPrimaryKeySelective(MsgWsContent record);
}