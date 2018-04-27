package com.tistory.deque.translationtranslater;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by HELLOEARTH on 2018-04-03.
 */
class ExcludingMember {
  private String key;
  private String origin;
  private String value;

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
}
public class ExcludeStringTranslate {
  private DBOpenHelper dbHelper;
  private Context context;
  private TextView translatedTextView;
  private String originalText;
  private String translatedText;

  private final static String PREDIX = "1234";

  private ArrayList<ExcludingMember> excludingTable;
  private int tableIndex;


  private String sourceLang = "en";
  private String targetLang = "ko";

  private String tag = "stringExclusionTranslateClass";

  public ExcludeStringTranslate(Context context, DBOpenHelper dbHelpler, TextView translatedTextView){
    this.context = context;
    this.dbHelper = dbHelpler;
    this.translatedTextView = translatedTextView;
    excludingTable = new ArrayList<ExcludingMember>();
  }

  public void setOriginalString(String originalText){
    this.originalText = originalText;
  }

  public void callbackEndTranslated(String translatedText){
    this.translatedText = translatedText;
    unHashingText();
    translatedTextView.setText(this.translatedText);
    return;
  }

  private String intTo4digitString(int value){
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

  public void doHashingText(){
      tableIndex = 1;
      String sql = "SELECT * FROM " + dbHelper.TABLE_NAME + ";";
      Cursor results = null;
      results = dbHelper.db.rawQuery(sql, null);
      Log.d(tag, "Cursor open");
      results.moveToFirst();
      while(!results.isAfterLast()){
        int id = results.getInt(0);
        String origin = results.getString(1);
        Log.d(tag, "get1 : " + origin);
        String value = results.getString(2);
        Log.d(tag, "get2 : " + value);

        //if origianalText has match text with origin of database, replace that...
        originalText = originalText.replaceAll("(?i)" + origin, intTo4digitString(tableIndex));
        excludingTable.add(new ExcludingMember(intTo4digitString(tableIndex), origin, value));
        Log.d(tag, "KEY : " + intTo4digitString(tableIndex) + ", ORIGIN : " + origin + ", VALUE : " + value);
        Log.d(tag, "ORIGINAL TEXT : " + originalText);
        tableIndex++;

        results.moveToNext();
    }
    results.close();
    Log.d(tag, "Cursor close");
  }
  public void unHashingText(){
    return;
  }

  public void translate(){
    doHashingText();
    try{
      ConnectivityManager connectivityManager =
        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
      if(networkInfo != null && networkInfo.isConnected()){ //check Internet connection
        new TranslateAsyncTask(this, sourceLang, targetLang).execute(originalText);
        //Android is not connect network main thread
        //So call TranslateAsyncTask which extends AsyncTask. It execute new thread for connecting network.
      }
      else{
        Toast.makeText(context, "NETWORK IS NOT CONNECTED", Toast.LENGTH_LONG).show();
      }
    }
    catch (Exception e){
      Log.d(tag, "translating exception : " + e.toString());
    }
  }
}