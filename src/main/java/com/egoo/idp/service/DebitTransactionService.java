package com.egoo.idp.service;


import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestParam;


public interface DebitTransactionService {

    /**
     * 处理通用交易
     * @param var_input 交易代码
     * @return 交易结果
     */
    JSONObject idOrCardInquire(String var_input,String opt, String u_ani, String u_connid);

    JSONObject queryCard(String CARDNOS,String CARDNO_SUFF);

    JSONObject queryPassword(String transServiceCode, String CARDNO, String u_ani, String u_connid);

    JSONObject personBankOfDeposit(String transServiceCode,String ACCTBRNO, String u_ani, String u_connid);

    JSONObject businessBankOfDeposit(String transServiceCode,String CARDNO, String u_ani, String u_connid);

    JSONObject balance(String transServiceCode, String CARDNO, String CUSTNO, String ONLNBL,String AVAILBL,String u_ani, String u_connid);

    JSONObject cardCheck(String transServiceCode,String debit_cardno,String credit_idno,String u_ani,String u_connid);

    JSONObject cardMenu(String transServiceCode,String CARDNO,String day,String u_ani,String u_connid);

    JSONObject phoneCompare(String transServiceCode,String ECIFCUSTNO,String u_ani,String u_connid);

    JSONObject checkCardLength(String cardNo);

}
