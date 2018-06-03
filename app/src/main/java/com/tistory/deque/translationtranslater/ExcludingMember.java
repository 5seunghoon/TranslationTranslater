package com.tistory.deque.translationtranslater;

/**
 * Created by hyunmoahn on 2018. 6. 3..
 */

public class ExcludingMember {
  private int id;
  private String key;
  private String origin;
  private String value;
  private final static String PREDIX = "1234";
  public static String intTo4digitString(int value){
    if(value <= 9) {
      return PREDIX + "000" + String.valueOf(value);
    }
    else if(value <= 99){
      return PREDIX + "00" + String.valueOf(value);
    }
    else if(value <= 999){
      return PREDIX + "0" + String.valueOf(value);
    }
    else{
      return String.valueOf(value);
    }
  }

  public ExcludingMember(int id, String key, String origin, String value){
    //ex) "0001", "PYTHON", "파이썬"
    this.id = id;
    this.key = key;
    this.origin = origin;
    this.value = value;
  }

  public ExcludingMember(String key, String origin, String value){
    //ex) "0001", "PYTHON", "파이썬"
    this.key = key;
    this.origin = origin;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
