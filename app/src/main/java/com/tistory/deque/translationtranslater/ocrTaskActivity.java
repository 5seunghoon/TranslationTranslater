package com.tistory.deque.translationtranslater;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ocrTaskActivity extends AppCompatActivity {

  private ImageView imageView;
  private TextView textView;
  private static Button okButton, cancleButton;

  private Toast backToast;
  private long backPressedTime;
  private String loadingText = "";

  private Uri resultImageURI;
  private static String tag = "ocrTaskActivityTAG";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ocr_task);
    Log.d(tag, "start Activity");

    imageView = findViewById(R.id.imageView);
    textView = findViewById(R.id.editText);
    okButton = findViewById(R.id.okButton);
    cancleButton = findViewById(R.id.cancleButton);
    okButton.setVisibility(View.INVISIBLE);
    cancleButton.setVisibility(View.INVISIBLE);

    setTitle(R.string.ocrTitle);

    ocr();
  }

  @Override
  public void onBackPressed(){
    if (System.currentTimeMillis() - backPressedTime < 2000) {
      backToast.cancel();
      finish();
    } else {
      backPressedTime = System.currentTimeMillis();
      backToast = Toast.makeText(getApplicationContext(), "\'뒤로\'버튼을 한번 더 누르시면 문자 감식이 취소됩니다.", Toast.LENGTH_LONG);
      backToast.show();
    }
  }
  private void ocr(){
    textView.setText(loadingText);
    Log.d(tag, "getBase64Encoded func");
    Intent intent = getIntent();
    resultImageURI = (Uri) intent.getExtras().get("IMAGE_URI");
    Log.d(tag, "image uri : " + resultImageURI);
    imageView.setImageURI(resultImageURI);

    ocrTask _ocrTask = new ocrTask(getApplicationContext(), okButton, cancleButton);
    Log.d(tag, "ocrTask success make");

    _ocrTask.setImageURI(resultImageURI);
    _ocrTask.setTextView(textView);
    _ocrTask.setImageView(imageView);
    _ocrTask.RUN();
  }
  public void okButtonClk(View view){
    Intent resultIntent = new Intent();
    resultIntent.putExtra("OCR_STRING", textView.getText().toString());
    setResult(RESULT_OK, resultIntent);
    finish();
  }
  public void cancleButtonClk(View veiw){
    Intent resultIntent = new Intent();
    resultIntent.putExtra("OCR_STRING", "");
    setResult(RESULT_CANCELED, resultIntent);
    finish();
  }

}
