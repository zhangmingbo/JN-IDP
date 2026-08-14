package com.egoo.idp.controller;

import com.egoo.idp.entity.common.DBResponse;
import com.egoo.idp.entity.dto.HistorySessionInfoDto;
import com.egoo.idp.entity.eurm.ResultCode;
import com.egoo.idp.service.HistorySessionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "会话信息管理")
@RestController
@RequestMapping("/if/v1/history/session")
public class HistorySessionController {

    @Resource
    private HistorySessionService historySessionService;

    @ApiOperation("根据电话查询网点电话")
    @GetMapping("/getAgentIdByPhone")
    public DBResponse page(
            @RequestParam String phoneNum,
            @RequestParam(defaultValue = "wddh") String tenantId
    ) {

        DBResponse dbResponse = new DBResponse(ResultCode.BAD_REQUEST.code(), ResultCode.BAD_REQUEST.message());
        if (!ObjectUtils.isEmpty(phoneNum)) {
            HistorySessionInfoDto historySessionInfoDto = historySessionService.getAgentIdByPhone(phoneNum,tenantId);
            if(!ObjectUtils.isEmpty(historySessionInfoDto)){
                dbResponse.setRetCode(ResultCode.SUCCESS.code());
                dbResponse.setMsg(ResultCode.SUCCESS.message());
                dbResponse.setBody(historySessionInfoDto);
            }
        }
        return dbResponse;
    }
}
