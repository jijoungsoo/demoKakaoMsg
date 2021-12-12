package com.example.coin.upbit;

import com.example.coin.kakao.KakaoLogin;
import org.junit.Test;
import dataset.DataRow;
import dataset.DataSet;
import dataset.DataTable;
import dataset.converter.DotNetXmlDataSetConverter;

public class KakaoLoginTest {

	@Test
	public void testLogin() {
		KakaoLogin  tmp = new KakaoLogin();
		
		DataSet IN_DS = new DataSet();
    	DataTable IN_PSET = IN_DS.addTable("IN_PSET");
    	IN_PSET.addColumn("REST_API_KEY");
    	IN_PSET.addColumn("REDIRECT_URI");
    	    	
    	DataRow DR_IN_PSET = IN_PSET.addRow();
    	DR_IN_PSET.setString("REST_API_KEY", "");
    	DR_IN_PSET.setString("REDIRECT_URI", "11");

        DataSet OUT_DS;
		try {
			OUT_DS = tmp.Login( IN_DS, null, null);
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
