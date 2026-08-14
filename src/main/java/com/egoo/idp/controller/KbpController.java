package com.egoo.idp.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.egoo.idp.entity.common.DBResponse;
import com.egoo.idp.entity.eurm.ResultCode;
import com.egoo.idp.service.KbpService;
import com.egoo.idp.utils.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;


@Api(tags = "知识库信息管理")
@RestController
@RequestMapping("/if/v1/rsp")
@Slf4j
public class KbpController {

    @Autowired
    private KbpService kbpService;

    @ApiOperation("获取知识点")
    @GetMapping("/getKbpAnswer")
    public DBResponse page(
            @RequestParam(required = false) String queryPrefix,
            @RequestParam(required = false) String querySuffix,
            @RequestParam String knowledgeBase,
            @RequestParam(required = false) Integer num
    ) {

        DBResponse dbResponse = new DBResponse(ResultCode.SUCCESS.code(), ResultCode.SUCCESS.message());

        kbpService.sendKbpRequestRsp(queryPrefix,querySuffix, knowledgeBase,dbResponse,num);


        return dbResponse;
    }

    @ApiOperation("test")
    @GetMapping("/test")
    public DBResponse test() {

        DBResponse dbResponse = new DBResponse(ResultCode.SUCCESS.code(), ResultCode.SUCCESS.message());
        JSONObject jsonObject = new JSONObject();
        StrUtil.capturePrompt("123",jsonObject);
        dbResponse.setBody(jsonObject);

        return dbResponse;
    }
}
