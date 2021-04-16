package com.example.technologyforum.cache;

public class CollectionKey extends BasePrefix{

	protected CollectionKey(String prefix) {
		super(prefix);
	}

	protected CollectionKey(String prefix, int seconds) {
		super(prefix, seconds);
	}

  public static final CollectionKey ESSAY_KEY_HOT = new CollectionKey("type:strahot");
  public static final CollectionKey QUESTION_KEY_HOT = new CollectionKey("type:questionhot");
  public static final CollectionKey RECOMMEND_KEY_HOT = new CollectionKey("type:topichot");
  // 评论数目
  public static final CollectionKey ESSAY_KEY_COM_NUM = new CollectionKey("type:stracom");
  public static final CollectionKey QUESTION_KEY_COM_NUM = new CollectionKey("type:questioncom");
  public static final CollectionKey TOPIC_KEY_COM_NUM = new CollectionKey("type:topiccom");

  //收藏数目
  public static final CollectionKey ESSAY_KEY_COL_NUM = new CollectionKey("type:stracol");
  public static final CollectionKey QUESTION_KEY_COL_NUM = new CollectionKey("type:questioncol");
  public static final CollectionKey TOPIC_KEY_COL_NUM = new CollectionKey("type:topiccol");

  }
