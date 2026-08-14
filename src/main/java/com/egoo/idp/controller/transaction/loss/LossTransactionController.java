package com.egoo.idp.controller.transaction.loss;

import com.egoo.idp.service.LossTransactionService;
import com.egoo.idp.service.MenuTransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "账单管理")
@RestController
@RequestMapping("/if/v1/transaction/loss")
public class LossTransactionController {

    @Autowired
    private LossTransactionService lossTransactionService;

    /**
     * 检查卡片是否挂失
     */
    @ApiOperation("检查卡片是否挂失")
    @GetMapping("/check")
    public Object check(
            @RequestParam String opt,
            @RequestParam String DCMTST,
            @RequestParam(required = false) String DRAWTYPE,
            @RequestParam String DCMTTP) {

        // 调用通用服务处理交易
        return lossTransactionService.check(opt,DCMTST,DCMTTP,DRAWTYPE);
    }


    /**
     * 借记卡挂失(pcva.trade.b038.01)
     */
    @ApiOperation("借记卡挂失")
    @GetMapping("/debit/{transServiceCode}")
    public Object debitLoss(
            @PathVariable String transServiceCode,
            @RequestParam String CARDNO,
            @RequestParam String DCMTNO,
            @RequestParam String DCMTST,
            @RequestParam String IDNO,
            @RequestParam String DCMTTP,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return lossTransactionService.debitLoss(transServiceCode,CARDNO,DCMTNO,DCMTST,IDNO,DCMTTP,u_ani,u_connid);
    }


    /**
     * 信用卡口头挂失(pcva.ccard.ccd010.01)
     */
    @ApiOperation("信用卡口头挂失")
    @GetMapping("/credit/repostloss/{transServiceCode}")
    public Object creditRepostloss(
            @PathVariable String transServiceCode,
            @RequestParam String CARDNO,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return lossTransactionService.creditRepostloss(transServiceCode,CARDNO,u_ani,u_connid);
    }




}
