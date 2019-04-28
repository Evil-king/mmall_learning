package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hwq
 * @date 2019/04/27
 * <p>
 *     redis分布式连接池
 * </p>
 */
public class RedisShardedPool {
    private static ShardedJedisPool pool;//连接池

    private static String ip1 = PropertiesUtil.getProperty("redis.ip1");
    private static Integer port1 = Integer.valueOf(PropertiesUtil.getProperty("redis.port1"));
    private static String ip2 = PropertiesUtil.getProperty("redis.ip2");
    private static Integer port2 = Integer.valueOf(PropertiesUtil.getProperty("redis.port2"));

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

        JedisShardInfo shardInfo1 = new JedisShardInfo(ip1,port1,1000*2);
        JedisShardInfo shardInfo2 = new JedisShardInfo(ip2,port2,1000*2);

        List<JedisShardInfo> list = new ArrayList<JedisShardInfo>(2);
        list.add(shardInfo1);
        list.add(shardInfo2);

        pool = new ShardedJedisPool(config,list, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);

    }

    static {
        initPool();
    }

    public static ShardedJedis getJedis(){
        return pool.getResource();
    }

    //归还连接
    public static void returnResource(ShardedJedis jedis){
        pool.returnResource(jedis);
    }

    //归还坏连接
    public static void returnBrokenResource(ShardedJedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis = pool.getResource();
        for(int i = 0;i<10;i++){
            jedis.set("key"+i,"value"+i);
        }
        returnResource(jedis);
        System.out.println("pool use end");
    }
}
