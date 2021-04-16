package com.example.technologyforum.web.service.Impl;

import com.example.technologyforum.cache.BasePrefix;
import com.example.technologyforum.cache.CollectionKey;
import com.example.technologyforum.util.RedisUtil;
import com.example.technologyforum.web.dto.HotMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RedisService {

  private final RedisUtil redisUtil;

  public RedisService(RedisUtil redisUtil) {
    this.redisUtil = redisUtil;
  }

  /**
   * 设置热度  废弃
   * @param id
   * @param key
   * @param type  1：被浏览 2： 被评论 3：被收藏
   */
  @Deprecated
  public void setHot(Integer id, BasePrefix key, String type) {
    Optional optional = Optional.ofNullable(redisUtil.get(key, "article-id:" + id + ":num"));
    if (optional.isPresent()) {
      int value = 1;
      if("2".equals(type)){
        value = 3;
      }
      if("3".equals(type)){
        value = 5;
      }
      redisUtil.increment(key, "article-id:" + id + ":num", value);
    } else {
      redisUtil.set(key, "article-id:" + id + ":num", 1);
    }
  }

  /**
   * 设置热度
   * @param id
   * @param scoreType 1：被浏览 2： 被评论 3：被收藏
   * @param type 攻略 问答 等
   * @param
   */
  public void addHot(Integer id,String scoreType,String type){
    Long value = redisUtil.getHotScore("article-id:"+id+":num",type);
    if(value == null){
       redisUtil.sortSetAdd(type,"article-id:" + id + ":num",1);
    }else{
        if(scoreType.equals("1")){
          redisUtil.sortSetZincrby(type,"article-id:" + id + ":num",1);
        }else if(scoreType.equals("2")){
          redisUtil.sortSetZincrby(type,"article-id:" + id + ":num",3);
        }else{
          redisUtil.sortSetZincrby(type,"article-id:" + id + ":num",5);
        }
    }
  }

  /**
   * 问答排名
   * @param id
   * @param type
   */
  public void addQuestionCommentNum(Integer id,String type){
    Long value = redisUtil.getHotScore("article-id:"+id+":num",type);
    if(value == null){
      redisUtil.sortSetAdd(type,"article-id:" + id + ":num",1);
    }else{
      redisUtil.sortSetZincrby(type,"article-id:" + id + ":num",1);
    }
  }

  /**
   * 去除热度值
   * @param id
   * @param type
   * @return
   */
  public boolean delHot(int id,String type){
    Long value = redisUtil.getHotScore("article-id:"+id+":num",type);
    if(value == null){
      return true;
    }else{
      return redisUtil.removeSetRang(type,"article-id:"+id+":num");
    }
  }
  /**
   * //获取前十的对应id
   * @param type
   * @return
   */
  public List<Integer> getTopNum(String type){
    Set<String> keys = redisUtil.sortSetRange(type,0,9);
    List<Integer> list = new ArrayList<>();
    if(keys.size() == 0){
      return null;
    }else {
      keys.forEach(key->{
        int last = key.lastIndexOf(":");
        String substring = key.substring(0, last);
        int secondLast = substring.lastIndexOf(":");
        String viewNumStr = substring.substring(secondLast + 1);
        list.add(Integer.valueOf(viewNumStr));
      });
    }
    return  list;
  }
  /**
   * 获取指定元素的值获取热度值
   */
  public Double getScore(String type, Integer id) {
    String key = "article-id:"+id+":num";
    return redisUtil.getScore(type,key);
  }
  /**
   * 设置收藏数目
   * @param id
   * @param key
   */
  public void setCollectNum(Integer id, BasePrefix key){
    Optional optional = Optional.ofNullable(redisUtil.get(key, "article-id:" + id + ":num"));
    if (optional.isPresent()) {
      redisUtil.increment(key, "article-id:" + id + ":num", 1);
    } else {
      redisUtil.set(key, "article-id:" + id + ":num", 1);
    }
  }

  /**
   * 减少收藏/评论数目
   * @param id
   * @param key
   */
  public void reCollectOrCommentNum(Integer id, BasePrefix key){
    Optional optional = Optional.ofNullable(redisUtil.get(key, "article-id:" + id + ":num"));
    if (optional.isPresent()) {
      redisUtil.increment(key, "article-id:" + id + ":num", -1);
    }
  }

  /**
   * 设置评论数目
   * @param id
   * @param key
   */
  public void setCommentNum(Integer id, BasePrefix key){
    Optional optional = Optional.ofNullable(redisUtil.get(key, "article-id:" + id + ":num"));
    if (optional.isPresent()) {
      redisUtil.increment(key, "article-id:" + id + ":num", 1);
    } else {
      redisUtil.set(key, "article-id:" + id + ":num", 1);
    }
  }

  /**
   * 根据view key获取Num值
   *
   * @param id
   * @param key
   * @return
   */
  public Number getViewNum(Integer id, BasePrefix key) {
    return redisUtil.get(key, "article-id:" + id + ":num");
  }

  /**
   * 根据view key进行删除
   *
   * @param id
   * @param key
   */
  public void deleteViewKey(Integer id, BasePrefix key) {
    redisUtil.delete(key, "article-id:" + id + ":num");
  }

  /**
   * 获取收藏键是否存在bool值
   *
   * @param type
   * @param articleId
   * @param userId
   * @return
   */
  public boolean getCollectionState(String type, Integer articleId, Integer userId) {
    Optional<Object> optional;
    if ("recommend".equals(type)) {
      optional =
          Optional.ofNullable(
              redisUtil.get(
                  CollectionKey.RECOMMEND_KEY_HOT, "user-id:" + userId + ":article-id:" + articleId));
    } else if ("essay".equals(type)) {
      optional =
          Optional.ofNullable(
              redisUtil.get(
                  CollectionKey.ESSAY_KEY_HOT, "user-id:" + userId + ":article-id:" + articleId));
    } else {
      optional =
          Optional.ofNullable(
              redisUtil.get(
                  CollectionKey.QUESTION_KEY_HOT, "user-id:" + userId + ":article-id:" + articleId));
    }
    if (optional.isPresent()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 设置收藏状态  // TODO 收藏状态暂时不存入redis
   *
   * @param type
   * @param articleId
   * @param userId
   */
  public void setCollectionState(String type, Integer articleId, Integer userId) {
    if ("recommend".equals(type)) {
      redisUtil.set(
          CollectionKey.RECOMMEND_KEY_HOT, "user-id:" + userId + ":article-id:" + articleId, articleId);
    } else if ("essay".equals(type)) {
      redisUtil.set(
          CollectionKey.ESSAY_KEY_HOT, "user-id:" + userId + ":article-id:" + articleId, articleId);
    } else {
      redisUtil.set(
          CollectionKey.QUESTION_KEY_HOT, "user-id:" + userId + ":article-id:" + articleId, articleId);
    }
  }

  public void delCollectionState(String type, Integer articleId, Integer userId) {
    if ("recommend".equals(type)) {
      redisUtil.delete(
          CollectionKey.RECOMMEND_KEY_HOT, "user-id:" + userId + ":article-id:" + articleId);
    } else if ("essay".equals(type)) {
      redisUtil.delete(CollectionKey.ESSAY_KEY_HOT, "user-id:" + userId + ":article-id:" + articleId);
    } else {
      redisUtil.delete(
          CollectionKey.QUESTION_KEY_HOT, "user-id:" + userId + ":article-id:" + articleId);
    }
  }

  /**
   * 根据用户编号获取分好类的文章编号
   *
   * @param userId
   * @return
   */
  public Map<String, List<Integer>> getCollectionArticlesId(Integer userId) {
    Map<String, List<Integer>> map = Maps.newHashMap();
    Set<String> keys = redisUtil.keys("CollectionKey:type:*:user-id:" + userId + ":article-id:*");
    List<Integer> essayIdList = Lists.newArrayList();
    List<Integer> questionIdList = Lists.newArrayList();
    List<Integer> recommendIdList = Lists.newArrayList();
    keys.forEach(
        key -> {
          if (key.contains("essay")) {
            int resultBefore = key.lastIndexOf(":");
            String idStr = key.substring(resultBefore + 1);
            essayIdList.add(Integer.valueOf(idStr));
          } else if (key.contains("question")) {
            int resultBefore = key.lastIndexOf(":");
            String idStr = key.substring(resultBefore + 1);
            questionIdList.add(Integer.valueOf(idStr));
          } else {
            int resultBefore = key.lastIndexOf(":");
            String idStr = key.substring(resultBefore + 1);
            recommendIdList.add(Integer.valueOf(idStr));
          }
        });
    map.put("essay", essayIdList);
    map.put("question", questionIdList);
    map.put("recommend", recommendIdList);
    return map;
  }

  /**
   * 根据redis推荐查询最热
   *
   * @return
   */
  public List<HotMap> Hot(String type) {
    Set<String> keys = redisUtil.keys("ViewKey:type:" + type + "*");
    ArrayList<HotMap> results = Lists.newArrayList();
    keys.forEach(
        key -> {
          int last = key.lastIndexOf(":");
          String substring = key.substring(0, last);
          int secondLast = substring.lastIndexOf(":");
          String viewNumStr = substring.substring(secondLast + 1);
          Number number = redisUtil.get(key);
          HotMap hotMap = new HotMap();
          hotMap.setId(Integer.valueOf(viewNumStr));
          hotMap.setViewNum(number.intValue());
          results.add(hotMap);
        });
    results.sort((r1, r2) -> r2.getViewNum() - r1.getViewNum());
    return results;
  }
}
