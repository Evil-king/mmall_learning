package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author hwq
 * @date 2019/04/23
 * <p>
 *     JedisPool连接池
 * </p>
 */
public class ReidsPool {
    private static JedisPool pool;//连接池

    private static String ip = PropertiesUtil.getProperty("redis.ip");

    private static Integer port = Integer.valueOf(PropertiesUtil.getProperty("redis.port"));

    private static Integer maxTotal = Integer.valueOf(PropertiesUtil.getProperty("redis.max.total"));//最大连接数
    private static Integer maxIdle = Integer.valueOf(PropertiesUtil.getProperty("redis.max.idle"));//最大空闲状态实例
    private static Integer minIdle = Integer.valueOf(PropertiesUtil.getProperty("redis.min.idle"));//最小空闲状态实例

    private static Boolean testOnBorrow = Boolean.valueOf(PropertiesUtil.getProperty("redis.test.borrow"));//在borrow一个jedis实例，是否要进行验证，如果赋值true，则得到jedis实例肯定是可以用的
    private static Boolean testOnReturn = Boolean.valueOf(PropertiesUtil.getProperty("redis.test.return"));//在return一个jedis实例，是否要进行验证，如果赋值true，则放回jedis实例肯定是可以用的

    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);//连接耗尽的时候，是否阻塞，false会抛出异常，true会一直阻塞直到超时，默认为true

        pool = new JedisPool(config,ip,port,1000*2);
    }

    static {
        initPool();
    }

    public static Jedis getJedis(){
        return pool.getResource();
    }

    //归还连接
    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }

    //归还坏连接
    public static void returnBrokenResource(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis = pool.getResource();
        returnResource(jedis);
        jedis.set("fox","SB");
        System.out.println("pool use end");
    }

}
