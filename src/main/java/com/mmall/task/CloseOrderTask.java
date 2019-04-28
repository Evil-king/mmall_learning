package com.mmall.task;

import com.mmall.service.OrderService;
import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author hwq
 * @date 2019/04/28
 */
@Slf4j
@Component
public class CloseOrderTask {

    @Autowired
    private OrderService orderService;

    @Scheduled(cron = "0 */1 * * * ?")//每一分钟执行一次
    public void closeOrderTaskV1(){
        log.info("关闭订单任务开启");
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time"));
        orderService.closeOrder(hour);
        log.info("关闭订单任务结束");
    }
}
