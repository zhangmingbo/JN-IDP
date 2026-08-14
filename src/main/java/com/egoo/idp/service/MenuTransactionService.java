package com.egoo.idp.service;


import com.alibaba.fastjson.JSONObject;

public interface MenuTransactionService {


    JSONObject queryPass(String transServiceCode, String CARDNO, String u_ani, String u_connid);

    JSONObject updateDate(String transServiceCode, String ACCNO, String BILLCYCLE, String u_ani, String u_connid);

    JSONObject updateAdress(String transServiceCode, String ACCNO, String BILLADDRCD, String u_ani, String u_connid);

    JSONObject outstandingBill(String transServiceCode, String CARDNO, String u_ani, String u_connid);

    JSONObject unbilled(String transServiceCode, String ACCNO, String u_ani, String u_connid);

    JSONObject getAddress(String transServiceCode,String IDTYPE, String IDNO,String ACCNO,String u_ani,String u_connid);



}
