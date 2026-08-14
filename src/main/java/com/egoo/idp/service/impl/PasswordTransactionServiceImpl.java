package com.egoo.idp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.egoo.idp.service.PasswordTransactionService;
import com.egoo.idp.utils.PwdUtil;
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

import static com.egoo.idp.utils.Util.change;


@Slf4j
@Service
public class PasswordTransactionServiceImpl implements PasswordTransactionService {

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;

    @Value("${gateway.transaction.requestUrl}")
    private String requestUrl;

    @Autowired
    public PasswordTransactionServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        // 初始化HTTP头信息
        this.httpHeaders = new HttpHeaders();
        this.httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public JSONObject passEncrypt(String transServiceCode, String CARDNO, String PINBLOCK, String opt_type,String u_ani, String u_connid) {

        JSONObject result = null;
        try {
            result = new JSONObject();
            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("CARDNO",CARDNO);
            temp.put("PINBLOCK",PINBLOCK);
            defObj.put("REQ_BODY",temp);

            // 4. 发送请求
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode","false");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));

            if(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                result.put("ReturnCode","true");
                if(!ObjectUtils.isEmpty(opt_type)){
                    result.put("PINBLOCK_old",jsonObj.getJSONObject("RSP_BODY").getString("PINBLOCK"));
                }else {
                    result.put("PINBLOCK",jsonObj.getJSONObject("RSP_BODY").getString("PINBLOCK"));
                }

            }
        } catch (Exception e) {
            log.error("获取加密密码异常",e);
        }

        return result;
    }

