package com.tistory.deque.translationtranslater;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  private static final int PICK_FROM_ALBUM = 100;
  private static final int PICK_FROM_CAMERA = 101;
  private static final int CROP_FROM_CAMERA = 102;
  private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
  private static final int MULTIPLE_PERMISSIONS = 200; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수
  Uri photoUri;

  EditText inputEditText;
  Button translateButton;
  EditText translateTextView;
  String originalString;
  InputMethodManager imm;

  String tag = "mainActivityTAG";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    checkPermissions();

    inputEditText = findViewById(R.id.inputEditText);
    translateButton = findViewById(R.id.translateButton);
    translateTextView = findViewById(R.id.translateTextView);

    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    Intent onIntent = getIntent();
    sharedToMe(onIntent);

    inputEditText.clearFocus(); //when create activity, we must hide keyboard
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
    switch (requestCode) {
      case MULTIPLE_PERMISSIONS: {
        if (grantResults.length > 0) {
          for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(this.permissions[0])) {
              if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                showNoPermissionToastAndFinish();
              }
            } else if (permissions[i].equals(this.permissions[1])) {
              if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                showNoPermissionToastAndFinish();

              }
            } else if (permissions[i].equals(this.permissions[2])) {
              if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                showNoPermissionToastAndFinish();

              }
            }
          }
        } else {
          showNoPermissionToastAndFinish();
        }
        return;
      }
    }
  }

  private void showNoPermissionToastAndFinish() {
    Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
    finish();
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
      Log.d(tag, e.toString());
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

  public void clickCameraButton(View view) {
    Intent ocrTaskActivityIntent = new Intent(getApplicationContext(), ocrTaskActivity.class);
    ocrTaskActivityIntent.putExtra("CASE", "CAMERA");
    startActivity(ocrTaskActivityIntent);
  }
  public void clickGalleryButton(View view){
    Intent ocrTaskActivityIntent = new Intent(getApplicationContext(), ocrTaskActivity.class);
    ocrTaskActivityIntent.putExtra("CASE", "ALBUM");
    startActivity(ocrTaskActivityIntent);
  }
  private boolean checkPermissions() {
    Log.d(tag, "check permissions func in");
    int result;
    List<String> permissionList = new ArrayList<>();
    for (String pm : permissions) {
      result = ContextCompat.checkSelfPermission(this, pm);
      if (result != PackageManager.PERMISSION_GRANTED) { //사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
        permissionList.add(pm);
      }
    }
    if (!permissionList.isEmpty()) { //권한이 추가되었으면 해당 리스트가 empty가 아니므로 request 즉 권한을 요청합니다.
      ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
      return false;
    }
    return true;
  }

}
