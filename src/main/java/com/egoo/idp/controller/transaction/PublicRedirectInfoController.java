package com.egoo.idp.controller.transaction;


import com.egoo.idp.service.RedirectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "公共路由管理")
@RestController
@RequestMapping("/if/v1/transaction/")
public class PublicRedirectInfoController {

    @Autowired
    private RedirectService redirectService;

    @ApiOperation("云信贷呼入重定向")
    @GetMapping("/redirect/info")
    public Object getRedirectInfo(
            @RequestParam String ANI) {

        // 调用通用服务处理交易
        return redirectService.getRedirectInfo(ANI);
    }
    @ApiOperation("老年人接口")
    @GetMapping("/elder/{transServiceCode}")
    public Object elder(
            @PathVariable String transServiceCode,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return redirectService.elder(transServiceCode, u_ani ,u_connid);
    }

}
