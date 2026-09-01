package com.egoo.idp.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.egoo.idp.service.CreditTransactionService;
import com.egoo.idp.service.DebitTransactionService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.egoo.idp.utils.Util.change;
import static com.egoo.idp.utils.Util.switchUserLevel;

@Service
@Slf4j
public class DebitTransactionServiceImpl implements DebitTransactionService {

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;

    @Value("${gateway.transaction.requestUrl}")
    private String requestUrl;

    private static final int KEY = 7;

    @Autowired
    public DebitTransactionServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        // 初始化HTTP头信息
        this.httpHeaders = new HttpHeaders();
        this.httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public JSONObject idOrCardInquire(String var_input, String opt ,String u_ani, String u_connid) {

        JSONObject result = new JSONObject();
        if (var_input.length() == 18) {

            try {
                String transcode = "pcva.trade.b036.01";

                //usagi.jar
                JSONObject defObj = Util.getBasicJson(transcode, u_ani, u_connid);
                JSONObject temp = new JSONObject();

                //-----user define
                temp.put("IDNO", var_input);
                temp.put("IDTYPE", "10101");
                temp.put("CHXUNBIS", "30");
                temp.put("QISHIBIS", "0");
                defObj.put("REQ_BODY", temp);

                //usagi.jar
                JSONObject jsonObj = sendRequest(defObj);


                result.put("ReturnCode","false");
                result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
                result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
                result.put("IDNO",var_input);

                if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                    int tempi = Integer.parseInt(jsonObj.getJSONObject("RSP_BODY").getString("TotalNum"));

                    JSONArray jsonArray = jsonObj.getJSONObject("RSP_BODY").getJSONArray("AccStruct");
                    if(tempi == 1){
                        String DCMTTP = jsonArray.getJSONObject(0).getString("DCMTTP");
                        String SUBACCNO = jsonArray.getJSONObject(0).getString("SUBACCNO");
                        if(checkCardType(opt,DCMTTP,SUBACCNO)){
                            result.put("CARDNO", jsonArray.getJSONObject(0).getString("ACCNO"));
                            result.put("CardType", "single");
                            //-----ReturnValue
                            result.put("ReturnCode", "true");
                            result.put("TotalNum",1);
                        }
                    }else if (tempi > 1){
                        StringBuilder cardstr = new StringBuilder("|");
                        int totalNum = 0;
                        for (Object obj : jsonArray) {
                            JSONObject card = (JSONObject) obj;
                            String DCMTTP = card.getString("DCMTTP");
                            String SUBACCNO = card.getString("SUBACCNO");
                            if(checkCardType(opt,DCMTTP,SUBACCNO)){
                                cardstr.append(card.getString("ACCNO")+"|");
                                totalNum++;
                            }
                        }
                        if(totalNum > 1){
                            result.put("ReturnCode", "multi");
                            result.put("CARDNO", cardstr);
                            result.put("CardType", "multi");
                            result.put("TotalNum",totalNum);
                        }else if (totalNum == 1){
                            result.put("CARDNO", cardstr.toString().replace("|",""));
                            result.put("CardType", "single");
                            //-----ReturnValue
                            result.put("ReturnCode", "true");
                            result.put("TotalNum",1);
                        }

                        if(tempi > 30){
                            result.put("ReturnCode", "toomany");
                        }
                    }
                }
            } catch (Exception e) {
                log.error("借记卡根据身份证查询异常",e);
            }
        } else if (!(var_input.length() == 18)) {
            try {
                String transcode = "pcva.trade.b023.01";
                //usagi.jar
                JSONObject defObj = Util.getBasicJson(transcode,u_ani,u_connid);
                JSONObject temp = new JSONObject();
                //-----user define
                temp.put("ACCNO",var_input);
                defObj.put("REQ_BODY",temp);
                //usagi.jar
                JSONObject jsonObj = sendRequest(defObj);

                //-----ReturnValue
                result.put("ReturnCode","false");
                result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
                result.put("ReturnMessage", StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));

                if(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                    result.put("CARDNO", var_input);
                    result.put("IDNO", jsonObj.getJSONObject("RSP_BODY").getString("IDNO"));
                    result.put("ReturnCode","true");
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

    Boolean checkCardType(String opt , String DCMTTP,String SUBACCNO){
        if (ObjectUtils.isEmpty(opt)){
            return true;
        }
        if(!ObjectUtils.isEmpty(DCMTTP)){
            if("1".equals(opt)){
                Set<Integer> VALID_DCMT_TYPES = new HashSet<>(Arrays.asList(
                        738, 742, 744, 745, 746, 748, 749, 750, 757, 758, 759, 760, 762, 767
                ));
                return VALID_DCMT_TYPES.contains(Integer.parseInt(DCMTTP));
            }else if("2".equals(opt)){
                Set<Integer> VALID_DCMT_TYPES = new HashSet<>(Arrays.asList(
                        731, 732, 734
                ));
                if(VALID_DCMT_TYPES.contains(Integer.parseInt(DCMTTP)) && "00001".equals(SUBACCNO)){
                    return true;
                }
                return false;
            }else if("3".equals(opt)){
                Set<Integer> VALID_DCMT_TYPES = new HashSet<>(Arrays.asList(
                        302, 737
                ));
                return VALID_DCMT_TYPES.contains(Integer.parseInt(DCMTTP));
            }
        }
        return false;
    }

    @Override
    public JSONObject queryCard(String CARDNOS, String CARDNO_SUFF) {
        JSONObject result = new JSONObject();
        List<String> cards = Arrays.asList(CARDNOS.split("\\|"));
        result.put("ReturnCode", "false");
        for (String card:cards) {
            if(!ObjectUtils.isEmpty(card) && CARDNO_SUFF.equals(card.substring(card.length() - 4))){
                result.put("CARDNO", card);
                result.put("ReturnCode", "true");
               return result;
            }
        }
        return result;
    }

    @Override
    public JSONObject queryPassword(String transServiceCode, String CARDNO, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try{

            //String transcode = "pcva.trade.b056.01";
            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            JSONObject temp = new JSONObject();
            result.put("ReturnCode","false");

            //-----user define
            temp.put("KEHUZHAO",CARDNO);
            temp.put("MIMAZLEI","3");
            defObj.put("REQ_BODY",temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode","false");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));


            if(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                result.put("ReturnMessage","true");
                result.put("SHIFOUBZ",jsonObj.getJSONObject("RSP_BODY").getString("SHIFOUBZ"));
                result.put("IDNO",jsonObj.getJSONObject("RSP_BODY").getString("IDNO"));
                result.put("CARDNO",CARDNO);
            }
        }
        catch(Exception e)
        {
           log.error("查询是否有交易密码异常",e);
        }
        return result;
    }

    @Override
    public JSONObject personBankOfDeposit(String transServiceCode, String ACCTBRNO, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try{
           // String transcode = "pcva.trade.b173.01";

            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("JIGOUHAO",ACCTBRNO);
            defObj.put("REQ_BODY",temp);

            String prompt = "";
            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode","false");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));

            if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                result.put("ReturnCode","true");
                result.put("JIGOUZWM", jsonObj.getJSONObject("RSP_BODY").getString("JIGOUZWM"));
                result.put("DIZHIIII", jsonObj.getJSONObject("RSP_BODY").getString("DIZHIIII"));
                prompt += "您的开户行是：" + StrUtil.paseStrUTF8(jsonObj.getJSONObject("RSP_BODY").getString("JIGOUZWM"));
                if (!jsonObj.getJSONObject("RSP_BODY").getString("DIZHIIII").trim().equals("")) {
                    //prompt += " 地址是：" + jsonObj.getJSONObject("RSP_BODY").getString("DIZHIIII");
                }
                if (!jsonObj.getJSONObject("RSP_BODY").getString("DIANHHMA").trim().equals("")) {
                    prompt += " 电话是：" + jsonObj.getJSONObject("RSP_BODY").getString("DIANHHMA");
                }
                String transcode = "pcva.trade.b189.01";
                defObj = Util.getBasicJson(transcode,u_ani,u_connid);

                //-----user define
                temp.put("SELBRNO",ACCTBRNO);
                defObj.put("REQ_BODY",temp);
                jsonObj = sendRequest(defObj);
                if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                    prompt += " 开户行号为：" +jsonObj.getJSONObject("RSP_BODY").getString("BANKNO") ;
                }
                temp = new JSONObject();

                result.put("prompt",prompt);
            }
        }
        catch(Exception e)
        {
            log.error("查询开户行异常",e);
        }
        return result;
    }

    @Override
    public JSONObject businessBankOfDeposit(String transServiceCode, String CARDNO, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();

        try {
            //String transcode = "pcva.trade.b186.01";

            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode, u_ani, u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("CARDNO", CARDNO);
            temp.put("QUERYFLAG", "0");
            temp.put("QUEYTP", "1");
            temp.put("PINBLOCK", "");
            defObj.put("REQ_BODY", temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode", "false");
            result.put("ConsumerSeqNo", defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage", StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
            String prompt = "查询失败,请稍后重试";
            if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                result.put("ReturnCode", "true");
                prompt = " 持卡人姓名: " + StrUtil.paseStrUTF8(jsonObj.getJSONObject("RSP_BODY").getString("CARDHOLDERNAME"));
                prompt += " 单位名称: " + StrUtil.paseStrUTF8(jsonObj.getJSONObject("RSP_BODY").getString("DANWEIMC"));
                prompt += " 开户行: " + StrUtil.paseStrUTF8(jsonObj.getJSONObject("RSP_BODY").getString("KAIHUHANG"));
            }
            result.put("prompt", prompt);
            result.put("BCARDNO",jsonObj.getJSONObject("RSP_BODY").getString("DGKHUZH"));
        } catch (Exception e) {
            log.error("公司开户行地址查询异常",e);
        }

        return result;
    }

    @Override
    public JSONObject balance(String transServiceCode, String CARDNO, String CUSTNO, String ONLNBL,String AVAILBL, String u_ani, String u_connid) {


        JSONObject result = new JSONObject();
        try{
            //String transcode = "pcva.trade.b048_Query.01";

            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("CARDNO",CARDNO);
            temp.put("ECIFCUSTNO",CUSTNO);
            defObj.put("REQ_BODY",temp);
            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode","false");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));

            String var_sub_JNYbalan = null;
            if(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                result.put("ReturnCode", "true");
                var_sub_JNYbalan = jsonObj.getJSONObject("RSP_BODY").getString("ZHANGHYE");
            }
            String cashup = change(AVAILBL);
            String cn_ttsread = "您账户当前可用余额为，"+cashup;
            cashup = change(ONLNBL);
            cn_ttsread = cn_ttsread + "，账户余额为，"+cashup;

            if (var_sub_JNYbalan != null && var_sub_JNYbalan.length() !=0)
            {
                cashup = change(var_sub_JNYbalan);
                cn_ttsread = cn_ttsread + "，江南盈余额为，"+cashup;
            }
            result.put("var_cn_ttsread",cn_ttsread);
        }
        catch(Exception e)
        {
            return result;
        }
        return result;
    }

    @Override
    public JSONObject cardCheck(String transServiceCode, String debit_cardno, String credit_idno, String u_ani, String u_connid) {
        JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
        JSONObject temp = new JSONObject();

        //-----user define
        temp.put("ACCNO",debit_cardno);
        defObj.put("REQ_BODY",temp);
        //usagi.jar
        JSONObject jsonObj = sendRequest(defObj);

        String ReturnMessage = StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage"));

        //-----ReturnValue
        if(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
            temp.put("ReturnMessage",ReturnMessage);
            String IDNO = jsonObj.getJSONObject("RSP_BODY").getString("IDNO");
            if(IDNO.equals(credit_idno)){
                temp.put("ReturnCode","true");
            }else {
                temp.put("ReturnCode","false");
            }
        }else {
            try {
                String[] errorMess = ReturnMessage.split("\\[111]");
                temp.put("ReturnMessage",errorMess[errorMess.length -1]);
            } catch (Exception e) {
                log.error("解析pcva.trade.b023.01错误信息异常",e);
                temp.put("ReturnCode","未知错误");
            }
            temp.put("ReturnCode","error");
        }
        return temp;
    }

    @Override
    public JSONObject cardMenu(String transServiceCode, String CARDNO, String day,String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try {
            //String transcode = "pcva.trade.m019.01";

            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode, u_ani, u_connid);
            JSONObject temp = new JSONObject();

            //-----user define - 第一次查询：获取总记录数
            temp.put("SHIFOUBZ", "0");
            temp.put("QUERYNUM", "1");
            temp.put("STARTNUM", "0");
            temp.put("MIMAZLEII", "0");
            temp.put("QUERYTYPE", "0");
            temp.put("STARTDATE",TimeUtil.getDayAgo(Integer.parseInt(day)));
            temp.put("ENDDATE",  TimeUtil.getDay());
            temp.put("KEHUZHAO", CARDNO);
            temp.put("ACCNUM", "00001");
            temp.put("CURRENCYCODE", "01");
            defObj.put("REQ_BODY", temp);

            //usagi.jar - 第一次查询
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode", "false");
            result.put("ConsumerSeqNo", defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage", StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));

            String prompt = "您共有";
            String AMOUNT = "";
            if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                result.put("ReturnCode", "true");
                
                // 获取总记录数
                int totalNum = Integer.parseInt(jsonObj.getJSONObject("RSP_BODY").getString("TotalNum"));
                prompt = prompt + totalNum + "笔交易记录,";
                result.put("TOTALNUM", String.valueOf(totalNum));
                
                // 计算起始位置：取最后10条
                int startNum = Math.max(0, totalNum - 10);
                int queryNum = Math.min(10, totalNum);
                
                log.info("[储蓄卡明细-v2] 总记录数={}, STARTNUM={}, QUERYNUM={}", totalNum, startNum, queryNum);
                
                // 第二次查询：取最后10条（最新的）
                JSONObject defObj2 = Util.getBasicJson(transServiceCode, u_ani, u_connid);
                JSONObject temp2 = new JSONObject();
                temp2.put("SHIFOUBZ", "0");
                temp2.put("QUERYNUM", String.valueOf(queryNum));
                temp2.put("STARTNUM", String.valueOf(startNum));
                temp2.put("MIMAZLEII", "0");
                temp2.put("QUERYTYPE", "0");
                temp2.put("STARTDATE",TimeUtil.getDayAgo(Integer.parseInt(day)));
                temp2.put("ENDDATE",  TimeUtil.getDay());
                temp2.put("KEHUZHAO", CARDNO);
                temp2.put("ACCNUM", "00001");
                temp2.put("CURRENCYCODE", "01");
                defObj2.put("REQ_BODY", temp2);
                
                JSONObject jsonObj2 = sendRequest(defObj2);
                
                if (jsonObj2.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                    int calli;
                    int size = jsonObj2.getJSONObject("RSP_BODY").getJSONArray("RspStruct").size();
                    int callt = Util.getBigNum(size);
                    
                    prompt = prompt + "最近" + callt + "笔如下:";

                    // 后端返回数据按时间正序（最旧在前），从后往前遍历显示最新的
                    for (calli = callt - 1; calli >= 0; calli--) {
                        JSONObject jsonObjCall = jsonObj2.getJSONObject("RSP_BODY").getJSONArray("RspStruct").getJSONObject(calli);
                        prompt = prompt + "第" + (callt - calli) + "笔:" + "交易时间:" + jsonObjCall.getString("TRANDATE").substring(0, 4) + "年" + jsonObjCall.getString("TRANDATE").substring(4, 6) + "月" + jsonObjCall.getString("TRANDATE").substring(6, 8) + "日";
                        prompt = prompt + "" + jsonObjCall.getString("TRADETIME");
                        if (jsonObjCall.getString("LOANSIGN").equals("D")) {
                            prompt = prompt + " 支出";
                            AMOUNT = jsonObjCall.getString("JFFASHEE");
                        } else if (jsonObjCall.getString("LOANSIGN").equals("C")) {
                            prompt = prompt + " 存入";
                            AMOUNT = jsonObjCall.getString("DFFASHEE");
                        }

                        prompt = prompt + "金额:" + change(AMOUNT.replaceAll("-", "")) + "交易描述:" + StrUtil.paseStrUTF8(jsonObjCall.getString("SHHUMGCH")) + "" +StrUtil.paseStrUTF8(jsonObjCall.getString("RECPACCNM"))+ ""+StrUtil.paseStrUTF8(jsonObjCall.getString("ABSTDESC"))+ "" + StrUtil.paseStrUTF8(jsonObjCall.getString("BEIZHUXX")) + "";
                        if (AMOUNT.indexOf("-") > -1) {
                            prompt = prompt + "该交易为冲正交易 ";
                        }
                        prompt = prompt + "交易后余额为:" + change(jsonObjCall.getString("ACCNOBL")) + ",";
                    }
                    // 循环结束后再调用分段
                    StrUtil.capturePrompt(prompt,result);
                    log.info("[储蓄卡明细-v2] 最终播报内容: {}", prompt);
                    log.info("[储蓄卡明细-v2] result.prompt: {}", result.getString("prompt"));
                    log.info("[储蓄卡明细-v2] result.prompt2: {}", result.getString("prompt2"));
                    log.info("[储蓄卡明细-v2] result.prompt3: {}", result.getString("prompt3"));
                }
            }
        } catch (Exception e) {
            log.error("[储蓄卡明细-v2] 查询异常", e);
            return result;
        }
        return result;
    }

    @Override
    public JSONObject phoneCompare(String transServiceCode, String ECIFCUSTNO, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try{
           // String transcode = "pcva.trade.b200.01";


            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            JSONObject THEME_TYPE_GRP = new JSONObject();
            THEME_TYPE_GRP.put("STARTNUM","1");
            THEME_TYPE_GRP.put("QUERY_TYPE","6");
            temp.put("ECIFCUSTNO",ECIFCUSTNO);
            temp.put("RESOLVE_TYPE","1");
            temp.put("THEME_TYPE_GRP",THEME_TYPE_GRP);

            defObj.put("REQ_BODY",temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode","false");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));

            if(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                String phone = jsonObj.getJSONObject("RSP_BODY").getString("PHONENUMA");
                if(u_ani.equals(phone)){
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
    public JSONObject checkCardLength(String cardNo) {
        JSONObject result = new JSONObject();
        if(19 == cardNo.length()){
            result.put("ReturnCode","true");
        }else {
            result.put("ReturnCode","false");
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


