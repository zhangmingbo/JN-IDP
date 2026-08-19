package com.egoo.idp.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.egoo.idp.service.CreditTransactionService;
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
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.egoo.idp.utils.Util.change;

@Service
@Slf4j
public class CreditTransactionServiceImpl implements CreditTransactionService {

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;

    @Value("${gateway.transaction.requestUrl}")
    private String requestUrl;

    private static final int KEY = 7;

    @Autowired
    public CreditTransactionServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        // 初始化HTTP头信息
        this.httpHeaders = new HttpHeaders();
        this.httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public JSONObject creditProgress(String transServiceCode, String IDNO, String u_ani, String u_connid) {

        JSONObject result = null;
        try {
            result = new JSONObject();
            result.put("ReturnMessage", "false");
            JSONObject defObj = Util.getBasicJson(transServiceCode, u_ani, u_connid);
            JSONObject temp = new JSONObject();
            //-----user define
            temp.put("IDTYPE", "10101");
            temp.put("pagerownum", "100");
            temp.put("IDNO", IDNO);
            defObj.put("REQ_BODY", temp);
            // 4. 发送请求
            JSONObject jsonObj = sendRequest(defObj);
            String prompt = "共查询到";
            //-----ReturnValue
            result.put("ConsumerSeqNo", defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));

            if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                result.put("ReturnCode", "true");
                if(jsonObj.getJSONObject("RSP_BODY").getString("TotalNum").equals("0")) {
                    prompt = "暂未查询到您的信用卡申请进度,如有疑问,请转接人工服务";
                }else{
                    int calli = 0;
                    int callt = jsonObj.getJSONObject("RSP_BODY").getJSONArray("RspStruct").size();
                    prompt = prompt + jsonObj.getJSONObject("RSP_BODY").getString("TotalNum") + "条记录,";
                    for (calli = 0; calli < callt; calli++) {
                        JSONObject jsonObjCall = jsonObj.getJSONObject("RSP_BODY").getJSONArray("RspStruct").getJSONObject(calli);
                        prompt = prompt + "第" + (calli + 1) + "条:" + " 申请日期:" + jsonObjCall.getString("CREATEDATE").substring(0, 4) + "年" + jsonObjCall.getString("CREATEDATE").substring(4, 6) + "月" + jsonObjCall.getString("CREATEDATE").substring(6, 8) + "日 ";
                        prompt = prompt  +StrUtil.paseStrUTF8(jsonObjCall.getString("ABSTDESC"))+ " 申请状态:" + Util.swtichAPPLICATIONSTATUS(StrUtil.paseStrUTF8(jsonObjCall.getString("APPLICATIONSTATUS"))) + ",";
                    }
                }
            } else {
                prompt = "暂未查询到您的信用卡申请进度,如有疑问,请转接人工服务";
            }
            StrUtil.capturePrompt(prompt, result);
        } catch (Exception e) {
            log.error("查询信用卡进度异常",e);
        }
        return result;
    }

    @Override
    public JSONObject idOrCardInquire(String var_input, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        if (var_input.length() == 18) {
            try {
                //-----user define
                String transcode = "pcva.ccard.ccd047.01";
                //usagi.jar
                JSONObject defObj = Util.getBasicJson(transcode, u_ani, u_connid);
                JSONObject temp = new JSONObject();
                //-----user define
                temp.put("IDNO", var_input);
                result.put("IDNO", var_input);
                temp.put("IDTYPE", "10101");
                defObj.put("REQ_BODY", temp);
                //usagi.jar
                JSONObject jsonObj = sendRequest(defObj);
                //-----ReturnValue
                result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
                result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
                result.put("ReturnCode","false");
                if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                    result.put("input_type","idno");
                    result.put("RECORDNUM", jsonObj.getJSONObject("RSP_BODY").getString("TotalNum"));
                    int tempi = Integer.valueOf(jsonObj.getJSONObject("RSP_BODY").getString("TotalNum"));
                    JSONArray jsonArray = jsonObj.getJSONObject("RSP_BODY").getJSONArray("RspStruct");
                    if (tempi == 1) {
                        result.put("CARDNO", jsonArray.getJSONObject(0).getString("CARDNO"));
                        result.put("CardType", "single");
                        result.put("ReturnCode", "true");
                    } else if(tempi > 1){
                        StringBuilder cardstr = new StringBuilder("|");
                        for (Object obj : jsonArray) {
                            JSONObject card = (JSONObject) obj;
                            cardstr.append(card.getString("CARDNO")+"|");
                        }
                        result.put("CARDNO", cardstr);
                        result.put("ReturnCode", "multi");
                        result.put("CardType", "multi");
                    }
                }
            } catch (Exception e) {
                log.error("获取多卡信息失败",e);
            }
        } else if (var_input.length() == 16) {
            try {
                //-----user define
                String transcode = "pcva.ccard.ccd048.01";

                //usagi.jar
                JSONObject defObj = Util.getBasicJson(transcode, u_ani, u_connid);
                JSONObject temp = new JSONObject();

                //-----user define
                temp.put("CARDNO", var_input);
                defObj.put("REQ_BODY", temp);

                //usagi.jar
                JSONObject jsonObj = sendRequest(defObj);
                result.put("ReturnCode","false");

                if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                    //-----ReturnValue
                    result.put("ReturnCode", "true");
                    result.put("IDNO", jsonObj.getJSONObject("RSP_BODY").getString("IDNO"));
                    result.put("input_type","cardno");
                    result.put("CARDNO", var_input);
                }
            } catch (Exception e) {
                log.error("查询单卡信息失败",e);
            }
        } else {
            result.put("RECORDNUM", "0");
            result.put("ReturnCode","false");
        }
        return result;
    }

    @Override
    public JSONObject cardStatus(String transServiceCode, String CARDNO, String u_ani, String u_connid) {

        JSONObject result = new JSONObject();
        try{
            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("CARDNO",CARDNO);

            defObj.put("REQ_BODY",temp);
            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode","true");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            //result.put("ReturnMessage","false");
            if(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                result.put("BSCSUPPIND",jsonObj.getJSONObject("RSP_BODY").getString("BSCSUPPIND"));
                result.put("PLASTICCD",jsonObj.getJSONObject("RSP_BODY").getString("PLASTICCD"));
                result.put("EXPIRYCCYYMM",jsonObj.getJSONObject("RSP_BODY").getString("EXPIRYCCYYMM"));
                result.put("CARDCODE",jsonObj.getJSONObject("RSP_BODY").getString("CARDCODE"));
                result.put("CARDNO",jsonObj.getJSONObject("RSP_BODY").getString("CARDNO"));
                if("S".equals(result.get("BSCSUPPIND"))){
                    result.put("ReturnCode","false");
                }
            }
        }
        catch(Exception e)
        {
            log.error("查询卡状态异常",e);
        }
        return result;
    }

    @Override
    public JSONObject getVisaInterview(String transServiceCode, String CARDNO, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try{
            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("CARDNO",CARDNO);
            defObj.put("REQ_BODY",temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode","false");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
            System.out.println(jsonObj);
            if(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                result.put("PHONE",jsonObj.getJSONObject("RSP_BODY").getString("PHONE").trim());
                result.put("IDNO",jsonObj.getJSONObject("RSP_BODY").getString("IDNO"));
                result.put("PLASTICCD",jsonObj.getJSONObject("RSP_BODY").getString("PLASTICCD"));
                result.put("PLASTICCDSTR",Util.getPlasticcdStr(jsonObj.getJSONObject("RSP_BODY").getString("PLASTICCD")));
                result.put("ACCNO", jsonObj.getJSONObject("RSP_BODY").getString("ACCNO"));
                result.put("IDTYPE", jsonObj.getJSONObject("RSP_BODY").getString("IDTYPE"));
                if(u_ani.equals(result.get("PHONE"))){
                    result.put("ReturnCode","true");
                }

            }
        }
        catch(Exception e)
        {
            return result;
        }
        return result;
    }

    @Override
    public JSONObject encryptcvv2(String var_input,String opt_type) {
        StringBuffer str2 = new StringBuffer();
        for(int i=0;i<var_input.length();i++)
        {
            char c = (char)(var_input.charAt(i) ^ KEY);
            str2.append(c);
        }

        JSONObject result = new JSONObject();
        result.put("var_"+opt_type+ "_encrypt",str2);
        result.put("retCode","true");
        //result.put("encrypt_value",state.getString("var_input"));
        return result;
    }

    @Override
    public JSONObject checkcvv2ValidDate(String transServiceCode, String CARDNO, String IDNO, String var_cvv2_encrypt, String var_validdate_encrypt, String u_ani, String u_connid) {

        JSONObject result = new JSONObject();
        try{
            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("CARDNO",CARDNO);
            temp.put("FINACIALNETDATA",var_cvv2_encrypt);
            temp.put("CVALID",StrUtil.reverSal(var_validdate_encrypt));
            temp.put("ADDDATA","1"+IDNO);
            defObj.put("REQ_BODY",temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode","false");
            if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
                result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
                result.put("ReturnCode","true");
            }else if(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("38")){
                result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
                result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
                result.put("ReturnCode","unknown");
            }
        }
        catch(Exception e)
        {
            log.error("校验查查cvv2异常",e);
        }
        return result;
    }

    /**
     * 根据身份证号获取卡号列表
     */
    private List<String> getCardNosByIdno(String IDNO, String u_ani, String u_connid) {
        List<String> cardNos = new ArrayList<>();
        try {
            String transcode = "pcva.ccard.ccd047.01";
            JSONObject defObj = Util.getBasicJson(transcode, u_ani, u_connid);
            JSONObject temp = new JSONObject();
            temp.put("IDNO", IDNO);
            temp.put("IDTYPE", "10101");
            defObj.put("REQ_BODY", temp);
            JSONObject jsonObj = sendRequest(defObj);
            if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                JSONArray jsonArray = jsonObj.getJSONObject("RSP_BODY").getJSONArray("RspStruct");
                for (int i = 0; i < jsonArray.size(); i++) {
                    String cardNo = jsonArray.getJSONObject(i).getString("CARDNO");
                    if (cardNo != null && !cardNo.trim().isEmpty()) {
                        cardNos.add(cardNo.trim());
                    }
                }
                log.info("[额度查询-v2] 卡号查询成功, 共{}张卡", cardNos.size());
            }
        } catch (Exception e) {
            log.warn("[额度查询-v2] 卡号查询异常, 将降级为序号播报", e);
        }
        return cardNos;
    }

    @Override
    public JSONObject getCreditLimit(String transServiceCode, String IDNO, String u_ani, String u_connid) {
        log.info("[额度查询-v2] 开始查询, IDNO={}, transServiceCode={}", IDNO, transServiceCode);
        JSONObject result = new JSONObject();
        try {
            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode, u_ani, u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("IDTYPE", "10101");
            temp.put("IDNO", IDNO);
            temp.put("currpage", "1");


            String prompt = " ";
            defObj.put("REQ_BODY", temp);
            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);
            //-----ReturnValue
            String resultString = jsonObj.toString();
            result.put("ReturnCode", "false");
            result.put("ConsumerSeqNo", JSONPath.eval(resultString, "$.SYS_HEAD.ConsumerSeqNo"));
            result.put("ReturnMessage", StrUtil.paseStrUTF8(JSONPath.eval(resultString, "$.SYS_HEAD.ReturnMessage").toString()));

            if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                result.put("ReturnCode", "true");
                /**
                 * CCARDLIMIT	是	信用额度
                 * CCARDAVAILLIMIT	是	可用额度
                 * CTDCASHAMOT	是	预借现金额度
                 * CCARDAVAILCASHLIMIT	是	可用现金额度
                 * STATUSCD 状态
                 *
                 */
                int totalPages = Integer.parseInt(jsonObj.getJSONObject("RSP_BODY").getString("TotalPages"));
                log.info("[额度查询-v2] 共{}页, 第一页{}张卡", totalPages, jsonObj.getJSONObject("RSP_BODY").getJSONArray("RspStruct").size());
                JSONArray allCards = new JSONArray();
                // 收集第一页数据
                for (int i = 0; i < jsonObj.getJSONObject("RSP_BODY").getJSONArray("RspStruct").size(); i++) {
                    allCards.add(jsonObj.getJSONObject("RSP_BODY").getJSONArray("RspStruct").getJSONObject(i));
                }
                // 翻页获取后续页数据
                for (int page = 2; page <= totalPages; page++) {
                    JSONObject defObjPage = Util.getBasicJson(transServiceCode, u_ani, u_connid);
                    JSONObject tempPage = new JSONObject();
                    tempPage.put("IDTYPE", "10101");
                    tempPage.put("IDNO", IDNO);
                    tempPage.put("currpage", String.valueOf(page));
                    defObjPage.put("REQ_BODY", tempPage);
                    JSONObject jsonObjPage = sendRequest(defObjPage);
                    if (jsonObjPage.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                        for (int i = 0; i < jsonObjPage.getJSONObject("RSP_BODY").getJSONArray("RspStruct").size(); i++) {
                            allCards.add(jsonObjPage.getJSONObject("RSP_BODY").getJSONArray("RspStruct").getJSONObject(i));
                        }
                    }
                }

                log.info("[额度查询-v2] 收集完成, 共{}张卡", allCards.size());

                // 通过身份证查卡号接口获取真实卡号列表
                List<String> cardNos = getCardNosByIdno(IDNO, u_ani, u_connid);
                log.info("[额度查询-v2] 卡号列表: {}", cardNos);

                if (allCards.size() == 1) {
                    JSONObject card = allCards.getJSONObject(0);
                    String statuscd = card.getString("STATUSCD");
                    if (statuscd == null || ObjectUtils.isEmpty(statuscd.trim())) {
                        // 优先用真实卡号, 降级用ACCTNO
                        String displayNo = (cardNos.size() > 0) ? cardNos.get(0) : card.getString("ACCTNO");
                        log.info("[额度查询-v2] 单卡 卡号={}, ACCTNO={}, CCARDLIMIT={}", displayNo, card.getString("ACCTNO"), card.getString("CCARDLIMIT"));
                        result.put("CARDNO", displayNo);
                        result.put("CCARDLIMIT", card.getString("CCARDLIMIT"));
                        result.put("CCARDAVAILLIMIT", card.getString("CCARDAVAILLIMIT"));
                        result.put("CTDCASHAMOT", card.getString("CTDCASHAMOT"));
                        result.put("CCARDAVAILCASHLIMIT", card.getString("CCARDAVAILCASHLIMIT"));
                        prompt = prompt + "您的信用卡信用额度为," + change(card.getString("CCARDLIMIT")) + " ";
                        prompt = prompt + "可用额度为," + change(card.getString("CCARDAVAILLIMIT")) + " ";
                        prompt = prompt + "预借现金额度为," + change(card.getString("CTDCASHAMOT")) + " ";
                        prompt = prompt + "可用现金额度为," + change(card.getString("CCARDAVAILCASHLIMIT")) + " ";
                    } else {
                        prompt = "没有查询到额度信息。";
                    }
                    StrUtil.capturePrompt(prompt, result);
                } else {
                    for (int calli = 0; calli < allCards.size(); calli++) {
                        try {
                            JSONObject card = allCards.getJSONObject(calli);
                            // 优先用真实卡号, 降级用ACCTNO, 再降级用序号
                            String displayNo = null;
                            if (calli < cardNos.size()) {
                                displayNo = cardNos.get(calli);
                            }
                            if (displayNo == null || displayNo.trim().isEmpty()) {
                                displayNo = card.getString("ACCTNO");
                            }
                            log.info("[额度查询-v2] 第{}张卡 卡号={}, ACCTNO={}, CCARDLIMIT={}, STATUSCD={}", calli + 1, displayNo, card.getString("ACCTNO"), card.getString("CCARDLIMIT"), card.getString("STATUSCD"));
                            String cardLabel;
                            if (displayNo != null && displayNo.length() >= 4) {
                                cardLabel = "尾号为" + displayNo.substring(displayNo.length() - 4) + "的信用卡";
                            } else {
                                cardLabel = "信用卡" + (calli + 1);
                            }
                            log.info("[额度查询-v2] 第{}张卡播报标签: {}", calli + 1, cardLabel);
                            result.put("CCARDLIMIT", card.getString("CCARDLIMIT"));
                            result.put("CCARDAVAILLIMIT", card.getString("CCARDAVAILLIMIT"));
                            result.put("CTDCASHAMOT", card.getString("CTDCASHAMOT"));
                            result.put("CCARDAVAILCASHLIMIT", card.getString("CCARDAVAILCASHLIMIT"));
                            prompt = prompt + cardLabel + ",信用额度为," + change(card.getString("CCARDLIMIT")) + " ";
                            prompt = prompt + "可用额度为," + change(card.getString("CCARDAVAILLIMIT")) + " ";
                            prompt = prompt + "预借现金额度为," + change(card.getString("CTDCASHAMOT")) + " ";
                            prompt = prompt + "可用现金额度为," + change(card.getString("CCARDAVAILCASHLIMIT")) + " ";
                        } catch (Exception e) {
                            log.warn("获取第{}张卡额度信息异常,跳过", calli + 1, e);
                        }
                    }
                    StrUtil.capturePrompt(prompt, result);
                }
            }
        } catch (Exception e)
        {
            log.error("获取额度异常",e);
            result.put("prompt", "没有查询到额度信息。");
        }
        return result;
    }


    @Override
    public JSONObject cardVerifi(String transServiceCode, String CARDNO, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try{
            //String transcode = "pcva.trade.b023.01";

            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("ACCNO",CARDNO);
            defObj.put("REQ_BODY",temp);
            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode","false");

            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
            String prompt = "";
            if(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                result.put("ReturnCode","true");
                result.put("DCMTTP",jsonObj.getJSONObject("RSP_BODY").getString("DCMTTP"));
                result.put("IDNO",jsonObj.getJSONObject("RSP_BODY").getString("IDNO"));
                result.put("DCMTST",jsonObj.getJSONObject("RSP_BODY").getString("DCMTST"));
                result.put("DCMTNO",jsonObj.getJSONObject("RSP_BODY").getString("DCMTNO"));
                result.put("DRAWTYPE",jsonObj.getJSONObject("RSP_BODY").getString("DRAWTYPE"));
                result.put("ACCNAME",StrUtil.paseStrUTF8(jsonObj.getJSONObject("RSP_BODY").getString("ACCNAME")));
                result.put("ACCTBRNO",jsonObj.getJSONObject("RSP_BODY").getString("ACCTBRNO"));
                result.put("ONLNBL",jsonObj.getJSONObject("RSP_BODY").getString("ONLNBL"));
                result.put("AVAILBL",jsonObj.getJSONObject("RSP_BODY").getString("AVAILBL"));
                result.put("CUSTNO",jsonObj.getJSONObject("RSP_BODY").getString("CUSTNO"));
                //DCMTST 1/4 is already rtl

                prompt = prompt + "  账户类型:" + Util.switchACCKIND1(jsonObj.getJSONObject("RSP_BODY").getString("ACCKIND"));
                prompt = prompt + "  开户日期:" + jsonObj.getJSONObject("RSP_BODY").getString("ACCTDATE").substring(0,4) + "年"+ jsonObj.getJSONObject("RSP_BODY").getString("ACCTDATE").substring(4,6) + "月"+ jsonObj.getJSONObject("RSP_BODY").getString("ACCTDATE").substring(6,8) + "日";
                prompt = prompt + "  账户状态:" + Util.switchACCSTATE(jsonObj.getJSONObject("RSP_BODY").getString("ACCSTATE"));
                prompt = prompt + "  余额:" + change(jsonObj.getJSONObject("RSP_BODY").getString("ONLNBL"));
                prompt = prompt + "  可用余额:" + change(jsonObj.getJSONObject("RSP_BODY").getString("AVAILBL"));

            }else {
                prompt = "暂时无法查到您的账户详细信息,请稍后重试:";
            }
            result.put("prompt",prompt);
        }
        catch(Exception e)
        {
            log.error("验证卡号异常",e);
        }
        return result;
    }

    @Override
    public JSONObject automaticPay(String transServiceCode, String ACCNO, String DEBITCARD, String AUTPMTTYPE, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try {
            // -----user define
            //String transcode = "pcva.trade.ccd023.01";//自动还款更新


            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode, u_ani, u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("ACCTNO", ACCNO);//ACCTNO 账号
            temp.put("DEBITCARD", DEBITCARD);
            temp.put("AUTPMTTYPE", AUTPMTTYPE);//自动还款类型  00-无自动还款 01-最小额还款02-全额还款
            temp.put("CURRENCY", "0");
            temp.put("OPRID", "96005");
            defObj.put("REQ_BODY", temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);
            String prompt = "";
            //-----ReturnValue
            result.put("ReturnCode","false");
            result.put("ConsumerSeqNo", defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage", StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));

            if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                result.put("ReturnCode","true");
                prompt = "自动还款设置成功";
            } else {
                prompt = Util.resolveReturnMessage(StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
            }
            result.put("prompt", prompt);
        }catch(Exception e)
        {
            log.error("自动还款设置异常",e);
        }
        return result;
    }

    @Override
    public JSONObject tranDetailSing(String transServiceCode, String CARDNO, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try {
            //String transcode = "pcva.trade.ccd024.01";

            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode, u_ani, u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
//            temp.put("CARDNO",CARDNO);
            temp.put("CARDNO", CARDNO);
            temp.put("CURRENCY", "0");
            temp.put("TRANDATE", TimeUtil.getDay());

            temp.put("pagerownum", "10000");
            String prompt = "您共有";
            defObj.put("REQ_BODY", temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode", "false");
            result.put("ConsumerSeqNo", defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage", StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
            System.out.println(jsonObj);
            if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                result.put("TOTALNUM", jsonObj.getJSONObject("RSP_BODY").getString("TotalNum"));
                String TotalNum = jsonObj.getJSONObject("RSP_BODY").getString("TotalNum");
                //发送成功逻辑
                int numall = Integer.parseInt(TotalNum);
                int calli = 0;
                int size = jsonObj.getJSONObject("RSP_BODY").getJSONArray("RspStruct").size();
                int callt = Util.getBigNum(size);
                prompt = prompt + jsonObj.getJSONObject("RSP_BODY").getString("TotalNum") + "笔交易记录,最近" + callt + "笔如下:";

                int displayCount = 0;
                for (calli = 0; calli < callt && displayCount < 10; calli++) {
                    JSONObject jsonObjCall = jsonObj.getJSONObject("RSP_BODY").getJSONArray("RspStruct").getJSONObject(calli);
                    // 过滤金额为0的记录
                    try {
                        if (Double.parseDouble(jsonObjCall.getString("AMOUNT").trim()) == 0) {
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        // 金额格式异常，跳过该条记录
                        continue;
                    }
                    displayCount++;
                    try {
                        prompt = prompt + "第" + displayCount + "笔:" + " 交易原因:" + StrUtil.paseStrUTF8(jsonObjCall.getString("MERCHNAME")).trim() + "" + StrUtil.paseStrUTF8(jsonObjCall.getString("TXNDESC")).trim() + " 金额:" + change(jsonObjCall.getString("AMOUNT").trim()) + ",";
                    } catch (Exception e) {
                        prompt = prompt + "第" + displayCount + "笔:" + " 交易原因:" + StrUtil.paseStrUTF8(jsonObjCall.getString("TXNDESC")).trim() + " 金额:" + change(jsonObjCall.getString("AMOUNT").trim()) + ",";
                    }
                }
                StrUtil.capturePrompt(prompt,result);
                result.put("ReturnCode", "true");

            } else {
                result.put("errMsg", Util.resolveReturnMessage(StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage"))));
            }

        } catch (Exception e)
        {
            log.error("查询信用卡当日明细异常",e);
        }
        return result;
    }

    @Override
    public JSONObject tranDetailMult(String transServiceCode, String ACCNO, String day, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try {

            String var_day = TimeUtil.getDay();
            String var_day_ago = TimeUtil.getDayAgo(Integer.parseInt(day));
            //-----user define
            //String transcode = "pcva.ccard.ccd019.01";

            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode, u_ani, u_connid);
            JSONObject temp = new JSONObject();
            String prompt = "您共有";
            //-----user define
//            temp.put("CARDNO",CARDNO);
            temp.put("ACCNO", ACCNO);
            temp.put("CURRENCY", "0");
            temp.put("STARTDATE", var_day_ago);
            temp.put("ENDDATE", var_day);
            temp.put("pagerownum", "10");
            defObj.put("REQ_BODY", temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode", "false");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
            System.out.println(jsonObj);
            if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                String TotalNum = jsonObj.getJSONObject("RSP_BODY").getString("TotalNum");
                result.put("TOTALNUM", jsonObj.getJSONObject("RSP_BODY").getString("TotalNum"));
                result.put("ReturnCode", "true");
                //发送成功逻辑
                int numall = Integer.parseInt(TotalNum);
                if (numall <= 10) {
                    int calli = 0;
                    int callt = jsonObj.getJSONObject("RSP_BODY").getJSONArray("RspStruct").size();
                    prompt = prompt + jsonObj.getJSONObject("RSP_BODY").getString("TotalNum") + "笔交易记录,最近" + callt + "笔如下:";

                    int displayCount = 0;
                    for (calli = 0; calli < callt && displayCount < 10; calli++) {
                        JSONObject jsonObjCall = jsonObj.getJSONObject("RSP_BODY").getJSONArray("RspStruct").getJSONObject(calli);
                        // 过滤金额为0的记录
                        try {
                            if (Double.parseDouble(jsonObjCall.getString("AMOUNT").trim()) == 0) {
                                continue;
                            }
                        } catch (NumberFormatException e) {
                            continue;
                        }
                        displayCount++;
                        prompt = prompt + "第" + displayCount + "笔:" + "交易时间:" + jsonObjCall.getString("TRANTIME").substring(0, 4) + "年" + jsonObjCall.getString("TRANTIME").substring(4, 6) + "月" + jsonObjCall.getString("TRANTIME").substring(6, 8) + "日" + "交易金额:" + change(jsonObjCall.getString("AMOUNT")) + "交易描述:" + StrUtil.paseStrUTF8(jsonObjCall.getString("MERCNAME")).trim()+" " +StrUtil.paseStrUTF8(jsonObjCall.getString("ABSTDESC")).trim() + ",";
                    }
                    StrUtil.capturePrompt(prompt,result);
                    result.put("data_debug", callt);
                } else if (numall >= 20) {
                    JSONObject defObj2 = Util.getBasicJson(transServiceCode, u_ani, u_connid);
                    JSONObject temp2 = new JSONObject();
                    temp2.put("ACCNO", ACCNO);
                    temp2.put("CURRENCY", "0");
                    temp2.put("STARTDATE", var_day_ago);
                    temp2.put("ENDDATE", var_day);
                    temp2.put("pagerownum", "" + (numall - 10));
                    temp2.put("currpage", "2");
                    defObj2.put("REQ_BODY", temp2);
                    //usagi.jar
                    JSONObject jsonObj2 = sendRequest(defObj2);
                    if (jsonObj2.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                        int calli = 0;
                        int size = jsonObj2.getJSONObject("RSP_BODY").getJSONArray("RspStruct").size();
                        int callt = Util.getBigNum(size);
                        prompt = prompt + jsonObj2.getJSONObject("RSP_BODY").getString("TotalNum") + "笔交易记录,最近" + callt + "笔如下:";

                        int displayCount = 0;
                        for (calli = 0; calli < callt && displayCount < 10; calli++) {
                            JSONObject jsonObjCall = jsonObj2.getJSONObject("RSP_BODY").getJSONArray("RspStruct").getJSONObject(calli);
                            // 过滤金额为0的记录
                            try {
                                if (Double.parseDouble(jsonObjCall.getString("AMOUNT").trim()) == 0) {
                                    continue;
                                }
                            } catch (NumberFormatException e) {
                                continue;
                            }
                            displayCount++;
                            prompt = prompt + "第" + displayCount + "笔:" + " 交易时间:" + jsonObjCall.getString("TRANTIME").substring(0, 4) + "年" + jsonObjCall.getString("TRANTIME").substring(4, 6) + "月" + jsonObjCall.getString("TRANTIME").substring(6, 8) + "日" + " 交易金额:" + change(jsonObjCall.getString("AMOUNT")) + " 交易描述:" + StrUtil.paseStrUTF8(jsonObjCall.getString("MERCNAME")).trim()+" " +StrUtil.paseStrUTF8(jsonObjCall.getString("ABSTDESC")).trim() + ",";
                        }
                        StrUtil.capturePrompt(prompt,result);
                        result.put("data_debug", callt);
                    }
                } else {
                    JSONObject defObj3 = Util.getBasicJson(transServiceCode, u_ani, u_connid);
                    JSONObject temp3 = new JSONObject();
                    temp3.put("ACCNO", ACCNO);
                    temp3.put("CURRENCY", "0");
                    temp3.put("STARTDATE", var_day_ago);
                    temp3.put("ENDDATE", var_day);
                    temp3.put("pagerownum", "20");
                    temp3.put("currpage", "1");
                    defObj3.put("REQ_BODY", temp3);
                    //usagi.jar
                    JSONObject jsonObj3 = sendRequest(defObj3);
                    if (jsonObj3.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                        int calli = 0;
                        int size = jsonObj3.getJSONObject("RSP_BODY").getJSONArray("RspStruct").size();
                        int callt = Util.getBigNum(size);
                        prompt = prompt + jsonObj3.getJSONObject("RSP_BODY").getString("TotalNum") + "笔交易记录,最近" + callt + "笔如下:";

                        int displayCount = 0;
                        for (calli = 0; calli < callt && displayCount < 10; calli++) {
                            JSONObject jsonObjCall = jsonObj3.getJSONObject("RSP_BODY").getJSONArray("RspStruct").getJSONObject(calli);
                            // 过滤金额为0的记录
                            try {
                                if (Double.parseDouble(jsonObjCall.getString("AMOUNT").trim()) == 0) {
                                    continue;
                                }
                            } catch (NumberFormatException e) {
                                continue;
                            }
                            displayCount++;
                            prompt = prompt + "第" + displayCount + "笔:" + " 交易时间:" + jsonObjCall.getString("TRANTIME").substring(0, 4) + "年" + jsonObjCall.getString("TRANTIME").substring(4, 6) + "月" + jsonObjCall.getString("TRANTIME").substring(6, 8) + "日" + " 交易金额:" + change(jsonObjCall.getString("AMOUNT")) +  " 交易描述:" + StrUtil.paseStrUTF8(jsonObjCall.getString("MERCNAME")).trim()+" " +StrUtil.paseStrUTF8(jsonObjCall.getString("ABSTDESC")).trim() + ",";
                        }
                        StrUtil.capturePrompt(prompt,result);
                        result.put("data_debug", callt);
                    }

                }
            }else{
                result.put("errMsg",Util.resolveReturnMessage(StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage"))));
            }

        }
        catch(Exception e)
        {
           log.error("查询多日交易明细异常",e);
        }
        return result;
    }

    @Override
    public JSONObject getActivaRes(String transServiceCode, String CARDNO, String IDNO, String validdate, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try{
            //String transcode = "pcva.ccard.ccd037.01";
            String prompt = "";

            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("CARDNO",CARDNO);
            temp.put("IDNO",IDNO);
            temp.put("IDTYPE","10101");
            temp.put("USERID","96005");
            temp.put("CARDLIMITDT",StrUtil.reverSal(validdate));
            defObj.put("REQ_BODY",temp);
            //04.通讯发送
            JSONObject jsonObj = sendRequest(defObj);

            //05.返回处理

            result.put("ConsumerSeqNo", defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            String returnCode = jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode");
            if ("000000".equals(returnCode) || "000013".equals(returnCode) || "000001".equals(returnCode)) {
                result.put("ReturnCode", returnCode);
                result.put("PHONENUMA",jsonObj.getJSONObject("RSP_BODY").getString("PHONENUMA"));
            }else {
                result.put("ReturnCode", "false");
                prompt = Util.resolveReturnMessage(StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
                result.put("prompt",prompt);
            }
        }
        catch(Exception e)
        {
            return result;
        }
        return result;
    }

    private String getCardLimitdt(String validdate){
        return new StringBuilder(validdate).reverse().toString();
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


