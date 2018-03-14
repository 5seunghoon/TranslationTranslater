package com.tistory.deque.developertranslater;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

  EditText inputEditText;
  Button translateButton;
  TextView translateTextView;
  String originalString;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    inputEditText = findViewById(R.id.inputEditText);
    translateButton = findViewById(R.id.translateButton);
    translateTextView = findViewById(R.id.translateTextView);

    inputEditText.clearFocus(); //앱을 켰을때 텍스트편집뷰에 포커스가 가면 화면을 가린다. 따라서 포커스를 제거
  }

  public void clickTranslateButton(View view){
    String originalString = inputEditText.getText().toString();
    try{
      ConnectivityManager connectivityManager =
        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
      if(networkInfo != null && networkInfo.isConnected()){ //인터넷에 연결 가능한지 체크
        new TranslateAsyncTask(translateTextView).execute(originalString);
        //안드로이드는 메인 스레드에서 네트워크에 접근할 수 없다.
        //따라서 AsyncTask를 상속받은 TranslateAsyncTask클래스의 스레드를 호출
      }
      else{
        Toast.makeText(getApplicationContext(), "NETWORK IS NOT CONNECTED", Toast.LENGTH_LONG).show();
      }
    }
    catch (Exception e){
      Log.d("MAIN_ACTIVITY", e.toString());
    }
  }
}
