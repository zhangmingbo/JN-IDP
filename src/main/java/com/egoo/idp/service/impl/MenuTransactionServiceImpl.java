package com.egoo.idp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
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
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static com.egoo.idp.utils.Util.change;

@Service
@Slf4j
public class MenuTransactionServiceImpl implements MenuTransactionService {

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;

    @Value("${gateway.transaction.requestUrl}")
    private String requestUrl;


    @Autowired
    public MenuTransactionServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        // 初始化HTTP头信息
        this.httpHeaders = new HttpHeaders();
        this.httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }


    @Override
    public JSONObject queryPass(String transServiceCode, String CARDNO, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try {
            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode, u_ani, u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("CARDNO", CARDNO);
            defObj.put("REQ_BODY", temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode", "false");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));

            if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                result.put("ALREADYSETFLAG",jsonObj.getJSONObject("RSP_BODY").getString("ALREADYSETFLAG"));
                result.put("IDNO",jsonObj.getJSONObject("RSP_BODY").getString("IDNO"));
                result.put("CARDNO",CARDNO);
                result.put("ReturnCode", "true");
            }
            return result;
        }
        catch(Exception e) {
            log.error("查询密码异常",e);
        }
        return result;
    }

    @Override
    public JSONObject updateDate(String transServiceCode, String ACCNO, String BILLCYCLE, String u_ani, String u_connid) {

        JSONObject result = new JSONObject();
        try {
           // String transcode = "pcva.ccard.ccd076.01";//客户信息更新

            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode, u_ani, u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("BILLCYCLE", BILLCYCLE);
            temp.put("ACCNO", ACCNO);
            temp.put("OPERID", "96005");
            defObj.put("REQ_BODY", temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);
            String prompt = "";
            //-----ReturnValue
            result.put("ReturnCode", "false");
            result.put("ConsumerSeqNo", JSONPath.eval(jsonObj,"$.SYS_HEAD.ConsumerSeqNo"));
            result.put("ReturnMessage", StrUtil.paseStrUTF8((String)JSONPath.eval(jsonObj,"$.SYS_HEAD.ReturnMessage")));

            if (JSONPath.eval(jsonObj,"$.SYS_HEAD.ReturnCode").equals("000000")) {
                result.put("ReturnCode", "true");
                prompt = "操作成功,您信用卡账户于" + TimeUtil.getNowDateStr() + "修改账单日为每月" + BILLCYCLE +"日，所有信用卡账户将同步更新，包括标准卡账户，融通分期账户，京东联名标准卡账户，于下一个账单日生效。";
            } else {
                prompt = Util.resolveReturnMessage((String)JSONPath.eval(jsonObj,"$.SYS_HEAD.ReturnMessage"));
            }
            result.put("prompt", prompt);
        }
        catch(Exception e)
        {
            log.error("修改账单日期失败",e);
        }
        return result;
    }

    @Override
    public JSONObject updateAdress(String transServiceCode, String ACCNO, String BILLADDRCD, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try {
            //String transcode = "pcva.ccard.ccd076.01";//客户信息更新

            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode, u_ani, u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("BILLADDRCD", BILLADDRCD);//H-家庭C-公司O-其他
            temp.put("ACCNO", ACCNO);
            temp.put("OPERID", "96005");
            defObj.put("REQ_BODY", temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);
            String prompt = "";
            //-----ReturnValue
            result.put("ReturnCode", "false");
            result.put("ConsumerSeqNo", defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage", StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));

            if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                prompt = "账单地址变更成功";
                result.put("ReturnCode", "true");
            } else {
                prompt = Util.resolveReturnMessage(StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
            }
            result.put("prompt", prompt);
        }
        catch(Exception e)
        {
            log.error("修改账单地址异常",e);
        }
        return result;
    }

    @Override
    public JSONObject outstandingBill(String transServiceCode, String CARDNO, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try{
            //String transcode = "pcva.ccard.ccd088.01";

            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode, u_ani, u_connid);
            JSONObject temp = new JSONObject();

            //-----user define

            temp.put("CURRENCY", "0");
            temp.put("CARDNO", CARDNO);
            temp.put("BILLDATEBEG", Util.getYYYYMMbefore(11));
            temp.put("BILLDATEEND", Util.getYYYYMMbefore(-1));
            temp.put("pagerownum", "12");
            String prompt = "共查询到";
            defObj.put("REQ_BODY",temp);
            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode","false");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));

            if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                result.put("ReturnCode","true");
                int calli = 0;
                int size = jsonObj.getJSONObject("RSP_BODY").getJSONArray("RspStruct").size();
                int callt = Util.getBigNum(size);

                prompt =  prompt + size + "条账单记录,";

                for (calli = 0; calli < callt; calli++) {
                    JSONObject jsonObjCall = jsonObj.getJSONObject("RSP_BODY").getJSONArray("RspStruct").getJSONObject(calli);
                    prompt = prompt + "第" + (calli + 1) + "条" +  "账单日:" + jsonObjCall.getString("PAYDATE").substring(0,4) + "年"+ jsonObjCall.getString("PAYDATE").substring(4,6)+"月"+ jsonObjCall.getString("PAYDATE").substring(6,8)+"日" + "账单金额:" + change(jsonObjCall.getString("CTDPMT"));
                    if(jsonObjCall.getString("CTDPMT").equals("0"))
                    {
                        prompt = prompt + ",";
                    }else{
                        prompt = prompt + "最低还款金额:" + change(jsonObjCall.getString("MINPMT")) + "最后还款日为:" + jsonObjCall.getString("PMTDUEDATE").substring(0,4) + "年"+ jsonObjCall.getString("PMTDUEDATE").substring(4,6)+"月"+ jsonObjCall.getString("PMTDUEDATE").substring(6,8)+"日"+ ",";
                    }
                }
                StrUtil.capturePrompt(prompt,result);

            }
        }
        catch(Exception e)
        {
            log.error("查询已出账单账单",e);
        }
        return result;
    }

    @Override
    public JSONObject unbilled(String transServiceCode, String ACCNO, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try {

            //String transcode = "pcva.ccard.ccd018.01";


            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode, u_ani, u_connid);
            JSONObject temp = new JSONObject();

            //-----user define

            temp.put("CURRENCY", "0");
            temp.put("ACCNO", ACCNO);
            temp.put("pagerownum", "1000");
            String prompt = "共查询到";
            defObj.put("REQ_BODY", temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode", "false");
            result.put("ConsumerSeqNo", defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage", StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
            System.out.println(jsonObj);
            if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                result.put("ReturnCode", "true");
                int calli = 0;
                int size = jsonObj.getJSONObject("RSP_BODY").getJSONArray("RspStruct").size();
                int callt = Util.getBigNum(size);
                prompt = prompt + size + "条记录,";
                // 逆序
                int num = 0;
                for (calli = callt, num = 0; calli != 0 && num <10; calli--,num++) {
                    JSONObject jsonObjCall = jsonObj.getJSONObject("RSP_BODY").getJSONArray("RspStruct").getJSONObject(calli - 1);
                    prompt = prompt + "第" + (num + 1) + "条" + "交易时间:" + jsonObjCall.getString("TRANDATE").substring(0, 4) + "年" + jsonObjCall.getString("TRANDATE").substring(4, 6) + "月" + jsonObjCall.getString("TRANDATE").substring(6, 8) + "日" + "交易金额:" + change(jsonObjCall.getString("AMOUNT")) + "交易描述:" + StrUtil.paseStrUTF8(jsonObjCall.getString("MERCNAME")).trim() + "" + StrUtil.paseStrUTF8(jsonObjCall.getString("DESC")) + ",";
                }
                StrUtil.capturePrompt(prompt,result);

            } else if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000001")) {
                prompt = "未查到记录 ";
                result.put("prompt", prompt);
            }
        } catch (Exception e)
        {
            log.error("未出账单异常",e);
        }
        return result;
    }

    @Override
    public JSONObject getAddress(String transServiceCode, String IDTYPE, String IDNO, String ACCNO, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try{

            //String transcode = " pcva.trade.ccd014.01";
            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("IDTYPE",IDTYPE);
            temp.put("IDNO",IDNO);
            temp.put("currpage","1");
            defObj.put("REQ_BODY",temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode", "false");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
            if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                result.put("ReturnCode", "true");
                int calli = 0;
                int callt = jsonObj.getJSONObject("RSP_BODY").getJSONArray("RspStruct").size();

                for (calli = 0; calli < callt; calli++) {
                    JSONObject jsonObjCall = jsonObj.getJSONObject("RSP_BODY").getJSONArray("RspStruct").getJSONObject(calli);
                    //System.out.println("--------------------jsonObjCall="+jsonObjCall);
                    if(jsonObjCall.getString("ACCTNO").equals(ACCNO)){
                        result.put("BILLADDRCD", jsonObjCall.getString("BILLADDRCD"));
                        result.put("BILLADDR", StrUtil.paseStrUTF8(jsonObjCall.getString("BILLADDR")));
                        result.put("BILLCYCLE", jsonObjCall.getString("BILLCYCLE"));
                    }
                }

            } else {

            }
        }
        catch(Exception e)
        {
            return result;
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
}


