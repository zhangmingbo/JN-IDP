package com.egoo.idp.service;


import com.alibaba.fastjson.JSONObject;


public interface CreditTransactionService {

    /**
     * 处理通用交易
     * @param transServiceCode 交易代码
     * @param IDNO 所有请求参数
     * @return 交易结果
     */
    JSONObject creditProgress(String transServiceCode, String IDNO, String u_ani, String u_connid);

    /**
     * 处理通用交易
     * @param var_input 交易代码
     * @return 交易结果
     */
    JSONObject idOrCardInquire(String var_input, String u_ani, String u_connid);


    JSONObject cardStatus(String transServiceCode,String CARDNO, String u_ani, String u_connid);


    JSONObject getVisaInterview(String transServiceCode,String CARDNO, String u_ani, String u_connid);

    JSONObject encryptcvv2(String var_input,String opt_type);

    JSONObject checkcvv2ValidDate(String transServiceCode,String CARDNO,String IDNO,String var_cvv2_encrypt,String var_validdate_encrypt, String u_ani, String u_connid);


    JSONObject getCreditLimit(String transServiceCode,String IDNO,String u_ani, String u_connid);

    JSONObject cardVerifi(String transServiceCode,String CARDNO,String u_ani, String u_connid);

    JSONObject automaticPay(String transServiceCode,String ACCNO,String DEBITCARD,String AUTPMTTYPE,String u_ani, String u_connid);

    JSONObject tranDetailSing(String transServiceCode,String CARDNO,String u_ani, String u_connid);

    JSONObject tranDetailMult(String transServiceCode,String ACCNO,String day,String u_ani, String u_connid);

    JSONObject getActivaRes(String transServiceCode,String CARDNO,String IDNO,String validdate,String u_ani,String u_connid);



}
