package com.example.coin.kakao;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.coin.util.HttpUtilKakao;
import com.example.framework.exception.BizException;
import com.example.framework.utils.PjtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import dataset.DataRow;
import dataset.DataSet;
import dataset.DataTable;
import running.common.SAProxy;
public class KakaoSendTextToMe extends SAProxy {

    public DataSet MsgSendTextToMe(DataSet InDs,String InDsNames, String outDsNames)  throws Exception {
    	String  URL="https://kapi.kakao.com/v2/api/talk/memo/default/send";
    	DataSet OUT_DS = new DataSet();
    	/*상태 */
    	DataTable OUT_RST = OUT_DS.addTable("OUT_RST");
    	OUT_RST.addColumn("URL");
    	OUT_RST.addColumn("QUERY_STRING");
    	OUT_RST.addColumn("JSON_OUT");
    	OUT_RST.addColumn("STATUS");  // E에러   S 성공
    	OUT_RST.addColumn("ERR_MSG");
    	OUT_RST.addColumn("ERR_CODE");  //100  외부 api 오류  200 내부오류
    	OUT_RST.addColumn("ERR_STACK_TRACE");

    	DataTable OUT_RSET = OUT_DS.addTable("OUT_RSET");
    	OUT_RSET.addColumn("RESULT_CODE");
    	
    
    	
    	
    
    	DataTable IN_PSET =InDs.getTable("IN_PSET");
    	String TEXT ="";
    	String WEB_URL =""; 
        if(IN_PSET.getRowCount()>0) {
    		TEXT = IN_PSET.getRow(0).getStringNullToEmpty("TEXT");
    		WEB_URL = IN_PSET.getRow(0).getStringNullToEmpty("WEB_URL");
    	}

        HashMap<String, String> json_link_params = new HashMap<>();
        json_link_params.put("web_url", WEB_URL);

    	HashMap<String, Object> json_params = new HashMap<>();
		json_params.put("object_type", "text");
		json_params.put("text", TEXT);
        json_params.put("link", json_link_params);
        json_params.put("button_title", "GO");

        
		
        String template_object =PjtUtil.g().ObjectToJsonString(json_params);
        HashMap<String, String> params = new HashMap<>();
		params.put("template_object", template_object);

		ArrayList<String> queryElements = new ArrayList<>();
	    for(Map.Entry<String, String> entity : params.entrySet()) {
            queryElements.add(entity.getKey() + "=" + entity.getValue());
        }

        String queryString = String.join("&", queryElements.toArray(new String[0]));
        System.out.println("queryString=>"+queryString);


    	DataTable dtKey =InDs.getTable("IN_KEY");
    	String KAKAO_API_ACCESS_KEY =null;
    	if(dtKey.getRowCount()>0) {
    		KAKAO_API_ACCESS_KEY = dtKey.getRow(0).getStringNullToEmpty("KAKAO_API_ACCESS_KEY");
    	}

	    DataRow drRst = OUT_RST.addRow();
	    drRst.setString("URL", URL);
	    drRst.setString("QUERY_STRING", queryString);

	    if(PjtUtil.g().isEmpty(KAKAO_API_ACCESS_KEY)){
            //에러처리
			drRst.setString("JSON_OUT", "");
			drRst.setString("STATUS", "E");
			drRst.setString("ERR_MSG", "KAKAO_API_ACCESS_KEY-API키가 인풋으로 넘어오지 않았습니다.");
			drRst.setString("ERR_CODE", "200");
			drRst.setString("ERR_STACK_TRACE", "");

			return OUT_DS;
	    }

	    String jsonOutString = "";
    	HttpUtilKakao  httpU = new HttpUtilKakao();
    	
    	
        try {
			jsonOutString = httpU.httpPostKakaoApi(KAKAO_API_ACCESS_KEY,URL,params);
		} catch (NoSuchAlgorithmException | URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();
			//System.out.println(exceptionAsString);
			//출처: https://blog.miyam.net/81 [낭만 프로그래머]

			drRst.setString("JSON_OUT", "");
			drRst.setString("STATUS", "E");
			drRst.setString("ERR_MSG", e.getMessage());
			drRst.setString("ERR_CODE", "100");
			drRst.setString("ERR_STACK_TRACE", exceptionAsString);
			return OUT_DS;
		} catch (BizException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();
			//System.out.println(exceptionAsString);
			//출처: https://blog.miyam.net/81 [낭만 프로그래머]

			drRst.setString("JSON_OUT", jsonOutString);
			drRst.setString("STATUS", "E");
			drRst.setString("ERR_MSG", e.getMessage());
			drRst.setString("ERR_CODE", "200");
			drRst.setString("ERR_STACK_TRACE", exceptionAsString);
			return OUT_DS;
		}
        drRst.setString("JSON_OUT", jsonOutString);

        HashMap<String,Object> c = null;
		try {
			c = PjtUtil.g().JsonStringToObject(jsonOutString, HashMap.class);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();
			//System.out.println(exceptionAsString);
			//출처: https://blog.miyam.net/81 [낭만 프로그래머]

			drRst.setString("JSON_OUT", "");
			drRst.setString("STATUS", "E");
			drRst.setString("ERR_CODE", "200");
			drRst.setString("ERR_MSG", e.getMessage());
			drRst.setString("ERR_STACK_TRACE", exceptionAsString);

			return OUT_DS;
		}
		drRst.setString("STATUS", "S");
		
		if(c!=null){				
			DataRow dr = OUT_RSET.addRow();
			dr.setString("RESULT_CODE",c.get("result_code").toString());		
		}
        return OUT_DS;
    }

}
