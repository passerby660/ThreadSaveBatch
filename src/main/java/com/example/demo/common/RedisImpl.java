package com.example.demo.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis简易封装,如有需要的方法不存在可以在此类中添加
 * 也可以使用RedisTemplate
 */
@Service
@Slf4j
public class RedisImpl implements Redis {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * key操作
     *
     * @param key
     * @return
     */
    private String appendKeyPrefix(Object key) {
        if (key instanceof String) {
            return ((String) key);
        }
        return key.toString();
    }

    /**
     * 删除key 可传多个key
     *
     * @param keys keys
     */
    @Override
    public void del(Object... keys) {
        List<String> keysList = new ArrayList<>();
        for (Object key : keys) {
            keysList.add(appendKeyPrefix(key));
        }
        redisTemplate.delete(keysList);
    }

    /**
     * 实现命令：TTL key，以秒为单位，返回给定 key的剩余生存时间(TTL, time to live)。
     *
     * @param key key
     * @return time
     */
    @Override
    public Long ttl(String key) {
        return redisTemplate.getExpire(appendKeyPrefix(key));
    }

    /**
     * 给指定的key设置过期时间  单位为毫秒
     *
     * @param key     key
     * @param timeout 过期时间 单位是秒
     */
    @Override
    public void expire(String key, Integer timeout) {
        redisTemplate.expire(appendKeyPrefix(key), timeout, TimeUnit.SECONDS);
    }


    /**
     * 移除key的过期时间，将key永久保存
     *
     * @param key key
     */
    @Override
    public void persist(String key) {
        redisTemplate.persist(appendKeyPrefix(key));
    }

    /**
     * 检验该key是否存在 存在返回true
     *
     * @param key key
     */
    @Override
    public boolean exists(String key) {
        Boolean exists = redisTemplate.hasKey(appendKeyPrefix(key));
        return exists != null ? exists : false;
    }

    /**
     * 返回 key 所储存的值的类型
     *
     * @param key key
     * @return DataType
     */
    @Override
    public DataType getType(String key) {
        return redisTemplate.type(appendKeyPrefix(key));
    }


