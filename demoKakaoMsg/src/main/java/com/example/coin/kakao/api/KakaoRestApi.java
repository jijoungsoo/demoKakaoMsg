package com.example.coin.kakao.api;

import com.example.coin.kakao.KakaoSendTextToMe;

import dataset.DataSet;
import running.common.SAProxy;

public class KakaoRestApi extends SAProxy {
    public DataSet KakaoTalkMemoSendText(DataSet InDs,String InDsNames, String outDsNames) throws Exception {
    	KakaoSendTextToMe tmp = new KakaoSendTextToMe();
    	DataSet OUT_DS =tmp.MsgSendTextToMe(InDs,InDsNames, outDsNames);
        return OUT_DS;
    } 
}
