package com.tistory.deque.translationtranslater;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class WordBookActivity extends AppCompatActivity {

  private DBOpenHelper dbHelper;
  private final int dbVersion = 1;

  EditText wordKey;
  EditText wordValue;
  Button addButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_word_book);

    addButton = findViewById(R.id.add_btn);
    wordKey = findViewById(R.id.word_key);
    wordValue = findViewById(R.id.word_value);

    addButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        String key = wordKey.getText().toString();
        String value = wordKey.getText().toString();

        addWordBook(key, value);

        wordKey.setText("");
        wordValue.setText("");
      }
    });

    wordBookDBOpen();
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
  }
}
