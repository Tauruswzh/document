package com.demo.netty.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * webSocket消息传输实体
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WsMsgDto {

    /**
     * 发送者ID
     */
    @NotNull(message = "发送者ID不能为空")
    private Long sender;

    /**
     * 接收者ID
     */
    @Size(min = 1, message = "接收者ID不能为空")
    private List<Long> receivers;

    /**
     * 消息主题，区分消息
     */
    @NotBlank(message = "消息主题不能为空")
    private String topic;

    /**
     * 消息类型: 1 文本; 2 图片; 3 链接; 4 图文
     */
    @NotNull(message = "消息类型不能为空")
    private Integer type;

    /**
     * 创建时间
     */
    @NotNull(message = "创建时间不能为空")
    private Date createTime;

    /**
     * 消息标题
     */
    @NotBlank(message = "消息标题不能为空")
    private String title;

    /**
     * 消息详情
     */
    @NotBlank(message = "消息详情不能为空")
    private String content;

    /**
     * 扩展信息,JSON格式
     */
    private String additional;
}
