package com.egoo.idp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.egoo.idp.service.LossTransactionService;
import com.egoo.idp.service.MenuTransactionService;
import com.egoo.idp.utils.StrUtil;
import com.egoo.idp.utils.TimeUtil;
import com.egoo.idp.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class LossTransactionServiceImpl implements LossTransactionService {

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;

    @Value("${gateway.transaction.requestUrl}")
    private String requestUrl;


    @Autowired
    public LossTransactionServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        // 初始化HTTP头信息
        this.httpHeaders = new HttpHeaders();
        this.httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public JSONObject check(String opt,String DCMTST,String DCMTTP,String DRAWTYPE) {
        JSONObject result = null;
        try {
            result = new JSONObject();
            if(ObjectUtils.isEmpty(DCMTTP)){
                result.put("accounttype", "0");
            }else{
                if("1".equals(opt)){
                    result.put("accounttype", determineDebit(DCMTTP));
                    checkDcmtst(result,DCMTST);
                }else if("3".equals(opt)){
                    result.put("accounttype", determineDepositBook(DCMTTP));
                    checkDcmtst(result,DCMTST);
                }else if("4".equals(opt)){
                    result.put("accounttype", determineAccountList(DCMTTP));
                    checkDcmtst(result,DCMTST);
                }else {
                    result.put("accounttype", "0");
                }
                if(!ObjectUtils.isEmpty(DRAWTYPE)){
                    // 检查 DRAWTYPE 是否为 N、E 或 H（不区分大小写）
                    boolean isValidDrawType = "N".equalsIgnoreCase(DRAWTYPE) ||
                            "E".equalsIgnoreCase(DRAWTYPE) ||
                            "H".equalsIgnoreCase(DRAWTYPE);
                    String accounttype = (String)result.get("accounttype");
                    if(isValidDrawType && "1".equals(accounttype)){
                        result.put("accounttype", "2");
                    }
                }
            }
        } catch (Exception e) {
            log.error("检查卡是否挂失异常",e);
        }
        return result;
    }

    void checkDcmtst(JSONObject result,String DCMTST){
        if("1".equals(result.get("accounttype")) && ("1".equals(DCMTST) || "4".equals(DCMTST))){
            result.put("accounttype","0");
        }
    }

    @Override
    public JSONObject debitLoss(String transServiceCode, String CARDNO, String DCMTNO, String DCMTST, String IDNO, String DCMTTP, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try{

            //-----user define
            //String transcode = "pcva.trade.b038.01";

            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("ACCNO",CARDNO);
            temp.put("DCMTST",DCMTST);
            temp.put("DCMTNO",DCMTNO);
            temp.put("IDNO",IDNO);
            temp.put("IDTYPE","10101");
            temp.put("DCMTTP",DCMTTP);
            temp.put("RPLSTP","1");
            defObj.put("REQ_BODY",temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode","true");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
            if(!jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                result.put("ReturnCode","false");
            }
        }
        catch(Exception e)
        {
            log.error("借记卡挂失异常",e);
        }
        return result;
    }

    @Override
    public JSONObject creditRepostloss(String transServiceCode, String CARDNO, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try{
            //String transcode = "pcva.ccard.ccd010.01";

            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("CARDNO",CARDNO);
            temp.put("USERID","000000");
            temp.put("REASON","FROM 96005");
            temp.put("CARDCODE","5");
            defObj.put("REQ_BODY",temp);
            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode","true");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
            if(!jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                result.put("ReturnCode","false");
            }
        }
        catch(Exception e)
        {
            log.error("信用卡口头挂失异常",e);

        }
        return result;
    }


    /**
     * 发送请求到后端服务
     */
    private JSONObject sendRequest(JSONObject requestJson) {
        try {
            HttpEntity<String> entity = new HttpEntity<>(requestJson.toString(), httpHeaders);
            log.info("发送请求: URL={}, 请求体={}", requestUrl, requestJson);
            String res = restTemplate.postForObject(requestUrl, entity, String.class);
            log.info("交易返回 ={}", res);
            return JSONObject.parseObject(res);
        } catch (Exception e) {
            log.error("请求异常:", e);
            throw new RuntimeException("服务调用失败", e);
        }
    }


    private String determineDebit(String DCMTTP) throws Exception{
        int i_DCMTTP =  Integer.parseInt(DCMTTP);
        switch (i_DCMTTP){
            case 744:
                return "1";
            case 738:
                return "1";
            case 742:
                return "1";
            case 745:
                return "1";
            case 746:
                return "1";
            case 749:
                return "1";
            case 757:
                return "1";
            case 758:
                return "1";
            case 759:
                return "1";
            case 760:
                return "1";
            case 748:
                return "1";
            case 762:
                return "1";
            case 767:
                return "1";
            case 750:
                return "1";
            default:
                return "false";
        }
    }

    private String determineDepositBook(String DCMTTP) throws Exception{
        int i_DCMTTP =  Integer.parseInt(DCMTTP);
        switch (i_DCMTTP){
            case 731:
                return "1";
            case 732:
                return "1";
            case 734:
                return "1";
            default:
                return "false";
        }
    }

    private String determineAccountList(String DCMTTP) throws Exception{
        int i_DCMTTP =  Integer.parseInt(DCMTTP);
        switch (i_DCMTTP) {
            case 302:
                return "1";
            case 737:
                return "1";
            default:
                return "false";
        }
    }
}


