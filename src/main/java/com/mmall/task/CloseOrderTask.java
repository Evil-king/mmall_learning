package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.service.OrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolutil;
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

    //@Scheduled(cron = "0 */1 * * * ?")//每一分钟执行一次
    public void closeOrderTaskV1(){
        log.info("关闭订单任务开启");
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time","2"));
        orderService.closeOrder(hour);
        log.info("关闭订单任务结束");
    }

    @Scheduled(cron = "0 */1 * * * ?")//每一分钟执行一次
    public void closeOrderTaskV2(){
        log.info("关闭订单任务开启");
        Long lockTime = Long.valueOf(PropertiesUtil.getProperty("lock.timeout"));//获取锁的时间
        //用CLOSE_ORDER_TASK_LOCK作为kye
        Long setnxResult = RedisShardedPoolutil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis()+lockTime));
        if(setnxResult !=null && setnxResult.intValue() != -1){
            //如果返回值是1，代表设置成功，获取锁
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        } else {
            log.info("没有获得分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }

        log.info("关闭订单任务结束");
    }





    private void closeOrder(String lockName){
        RedisShardedPoolutil.expire(lockName,5);//设置过期时间，防止死锁
        log.info("获取{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        //TODO 处理自己的业务逻辑
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time","2"));
        orderService.closeOrder(hour);

        RedisShardedPoolutil.del(lockName);//释放锁
        log.info("释放{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        log.info("===============================");
    }
}