    /**
     * set字符串
     *
     * @param key   key
     * @param value value
     */
    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(appendKeyPrefix(key), value);

    }

    /**
     * set字符串 并加上失效时间  以豪秒为单位
     *
     * @param key    key
     * @param value  value
     * @param second 失效时间 单位为秒
     */
    @Override
    public void setex(Object key, Integer second, Object value) {
        redisTemplate.opsForValue().set(appendKeyPrefix(key), value, second, TimeUnit.SECONDS);
    }

    /**
     * 当key不存在时 设置key value
     *
     * @param key   key
     * @param value value
     * @return boolean true为成功，可能为空
     */
    @Override
    public Boolean setNx(String key, Long timeout, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(appendKeyPrefix(key), value, timeout, TimeUnit.SECONDS);
    }


    /**
     * 根据key获取value
     *
     * @param key key
     * @return value
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(appendKeyPrefix(key));
    }

    /**
     * 获取所有(一个或多个)给定 key 的值
     *
     * @param keys keys
     * @return values
     */
    @Override
    public List<Object> mGet(Object... keys) {
        List<String> keysList = new ArrayList<>();
        for (Object key : keys) {
            keysList.add(appendKeyPrefix(key));
        }
        return redisTemplate.opsForValue().multiGet(keysList);
    }

    /**
     * 同时设置多个key，value
     *
     * @param map map
     */
    @Override
    public void mSet(Map<String, Object> map) {
        for (String key : map.keySet()) {
            Object value = map.remove(key);
            map.put(appendKeyPrefix(key), value);
        }
        redisTemplate.opsForValue().multiSet(map);
    }

    /**
     * 所有给定的key都不存在时，设置多个key，value
     *
     * @param map map
     */
    @Override
    public void mSetNx(Map<String, Object> map) {
        for (String key : map.keySet()) {
            Object value = map.remove(key);
            map.put(appendKeyPrefix(key), value);
        }
        redisTemplate.opsForValue().multiSetIfAbsent(map);
    }


    /**
     * 当前key存在时  向这个key对应value的默认追加上此字符
     *
     * @param key   key
     * @param value 要追加的字符
     */
    @Override
    public void appendStr(Object key, String value) {
        redisTemplate.opsForValue().append(appendKeyPrefix(key), value);
    }


    /**
     * 删除key中指定的hashkey的值  相当于一个key中存储着map值 这个方法就是删除这个key中的map里面的一个或多个key
     *
     * @param key      key
     * @param hashKeys hashKeys
     */
    @Override
    public void hdel(Object key, Object... hashKeys) {
        redisTemplate.opsForHash().delete(appendKeyPrefix(key), hashKeys);
    }

    /**
     * 添加一个hash值
     *
     * @param key     key
     * @param hashKey 相当于 map中的key
     * @param value   存储的值 相当于map中的value
     */
    @Override
    public void put(Object key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(appendKeyPrefix(key), hashKey, value);
    }


    /**
     * 添加一个map
     *
     * @param key key
     * @param map map
     */
    @Override
    public void putAll(String key, Map<Object, Object> map) {
        redisTemplate.opsForHash().putAll(appendKeyPrefix(key), map);
    }

    /**
     * 获取redis中的map
     *
     * @param key key
     * @return map
     */
    @Override
    public Map<Object, Object> getRedisMap(String key) {
        return redisTemplate.opsForHash().entries(appendKeyPrefix(key));
    }

    /**
     * 返回这个key中的所有value
     *
     * @param key key
     * @return value
     */
    @Override
    public List<Object> getValues(Object key) {
        return redisTemplate.opsForHash().values(appendKeyPrefix(key));
    }

    /**
     * 判断key中的hashKey是否存在
     *
     * @param key     key
     * @param hashKey hashKey
     */
    @Override
    public Boolean hashMapKey(Object key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(appendKeyPrefix(key), hashKey);
    }

    /**
     * 从左入栈
     *
     * @param key   key
     * @param value value
     */
    @Override
    public void lpush(String key, Object value) {
        redisTemplate.opsForList().leftPush(appendKeyPrefix(key), value);
    }

    /**
     * 从右入栈
     *
     * @param key   key
     * @param value value
     */
    @Override
    public void rpush(String key, Object value) {
        redisTemplate.opsForList().rightPush(appendKeyPrefix(key), value);
    }

    /**
     * 全部插入
     *
     * @param key   key
     * @param values values
     */
    @Override
    public void rpush(String key, Collection<T> values) {
        redisTemplate.opsForList().rightPushAll(appendKeyPrefix(key), values);
    }


    /**
     * 从左出栈
     *
     * @param key key
     * @return value
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T lPop(String key) {
        return (T) redisTemplate.opsForList().leftPop(appendKeyPrefix(key));
    }

    /**
     * 从右出栈
     *
     * @param key key
     * @return value
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T rPop(String key) {
        return (T) redisTemplate.opsForList().rightPop(appendKeyPrefix(key));
    }


    /**
     * 获取该key index处的元素
     *
     * @param key   key
     * @param index index
     * @return value
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getKeyIndex(String key, int index) {
        return (T) redisTemplate.opsForList().index(appendKeyPrefix(key), index);
    }

    /**
     * 获取列表的长度
     *
     * @param key key
     * @return length
     */
    @Override
    public Long getLength(String key) {
        return redisTemplate.opsForList().size(appendKeyPrefix(key));
    }

    /**
     * 获取key中下标从start到end处的值
     *
     * @param key   key
     * @param start 开始下标
     * @param end   结束下标
     * @return values
     */
    @Override
    public List<Object> range(String key, int start, int end) {
        return redisTemplate.opsForList().range(appendKeyPrefix(key), start, end);
    }


    //set操作  无序集合

    /**
     * 向集合中添加
     *
     * @param key    key
     * @param values values
     */
    @Override
    public void addSet(String key, Object... values) {
        redisTemplate.opsForSet().add(appendKeyPrefix(key), values);
    }

    /**
     * 批量向集合中添加
     * @param key
     * @param values
     */
    @Override
    public void addSetAll(String key, Collection values) {
        for (Object value : values) {
            redisTemplate.opsForSet().add(appendKeyPrefix(key), value);
        }
    }

    /**
     * 移除并取出第一个元素
     *
     * @param key key
     * @return value
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getSet(String key) {
        return (T) redisTemplate.opsForSet().pop(appendKeyPrefix(key));
    }

    /**
     * 返回集合中所有的元素
     *
     * @param key key
     * @return values
     */
    @Override
    public Set<Object> getSets(String key) {
        return redisTemplate.opsForSet().members(appendKeyPrefix(key));
    }


    /**
     * 返回集合中的长度
     *
     * @param key key
     * @return length
     */
    @Override
    public Long getSetsNum(String key) {
        return redisTemplate.opsForSet().size(appendKeyPrefix(key));
    }

    /**
     * 返回集合中的所有元素
     *
     * @param key key
     * @return values
     */
    @Override
    public Set<Object> members(String key) {
        return redisTemplate.opsForSet().members(appendKeyPrefix(key));
    }


    //zSet操作 有序集合

    /**
     * 添加数据
     * <p>
     * 添加方式：
     * 1.创建一个set集合
     * Set<ZSetOperations.TypedTuple<Object>> sets=new HashSet<>();
     * 2.创建一个有序集合
     * ZSetOperations.TypedTuple<Object> objectTypedTuple1 = new DefaultTypedTuple<Object>(value,排序的数值，越小越在前);
     * 4.放入set集合
     * sets.add(objectTypedTuple1);
     * 5.放入缓存
     * reidsImpl.Zadd("zSet", list);
     *
     * @param key    key
     * @param tuples tuples
     */
    @Override
    public void zadd(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
        redisTemplate.opsForZSet().add(appendKeyPrefix(key), tuples);
    }


    /**
     * 返回 分数在min至max之间的数据 按分数值递减(从大到小)的次序排列。
     *
     * @param key key
     * @param min min
     * @param max max
     * @return values
     */
    @Override
    public Set<Object> reverseRange(String key, Double min, Double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(appendKeyPrefix(key), min, max);
    }


    /**
     * 实现命令 : RPOPLPUSH 源list 目标list
     * 将 源list 的最右端元素弹出，推入到 目标list 的最左端，
     *
     * @param sourceKey 源list
     * @param targetKey 目标list
     * @return 弹出的元素
     */
    @Override
    public Object rPopLPush(String sourceKey, String targetKey) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, targetKey);
    }

    /**
     * 实现命令 : BRPOPLPUSH 源list 目标list timeout
     * (阻塞式)将 源list 的最右端元素弹出，推入到 目标list 的最左端，如果 源list 没有元素，将一直等待直到有元素或超时为止
     *
     * @param sourceKey 源list
     * @param targetKey 目标list
     * @param timeout   超时时间，单位秒， 0表示无限阻塞
     * @return 弹出的元素
     */
    @Override
    public Object bRPopLPush(String sourceKey, String targetKey, int timeout) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, targetKey, timeout, TimeUnit.SECONDS);
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    @Override
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 格式化Key
     */
    @Override
    public String keyFormat(String formatKey, Object... keyValues) {
        if (keyValues == null || keyValues.length == 0) {
            return formatKey;
        }
        StringBuilder key = new StringBuilder();
        char[] chars = formatKey.toCharArray();
        int index = -1;
        boolean inmark = false;
        boolean firstinmark = false;
        for (char ch : chars) {
            if (ch == '{') {
                index++;
                inmark = true;
                firstinmark = true;
            } else if (ch == '}') {
                inmark = false;
            } else if (inmark) {
                if (firstinmark) {
                    firstinmark = false;
                    key.append(keyValues[index]);
                }
            } else {
                key.append(ch);
            }
        }
        return key.toString();
    }

    //================================Map=================================
    /**
     * HashGet
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key,String item){
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object,Object> hmget(String key){
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String,Object> map){
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String,Object> map, Integer time){
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if(time>0){
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key,String item,Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key,String item,Object value,Integer time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if(time>0){
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item){
        redisTemplate.opsForHash().delete(key,item);
    }

    /**
     * 判断hash表中是否有该项的值
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item){
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param key 键
     * @param item 项
     * @param by 要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item,double by){
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     * @param key 键
     * @param item 项
     * @param by 要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item,double by){
        return redisTemplate.opsForHash().increment(key, item,-by);
    }

    @Bean
    public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<Object,Object> redisTemplate = new RedisTemplate<>();
        // 设置redis连接
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 使用Jackson2JsonRedisSerialize 替换默认序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 设置value的序列化规则和 key的序列化规则
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // 将redisTemplate的序列化方式更改为StringRedisSerializer
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}
