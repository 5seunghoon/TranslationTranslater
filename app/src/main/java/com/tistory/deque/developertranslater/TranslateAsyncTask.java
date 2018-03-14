package com.tistory.deque.developertranslater;

/**
 * Created by Oh seunghoon on 2018-03-14.
 */

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

public class TranslateAsyncTask extends AsyncTask<String, String, String> {
  private final String TAG = "TranslateAsyncTask";

  private String resultString;
  private TextView translateTextView;

  private final String clientId = "apaShxAPOVRY4Rm_auHO";
  private final String clientSecret = "Gjkp5UpH5o";

  public TranslateAsyncTask(TextView translateTextView) {
    super();
    this.setTranslateTextView(translateTextView);
  }

  @Override
  protected String doInBackground(String... strings) {
    try{
      StringBuffer response = getHttpResponseFromPapagoAPI(strings[0]);
      String translatedText = jsonToText(new JSONObject(response.toString())));
      setResultString(translatedText);
      Log.d(TAG, "Translate success");
    }
    catch(Exception e){
      Log.d(TAG, "Translate error");
      setResultString("Translate Error");
    }
    return getResultString();
  }

  @Override
  protected void onPostExecute(String s) {
    super.onPostExecute(s);
    getTranslateTextView().setText(getResultString());
  }

  private StringBuffer getHttpResponseFromPapagoAPI(String input){
    StringBuffer response = new StringBuffer();
    try{
      String text = URLEncoder.encode(input, "UTF-8");
      Log.d(TAG, "Encoded text : " + text);
      String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
      URL url = new URL(apiURL);
      HttpURLConnection con = (HttpURLConnection)url.openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("X-Naver-Client-Id", getClientId());
      con.setRequestProperty("X-Naver-Client-Secret", getClientSecret());
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
      response = new StringBuffer();
      while ((inputLine = br.readLine()) != null) {
        response.append(inputLine);
      }
      br.close();

      Log.d(TAG, "Response Success");

    } catch (Exception e) {
      setResultString(e.toString()); // 에러
      Log.d(TAG, "Response Error");
    }
    return response;
  }

  private String jsonToText(JSONObject jsonObject){
    String result;
    try{
      result = jsonObject.getJSONObject("message").getJSONObject("result").getString("translatedText");

      Log.d(TAG, "Json response translated text : " + result);
      Log.d(TAG, "JSON success");
    }
    catch (Exception e){
      result = "JSON ERROR";
    }
    return result;
  }

  public String getResultString() {
    return resultString;
  }

  public void setResultString(String resultString) {
    this.resultString = resultString;
  }

  public TextView getTranslateTextView() {
    return translateTextView;
  }

  public void setTranslateTextView(TextView translateTextView) {
    this.translateTextView = translateTextView;
  }

  public String getClientId() {
    return clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }
}