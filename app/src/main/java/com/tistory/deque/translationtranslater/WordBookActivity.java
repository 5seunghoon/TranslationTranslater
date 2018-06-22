package com.tistory.deque.translationtranslater;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class WordBookActivity extends AppCompatActivity {
  private DBOpenHelper dbHelper;
  private final int dbVersion = 1;

  ListView wordList;
  EditText wordKey;
  EditText wordValue;
  Button addButton;
  Button deleteButton;
  Button editButton;
  Button addModeButton;

  ArrayList<ExcludingMember> WordBookList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_word_book);

    wordBookDBOpen();

    addButton = findViewById(R.id.add_btn);
    addModeButton = findViewById(R.id.add_mode_btn);
    deleteButton = findViewById(R.id.delete_btn);
    editButton = findViewById(R.id.edit_btn);
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
        clearInput();
      }
    });

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

  private void addWordBook(String origin, String translated) {
    dbHelper.insertWord(origin, translated);
    updateView();
  }
  private void toggleMode(boolean editMode) {
    Button addBtn = findViewById(R.id.add_btn);
    Button editBtn = findViewById(R.id.edit_btn);
    Button deleteBtn = findViewById(R.id.delete_btn);
    Button addModeBtn = findViewById(R.id.add_mode_btn);

    if (editMode) {
      addBtn.setVisibility(View.GONE);
      editBtn.setVisibility(View.VISIBLE);
      deleteBtn.setVisibility(View.VISIBLE);
      addModeBtn.setVisibility(View.VISIBLE);
    } else {
      addBtn.setVisibility(View.VISIBLE);
      editBtn.setVisibility(View.GONE);
      deleteBtn.setVisibility(View.GONE);
      addModeBtn.setVisibility(View.GONE);
    }
  }

  public void clearInput() {
    wordKey.setText("");
    wordValue.setText("");
  }
    wordList.setAdapter(adapter);
  }
}
