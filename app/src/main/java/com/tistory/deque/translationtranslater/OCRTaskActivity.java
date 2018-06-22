package com.tistory.deque.translationtranslater;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class OCRTaskActivity extends AppCompatActivity {

  private ImageView imageView;
  private TextView OCRTextView;
  private static Button okButton, cancleButton;
  private ProgressBar OCRWaitProgressBar;

  private Toast backToast;
  private long backPressedTime;
  private String loadingText = "";

  private Uri resultImageUri;
  private static String TAG = "ocrTaskActivityTAG";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ocr_task);
    Log.d(TAG, "start Activity");

    OCRWaitProgressBar = findViewById(R.id.OCRWaitProgressBar);
    imageView = findViewById(R.id.imageView);
    OCRTextView = findViewById(R.id.OCRtextView);
    okButton = findViewById(R.id.okButton);
    cancleButton = findViewById(R.id.cancleButton);

    setTitle(R.string.ocrTitle);

    preOCR();
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
    OCRTextView.setText(loadingText);
    Log.d(TAG, "getBase64Encoded func");
    Intent intent = getIntent();
    resultImageUri = (Uri) intent.getExtras().get("IMAGE_URI");
    Log.d(TAG, "image uri : " + resultImageUri);
    imageView.setImageURI(resultImageUri);

    OCRTask _OCRTask = new OCRTask(getApplicationContext(), this);
    Log.d(TAG, "OCRTask success make");

    _OCRTask.setmImageURI(resultImageUri);
    _OCRTask.setImageView(imageView);
    _OCRTask.RUN();
  }
  public void okButtonClk(View view){
    Intent resultIntent = new Intent();
    resultIntent.putExtra("OCR_STRING", OCRTextView.getText().toString());
    setResult(RESULT_OK, resultIntent);
    finish();
  }
  public void cancleButtonClk(View veiw){
    Intent resultIntent = new Intent();
    resultIntent.putExtra("OCR_STRING", "");
    setResult(RESULT_CANCELED, resultIntent);
    finish();
  }

  public void preOCR(){
    okButton.setVisibility(View.INVISIBLE);
    cancleButton.setVisibility(View.INVISIBLE);
    OCRTextView.setVisibility(View.GONE);
  }
  public void successOCR(String result){
    okButton.setVisibility(View.VISIBLE);
    cancleButton.setVisibility(View.VISIBLE);

    OCRWaitProgressBar.setVisibility(View.GONE);
    OCRTextView.setText(result);
    OCRTextView.setVisibility(View.VISIBLE);
  }

}
