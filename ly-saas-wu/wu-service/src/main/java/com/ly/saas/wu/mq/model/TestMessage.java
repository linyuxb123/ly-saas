package com.ly.saas.wu.mq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 测试消息实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 消息ID
     */
    private String id;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
}