package com.egoo.idp.service.impl;

import com.egoo.idp.entity.common.DBResponse;
import com.egoo.idp.entity.common.FastjsonMapGeneratorCorrected;
import com.egoo.idp.entity.eurm.ResultCode;
import com.egoo.idp.service.KbpService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class KbpServiceImpl implements KbpService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpHeaders httpHeaders;

    @Value("${gateway.kbp.requestRspUrl}")
    private String requestRspUrl;

    @Value("${gateway.kbp.queryPrefix}")
    private String queryPrefixStr;

    @Value("${gateway.kbp.querySuffix}")
    private String querySuffixStr;

    public KbpServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        // 初始化HTTP头信息
        this.httpHeaders = new HttpHeaders();
        this.httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @PostConstruct
    public void init() {
        // 验证必要配置是否存在
        if (ObjectUtils.isEmpty(requestRspUrl)) {
            log.warn("KBP接口地址未配置，请检查gateway.kbp.requestRspUrl");
        }
    }

    @Override
    public void sendKbpRequestRsp(String queryPrefix, String querySuffix, String knowledgeBase, DBResponse dbResponse, Integer num) {
        // 参数校验
        if (ObjectUtils.isEmpty(knowledgeBase)) {
            log.error("知识库参数不能为空");
            return;
        }

        String answer = null;
        try {
            // 构建查询字符串
            String query = buildQuery(queryPrefix, querySuffix);
            log.info("生成的KBP查询字符串: {}", query);

            // 生成请求体
            String requestBody = FastjsonMapGeneratorCorrected.generateRequestJsonWithMap(query, knowledgeBase);
            if (ObjectUtils.isEmpty(requestBody)) {
                log.error("生成KBP请求体失败");
                return;
            }

            // 发送请求
            HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
            log.info("发送KBP请求: URL={}, 请求体={}", requestRspUrl, requestBody);

            String kbpResp = null;
            try {
                kbpResp = restTemplate.postForObject(requestRspUrl, entity, String.class);
                log.info("收到KBP响应: {}", kbpResp);
            } catch (Exception e) {
                log.error("调用kbp异常",e);
            }

            // 解析响应
            if (!ObjectUtils.isEmpty(kbpResp)) {
                answer = parseAnswerFromResponse(kbpResp);
                dbResponse.setBody(answer);
                if(!ObjectUtils.isEmpty(answer) && !answer.equals("无法识别") && !answer.equals("人工")){
                    dbResponse.setRetCode(ResultCode.SUCCESS.code());
                    dbResponse.setMsg(ResultCode.SUCCESS.message());
                }else {
                    if(ObjectUtils.isEmpty(num)){
                        dbResponse.setRequestNum(1);
                    }else if(num <=2){
                        dbResponse.setRequestNum(++num);
                    }else {
                        if("无法识别".equals(answer)){
                            dbResponse.setMsg(ResultCode.BAD_REQUEST.message());
                            dbResponse.setRetCode(ResultCode.BAD_REQUEST.code());
                        }
                        if("人工".equals(answer)){
                            dbResponse.setMsg(ResultCode.UNAUTHORIZED.message());
                            dbResponse.setRetCode(ResultCode.UNAUTHORIZED.code());
                        }
                    }
                }
            } else {
                log.error("KBP返回空响应");
            }

        } catch (Exception e) {
            log.error("调用KBP接口异常", e);
        }
    }

    /**
     * 构建查询字符串
     */
    private String buildQuery(String queryPrefix, String querySuffix) {
        boolean isPrefixEmpty = ObjectUtils.isEmpty(queryPrefix);
        boolean isSuffixEmpty = ObjectUtils.isEmpty(querySuffix);

        if (isPrefixEmpty && isSuffixEmpty) {
            return queryPrefixStr + querySuffixStr;
        } else if (isPrefixEmpty) {
            return queryPrefixStr + querySuffix;
        } else if (isSuffixEmpty) {
            return queryPrefix + querySuffixStr;
        } else {
            return queryPrefix + querySuffix;
        }
    }

    /**
     * 从响应中解析答案，处理可能的空指针
     */
    private String parseAnswerFromResponse(String response) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(response);

        JsonNode transactionNode = jsonNode.get("Transaction");
        if (transactionNode == null) {
            log.error("KBP响应中缺少Transaction节点");
            return null;
        }

        JsonNode bodyNode = transactionNode.get("Body");
        if (bodyNode == null) {
            log.error("KBP响应中缺少Body节点");
            return null;
        }

        JsonNode responseNode = bodyNode.get("response");
        if (responseNode == null) {
            log.error("KBP响应中缺少response节点");
            return null;
        }

        JsonNode bizBodyNode = responseNode.get("bizBody");
        if (bizBodyNode == null) {
            log.error("KBP响应中缺少bizBody节点");
            return null;
        }

        JsonNode answerNode = bizBodyNode.get("answer");
        return answerNode != null ? answerNode.asText() : null;
    }
}
