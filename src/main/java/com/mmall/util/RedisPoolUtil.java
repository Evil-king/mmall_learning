package com.mmall.util;

import com.mmall.common.ReidsPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * @author hwq
 * @date 2019/04/23
 */
@Slf4j
public class RedisPoolUtil {

    public static String get(String key) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = ReidsPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get: key={}", key, e);
        }
        ReidsPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key) {
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = ReidsPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del: key={}", key, e);
        }
        ReidsPool.returnResource(jedis);
        return result;
    }

    public static String set(String key, String value) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = ReidsPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set: key={} value={}", key, value, e);
        }
        ReidsPool.returnResource(jedis);
        return result;
    }

    //exTime的单位是秒
    public static String setEx(String key, String value, int exTime) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = ReidsPool.getJedis();
            result = jedis.setex(key, exTime, value);
        } catch (Exception e) {
            log.error("setEx: key={} exTime={} value={}", key, exTime, value, e);
        }
        ReidsPool.returnResource(jedis);
        return result;
    }

    /**
     * 设置key的有效期，单位是秒
     * @param key
     * @param exTime
     * @return
     */
    public static Long expire(String key, int exTime) {
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = ReidsPool.getJedis();
            result = jedis.expire(key, exTime);
        } catch (Exception e) {
            log.error("expire: key={} exTime={}", key, exTime, e);
        }
        ReidsPool.returnResource(jedis);
        return result;
    }

    public static Long setnx(String key, String value) {
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = ReidsPool.getJedis();
            result = jedis.setnx(key, value);
        } catch (Exception e) {
            log.error("setnx: key={} value={}", key, value, e);
        }
        ReidsPool.returnResource(jedis);
        return result;
    }

    public static String getSet(String key, String value) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = ReidsPool.getJedis();
            result = jedis.getSet(key, value);
        } catch (Exception e) {
            log.error("getSet: key={} value={}", key, value, e);
        }
        ReidsPool.returnResource(jedis);
        return result;
    }
}
