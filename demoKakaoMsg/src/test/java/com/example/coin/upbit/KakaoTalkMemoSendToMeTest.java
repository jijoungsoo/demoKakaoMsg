package com.example.coin.upbit;

import static org.junit.jupiter.api.Assertions.fail;

import com.example.coin.kakao.KakaoTalkMemoSendToMe;

import org.junit.Test;

import dataset.DataRow;
import dataset.DataSet;
import dataset.DataTable;
import dataset.converter.DotNetXmlDataSetConverter;

public class KakaoTalkMemoSendToMeTest {

	@Test
	public void testExchangePostOrders() {
		KakaoTalkMemoSendToMe  tmp = new KakaoTalkMemoSendToMe();
		
		DataSet IN_DS = new DataSet();
    	DataTable IN_PSET = IN_DS.addTable("IN_PSET");
    	IN_PSET.addColumn("TEXT");
    	IN_PSET.addColumn("WEB_URL");
    	    	
    	DataRow DR_IN_PSET = IN_PSET.addRow();
    	DR_IN_PSET.setString("TEXT", "테스트 가으자");
    	DR_IN_PSET.setString("WEB_URL", "http://www.naver.com");

        DataTable IN_KEY = IN_DS.addTable("IN_KEY");
    	IN_KEY.addColumn("KAKAO_API_ACCESS_KEY");

        String accessKey = System.getenv("KAKAO_API_ACCESS_KEY");
        DataRow drKey = IN_KEY.addRow();
        drKey.setString("KAKAO_API_ACCESS_KEY", accessKey);
		DataSet OUT_DS;
		try {
			OUT_DS = tmp.MsgSendTextToMe( IN_DS, null, null);
			String inString =DotNetXmlDataSetConverter.convertFromDataSet(IN_DS);
			System.out.println(inString);
			String outString =DotNetXmlDataSetConverter.convertFromDataSet(OUT_DS);
			System.out.println(outString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
