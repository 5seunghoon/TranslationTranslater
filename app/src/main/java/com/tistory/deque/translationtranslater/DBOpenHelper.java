package com.tistory.deque.translationtranslater;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by HELLOEARTH on 2018-04-02.
 */

public class DBOpenHelper extends SQLiteOpenHelper{
  private static DBOpenHelper dbHelper;
  public SQLiteDatabase db;

  private static final String tag = "DBOpenHelper";
  public static final String TABLE_NAME = "WORDBOOK";
  
  public static final String ORIGINAL_WORD_KEY = "ORIGINAL_WORD";
  public static final String TRANSLATED_WORD_KEY = "TRANSLATED_WORD";

  private DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
    super(context, name, factory, version);
  }

  public static DBOpenHelper getDbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
    if(dbHelper != null) {
      Log.d(tag, "call singletun : helper not null");
      return dbHelper;
    }
    else{
      dbHelper = new DBOpenHelper(context, name, factory, version);
      Log.d(tag, "call singletun : helper null");
      return dbHelper;
    }
  }

  public void dbOpen(){
    db = dbHelper.getWritableDatabase();
    //dbHelper.onCreate(db);
  }

  public void dbClose(){
    db.close();
    Log.d(tag, "database close");
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    createTable(db);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
    onCreate(db);
  }

  public void createTable(SQLiteDatabase db){
    String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
      "_ID INTEGER PRIMARY KEY AUTOINCREMENT, "+
      ORIGINAL_WORD_KEY + " TEXT" +
      ", " +
      TRANSLATED_WORD_KEY + " TEXT" +
      ")";
    Log.d(tag, "SQL EXEC : " + sql);
    try{
      db.execSQL(sql);
      Log.d(tag, "create db : " + TABLE_NAME);
    } catch (Exception e){
     Log.d(tag, "create table exception : " + e.toString());
    }
  }

  public boolean insertWord(String originalWord, String translatedWord){
    ContentValues wordValue = new ContentValues();
    wordValue.put(ORIGINAL_WORD_KEY, originalWord);
    wordValue.put(TRANSLATED_WORD_KEY, translatedWord);
    long result = db.insert(TABLE_NAME, null, wordValue);
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
