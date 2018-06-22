package com.tistory.deque.translationtranslater.Controler;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.tistory.deque.translationtranslater.DB.DBOpenHelper;
import com.tistory.deque.translationtranslater.Model.ExcludingMember;

import java.util.ArrayList;

/**
 * Created by HELLOEARTH on 2018-04-03.
 */

public class ExcludeStringTranslate {
  private DBOpenHelper dbHelper;
  private Context context;
  private TextView translatedTextView;
  private String originalText;
  private String translatedText;

  private ArrayList<ExcludingMember> excludingTable;
  private int tableIndex;


  private String sourceLang = "en";
  private String targetLang = "ko";

  private String tag = "stringExclusionTranslateClass";

  ExcludeStringTranslate(Context context, DBOpenHelper dbHelpler, TextView translatedTextView) {
    this.context = context;
    this.dbHelper = dbHelpler;
    this.translatedTextView = translatedTextView;
    excludingTable = new ArrayList<ExcludingMember>();
  }

  public void setOriginalString(String originalText) {
    this.originalText = originalText;
  }

  public void callbackEndTranslated(String translatedText) {
    this.translatedText = translatedText;
    unHashingText();
    translatedTextView.setText(this.translatedText);
  }

  private void doHashingText() {
    tableIndex = 1;
    String sql = "SELECT * FROM " + dbHelper.TABLE_NAME + ";";
    Cursor results = null;
    results = dbHelper.db.rawQuery(sql, null);
    Log.d(tag, "Cursor open");
    results.moveToFirst();
    while (!results.isAfterLast()) {
      int id = results.getInt(0);
      String origin = results.getString(1);
      Log.d(tag, "get1 : " + origin);
      String value = results.getString(2);
      Log.d(tag, "get2 : " + value);

      //if origianalText has match text with origin of database, replace that...
      originalText = originalText.replaceAll("(?i)" + origin, ExcludingMember.intTo4digitString(tableIndex));
      excludingTable.add(new ExcludingMember(ExcludingMember.intTo4digitString(tableIndex), origin, value));
      Log.d(tag, "KEY : " + ExcludingMember.intTo4digitString(tableIndex) + ", ORIGIN : " + origin + ", VALUE : " + value);
      Log.d(tag, "ORIGINAL TEXT : " + originalText);
      tableIndex++;

      results.moveToNext();
    }
    results.close();
    Log.d(tag, "Cursor close");
  }

  private void unHashingText() {
    Log.d(tag, "do unHasingText");
    for(ExcludingMember excludingMember : excludingTable){
      String excludingMemberKey = excludingMember.getKey();
      translatedText = translatedText.replaceAll("" + excludingMemberKey, excludingMember.getValue());
    }
    Log.d(tag, "end unHashingText");
  }

  public void translate() {
    doHashingText();
    try {
      ConnectivityManager connectivityManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
      if (networkInfo != null && networkInfo.isConnected()) { //check Internet connection
        new TranslateAsyncTask(this, sourceLang, targetLang).execute(originalText);
        //Android is not connect network main thread
        //So call TranslateAsyncTask which extends AsyncTask. It execute new thread for connecting network.
      } else {
        Toast.makeText(context, "NETWORK IS NOT CONNECTED", Toast.LENGTH_LONG).show();
      }
    } catch (Exception e) {
      Log.d(tag, "translating exception : " + e.toString());
    }
  }
}