    @Override
    public JSONObject debitCheck(String transServiceCode, String CARDNO, String PINBLOCK, Integer num,String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try{
            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("KEHUZHAO",CARDNO);
            temp.put("MIMAMMMM",PINBLOCK);
            temp.put("YANMBZHI","2");
            defObj.put("REQ_BODY",temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode","true");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));

            if(!jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                result.put("ReturnCode","false");
                if (ObjectUtils.isEmpty(num)){
                    result.put("num",1);
                }else if(num < 2){
                    result.put("num",++num);
                }else {
                    result.put("ReturnCode","error");
                }
                result.put("errMsg", Util.resolveReturnMessage(StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage"))));
            }
        }
        catch(Exception e)
        {
            log.error("校验密码异常",e);
        }
        return result;
    }

    @Override
    public JSONObject debitSet(String transServiceCode, String u_ani, String u_connid, String CARDNO,String CUSTIDNO,String IDNO ,String PINBLOCK) {

        JSONObject result = new JSONObject();
        try{
            JSONObject temp = new JSONObject();
            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);

            //-----user define
            temp.put("CUSTIDNO",CUSTIDNO);
            temp.put("KEHUZHAO",CARDNO);
            temp.put("CUSTACCTYPE","1");
            temp.put("KEHUZWMC","1");
            temp.put("MIMAZLEII","2");
            temp.put("MIMAZLEI","2");
            temp.put("ZHJNZLEI","10101");
            temp.put("IDNO",IDNO);
            temp.put("CHULIBZH","A");
            temp.put("MIMAMINW",PINBLOCK);
            defObj.put("REQ_BODY",temp);
            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);
            //-----ReturnValue
            result.put("ReturnCode","false");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));


            if(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                result.put("ReturnCode","true");
            }else{
                result.put("errMsg",Util.resolveReturnMessage(StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage"))));
            }
        }
        catch(Exception e)
        {
            return result;
        }
        return result;
    }

    @Override
    public JSONObject creditSet(String transServiceCode, String u_ani, String u_connid, String CARDNO, String IDNO, String PINBLOCKFLAG, String PINBLOCK,String PINBLOCK_old,String cvalId) {
        JSONObject result = new JSONObject();
        try{
            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            JSONObject temp = new JSONObject();
            //-----user define
            temp.put("CURRENCY","1");
            temp.put("CARDNO",CARDNO);
            temp.put("IDNO",IDNO);
            temp.put("IDTYPE","10101");
            temp.put("PINBLOCKFLAG",PINBLOCKFLAG);
            temp.put("SCINFO",PINBLOCK);
            temp.put("PINBLOCK",PINBLOCK_old);
            temp.put("ADDDATA","1"+IDNO);
            if(!"2".equals(PINBLOCKFLAG)){
                temp.put("CVALID",cvalId);
            }
            defObj.put("REQ_BODY",temp);
            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);
            //-----ReturnValue
            result.put("ReturnCode","true");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
            if(!jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                result.put("ReturnCode","false");
                result.put("errMsg",Util.resolveReturnMessage(StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage"))));
            }
        }
        catch(Exception e)
        {
            log.error("信用卡密码上传异常",e);
        }
        return result;
    }

    @Override
    public JSONObject checkWeakPassword(String u_ani, String IDNO, String CARDNO, String PINBLOCK) {
        JSONObject result = new JSONObject();

        try{
            boolean flag_weakpassword_b = PwdUtil.con_contiunity(PINBLOCK) || PwdUtil.con_AAABBB(PINBLOCK) || PwdUtil.con_AABBCC(PINBLOCK)
                    || PwdUtil.con_string(PINBLOCK, u_ani) || PwdUtil.con_string(PINBLOCK, IDNO) || PwdUtil.con_AAAAAA(PINBLOCK)
                    || PwdUtil.con_string(PINBLOCK, CARDNO) || PwdUtil.con_ABCABC(PINBLOCK);

            String flag_weakpassword = String.valueOf(flag_weakpassword_b);


            result.put("flag_weakpassword",flag_weakpassword);

        }catch(Exception e)
        {
            log.error("校验弱密码异常",e);
        }

        return result;
    }

    @Override
    public JSONObject creditCheck(String transServiceCode,String CARDNO, String PINBLOCK, String u_ani, String u_connid,Integer num) {
        JSONObject result = new JSONObject();
        try{
            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode, u_ani, u_connid);
            JSONObject temp = new JSONObject();
            temp.put("TRANTIME", TimeUtil.getDay());
            temp.put("CARDNO",CARDNO);
            temp.put("LOCALTIME",TimeUtil.getMmddhhmmss());
            temp.put("PINBLOCK",PINBLOCK);
            temp.put("CURRENCY", "0");
            defObj.put("REQ_BODY",temp);
            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);
            //-----ReturnValue
            result.put("ReturnCode","true");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
            if(!jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                result.put("ReturnCode","false");if (ObjectUtils.isEmpty(num)){
                    result.put("num",1);
                }else if(num < 2){
                    result.put("num",++num);
                }else {
                    result.put("ReturnCode","error");
                }

                result.put("errMsg",Util.resolveReturnMessage(StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage"))));
            }
        }
        catch(Exception e)
        {
             log.error("校验信用卡密码异常",e);
        }
        return result;
    }

    @Override
    public JSONObject getEcif(String transServiceCode, String CARDNO, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try{
            //String transcode = "pcva.trade.b054.01";
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
                result.put("ECIFCUSTNO",jsonObj.getJSONObject("RSP_BODY").getString("ECIFCUSTNO"));
                result.put("IDNO",jsonObj.getJSONObject("RSP_BODY").getString("IDNO"));
                result.put("ACCNAME",jsonObj.getJSONObject("RSP_BODY").getString("ACCNAME"));
                result.put("CARDNO",CARDNO);

                int calli = 0;
                int callt = jsonObj.getJSONObject("RSP_BODY").getJSONArray("Detail").size();
                int recordNum = 0;
                for (calli = 0; calli < callt; calli++) {
                    JSONObject jsonObjCall = jsonObj.getJSONObject("RSP_BODY").getJSONArray("Detail").getJSONObject(calli);
                    if(!jsonObjCall.getString("DEPOSITTERM").equals("0D")){
                        recordNum +=1;
                    }
                }
                prompt = prompt + recordNum + "条记录,";
                int promptNum = 0;
                for (calli = 0; calli < callt; calli++) {
                    JSONObject jsonObjCall = jsonObj.getJSONObject("RSP_BODY").getJSONArray("Detail").getJSONObject(calli);
                    if(!jsonObjCall.getString("DEPOSITTERM").equals("0D")){
                        prompt = prompt + "第" + (promptNum + 1) + "条:" +StrUtil.paseStrUTF8(jsonObjCall.getString("PRODUCTDESC")) +"存款金额:" + change(jsonObjCall.getString("ONLNBL")) + ".存款属性:" + StrUtil.paseStrUTF8(jsonObjCall.getString("PRODUCTDESC")) + ".存期:" + jsonObjCall.getString("DEPOSITTERM") +  ".";
                        promptNum +=1;
                        if (promptNum >= 10){
                            break;
                        }
                    }
                }
            }else{
                prompt = prompt + "0笔记录.查询结束";
            }
            StrUtil.capturePrompt(prompt,result);

        }
        catch(Exception e)
        {
            return result;
        }
        return result;
    }

    @Override
    public JSONObject debitQueryCheck(String transServiceCode, String CARDNO, String PINBLOCK, String u_ani, String u_connid,Integer num) {

        JSONObject result = new JSONObject();
        try{
           // String transcode = "pcva.trade.b175.01";

            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            temp.put("KEHUZHAO",CARDNO);
            temp.put("MIMAMMMM",PINBLOCK);
            temp.put("YANMBZHI","1");
            defObj.put("REQ_BODY",temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode","true");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));

            if(!jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                result.put("ReturnCode","false");
                if (ObjectUtils.isEmpty(num)){
                    result.put("num",1);
                }else if(num < 2){
                    result.put("num",++num);
                }else {
                    result.put("ReturnCode","error");
                }
                result.put("errMsg",Util.resolveReturnMessage(StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage"))));
            }
        }
        catch(Exception e)
        {
            log.error("校验查询密码异常",e);
        }
        return result;
    }

    @Override
    public JSONObject creditQueryCheck(String transServiceCode, String CARDNO, String PINBLOCK, String u_ani, String u_connid,Integer num) {
        JSONObject result = new JSONObject();
        try{
            //String transcode = "pcva.ccard.ccd052.01";

            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode, u_ani, u_connid);
            JSONObject temp = new JSONObject();
            temp.put("PINBLOCK",PINBLOCK);
            temp.put("CARDNO",CARDNO);
            temp.put("CURRENCY", "0");
            defObj.put("REQ_BODY",temp);
            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode","true");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));

            if(!jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                result.put("ReturnCode","false");
                if (ObjectUtils.isEmpty(num)){
                    result.put("num",1);
                }else if(num < 2){
                    result.put("num",++num);
                }else {
                    result.put("ReturnCode","error");
                }
                result.put("errMsg",Util.resolveReturnMessage(StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage"))));
            }
        }
        catch(Exception e)
        {
            log.error("信用卡查询密码校验异常",e);
        }
        return result;
    }

    @Override
    public JSONObject debitQuerySet(String transServiceCode, String CARDNO,String CUSTIDNO,String IDNO,String MIMAZLEII, String PINBLOCK, String PINBLOCK_old, String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try{

            JSONObject temp = new JSONObject();
            //String transcode = "pcva.trade.b058.01";
            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            //-----user define
            temp.put("CUSTIDNO",CUSTIDNO);
            temp.put("KEHUZHAO",CARDNO);
            temp.put("CUSTACCTYPE","1");
            temp.put("KEHUZWMC","1");
            temp.put("MIMAZLEII",MIMAZLEII);
            temp.put("ZHJNZLEI","10101");
            temp.put("IDNO",IDNO);
            temp.put("CHULIBZH","1");
            temp.put("XINMIMAA",PINBLOCK);
            temp.put("YJYIMIMA",PINBLOCK_old);
            defObj.put("REQ_BODY",temp);
            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);
            //-----ReturnValue
            result.put("ReturnCode","true");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));

            if(!jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                result.put("ReturnCode","false");
                result.put("errMsg",Util.resolveReturnMessage(StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage"))));
            }
        }
        catch(Exception e)
        {
           log.error("设置查询密码异常",e);
        }
        return result;
    }

    @Override
    public JSONObject creditQuerySet(String transServiceCode, String CARDNO, String IDNO, String PINBLOCK, String PINBLOCK_old, String u_ani, String u_connid,String PINBLOCKFLAG) {
        JSONObject result = new JSONObject();
        try{
            //String transcode = "pcva.ccard.ccd006.01";

            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            JSONObject temp = new JSONObject();

            //-----user define
            //2代表修改 3代表重置
            if("2".equals(PINBLOCKFLAG)){
                temp.put("PINBLOCK",PINBLOCK_old);
            }else if ("1".equals(PINBLOCKFLAG) || "3".equals(PINBLOCKFLAG)){
                temp.put("ADDDATA","1"+IDNO);
            }else {
                result.put("ReturnCode","FFFFFF");
                return result;
            }
            temp.put("PINBLOCKFLAG",PINBLOCKFLAG);
            temp.put("CURRENCY","0");
            temp.put("CARDNO",CARDNO);
            temp.put("IDNO",IDNO);
            temp.put("IDTYPE","10101");
            temp.put("SCINFO",PINBLOCK);

            defObj.put("REQ_BODY",temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode","true");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage"));

            if(!jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                result.put("ReturnCode","false");
                result.put("errMsg",Util.resolveReturnMessage(StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage"))));
            }
        }
        catch(Exception e)
        {
            log.error("信用啦查询密码修改异常",e);
        }
        return result;
    }

    @Override
    public JSONObject debitQueryReset(String transServiceCode, String CARDNO, String ECIFCUSTNO,String IDNO ,String PINBLOCK, String u_ani, String u_connid) {

        JSONObject result = new JSONObject();
        try{

            JSONObject temp = new JSONObject();
            //String transcode = "pcva.trade.b037.01";
            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
            //-----user define
            temp.put("ECIFCUSTNO",ECIFCUSTNO);
            temp.put("ACCNO",CARDNO);
            temp.put("CUSTACCTYPE","1");
            temp.put("PWDTYPE","2");
            temp.put("CUSTNAME","1");
            temp.put("MIMAZLEII","11");
            temp.put("ZHJNZLEI","10101");
            temp.put("IDTYPE","10101");
            temp.put("IDNO",IDNO);
            temp.put("SETPINBLOCK",PINBLOCK);
            defObj.put("REQ_BODY",temp);

            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);

            //-----ReturnValue
            result.put("ReturnCode","true");
            result.put("ConsumerSeqNo",defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage",StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));

            if(!jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
                result.put("ReturnCode","false");
                result.put("errMsg",Util.resolveReturnMessage(StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage"))));
            }
        }
        catch(Exception e)
        {
            return result;

        }
        return result;
    }

    private JSONObject getEcif(String CARDNO,String u_ani,String  u_connid){

        JSONObject temp1 = new JSONObject();
        String transServiceCode = "pcva.trade.b054.01";
        JSONObject defObj = Util.getBasicJson(transServiceCode,u_ani,u_connid);
        JSONObject param = new JSONObject();
        //-----user define
        param.put("ACCNO",CARDNO);
        defObj.put("REQ_BODY",param);
        //usagi.jar
        JSONObject jsonObj = sendRequest(defObj);

        if(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")){
            temp1.put("ECIFCUSTNO",jsonObj.getJSONObject("RSP_BODY").getString("ECIFCUSTNO"));
            temp1.put("IDNO",jsonObj.getJSONObject("RSP_BODY").getString("IDNO"));
            temp1.put("ACCNO",CARDNO);
        }
        return temp1;
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
