package com.tistory.deque.translationtranslater;

import android.content.Context;
import android.content.Intent;
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
  public Intent data;
  public Uri imageUri;
  public Context context;
  public String encodedImage;
  
  static String tag = "OCR TASK";

  public ocrTask(Bitmap bitmap, Context context) {
    this.data = data;
    this.context = context;
  }
  public String imageUriToBase64(){
    if(imageUri == null) return "";
    final InputStream imageStream;
    imageUri = data.getData();
    try {
      imageStream = context.getContentResolver().openInputStream(imageUri);
      final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
      Log.d(tag, "COMPELETE BITMAP");
      encodedImage = encodeImage(selectedImage);
      Log.d(tag, "COMPELETE ENCODE");
      Log.d(tag, "ENCODE : "+ encodedImage);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }finally {
      return encodedImage;
    }
  }
  public String bitmapToBase64(){
    Bitmap captureBitmap = (Bitmap) data.getExtras().get("data");
    encodedImage = encodeImage(captureBitmap);
    return encodedImage;
  }
  private String encodeImage(Bitmap bm)
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
    byte[] b = baos.toByteArray();
    String encImage = Base64.encodeToString(b, Base64.DEFAULT);

    return encImage;
  }
}
