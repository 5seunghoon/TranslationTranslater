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
  public Context context;
  public String encodedImage;
  
  static String tag = "OCR TASK";

  public ocrTask(Context context) {
    this.context = context;
  }
  public String imageUriToBase64(Uri imageUri){
    if(imageUri == null) return "";
    final InputStream imageStream;
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
  public String bitmapToBase64(Bitmap captureBitmap){
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
