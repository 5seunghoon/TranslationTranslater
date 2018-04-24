package com.tistory.deque.translationtranslater;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HELLOEARTH on 2018-03-27.
 */

public class OCRTask {
  /**
   * 1. make OCRTask Class by context
   * 2. set Image URI, textView, imageView
   * 3. call RUN()
   */

  private static final String CLOUD_VISION_API_KEY = "AIzaSyB3Ccj7Bd-QJt_zv2vS7ftc-siVgclrm3w";
  private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
  private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
  private static final int MAX_LABEL_RESULTS = 10;
  private static final int MAX_DIMENSION = 1200;

  private Context context;
  private Button okButton, cancleButton;
  private Uri mImageURI;
  private TextView mImageDetails;
  private ImageView mMainImage;
  
  static private String tag = "ocrTaskClass";

  public OCRTask(Context context, Button okButton, Button cancleButton) {
    this.context = context;
    this.okButton = okButton;
    this.cancleButton = cancleButton;
  }

  public void setmImageURI(Uri imageUri){
    this.mImageURI = imageUri;
    Log.d(tag, "image uri set success");
  }
  public void setTextView(TextView textView){
    this.mImageDetails = textView;
    Log.d(tag, "text view set success");
  }
  public void setImageView(ImageView imageView){
    this.mMainImage = imageView;
    Log.d(tag, "image view set success");
  }

  public void RUN(){
    Log.d(tag, "RUN OCR TASK");
    VisionAPI();
    return;
  }

  private void VisionAPI(){
    /**
     * 1. uri로부터 이미지 들고와서 bitmap을 만든 다음 callCloudVision() 콜 (사이즈가 크면 리사이징 먼저)
     * 2. textDetectionTask를 만들고, 그 task를 execute. (이 task는 스래드임.)
     * 2-1. textDetectionTask의 인자인 annotationRequest는 구글로 보낼 request를 만드는 함수
     * 2-2. textDetectionTask는 execute되면 doInBackground를 실행
     * 3. doInBackground에서 구글로 request를 하고, message를 받으면 convertRequestToString()을 콜
     * 4. convertRequestToString()으로 반환된 string은 onPostExecute의 인자로 들어감 (onPostExecute는 스래드가 끝나면 콜백되는 함수)
     * 5. onPostExecute에서 미리 세팅된 textView의 text로 setting됨.
     */
    Log.d(tag, "visionapi func");
    uploadImage(mImageURI);
    return;
  }

