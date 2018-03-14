package com.tistory.deque.developertranslater;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
/**
 * Created by HELLOEARTH on 2018-03-14.
 */

public class TranslateAsyncTask extends AsyncTask<String, String, String> {
  private final String TAG = "TranslateAsyncTask";

  private String resultString;
  private TextView translateTextView;

  private final String clientId = "apaShxAPOVRY4Rm_auHO";
  private final String clientSecret = "Gjkp5UpH5o";

  public TranslateAsyncTask(TextView translateTextView) {
    super();
    this.translateTextView = translateTextView;
  }

  @Override
  protected String doInBackground(String... strings) {
    try{
      String input = strings[0];
      String text = URLEncoder.encode(input, "UTF-8");
      Log.d(TAG, "Encoded text : " + text);
      String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
      URL url = new URL(apiURL);
      HttpURLConnection con = (HttpURLConnection)url.openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("X-Naver-Client-Id", clientId);
      con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
      Log.d(TAG, "connection Success");
      // post request
      String postParams = "source=en&target=ko&text=" + text;
      Log.d(TAG, "Post Parameter : " + postParams);
      con.setDoOutput(true);
      Log.d(TAG, "set do ouput");
      DataOutputStream wr = new DataOutputStream(con.getOutputStream());
      Log.d(TAG, "Data Output Stream open");
      wr.writeBytes(postParams);
      Log.d(TAG, "write bytes");
      wr.flush();
      Log.d(TAG, "flush");
      wr.close();
      Log.d(TAG, "Data Output Stream close");
      int responseCode = con.getResponseCode();
      BufferedReader br;
      if(responseCode==200) { // 정상 호출
        br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        Log.d(TAG, "response code : 200, success");
      } else {  // 에러 발생
        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        Log.d(TAG, "response code : " + responseCode + ", Error");
      }
      String inputLine;
      StringBuffer response = new StringBuffer();
      while ((inputLine = br.readLine()) != null) {
        response.append(inputLine);
      }
      br.close();

      JSONObject jsonObject = new JSONObject(response.toString());
      resultString = jsonObject.getJSONObject("message").getJSONObject("result").getString("translatedText");


      Log.d(TAG, "Translate text : " + resultString);
      Log.d(TAG, "Translate success");
    } catch (Exception e) {
      resultString = e.toString(); // 에러
      Log.d(TAG, "Translate Error");
    }
    return resultString;
  }

  @Override
  protected void onPostExecute(String s) {
    super.onPostExecute(s);
    translateTextView.setText(resultString);
  }
}

class Data{
  public String srcLangType;
  public String tarLangType;
  public String translatedText;
}