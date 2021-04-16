package com.example.technologyforum.util;

import com.alibaba.fastjson.JSON;
import com.example.technologyforum.cache.KeyPrefix;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存操作工具类
 *
 * @author Monster
 * @since 1.0.0-SNAPSHOT
 */
@Component
public class RedisUtil {

  private final RedisTemplate<String, Object> template;

  public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
    this.template = redisTemplate;
  }

  /**
   * 添加指定元素到有序集合中  排名
   *
   * @param type
   * @param key
   * @param score
   * @return
   */
  public boolean sortSetAdd(String type, String key, double score) {
    try {
      return template.opsForZSet().add(type, key, score);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * redisTemplate.opsForZSet().reverseRank("test:zSet",keyWord)
   * 获取指定的参数排名位置(根据value值来获取)，可以作为判断该数值是否已经存在的依据
   * 1、无该value则返回null，
   * 2、有则返回具体的位置排行从0开始计算
   */
  public Long getHotScore(String key, String type) {
    return template.opsForZSet().reverseRank(type, key);
  }

  /**
   * 有序集合中对指定成员的分数加上增量 increment
   * redisTemplate.opsForZSet().incrementScore("test:zSet",keyWord,1);
   * 只有数据存在的时候才会加
   *
   * @param
   * @param key
   * @param i
   * @param
   * @return
   */
  public Double sortSetZincrby(String type, String key, double i) {
    try {
      //返回新增元素后的分数
      return template.opsForZSet().incrementScore(type, key, i);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0.0;
  }

  /**
   * 获取指定元素的值
   */
  public Double getScore(String type, String key) {
    return template.opsForZSet().incrementScore(type, key, 0);
  }

  /**
   * 获得有序集合指定范围元素 (从大到小)
   *
   * @param type
   * @param start
   * @param end
   * @return
   */
  public Set sortSetRange(String type, int start, int end) {
    try {
      return template.opsForZSet().reverseRange(type, start, end);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  public Boolean removeSetRang(String type,String key){
    try {
       template.opsForZSet().remove(type,key);
       return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 带权重 返回前n条数据
   * @param type
   * @param start
   * @param end
   * @return
   */
  public Set sortSetRangeAndScore(String type,int start,int end) {
    Set<ZSetOperations.TypedTuple<Object>> redisValues = template.opsForZSet().reverseRangeWithScores(type, start, end);
    return redisValues;
  }

  /**
   * set 操作
   *
   * @param prefix
   * @param key
   * @param value
   * @param <T>
   * @return
   */
  public <T> boolean set(KeyPrefix prefix, String key, T value) {
    ValueOperations<String, Object> ops = template.opsForValue();
    // String str = bean2String(value);
    if (value == null || (value instanceof String && ((String) value).length() <= 0) ) {
      return false;
    }
    String realKey = prefix.getPrefix() + key;
    int expire = prefix.expireSeconds();
    if (expire > 0) {
      ops.set(realKey, value, expire, TimeUnit.SECONDS);
    } else {
      ops.set(realKey, value);
    }
    return true;
  }

  /**
   * get 操作
   *
   * @param prefix
   * @param key
   * @param <T>
   * @return
   */
  public <T> T get(KeyPrefix prefix, String key) {
    String realKey = prefix.getPrefix() + key;
    Object o = template.opsForValue().get(realKey);
    return (T) o;
  }

  public <T> T get(String key) {
    return (T) template.opsForValue().get(key);
  }

  /**
   * redis自增操作
   * @param prefix
   * @param key
   * @return
   */
  public long increment(KeyPrefix prefix, String key, int delta) {
    ValueOperations<String, Object> ops = template.opsForValue();
    String realKey = prefix.getPrefix() + key;
    return ops.increment(realKey, delta);
  }


  /**
   * 删除操作
   * @param prefix
   * @param key
   */
  public void delete(KeyPrefix prefix, String key) {
    String realKey = prefix.getPrefix() + key;
    template.delete(realKey);
  }

  /**
   * 查询匹配的所有key
   * @param pattern
   * @return
   */
  public Set<String> keys(String pattern) {
    return template.keys(pattern);
  }

  /**
   * 实体类转为字符串
   *
   * @param value
   * @param <T>
   * @return
   */
  public static <T> String bean2String(T value) {
    if (value == null) {
      return null;
    }
    Class<?> clazz = value.getClass();
    if (clazz == String.class) {
      return (String) value;
    } else {
      return JSON.toJSONString(value);
    }
  }

  /**
   * 字符串转为实体类
   *
   * @param str
   * @param clazz
   * @param <T>
   * @return
   */
  public static <T> T string2Bean(String str, Class<T> clazz) {
    if (str == null || str.length() <= 0 || clazz == null) {
      return null;
    }
    if (clazz == String.class) {
      return (T) str;
    } else {
      return JSON.toJavaObject(JSON.parseObject(str), clazz);
    }
  }


}
