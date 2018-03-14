package com.tistory.deque.developertranslater;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by oh seunghoon on 2018-03-14.
 */

public class Translate {
  static private String clientId = String.valueOf(R.string.naver_api_client_id);
  static private String clientSecret = String.valueOf(R.string.naver_api_client_secret);

  static public String translating(String input){
    String result;
    try {
      String text = URLEncoder.encode(input, "UTF-8");
      String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
      URL url = new URL(apiURL);
      HttpURLConnection con = (HttpURLConnection)url.openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("X-Naver-Client-Id", clientId);
      con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
      // post request
      String postParams = "source=ko&target=en&text=" + text;
      con.setDoOutput(true);
      DataOutputStream wr = new DataOutputStream(con.getOutputStream());
      wr.writeBytes(postParams);
      wr.flush();
      wr.close();
      int responseCode = con.getResponseCode();
      BufferedReader br;
      if(responseCode==200) { // 정상 호출
        br = new BufferedReader(new InputStreamReader(con.getInputStream()));
      } else {  // 에러 발생
        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
      }
      String inputLine;
      StringBuffer response = new StringBuffer();
      while ((inputLine = br.readLine()) != null) {
        response.append(inputLine);
      }
      br.close();
      result = response.toString();
    } catch (Exception e) {
      result = "-1"; // 에러
    }

    return result;
  }
}
