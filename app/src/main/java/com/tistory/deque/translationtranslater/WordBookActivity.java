package com.tistory.deque.translationtranslater;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;

public class WordBookActivity extends AppCompatActivity {

  private DBOpenHelper dbHelper;
  private final int dbVersion = 1;

  ListView wordList;
  EditText wordKey;
  EditText wordValue;
  Button addButton;

  ArrayList<ExcludingMember> WordBookList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_word_book);

    wordBookDBOpen();

    addButton = findViewById(R.id.add_btn);
    wordKey = findViewById(R.id.word_key);
    wordValue = findViewById(R.id.word_value);

    wordList = (ListView)findViewById(R.id.word_list);
    WordBookList = dbHelper.getWords();
    WordBookAdapter adapter = new WordBookAdapter(this, WordBookList);
    wordList.setAdapter(adapter);

    addButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        String key = wordKey.getText().toString();
        String value = wordValue.getText().toString();

        addWordBook(key, value);

        wordKey.setText("");
        wordValue.setText("");
      }
    });

    dbHelper.getWords();
  }

  private void wordBookDBOpen() {
    dbHelper = DBOpenHelper.getDbOpenHelper(
      getApplicationContext(),
      DBOpenHelper.TABLE_NAME,
      null, dbVersion
    );
    dbHelper.dbOpen();
  }

  private void addWordBook(String key, String value) {
    dbHelper.insertWord(key, value);
    WordBookAdapter adapter = new WordBookAdapter(this, dbHelper.getWords());
    wordList.setAdapter(adapter);
  }
}
