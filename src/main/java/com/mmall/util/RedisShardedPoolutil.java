package com.mmall.util;

import com.mmall.common.RedisShardedPool;
import com.mmall.common.ReidsPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

/**
 * @author hwq
 * @date 2019/04/23
 * <p>
 *     分布式redisShardedPool
 * </p>
 */
@Slf4j
public class RedisShardedPoolutil {

    public static String get(String key) {
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get: key={}", key, e);
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key) {
        ShardedJedis jedis = null;
        Long result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del: key={}", key, e);
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String set(String key, String value) {
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set: key={} value={}", key, value, e);
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    //exTime的单位是秒
    public static String setEx(String key, String value, int exTime) {
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.setex(key, exTime, value);
        } catch (Exception e) {
            log.error("setEx: key={} exTime={} value={}", key, exTime, value, e);
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    /**
     * 设置key的有效期，单位是秒
     * @param key
     * @param exTime
     * @return
     */
    public static Long expire(String key, int exTime) {
        ShardedJedis jedis = null;
        Long result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.expire(key, exTime);
        } catch (Exception e) {
            log.error("expire: key={} exTime={}", key, exTime, e);
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }
}
