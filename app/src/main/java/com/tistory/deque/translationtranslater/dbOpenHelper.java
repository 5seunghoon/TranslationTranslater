package com.tistory.deque.translationtranslater;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by HELLOEARTH on 2018-04-02.
 */

public class dbOpenHelper extends SQLiteOpenHelper{
  private static dbOpenHelper helper;

  private static final String tag = "dbOpenHelper";
  public static final String TABLE_NAME = "wordbook";
  
  public static final String ORIGINAL_WORD_KEY = "ORIGINAL_WORD";
  public static final String TRANSLATED_WORD_KEY = "TRANSLATED_WORD";

  private dbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
    super(context, name, factory, version);
  }

  public static dbOpenHelper getDbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
    if(helper != null) return helper;
    else{
      helper = new dbOpenHelper(context, name, factory, version);
      return helper;
    }
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    createTable(db);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }

  public void createTable(SQLiteDatabase db){
    String sql = "CREATE TABLE " + TABLE_NAME + "(name text)";
    try{
      db.execSQL(sql);
      Log.d(tag, "create db : " + TABLE_NAME);
    } catch (Exception e){
     Log.d(tag, "create table exception : " + e.toString());
    }
  }

  public boolean insertWord(SQLiteDatabase db, String originalWord, String translatedWord){
    ContentValues wordValue = new ContentValues();
    wordValue.put(ORIGINAL_WORD_KEY, originalWord);
    wordValue.put(TRANSLATED_WORD_KEY, translatedWord);
    db.beginTransaction();
    long result = db.insert(TABLE_NAME, null, wordValue);
    db.endTransaction();
    if(result == -1){
      Log.d(tag, "insert error : orig : " + originalWord + " , trans : " + translatedWord);
      return false;
    }
    else{
      Log.d(tag, "insert success : orig : " + originalWord + " , trans : " + translatedWord);
      return true;
    }
  }
}
