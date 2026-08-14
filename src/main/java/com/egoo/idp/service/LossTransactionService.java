package com.egoo.idp.service;


import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestParam;

public interface LossTransactionService {

    JSONObject check(String opt,String DCMTST,String DCMTTP,String DRAWTYPE);

    JSONObject debitLoss(String transServiceCode,String CARDNO,String DCMTNO,String DCMTST,String IDNO,String DCMTTP,String u_ani,String u_connid);

    JSONObject creditRepostloss(String transServiceCode,String CARDNO,String u_ani,String u_connid);

}
