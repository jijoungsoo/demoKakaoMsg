package com.example.coin.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.example.framework.exception.BizException;
import com.example.framework.utils.PjtUtil;

public class HttpUtilKakao {


    public String httpGetKakaoApi(String URL, String QueryString)
        throws URISyntaxException, ClientProtocolException, IOException, NoSuchAlgorithmException, BizException {
        String rtn = "";
        try {

            if (!PjtUtil.g().isEmpty(QueryString)) {
                URL = URL + "?" + QueryString;
            }

            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(URL);
            // request.setHeader("Content-Type", "application/json");
            HttpResponse response = client.execute(request);
            org.apache.http.HttpEntity entity = response.getEntity();
            rtn = EntityUtils.toString(entity, "UTF-8");
            int status_code = response.getStatusLine().getStatusCode();
            System.out.println("status_code=>" + status_code);
            System.out.println("URL=>" + URL);
            System.out.println("QueryString=>" + QueryString);
            System.out.println("rtn=>" + rtn);
            if (status_code >= 400 && status_code < 500) {
                // 에러
                /*
                * Object error String error.name String error.message
                * {"error":{"message":"권한이 부족합니다.","name":"out_of_scope"}}
                */
                throw new com.example.framework.exception.BizException(rtn);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rtn;
    }
    public String httpPostKakaoApi(String KAKAO_API_ACCESS_KEY,String URL,  HashMap<String, String> params) throws URISyntaxException, ClientProtocolException, IOException, NoSuchAlgorithmException, com.example.framework.exception.BizException{
        String rtn="";
        String authenticationToken = "Bearer " + KAKAO_API_ACCESS_KEY;

        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            /* post 전송이므로 빠져야한다.
            if(!PjtUtil.isEmpty(queryString)){
                URL=URL+"?"+queryString;
            }
            */

            //StringEntity body = new StringEntity(new Gson().toJson(params));
            HttpPost request = new HttpPost(URL);
            request.setHeader("Content-Type", "application/x-www-form-urlencoded");
            request.setHeader("charset", "UTF-8");
            request.addHeader("Authorization", authenticationToken);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();  
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key   = entry.getKey();
                String value =  entry.getValue();
                nameValuePairs.add(new BasicNameValuePair(key,value));
            }


            UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(nameValuePairs,"UTF-8");
         
            request.setEntity(encodedFormEntity);
           

            System.out.println("body:"+ encodedFormEntity.toString());
            System.out.println("URL:"+URL);
            System.out.println("request.getURI().getRawQuery():"+request.getURI().getRawQuery());

            HttpResponse response = client.execute(request);
            StatusLine tmp_status =response.getStatusLine();
            int status_code = tmp_status.getStatusCode();
            System.out.println("status_code : "+ status_code);
            org.apache.http.HttpEntity entity = response.getEntity();
            rtn = EntityUtils.toString(entity, "UTF-8");
            System.out.println("rtn:"+rtn);
            if(status_code==404){
                throw new BizException("페이지 주소가 잘못되었다. URL :"+URL);
            }

            if((status_code==400) || (status_code==401)){
                PjtUtil.g();
				HashMap<String,Object> tmp_error=PjtUtil.g().JsonStringToObject(rtn, HashMap.class);
                throw new BizException("error :"+tmp_error.get("msg"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  rtn;
	}
}