  public void uploadImage(Uri uri) {
    Log.d(tag, "uploadimage func");
    if (uri != null) {
      try {
        // scale the image to save on bandwidth
        Bitmap bitmap =
          scaleBitmapDown(
            MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri),
            MAX_DIMENSION);
        Log.d(tag, "bitmap scale down");

        callCloudVision(bitmap);
        mMainImage.setImageBitmap(bitmap);

      } catch (IOException e) {
        Log.d(tag, "Image picking failed because " + e.getMessage());
        Toast.makeText(context, context.getResources().getString(R.string.image_picker_error), Toast.LENGTH_LONG).show();
      }
    } else {
      Log.d(tag, "Image picker gave us a null image.");
      Toast.makeText(context, context.getResources().getString(R.string.image_picker_error), Toast.LENGTH_LONG).show();
    }
  }
  private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
    Log.d(tag, "scale bitmap down");

    int originalWidth = bitmap.getWidth();
    int originalHeight = bitmap.getHeight();
    int resizedWidth = maxDimension;
    int resizedHeight = maxDimension;

    if (originalHeight > originalWidth) {
      resizedHeight = maxDimension;
      resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
    } else if (originalWidth > originalHeight) {
      resizedWidth = maxDimension;
      resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
    } else if (originalHeight == originalWidth) {
      resizedHeight = maxDimension;
      resizedWidth = maxDimension;
    }
    return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
  }

  private void callCloudVision(final Bitmap bitmap) {
    // Switch text to loading
    //mImageDetails.setText(R.string.loading_message);

    // Do the real work in an async task, because we need to use the network anyway
    try {
      AsyncTask<Object, Void, String> textDetectionTask =
        new TextDetectionTask(
          mImageDetails,
          prepareAnnotationRequest(bitmap),
          okButton,
          cancleButton
        );

      textDetectionTask.execute();
    } catch (IOException e) {
      Log.d(tag, "failed to make API request because of other IOException " +
        e.getMessage());
    }
  }


  private Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
    /**
     * make request.
     * put image, type, packagename.. etc
     */
    Log.d(tag,"prepareAnnotationRequest func");
    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    VisionRequestInitializer requestInitializer =
      new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
        /**
         * We override this so we can inject important identifying fields into the HTTP
         * headers. This enables use of a restricted cloud platform API key.
         */
        @Override
        protected void initializeVisionRequest(VisionRequest<?> visionRequest)
          throws IOException {
          super.initializeVisionRequest(visionRequest);

          String packageName = context.getPackageName();
          Log.d(tag, "package name : " + context.getPackageName());
          visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);
          Log.d(tag, "vision request set");
          String sig = PackageManagerUtils.getSignature(context.getPackageManager(), packageName);
          Log.d(tag, "set string .. package maneger utils 's signiture");
          visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
          Log.d(tag, "initialize vision request end");
        }
      };

    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
    Log.d(tag, "vision builder init");
    builder.setVisionRequestInitializer(requestInitializer);
    Log.d(tag, "vision builder init end");
    Vision vision = builder.build();
    Log.d(tag, "vision build");
    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
      new BatchAnnotateImagesRequest();
    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
      AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

      // Add the image
      Log.d(tag, "Add the image");
      Image base64EncodedImage = new Image();
      // Convert the bitmap to a JPEG
      Log.d(tag, "Convert the bitmap to a JPEG");
      // Just in case it's a format that Android understands but Cloud Vision
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
      byte[] imageBytes = byteArrayOutputStream.toByteArray();

      // Base64 encode the JPEG
      Log.d(tag, "Base64 encode the JPEG");
      base64EncodedImage.encodeContent(imageBytes);
      annotateImageRequest.setImage(base64EncodedImage);

      // add the features we want
      annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
        Feature textDetaction = new Feature();
        textDetaction.setType("TEXT_DETECTION");
        textDetaction.setMaxResults(MAX_LABEL_RESULTS);
        add(textDetaction);
      }});

      // Add the list of one thing to the request
      add(annotateImageRequest);
    }});
    Log.d(tag, "create batchAnnotateImagesRequest");
    Vision.Images.Annotate annotateRequest =
      vision.images().annotate(batchAnnotateImagesRequest);
    // Due to a bug: requests to Vision API containing large images fail when GZipped.
    annotateRequest.setDisableGZipContent(true);
    Log.d(tag, "created Cloud Vision request object, sending request");

    return annotateRequest;
  }



  private static class TextDetectionTask extends AsyncTask<Object, Void, String> {
    private TextView mImageDetails;
    private Vision.Images.Annotate mRequest;
    private Button okButton, cancleButton;

    TextDetectionTask(TextView mImageDetails, Vision.Images.Annotate annotate, Button okButton, Button cancleButton) {
      this.mImageDetails = mImageDetails;
      this.okButton = okButton;
      this.cancleButton = cancleButton;
      mRequest = annotate;
    }

    @Override
    protected String doInBackground(Object... params) {
      try {
        Log.d(tag, "created Cloud Vision request object, sending request");
        BatchAnnotateImagesResponse response = mRequest.execute();
        return convertResponseToString(response);

      } catch (GoogleJsonResponseException e) {
        Log.d(tag, "failed to make API request because " + e.getContent());
      } catch (IOException e) {
        Log.d(tag, "failed to make API request because of other IOException " +
          e.getMessage());
      }
      return "Cloud Vision API request failed. Check logs for details.";
    }

    protected void onPostExecute(String result) {
      Log.d(tag, "GET message from google : " +  result);
      mImageDetails.setText(result);
      okButton.setVisibility(View.VISIBLE);
      cancleButton.setVisibility(View.VISIBLE);
    }
  }

  private static String convertResponseToString(BatchAnnotateImagesResponse response) {
    StringBuilder message = new StringBuilder("");

    List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
    if (labels != null) {
      message.append(labels.get(0).getDescription());
    } else {
      message.append("문자 감식 실패..");
    }

    return message.toString();
  }

}
