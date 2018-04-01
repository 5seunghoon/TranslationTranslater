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

public class ocrTaskActivity extends AppCompatActivity {

  private ImageView imageView;
  private TextView textView;
  private static Button okButton, cancleButton;

  private String loadingText = "LOADING... PLEASE WAIT....";

  private Uri resultImageURI;
  private static String tag = "ocrTaskActivityTAG";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ocr_task);
    Log.d(tag, "start Activity");

    imageView = findViewById(R.id.imageView);
    textView = findViewById(R.id.textView);
    okButton = findViewById(R.id.okButton);
    cancleButton = findViewById(R.id.cancleButton);
    okButton.setVisibility(View.INVISIBLE);
    cancleButton.setVisibility(View.INVISIBLE);

    ocr();
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
    resultIntent.putExtra("OCR_STRING", textView.getText());
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
