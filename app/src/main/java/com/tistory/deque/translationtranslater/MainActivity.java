package com.tistory.deque.translationtranslater;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  private static final int REQUEST_TAKE_PHOTO = 101;
  private static final int REQUEST_TAKE_ALBUM = 102;
  private static final int REQUEST_IMAGE_CROP = 103;
  private static final int MULTIPLE_PERMISSIONS = 200; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수

  private static final int MAX_widthMulHeight = 200000;

  private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

  Uri imageUri, cropSoureURI, cropEndURI;
  String mCurrentPhotoPath;

  EditText inputEditText;
  Button translateButton;
  EditText translateTextView;
  String originalString;
  InputMethodManager imm;
  ImageView iv_view;

  String tag = "mainActivityTAG";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    inputEditText = findViewById(R.id.inputEditText);
    translateButton = findViewById(R.id.translateButton);
    translateTextView = findViewById(R.id.translateTextView);
    iv_view = findViewById(R.id.imageView);

    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    Intent onIntent = getIntent();
    sharedToMe(onIntent);

    inputEditText.clearFocus(); //when create activity, we must hide keyboard
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode){
      case REQUEST_TAKE_PHOTO:
        if(resultCode == Activity.RESULT_OK){
          try{
            Log.d(tag, "REQUEST TAKE PHOTO OK");
            File albumFile = null;
            albumFile = createImageFile();
            //cropImage(): cropSoureURI 위치의 파일을 잘라서 cropEndURI로 저장
            //따라서 imageURI에 담겨있는 파일위치를 자를 것이기 때문에 cropSoureURI imageURI를 저장해야 함
            cropSoureURI = imageUri;
            cropEndURI = Uri.fromFile(albumFile);
            cropImage();
          } catch (Exception e){
            Log.e(tag, "REQUEST TAKE PHOTO" + e.toString());
          }
        }
        else{
          Toast.makeText(this, "사진찍기를 취소하였습니다.", Toast.LENGTH_LONG).show();
        }
        break;

      case REQUEST_TAKE_ALBUM:
        if(resultCode == Activity.RESULT_OK){
          if(data.getData() != null){
            try{
              File albumFile = null;
              albumFile = createImageFile();
              cropSoureURI = data.getData();
              cropEndURI = Uri.fromFile(albumFile);
              cropImage();
            } catch (Exception e){
              Log.e(tag, "REQUEST TAKE ALBUM" + e.toString());
            }
          }
        }
        break;
      case REQUEST_IMAGE_CROP:
        if(resultCode == Activity.RESULT_OK){
          galleryAddPic();
          startOcrTaskActivity(cropEndURI);
        }
        break;
    }
  }

  public void startOcrTaskActivity(Uri resultImageUri){
    Intent ocrTaskActivityIntent = new Intent(getApplicationContext(), ocrTaskActivity.class);
    ocrTaskActivityIntent.putExtra("IMAGE_URI", resultImageUri);
    startActivity(ocrTaskActivityIntent);
  }

  /** 이하 권한 **/

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

  /** --- **/

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
    checkPermissions();
    Log.d(tag, "check permission end");
    captureCamera();
    Log.d(tag, "capture camera func end");
  }

  public void clickGalleryButton(View view){
    checkPermissions();
    Log.d(tag, "check permission end");
    getAlbum();
    Log.d(tag, "get album func end");
  }
  private void captureCamera(){
    String state = Environment.getExternalStorageState();
    if(Environment.MEDIA_MOUNTED.equals(state)){
      Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      Log.d(tag, "intent takePictureIntent init");

      if(takePictureIntent.resolveActivity(getPackageManager()) != null){
        Log.d(tag, "if takePictureIntent.resolveActivity(getPackageManager()) != null");
        File photoFile = null;
        try{
          photoFile = createImageFile();
        } catch (IOException ex){
          Log.e(tag, "captureCamera Error" + ex.toString());
        }
        if(photoFile != null){
          Log.d(tag, "photo file make success");
          //make photo file success
          Uri providerURI = FileProvider.getUriForFile(this, "com.tistory.deque.translationtranslater", photoFile);
          Log.d(tag, "providerURI : " + providerURI);
          imageUri = providerURI;
          takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);
          Log.d(tag, "intent put extra (providerURI) success : " + providerURI);
          Log.d(tag, "start activity : takepictureintent");
          startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
      }
      else{
        Toast.makeText(this, "저장공간이 접근 불가능한 기기입니다.", Toast.LENGTH_LONG).show();
      }
    }
  }
  public File createImageFile() throws IOException {
    Log.d(tag, "createImageFile func");
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + ".jpg";
    File imageFile = null;
    File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "t2");
    Log.d(tag, "storageDir : " + storageDir);
    if (!storageDir.exists()) {
      Log.d(tag, storageDir.toString() + " is not exist");
      storageDir.mkdir();
      Log.d(tag, "storageDir make");
    }
    imageFile = new File(storageDir, imageFileName);
    Log.d(tag, "imageFile init");
    mCurrentPhotoPath = imageFile.getAbsolutePath();
    Log.d(tag, "mCurrentPhotoPath : " + mCurrentPhotoPath);

    return imageFile;
  }


  private void getAlbum(){
    Log.d(tag, "getAlbum()");
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType("image/*");
    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
    Log.d(tag, "start Activity : album intent");
    startActivityForResult(intent, REQUEST_TAKE_ALBUM);
  }

  private void galleryAddPic(){
    Log.d(tag, "galleryAddPic");
    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    File f = new File(mCurrentPhotoPath);
    Uri contentUri = Uri.fromFile(f);
    mediaScanIntent.setData(contentUri);
    sendBroadcast(mediaScanIntent);
    //Toast.makeText(this, "사진이 앨범에 저장되었습니다", Toast.LENGTH_LONG).show();

    resizingImage();
  }
  public void cropImage(){
    /**
     * cropSoureURI = 자를 uri
     * cropEndURI = 자르고 난뒤 uri
     */
    Log.d(tag, "cropImage() CALL");
    Log.d(tag, "cropImage() : Photo URI, Album URI" + cropSoureURI + ", " + cropEndURI);

    Intent cropIntent = new Intent("com.android.camera.action.CROP");

    cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    cropIntent.setDataAndType(cropSoureURI, "image/*");
    //cropIntent.putExtra("aspectX", 1);
    //cropIntent.putExtra("aspectY", 1);
    //cropIntent.putExtra("scale", true);
    cropIntent.putExtra("output", cropEndURI);
    startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
  }
  public void resizingImage(){
    Log.d(tag, "call resizing image");
    int org_width, org_height;
    int result_width, result_height;


    Bitmap srcBmp;
    //srcBmp = BitmapFactory.decodeFile(cropEndURI.getPath());
    try{
      srcBmp = MediaStore.Images.Media.getBitmap(getContentResolver(), cropEndURI);
    } catch (Exception e){
      Log.d(tag, "bitmap load exception : " + e.toString());
      return;
    }
    Log.d(tag, "Bitmap load success");
    org_width = srcBmp.getWidth();
    org_height = srcBmp.getHeight();

    if(MAX_widthMulHeight < org_height * org_width){
      double rate = Math.sqrt((org_height * org_width) / MAX_widthMulHeight);
      Log.d(tag, "orginal width : " + org_width + " , orginal height : " + org_height);
      Log.d(tag, "it is big iamge. resizing " + rate + " percent.");
      result_width = (int) (org_width / rate);
      result_height = (int) (org_height / rate);

      FileOutputStream fosObj;

      try {
        Log.d(tag, "resizing start : " + result_height + " , " + result_width);
        Bitmap resizedBmp = Bitmap.createScaledBitmap(srcBmp, result_width, result_height, true);
        Log.d(tag, "resizing");
        fosObj = new FileOutputStream(cropEndURI.getPath());
        Log.d(tag, "get file output stream");
        resizedBmp.compress(Bitmap.CompressFormat.JPEG, 100, fosObj);
        Log.d(tag, "rewrite");
        fosObj.flush();
        fosObj.close();
      } catch (Exception e){
        Log.d(tag, "file write exception : " + e.toString());
      }
    }

  }
}
