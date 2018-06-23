package com.tistory.deque.translationtranslater.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tistory.deque.translationtranslater.Model.DB.DBOpenHelper;
import com.tistory.deque.translationtranslater.Controler.ExcludeStringTranslate;
import com.tistory.deque.translationtranslater.Util.Permission;
import com.tistory.deque.translationtranslater.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
  public enum viewState {NORMAL, EXTAND}

  private static final int REQUEST_TAKE_PHOTO = 101;
  private static final int REQUEST_TAKE_ALBUM = 102;
  private static final int REQUEST_IMAGE_CROP = 103;
  private static final int REQUEST_OCR_STRING = 104;
  private static final int MULTIPLE_PERMISSIONS = 200; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수

  private static final int MAX_IMAGE_SIZE = 100000; // width * hieght

  private final String TAG = "mainActivityTAG";

  private String preEditString;

  protected DBOpenHelper dbHelper;
  //protected SQLiteDatabase db;
  Uri imageURI, cropSourceURI, cropEndURI;
  long backPressedTime;
  String mCurrentPhotoPath;
  viewState viewstate;
  Snackbar backSnackbar;

  EditText inputEditText;
  Button translateButton, shareButton, resetButton;
  EditText translateTextView;
  String originalString;
  InputMethodManager imm;
  LinearLayout originalTextAndButtonLayout;
  LinearLayout moreButtonLayout;
  ActionBar actionBar;
  Permission permission;
  CoordinatorLayout mainAcitivyMainLayout;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    inputEditText = findViewById(R.id.inputEditText);
    translateButton = findViewById(R.id.translateButton);
    translateTextView = findViewById(R.id.translateTextView);
    //translateTextView.setClickable(false);
    translateTextView.setFocusableInTouchMode(false);
    originalTextAndButtonLayout = findViewById(R.id.originalTextAndButtonLayout);
    moreButtonLayout = findViewById(R.id.moreButtonLayout);
    mainAcitivyMainLayout = findViewById(R.id.mainActivityMainLayout);

    shareButton = findViewById(R.id.shareButton);
    resetButton = findViewById(R.id.resetButton);

    actionBar = getSupportActionBar();

    setClickListener();

    permission = new Permission(getApplicationContext(), this);
    permission.permissionSnackbarInit(findViewById(R.id.mainActivityMainLayout));

    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    setTitle(R.string.app_name);
    dbOpen();

    //  dbInsertTest();
    permission.checkPermissions();

    viewstate = viewState.NORMAL;
    backPressedTime = 0;

    Intent onIntent = getIntent();
    sharedToMe(onIntent);

    inputEditText.clearFocus(); //when create activity, we must hide keyboard
  }

  private void setClickListener() {
    translateButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        clickTranslateButton(v);
      }
    });

    shareButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        clickShareButton(v);
      }
    });

    resetButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        clickResetButton();
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (dbHelper != null) {
      dbHelper.dbClose();
    }
  }

  @Override
  public void onBackPressed() {
    if (viewstate == viewState.NORMAL) {
      if (System.currentTimeMillis() - backPressedTime < 2000) {
        backSnackbar.dismiss();
        finish();
      } else {
        backPressedTime = System.currentTimeMillis();
        backSnackbar = Snackbar.make(
          mainAcitivyMainLayout,
          "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.",
          Snackbar.LENGTH_LONG
        );
        backSnackbar.show();
      }
    }
    if (viewstate == viewState.EXTAND) {
      doExtandStateToNormalState();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actionbar_actions, menu); // 액션바에 actionbar_actions를 붙임
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_camera:
        doCamera();
        break;
      case R.id.action_gallery:
        doGallery();
        break;
      case R.id.action_history:
        doHistory();
        break;
      case R.id.action_wordbook:
        doWordbook();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  private void dbOpen() {
    dbHelper = DBOpenHelper.getDbOpenHelper(
      getApplicationContext(),
      DBOpenHelper.TABLE_NAME,
      null, DBOpenHelper.dbVersion
    );
    dbHelper.dbOpen();
  }

  public void sharedToMe(Intent intent) {
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

  public void dbInsertTest() {
    dbHelper.insertHistory("Is it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant value?",
      "안녕, 여긴 당신의 집!, 그리고 돌아갈 장소가 될 수 있습니다. 아마도요. 길이를 채우기 위한 공작일 뿐이죠");
    dbHelper.insertHistory("Is it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant valueIs it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant value" +
        "Is it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant value" +
        "Is it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant valueIs it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant value?",
      "안녕, 여긴 당신의 집!, 그리고 돌아갈 장소가 될 수 있습니다. 아마도요. 길이를 채우기 위한 공작일 뿐이죠Is it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant valueIs it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant value");
    dbHelper.insertHistory("Is it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant value?",
      "안녕, 여긴 당신의 집!, 그리고 돌아갈 장소가 될 수 있습니다. 아마도요. 길이를 채우기 위한 공작일 뿐이죠");
    dbHelper.insertHistory("Is it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant valueIs it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant value" +
        "Is it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant value" +
        "Is it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant valueIs it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant value?",
      "안녕, 여긴 당신의 집!, 그리고 돌아갈 장소가 될 수 있습니다. 아마도요. 길이를 채우기 위한 공작일 뿐이죠Is it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant valueIs it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant value");
    dbHelper.insertHistory("Is it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant value?",
      "안녕, 여긴 당신의 집!, 그리고 돌아갈 장소가 될 수 있습니다. 아마도요. 길이를 채우기 위한 공작일 뿐이죠");
    dbHelper.insertHistory("Is it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant valueIs it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant value" +
        "Is it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant value" +
        "Is it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant valueIs it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant value?",
      "안녕, 여긴 당신의 집!, 그리고 돌아갈 장소가 될 수 있습니다. 아마도요. 길이를 채우기 위한 공작일 뿐이죠Is it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant valueIs it possible to write some kind of a filter, that makes postgres's RANDOM() return always constant value");
  }

  public void startOcrTaskActivity(Uri resultImageUri) {
    Intent ocrTaskActivityIntent = new Intent(getApplicationContext(), OCRTaskActivity.class);
    ocrTaskActivityIntent.putExtra("IMAGE_URI", resultImageUri);
    startActivityForResult(ocrTaskActivityIntent, REQUEST_OCR_STRING);
  }

  public void clickFloatingActionButton(View view) {
    if (viewstate == viewState.NORMAL) {
      doNormalStateToExtandState();
    } else if (viewstate == viewState.EXTAND) {
      doExtandStateToNormalState();
    }
  }

  public void doNormalStateToExtandState() {
    preEditString = translateTextView.getText().toString();
    viewstate = viewState.EXTAND;
    originalTextAndButtonLayout.setVisibility(View.GONE);
    moreButtonLayout.setVisibility(View.VISIBLE);
    translateTextView.setClickable(true);
    translateTextView.setFocusableInTouchMode(true);
    translateTextView.requestFocus();
  }

  public void doExtandStateToNormalState() {
    viewstate = viewState.NORMAL;
    originalTextAndButtonLayout.setVisibility(View.VISIBLE);
    moreButtonLayout.setVisibility(View.GONE);
    translateTextView.setClickable(false);
    translateTextView.setFocusableInTouchMode(false);
    translateTextView.clearFocus();
  }

  public void doHistory() {
    Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
    startActivity(intent);
  }

  public void doWordbook() {
    Intent intent = new Intent(this, WordBookActivity.class);

    startActivity(intent);
    return;
  }

  /**
   * 카메라 버튼을 클릭했을 때,
   * 1. 권한 체크
   * 2. 카메라를 찍을수 있는 intent를 만들어서 startActivityForResult
   * 2-1. 이때 사진을 찍어서 그걸 저장할 photoFile을 만들고,
   * 2-2. 이 photoFile로 providerURI를 만든 뒤, imageURI에도 저장
   * 2-3. 카메라를 찍을수 있는 intent를 만들고
   * 2-4. providerURI를 MediaStore.EXTRA_OUTPUT를 키로 intent에 넣고 startActivityForResult
   * 3. 카메라로 찍으면 onActivityResult에서 크롭할 준비를 함
   * 3-1. 크롭하기 전 이미지의 URI와 크롭된 이미지를 저장할 URI를 준비한뒤 cropImage()호출
   * 3-2. "com.android.camera.action.CROP"액션, 인텐트에 output로 저장될 URI를 지정해준 뒤 intent startActivityForResult
   * 3-3. galleryAddPic()이 호출되면서 미디어 스캔을 실시
   * 4. 그 URI를 들고 ocrTaskActivity를 start
   * <p>
   * 갤러리 버튼을 클릭했을 때,
   * 1. 권한 체크
   * 2. Intent.ACTION_PICK의 액션과 image를 타입으로 지정해주고 startActivityForResult
   * 3. 카메라 때와 마찬가지로 cropimage를 호출하되, 크롭할 이미지의 URI는 intent.getdata로 바로 받아옴
   * 4. 카메라 때와 마찬가지로 미디어스캔 후 ocrTaskActivity를 시작
   */

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case REQUEST_TAKE_PHOTO:
        if (resultCode == Activity.RESULT_OK) {
          try {
            Log.d(TAG, "REQUEST TAKE PHOTO OK");
            File albumFile = null;
            albumFile = createImageFile();
            //cropImage(): cropSoureURI 위치의 파일을 잘라서 cropEndURI로 저장
            //따라서 imageURI에 담겨있는 파일위치를 자를 것이기 때문에 cropSoureURI imageURI를 저장해야 함
            cropSourceURI = imageURI;
            cropEndURI = Uri.fromFile(albumFile);
            cropImage();
          } catch (Exception e) {
            Log.e(TAG, "REQUEST TAKE PHOTO" + e.toString());
          }
        } else {
          Toast.makeText(this, "사진찍기를 취소하였습니다.", Toast.LENGTH_LONG).show();
        }
        break;

      case REQUEST_TAKE_ALBUM:
        if (resultCode == Activity.RESULT_OK) {
          if (data.getData() != null) {
            try {
              File albumFile = null;
              albumFile = createImageFile();
              cropSourceURI = data.getData();
              cropEndURI = Uri.fromFile(albumFile);
              cropImage();
            } catch (Exception e) {
              Log.e(TAG, "REQUEST TAKE ALBUM" + e.toString());
            }
          }
        }
        break;
      case REQUEST_IMAGE_CROP:
        if (resultCode == Activity.RESULT_OK) {
          resizingImage();
          galleryAddPic();
          startOcrTaskActivity(cropEndURI);
        }
        break;
      case REQUEST_OCR_STRING:
        if (resultCode == Activity.RESULT_OK) {
          inputEditText.setText(data.getStringExtra("OCR_STRING"));
        }
        break;
      // TODO need add "default" case
    }
  }

  public void clickTranslateButton(View view) {
    originalString = inputEditText.getText().toString();
    if (originalString.isEmpty()) return;
    imm.hideSoftInputFromWindow(inputEditText.getWindowToken(), 0);//if click button, keyboard will hide.

    ExcludeStringTranslate translatingClass =
      new ExcludeStringTranslate(getApplicationContext(), dbHelper, translateTextView);
    translatingClass.setOriginalString(inputEditText.getText().toString());
    translatingClass.translate();

  }

  public void clickShareButton(View view) {
    doShare();
  }

  public void doShare() {
    /**
     * if click share button and share to kakaotalk, it will send <<Translated Text>> massage.
     */
    String translatedString = translateTextView.getText().toString();
    Intent shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_name));
    shareIntent.putExtra(Intent.EXTRA_TEXT, translatedString + "\n - translated by " + getString(R.string.app_name));
    startActivity(Intent.createChooser(shareIntent, "번역 결과 공유하기"));
  }

  public void clickResetButton() {
    doReset();
  }

  public void doReset() {
    translateTextView.setText(preEditString);
  }

  public void doCamera() {
    if (permission.checkPermissions()) {
      Log.d(TAG, "check permission end");
      captureCamera();
      Log.d(TAG, "capture camera func end");
    }
  }

  public void doGallery() {
    if (permission.checkPermissions()) {
      Log.d(TAG, "check permission end");
      getAlbum();
      Log.d(TAG, "get album func end");
    }
  }

  private void captureCamera() {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      Log.d(TAG, "intent takePictureIntent init");

      if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        Log.d(TAG, "if takePictureIntent.resolveActivity(getPackageManager()) != null");
        File photoFile = null;
        try {
          photoFile = createImageFile();
        } catch (IOException ex) {
          Log.e(TAG, "captureCamera Error" + ex.toString());
        }
        if (photoFile != null) {
          Log.d(TAG, "photo file make success");
          //make photo file success
          Uri providerURI = FileProvider.getUriForFile(this, "com.tistory.deque.translationtranslater", photoFile);
          Log.d(TAG, "providerURI : " + providerURI);
          imageURI = providerURI;
          takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);
          Log.d(TAG, "intent put extra (providerURI) success : " + providerURI);
          Log.d(TAG, "start activity : takepictureintent");
          startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
      } else {
        Toast.makeText(this, "저장공간이 접근 불가능한 기기입니다.", Toast.LENGTH_LONG).show();
      }
    }
  }

  public File createImageFile() throws IOException {
    Log.d(TAG, "createImageFile func");
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + ".jpg";
    File imageFile = null;
    File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "t2");
    Log.d(TAG, "storageDir : " + storageDir);
    if (!storageDir.exists()) {
      Log.d(TAG, storageDir.toString() + " is not exist");
      storageDir.mkdir();
      Log.d(TAG, "storageDir make");
    }
    imageFile = new File(storageDir, imageFileName);
    Log.d(TAG, "imageFile init");
    mCurrentPhotoPath = imageFile.getAbsolutePath();
    Log.d(TAG, "mCurrentPhotoPath : " + mCurrentPhotoPath);

    return imageFile;
  }


  private void getAlbum() {
    Log.d(TAG, "getAlbum()");
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType("image/*");
    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
    Log.d(TAG, "start Activity : album intent");
    startActivityForResult(intent, REQUEST_TAKE_ALBUM);
  }

  private void galleryAddPic() {
    Log.d(TAG, "galleryAddPic, do media scan");
    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    File f = new File(mCurrentPhotoPath);
    Uri contentUri = Uri.fromFile(f);
    mediaScanIntent.setData(contentUri);
    sendBroadcast(mediaScanIntent);
    Log.d(TAG, "media scanning end");
  }

  public void cropImage() {
    /**
     * cropSourceURI = 자를 uri
     * cropEndURI = 자르고 난뒤 uri
     */
    Log.d(TAG, "cropImage() CALL");
    Log.d(TAG, "cropImage() : Photo URI, Album URI" + cropSourceURI + ", " + cropEndURI);

    Intent cropIntent = new Intent("com.android.camera.action.CROP");

    cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    cropIntent.setDataAndType(cropSourceURI, "image/*");
    //cropIntent.putExtra("aspectX", 1);
    //cropIntent.putExtra("aspectY", 1);
    //cropIntent.putExtra("scale", true);
    cropIntent.putExtra("output", cropEndURI);
    startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
  }

  public void resizingImage() {
    Log.d(TAG, "call resizing image");
    int org_width, org_height;
    int result_width, result_height;

    Bitmap srcBmp;
    //srcBmp = BitmapFactory.decodeFile(cropEndURI.getPath());
    try {
      srcBmp = MediaStore.Images.Media.getBitmap(getContentResolver(), cropEndURI);
    } catch (Exception e) {
      Log.d(TAG, "bitmap load exception : " + e.toString());
      return;
    }
    Log.d(TAG, "Bitmap load success");
    org_width = srcBmp.getWidth();
    org_height = srcBmp.getHeight();

    if (MAX_IMAGE_SIZE < org_height * org_width) {
      double rate = Math.sqrt((org_height * org_width) / MAX_IMAGE_SIZE);
      Log.d(TAG, "orginal width : " + org_width + " , orginal height : " + org_height);
      Log.d(TAG, "it is big iamge. resizing " + rate + " percent.");
      result_width = (int) (org_width / rate);
      result_height = (int) (org_height / rate);

      FileOutputStream fosObj;

      try {
        Log.d(TAG, "resizing start : " + result_height + " , " + result_width);
        Bitmap resizedBmp = Bitmap.createScaledBitmap(srcBmp, result_width, result_height, true);
        Log.d(TAG, "resizing");
        fosObj = new FileOutputStream(cropEndURI.getPath());
        Log.d(TAG, "get file output stream");
        resizedBmp.compress(Bitmap.CompressFormat.JPEG, 100, fosObj);
        Log.d(TAG, "rewrite");
        fosObj.flush();
        fosObj.close();
      } catch (Exception e) {
        Log.d(TAG, "file write exception : " + e.toString());
      }
    }
  }

  /**
   * 이하 권한
   **/

  @Override
  public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
    permission.requestPermissionsResult(requestCode, permissions, grantResults);
  }


}
