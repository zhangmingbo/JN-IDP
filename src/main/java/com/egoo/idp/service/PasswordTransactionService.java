package com.egoo.idp.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public interface PasswordTransactionService {

    JSONObject passEncrypt(String transServiceCode,String CARDNO,String PINBLOCK,String opt_type,String u_ani,String u_connid);

    JSONObject debitCheck(String transServiceCode,String CARDNO,String PINBLOCK,Integer num,String u_ani,String u_connid);

    JSONObject debitSet(String transServiceCode,String u_ani,String u_connid,String CARDNO,String CUSTIDNO,String IDNO,String PINBLOCK);

    JSONObject creditSet(String transServiceCode,String u_ani,String u_connid,String CARDNO,String IDNO,String var_date,String PINBLOCK,String PINBLOCK_old,String cvalId);

    JSONObject checkWeakPassword(String u_ani,String u_connid,String CARDNO,String PINBLOCK);

    JSONObject creditCheck(String transServiceCode,String CARDNO,String PINBLOCK,String u_ani,String u_connid,Integer num);

    JSONObject getEcif(String transServiceCode,String CARDNO,String u_ani,String  u_connid);

    JSONObject debitQueryCheck(String transServiceCode,String CARDNO,String PINBLOCK,String u_ani,String u_connid,Integer num);

    JSONObject creditQueryCheck(String transServiceCode,String CARDNO,String PINBLOCK,String u_ani,String u_connid,Integer num);

    JSONObject debitQuerySet(String transServiceCode,String CARDNO,String CUSTIDNO,String IDNO,String MIMAZLEII,String PINBLOCK,String PINBLOCK_old,String u_ani,String u_connid);

    JSONObject creditQuerySet(String transServiceCode,String CARDNO,String IDNO,String PINBLOCK,String PINBLOCK_old,String u_ani,String u_connid,String PINBLOCKFLAG);

    JSONObject debitQueryReset(String transServiceCode,String CARDNO,String ECIFCUSTNO,String IDNO,String PINBLOCK,String u_ani,String u_connid);
}
