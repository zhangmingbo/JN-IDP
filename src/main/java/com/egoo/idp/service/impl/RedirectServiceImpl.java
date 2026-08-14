package com.egoo.idp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.egoo.idp.service.RedirectService;
import com.egoo.idp.utils.StrUtil;
import com.egoo.idp.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class RedirectServiceImpl implements RedirectService {

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;

    @Value("${gateway.transaction.uservice}")
    private String uservice;

    @Value("${gateway.transaction.requestUrl}")
    private String requestUrl;

    public RedirectServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        // 初始化HTTP头信息
        this.httpHeaders = new HttpHeaders();
        this.httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public JSONObject getRedirectInfo(String ANI) {

        JSONObject result = new JSONObject();
        try {

            // 处理 ANI 格式，提取手机号部分
            if (ANI.contains(";")) {
                ANI = ANI.split(";")[0];
            }

            // usagi.jar
            JSONObject temp = new JSONObject();

            //-----user define
            result.put("ANI", ANI);

            result.put("AN2", ANI);
            temp.put("phoneNo", ANI);

            JSONObject defObj = sendServiceRequest(temp, "/transaction/rdr001/q");

            if ("0".equals(defObj.get("code"))) {
                result.put("routeType", defObj.getJSONObject("data").getString("routeType"));
                result.put("routeDest", defObj.getJSONObject("data").getString("routeDest"));
                result.put("routeCode", "true");
            } else {
                result.put("routeCode", "false");
                result.put("code", defObj.get("code"));
            }

            // XQXQ2024-1005云信贷客户经理与总行审批岗语音联系需求
            // 转接给指定的坐席工号
            JSONObject jsonObject = sendServiceRequest(temp,  "/transaction/tra002/query");
            result.put("targetCode", "false");
            if ("0".equals(jsonObject.get("code"))) {
                String targetType = (String)jsonObject.get("targetType");
                if(!ObjectUtils.isEmpty(targetType)){
                    result.put("targetType", targetType);
                    result.put("target", jsonObject.get("target"));
                    result.put("userData", jsonObject.get("userData"));
                    result.put("targetCode", "true");
                }
            }

        } catch (Exception e) {
            log.error("云信贷呼入重定向异常",e);
        }
        return result;
    }

    @Override
    public JSONObject elder(String transServiceCode,String u_ani, String u_connid) {
        JSONObject result = new JSONObject();
        try {

            //input:
            // -----Q074 查老年账户
            // 入参 PHONE_NO
            // 返回 OLD_FLAG 0 1
            //String transcode = "pcva.trade.q074.01";

            //usagi.jar
            JSONObject defObj = Util.getBasicJson(transServiceCode, u_ani, u_connid);
            JSONObject temp = new JSONObject();

            // u_ani处理
            // 判断号码长度是否大于12,如果大于12且有86 +86前缀，则去掉前缀
            if (u_ani.length() > 12 && u_ani.startsWith("86")) {
                u_ani = u_ani.substring(2);
            }

            //-----user define
            temp.put("PHONE_NO", u_ani);
            defObj.put("REQ_BODY", temp);


            //usagi.jar
            JSONObject jsonObj = sendRequest(defObj);
            String prompt = "";
            //-----ReturnValue
            result.put("ReturnCode", "false");
            result.put("ConsumerSeqNo", defObj.getJSONObject("SYS_HEAD").getString("ConsumerSeqNo"));
            result.put("ReturnMessage", StrUtil.paseStrUTF8(jsonObj.getJSONObject("SYS_HEAD").getString("ReturnMessage")));
            if (jsonObj.getJSONObject("SYS_HEAD").getString("ReturnCode").equals("000000")) {
                result.put("OLD_FLAG", jsonObj.getJSONObject("RSP_BODY").getString("OLD_FLAG"));
                result.put("ReturnCode", "true");
            } else {
                // 如果报错，默认给OLD_FLAG赋值为0
                result.put("OLD_FLAG", "0");
            }
        }
        catch(Exception e)
        {
            log.error("获取老年人异常",e);
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

    /**
     * 发送请求到后端服务
     */
    private JSONObject sendServiceRequest(JSONObject requestJson,String method) {
        try {
            HttpEntity<String> entity = new HttpEntity<>(requestJson.toString(), httpHeaders);
            log.info("发送请求: URL={}, 请求体={}", uservice+method, requestJson);
            String res = restTemplate.postForObject(uservice+method, entity, String.class);
            log.info("交易返回 ={}", res);
            return JSONObject.parseObject(res);
        } catch (Exception e) {
            log.error("请求异常:", e);
            throw new RuntimeException("服务调用失败", e);
        }
    }
}
