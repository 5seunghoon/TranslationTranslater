package com.tistory.deque.translationtranslater;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by HELLOEARTH on 2018-03-27.
 */

public class ocrTask {
  /**
   * 1. make ocrTask Class by context
   * 2. set Image URI
   * 3. call RUN()
   */
  private Context context;
  private Uri imageUri;
  private String encodedImageString;
  private String resultOCRString = "NULL_TEXT";
  
  static private String tag = "ocrTaskClass";

  public ocrTask(Context context) {
    this.context = context;
  }

  public void setImageURI(Uri imageUri){
    this.imageUri = imageUri;
    Log.d(tag, "image uri set success");
  }

  public String RUN(){
    Log.d(tag, "RUN OCR TASK");
    imageUriToBase64();
    return VisionAPI();
  }

  private void imageUriToBase64(){
    if(imageUri == null){
      Log.d(tag, "image uri is empty");
      return;
    }
    Log.d(tag, "start image uri to base 64");
    final InputStream imageStream;
    try {
      imageStream = context.getContentResolver().openInputStream(imageUri);
      final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
      Log.d(tag, "COMPELETE BITMAP");
      encodedImageString = encodeImage(selectedImage);
      Log.d(tag, "COMPELETE ENCODE");
      Log.d(tag, "ENCODE : "+ encodedImageString);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  private String bitmapToBase64(Bitmap captureBitmap){
    encodedImageString = encodeImage(captureBitmap);
    return encodedImageString;
  }
  private String encodeImage(Bitmap bm)
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
    byte[] b = baos.toByteArray();
    String encImage = Base64.encodeToString(b, Base64.DEFAULT);

    return encImage;
  }

  private String VisionAPI(){
    return resultOCRString;
  }
}
