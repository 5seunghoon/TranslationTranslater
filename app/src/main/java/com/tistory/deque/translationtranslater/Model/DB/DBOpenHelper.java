package com.tistory.deque.translationtranslater.Model.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tistory.deque.translationtranslater.Model.ExcludingMember;

import java.util.ArrayList;

/**
 * Created by HELLOEARTH on 2018-04-02.
 */

public class DBOpenHelper extends SQLiteOpenHelper{
  private static DBOpenHelper dbHelper;
  public SQLiteDatabase db;

  private static final String TAG = "DBOpenHelper";
  public static final String TABLE_NAME = "WORDBOOK";
  public static final String TABLE_HISTORY = "HISTORY";

  public static final String ORIGINAL_WORD_KEY = "ORIGINAL_WORD";
  public static final String TRANSLATED_WORD_KEY = "TRANSLATED_WORD";
  public static final String ORIGINAL_PASSAGE = "ORIGINAL_PASSAGE";
  public static final String TRANSLATED_PASSAGE = "TRANSLATED_PASSAGE";

  private DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
    super(context, name, factory, version);
  }

  public static DBOpenHelper getDbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
    if(dbHelper != null) {
      Log.d(TAG, "call singletun : helper not null");
      return dbHelper;
    }
    else{
      dbHelper = new DBOpenHelper(context, name, factory, version);
      Log.d(TAG, "call singletun : helper null");
      return dbHelper;
    }
  }

  public void dbOpen(){
    db = dbHelper.getWritableDatabase();
    //dbHelper.onCreate(db);
  }

  public void dbClose(){
    db.close();
    Log.d(TAG, "database close");
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    createTable(db);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
    db.execSQL("DROP TABLE IF EXISTS "+TABLE_HISTORY);
    onCreate(db);

  }

  public void createTable(SQLiteDatabase db){
    String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
      "_ID INTEGER PRIMARY KEY AUTOINCREMENT, "+
      ORIGINAL_WORD_KEY + " TEXT" +
      ", " +
      TRANSLATED_WORD_KEY + " TEXT" +
      ")";
    Log.d(TAG, "SQL EXEC : " + sql);
    try{
      db.execSQL(sql);
      Log.d(TAG, "create db : " + TABLE_NAME);
    } catch (Exception e){
     Log.d(TAG, "create table exception : " + e.toString());
    }

    sql = "CREATE TABLE IF NOT EXISTS " + TABLE_HISTORY + "(_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ORIGINAL_PASSAGE + " TEXT, " + TRANSLATED_PASSAGE + " TEXT)";
    try{
      db.execSQL(sql);
      Log.d(TAG, "create db : " + TABLE_HISTORY);
    } catch (Exception e){
      Log.d(TAG, "create table exception : " + e.toString());
    }
  }

  public boolean insertWord(String originalWord, String translatedWord) {
    ContentValues wordValue = new ContentValues();
    wordValue.put(ORIGINAL_WORD_KEY, originalWord);
    wordValue.put(TRANSLATED_WORD_KEY, translatedWord);
    long result = db.insert(TABLE_NAME, null, wordValue);
    if (result == -1) {
      Log.d(TAG, "insert error : orig : " + originalWord + " , trans : " + translatedWord);
      return false;
    } else {
      Log.d(TAG, "insert success : orig : " + originalWord + " , trans : " + translatedWord);
      return true;
    }
  }

  public boolean insertHistory(String originalPhrase, String translatedPhrase){
    ContentValues wordValue = new ContentValues();
    wordValue.put(ORIGINAL_PASSAGE, originalPhrase);
    wordValue.put(TRANSLATED_PASSAGE, translatedPhrase);
    long result = db.insert(TABLE_HISTORY, null, wordValue);
    if(result == -1){
      Log.d(TAG, "insert error : orig : " + originalPhrase + " , trans : " + translatedPhrase);
      return false;
    }
    else{
      Log.d(TAG, "insert success : orig : " + originalPhrase + " , trans : " + translatedPhrase);
      return true;
    }
  }

  public void deleteWord(int id) {
    db.delete(TABLE_NAME, "_ID=?", new String[]{id+""});
  }

  public void updateWord(int id, String originalWord, String translatedWord) {
    ContentValues newValues = new ContentValues();
    newValues.put(ORIGINAL_WORD_KEY, originalWord);
    newValues.put(TRANSLATED_WORD_KEY, translatedWord);

    db.update(TABLE_NAME, newValues, "_ID=?", new String[]{id+""});
  }

  public ArrayList<HistoryItem> getHistory(){
    int tableIndex = 1;
    ArrayList<HistoryItem> HistoryList = new ArrayList<HistoryItem>();

    String sql = "SELECT * FROM " + dbHelper.TABLE_HISTORY + ";";
    Cursor results = null;
    results =dbHelper.db.rawQuery(sql, null);
    results.moveToFirst();
    while(!results.isAfterLast()) {
      int id = results.getInt(0);
      String origin = results.getString(1);
      String translated = results.getString(2);

      HistoryItem newEntry = new HistoryItem(origin,translated);
      tableIndex++;

      HistoryList.add(newEntry);
      results.moveToNext();
    }
    results.close();
    return HistoryList;
  }
  public ArrayList<ExcludingMember> getWords() {
    int tableIndex = 1;
    ArrayList<ExcludingMember> wordBook = new ArrayList<ExcludingMember>();

    String sql = "SELECT * FROM " + dbHelper.TABLE_NAME + ";";
    Cursor results = null;
    results = dbHelper.db.rawQuery(sql, null);
    results.moveToFirst();
    while(!results.isAfterLast()){
      int id = results.getInt(0);
      String origin = results.getString(1);
      String value = results.getString(2);

      ExcludingMember temp = new ExcludingMember(id, ExcludingMember.intTo4digitString(tableIndex), origin, value);
      tableIndex++;

      wordBook.add(temp);
      results.moveToNext();
    }
    results.close();
    return wordBook;
  }
}
