package com.tistory.deque.developertranslater;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

  EditText inputEditText;
  Button translateButton;
  TextView translateTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    inputEditText = findViewById(R.id.inputEditText);
    translateButton = findViewById(R.id.translateButton);
    translateTextView = findViewById(R.id.translateTextView);

    inputEditText.clearFocus(); //앱을 켰을때 텍스트편집뷰에 포커스가 가면 화면을 가린다. 따라서 포커스를 제거
  }
}
