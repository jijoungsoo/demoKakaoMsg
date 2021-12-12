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
public class KakaoLogin extends SAProxy {

    public DataSet Login(DataSet InDs,String InDsNames, String outDsNames)  throws Exception {
        //GET /oauth/authorize?client_id={REST_API_KEY}&redirect_uri={REDIRECT_URI}&response_type=code HTTP/1.1
        //Host: kauth.kakao.com

        /*프로젝트 포기 ------ 인증을  콜백형태로 가져온다. 중간에 웹페이지가 있음 구현 불가 */
    	String  URL="https://kauth.kakao.com/oauth/authorize";
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
    	OUT_RSET.addColumn("CODE");
        OUT_RSET.addColumn("STATE");
        OUT_RSET.addColumn("ERROR");
        OUT_RSET.addColumn("ERROR_DESCRIPTION");

        /*
        Error Message
        error	error_description	Description
        access_denied	User denied access	사용자가 로그인을 취소한 것이므로, 초기 화면으로 이동시키는 등의 조치가 필요합니다.
        access_denied	Not allowed under age 14	만 14세 미만 사용자의 보호자 동의에 실패한 것이므로, 초기 화면으로 이동시키는 등의 조치가 필요합니다.
        login_required	user authentication required.	prompt 파라미터 값을 none으로 전달했으나, 사용자의 카카오계정 인증이 필요한 경우입니다.
        consent_required	user authentication required.	prompt 파라미터 값을 none으로 전달했으나, 동의 화면을 통한 사용자 동의가 필요한 경우입니다.
        interaction_required	need to collect additional personal information.	prompt 파라미터 값을 none으로 전달했으나, 카카오계정 약관 동의 또는 사용자 정보 수집 등 기타 사용자 동작이 필요한 경우입니다.
        */
    
    	DataTable IN_PSET =InDs.getTable("IN_PSET");
    	String REST_API_KEY ="";
    	String REDIRECT_URI =""; 
        if(IN_PSET.getRowCount()>0) {
    		REST_API_KEY = IN_PSET.getRow(0).getStringNullToEmpty("REST_API_KEY");
    		REDIRECT_URI = IN_PSET.getRow(0).getStringNullToEmpty("REDIRECT_URI");
    	}

        DataRow drRst = OUT_RST.addRow();
        if(PjtUtil.g().isEmpty(REST_API_KEY)){
            //에러처리
			drRst.setString("JSON_OUT", "");
			drRst.setString("STATUS", "E");
			drRst.setString("ERR_MSG", "REST_API_KEY-API키가 인풋으로 넘어오지 않았습니다.");
			drRst.setString("ERR_CODE", "200");
			drRst.setString("ERR_STACK_TRACE", "");

			return OUT_DS;
	    }

        if(PjtUtil.g().isEmpty(REDIRECT_URI)){
            //에러처리
			drRst.setString("JSON_OUT", "");
			drRst.setString("STATUS", "E");
			drRst.setString("ERR_MSG", "REDIRECT_URI-API키가 인풋으로 넘어오지 않았습니다.");
			drRst.setString("ERR_CODE", "200");
			drRst.setString("ERR_STACK_TRACE", "");

			return OUT_DS;
	    }


    	HashMap<String, String> params = new HashMap<>();
		params.put("client_id", REST_API_KEY);
		params.put("redirect_uri", REDIRECT_URI);
        params.put("response_type","code");
        params.put("prompt","none");
        params.put("scope","talk_message");
        /*
        prompt	String	동의 화면 요청 시 추가 상호작용을 요청하고자 할 때 전달하는 파라미터
        쉼표(,)로 구분된 문자열 값 목록으로 전달
        다음 값 사용 가능:
        login: 기존 사용자 인증 여부와 상관없이 사용자에게 카카오계정 로그인 화면을 출력하여 다시 사용자 인증을 수행하고자 할 때 사용, 카카오톡 인앱 브라우저에서는 이 기능이 제공되지 않음
        none: 사용자에게 동의 화면과 같은 대화형 UI를 노출하지 않고 인가 코드 발급을 요청할 때 사용, 인가 코드 발급을 위해 사용자의 동작이 필요한 경우 에러 응답 전달
        */

		ArrayList<String> queryElements = new ArrayList<>();
	    for(Map.Entry<String, String> entity : params.entrySet()) {
            queryElements.add(entity.getKey() + "=" + entity.getValue());
        }

        String queryString = String.join("&", queryElements.toArray(new String[0]));
        System.out.println("queryString=>"+queryString);


	    
	    drRst.setString("URL", URL);
	    drRst.setString("QUERY_STRING", queryString);


	    String jsonOutString = "";
    	HttpUtilKakao  httpU = new HttpUtilKakao();
    	
    	
        try {
			jsonOutString = httpU.httpGetKakaoApi(URL,queryString);
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
