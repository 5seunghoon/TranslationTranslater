package com.tistory.deque.translationtranslater.Activity;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.tistory.deque.translationtranslater.Model.DB.*;
import com.tistory.deque.translationtranslater.Model.ExcludingMember;
import com.tistory.deque.translationtranslater.R;
import com.tistory.deque.translationtranslater.Activity.Adapter.*;

import java.util.ArrayList;

public class WordBookActivity extends AppCompatActivity {
  private DBOpenHelper dbHelper;
  private final int dbVersion = 1;

  ExcludingMember target;
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
    toggleMode(false);

    wordBookDBOpen();

    addButton = findViewById(R.id.add_btn);
    addModeButton = findViewById(R.id.add_mode_btn);
    deleteButton = findViewById(R.id.delete_btn);
    editButton = findViewById(R.id.edit_btn);
    wordKey = findViewById(R.id.word_key);
    wordValue = findViewById(R.id.word_value);

    wordList = (ListView) findViewById(R.id.word_list);

    setTitle("단어장 편집");

    updateView();

    addButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        String key = wordKey.getText().toString();
        String value = wordValue.getText().toString();
        if (key.length() == 0 || value.length() == 0) {
          return;
        }
        addWordBook(key, value);
        clearInput();
        Snackbar.make(v, "단어 추가 완료", Snackbar.LENGTH_LONG).show();
      }
    });

    editButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String origin = wordKey.getText().toString();
        String translated = wordValue.getText().toString();

        updateWordBook(target.getId(), origin, translated);
        clearInput();
        updateView();
        toggleMode(false);
        Snackbar.make(view, "단어 편집 완료", Snackbar.LENGTH_LONG).show();
      }
    });

    deleteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        deleteWordBook(target.getId());
        clearInput();
        toggleMode(false);
        Snackbar.make(view, "단어 삭제 완료", Snackbar.LENGTH_LONG).show();
      }
    });

    addModeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        clearInput();
        toggleMode(false);
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

  public void deleteWordBook(int id) {
    dbHelper.deleteWord(id);
    updateView();
  }

  public void updateWordBook(int id, String origin, String translated) {
    dbHelper.updateWord(id, origin, translated);
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
      editBtn.setVisibility(View.INVISIBLE);
      deleteBtn.setVisibility(View.INVISIBLE);
      addModeBtn.setVisibility(View.GONE);
    }
  }

  public void clearInput() {
    wordKey.setText("");
    wordValue.setText("");
  }

  private void updateView() {
    WordBookList = dbHelper.getWords();
    WordBookAdapter adapter = new WordBookAdapter(this, WordBookList);
    wordList.setAdapter(adapter);
    wordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
        toggleMode(true);
        target = WordBookList.get(index);

        wordKey.setText(target.getOrigin());
        wordValue.setText(target.getValue());
      }
    });
  }
}
