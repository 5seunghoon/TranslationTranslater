package com.tistory.deque.translationtranslater;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

  EditText inputEditText;
  Button translateButton;
  EditText translateTextView;
  String originalString;
  InputMethodManager imm;

  String AppName;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    inputEditText = findViewById(R.id.inputEditText);
    translateButton = findViewById(R.id.translateButton);
    translateTextView = findViewById(R.id.translateTextView);

    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    Intent onIntent = getIntent();
    sharedToMe(onIntent);

    inputEditText.clearFocus(); //when create activity, we must hide keyboard
  }

  public void sharedToMe(Intent intent){
    /**
     * If user click share on other app, android will show this app.
     * And if user click this app, this app will set [original text] to [user's picked text].
     */
    String onAction = intent.getAction();
    String onType = intent.getType();
    if (Intent.ACTION_SEND.equals(onAction) && onType != null) {
      if ("text/plain".equals(onType)) {
        inputEditText.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
      }
    }
  }
  public void clickTranslateButton(View view){
    originalString = inputEditText.getText().toString();
    try{
      ConnectivityManager connectivityManager =
        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
      if(networkInfo != null && networkInfo.isConnected()){ //check Internet connection
        new TranslateAsyncTask(translateTextView, "en", "ko").execute(originalString);
        imm.hideSoftInputFromWindow(inputEditText.getWindowToken(), 0);//if click button, keyboard will hide.

        //Android is not connect network main thread
        //So call TranslateAsyncTask which extends AsyncTask. It execute new thread for connecting network.
      }
      else{
        Toast.makeText(getApplicationContext(), "NETWORK IS NOT CONNECTED", Toast.LENGTH_LONG).show();
      }
    }
    catch (Exception e){
      Log.d("MAIN_ACTIVITY", e.toString());
    }
  }

  public void clickShareButton(View view){
    /**
     * if click share button and share to kakaotalk, it will send <<Translated Text>> massage.
     */
    String translatedString = translateTextView.getText().toString();
    Intent shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_name));
    shareIntent.putExtra(Intent.EXTRA_TEXT,translatedString + "\n - translated by " + getString(R.string.app_name));
    startActivity(Intent.createChooser(shareIntent, "번역 결과 공유하기"));
  }
}
