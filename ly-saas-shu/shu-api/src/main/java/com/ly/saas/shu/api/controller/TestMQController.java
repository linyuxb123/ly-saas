package com.ly.saas.shu.api.controller;

import com.ly.saas.shu.core.constant.Constants;
import com.ly.saas.shu.mq.producer.TestMessageProducer;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试MQ控制器
 */
@RestController(Constants.PREFIX + "TestMQController")
@RequestMapping(Constants.API_PREFIX + "/test/mq")
public class TestMQController {

    @Resource(name = "shuTestMessageProducer")
    private TestMessageProducer testMessageProducer;

    /**
     * 测试同步发送消息
     *
     * @param content 消息内容
     * @return 发送结果
     */
    @GetMapping("/sync")
    public String testSyncSend(@RequestParam("content") String content) {
        return testMessageProducer.sendMessage(content).toString();
    }

    /**
     * 测试异步发送消息
     *
     * @param content 消息内容
     * @return 提示信息
     */
    @GetMapping("/async")
    public String testAsyncSend(@RequestParam("content") String content) {
        testMessageProducer.sendAsyncMessage(content);
        return "异步消息已发送，请查看日志";
    }

    /**
     * 测试单向发送消息
     *
     * @param content 消息内容
     * @return 提示信息
     */
    @GetMapping("/oneway")
    public String testOneWaySend(@RequestParam("content") String content) {
        testMessageProducer.sendOneWayMessage(content);
        return "单向消息已发送，请查看日志";
    }
}