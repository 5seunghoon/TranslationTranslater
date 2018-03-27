package com.tistory.deque.translationtranslater;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ocrTaskActivity extends AppCompatActivity {
  private static final int PICK_FROM_ALBUM = 100;
  private static final int PICK_FROM_CAMERA = 101;
  private static final int CROP_FROM_CAMERA = 102;

  private ImageView imageView;
  private TextView textView;

  private Uri photoUri;
  String encodedImage;
  private static String tag = "ocrTaskActivityTAG";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ocr_task);

    imageView = findViewById(R.id.imageView);
    textView = findViewById(R.id.textView);

    Bundle extras = getIntent().getExtras();
    String caseString = extras.getString("CASE");
    Log.d(tag, caseString);
    if(caseString == "ALBUM"){
      Log.d(tag, "ALBUM");
      this.goToAlbum();
    }
    else if(caseString == "CAMERA"){
      Log.d(tag, "CAMERA");
      this.takePhoto();
    }

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != RESULT_OK) {
      Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
    }
    if (requestCode == PICK_FROM_ALBUM) {
      if(data==null){
        return;
      }
      photoUri = data.getData();
      cropImage();
    } else if (requestCode == PICK_FROM_CAMERA) {
      cropImage();
      MediaScannerConnection.scanFile(this, //앨범에 사진을 보여주기 위해 Scan을 합니다.
        new String[]{photoUri.getPath()}, null,
        new MediaScannerConnection.OnScanCompletedListener() {
          public void onScanCompleted(String path, Uri uri) {
          }
        });
    } else if (requestCode == CROP_FROM_CAMERA) {
      try { //저는 bitmap 형태의 이미지로 가져오기 위해 아래와 같이 작업하였으며 Thumbnail을 추출하였습니다.

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 128, 128);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bs); //이미지가 클 경우 OutOfMemoryException 발생이 예상되어 압축


        //여기서는 ImageView에 setImageBitmap을 활용하여 해당 이미지에 그림을 띄우시면 됩니다.

        imageView.setImageBitmap(thumbImage);
        textView.setText(bitmapToBase64(thumbImage));
      } catch (Exception e) {
        Log.e("ERROR", e.getMessage().toString());
      }
    }
  }

  public String bitmapToBase64(Bitmap bitmap){
    encodedImage = encodeImage(bitmap);
    return encodedImage;
  }
  public String encodeImage(Bitmap bm)
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
    byte[] b = baos.toByteArray();
    String encImage = Base64.encodeToString(b, Base64.DEFAULT);

    return encImage;
  }

  public void takePhoto() {
    Log.d(tag, "takePhoto function in");
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //사진을 찍기 위하여 설정합니다.
    File photoFile = null;
    try {
      Log.d(tag, "takephoto try");
      photoFile = createImageFile();
    } catch (IOException e) {
      Log.d(tag, "takephoto error");
      Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
      finish();
    }
    if (photoFile != null) {
      Log.d(tag, "takePhoto camera ready");
      photoUri = FileProvider.getUriForFile(this,
        "com.example.test.provider", photoFile); //FileProvider의 경우 이전 포스트를 참고하세요.
      intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //사진을 찍어 해당 Content uri를 photoUri에 적용시키기 위함

      Log.d(tag, "camera put extra");
      startActivityForResult(intent, PICK_FROM_CAMERA);

      Log.d(tag, "end camera activity");
    }
  }

  public static File createImageFile() throws IOException {

    Log.d(tag, "create image file function");
    // Create an image file name
    String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
    String imageFileName = "IP" + timeStamp + "_";
    File storageDir = new File(Environment.getExternalStorageDirectory() + "/test/"); //test라는 경로에 이미지를 저장하기 위함

    Log.d(tag, "init storage dir file");
    if (!storageDir.exists()) {
      storageDir.mkdirs();

      Log.d(tag, "make dir");
    }
    File image = File.createTempFile(
      imageFileName,
      ".jpg",
      storageDir
    );
    Log.d(tag, "make image");
    return image;
  }

  public void goToAlbum() {
    Intent intent = new Intent(Intent.ACTION_PICK); //ACTION_PICK 즉 사진을 고르겠다!
    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
    startActivityForResult(intent, PICK_FROM_ALBUM);
  }

  public void cropImage() {

    Log.d(tag, "crop image function ");
    this.grantUriPermission("com.android.camera", photoUri,
      Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
    Intent intent = new Intent("com.android.camera.action.CROP");
    intent.setDataAndType(photoUri, "image/*");

    List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
    grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
      Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
    int size = list.size();
    if (size == 0) {
      Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
      return;
    } else {
      Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
      intent.putExtra("crop", "true");
      intent.putExtra("aspectX", 4);
      intent.putExtra("aspectY", 3);
      intent.putExtra("scale", true);
      File croppedFileName = null;
      try {
        croppedFileName = createImageFile();
      } catch (IOException e) {
        e.printStackTrace();
      }

      File folder = new File(Environment.getExternalStorageDirectory() + "/test/");
      File tempFile = new File(folder.toString(), croppedFileName.getName());

      photoUri = FileProvider.getUriForFile(this,
        "com.example.test.provider", tempFile);

      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


      intent.putExtra("return-data", false);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
      intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); //Bitmap 형태로 받기 위해 해당 작업 진행

      Intent i = new Intent(intent);
      ResolveInfo res = list.get(0);
      i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
      grantUriPermission(res.activityInfo.packageName, photoUri,
        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

      i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
      startActivityForResult(i, CROP_FROM_CAMERA);
    }
  }
}
