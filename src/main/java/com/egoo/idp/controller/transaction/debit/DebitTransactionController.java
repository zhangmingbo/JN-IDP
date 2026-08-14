package com.egoo.idp.controller.transaction.debit;

import com.egoo.idp.service.DebitTransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "借记卡接口管理")
@RestController
@RequestMapping("/if/v1/transaction/debit")
public class DebitTransactionController {

    @Autowired
    private DebitTransactionService debitTransactionService;

    /**
     * 信用卡申请进度查询(pcva.ccard.ccd002.01)
     */
    @ApiOperation("根据卡号或身份证查询借记卡")
    @GetMapping("/idOrCardInquire")
    public Object idOrCardInquire(
            @RequestParam String var_input,
            @RequestParam(required = false) String opt,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return debitTransactionService.idOrCardInquire(var_input,opt,u_ani,u_connid);
    }

    /**
     * 确认最终卡号
     */
    @ApiOperation("确认最终卡号")
    @GetMapping("/query/card")
    public Object queryPassword(
            @RequestParam String CARDNOS,
            @RequestParam String CARDNO_SUFF) {

        // 调用通用服务处理交易
        return debitTransactionService.queryCard(CARDNOS,CARDNO_SUFF);
    }


    /**
     * 查看借机卡查询密码(pcva.trade.b056.01)
     */
    @ApiOperation("查看借机卡查询密码")
    @GetMapping("/query/password/{transServiceCode}")
    public Object queryPassword(
            @PathVariable String transServiceCode,
            @RequestParam String CARDNO,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return debitTransactionService.queryPassword(transServiceCode,CARDNO,u_ani,u_connid);
    }

    /**
     * 个人账户开户行查询(pcva.trade.b173.01)
     */
    @ApiOperation("个人借记卡开户行查询")
    @GetMapping("/person/bankOfDeposit/{transServiceCode}")
    public Object personBankOfDeposit(
            @PathVariable String transServiceCode,
            @RequestParam String ACCTBRNO,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return debitTransactionService.personBankOfDeposit(transServiceCode,ACCTBRNO,u_ani,u_connid);
    }

    /**
     * 公司账户开户行查询(pcva.trade.b186.01)
     */
    @ApiOperation("个人借记卡开户行查询")
    @GetMapping("/business/bankOfDeposit/{transServiceCode}")
    public Object businessBankOfDeposit(
            @PathVariable String transServiceCode,
            @RequestParam String CARDNO,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return debitTransactionService.businessBankOfDeposit(transServiceCode,CARDNO,u_ani,u_connid);
    }

    /**
     * 借记卡余额(pcva.trade.b048_Query.01)
     */
    @ApiOperation("个人借记卡开户行查询")
    @GetMapping("/balance/{transServiceCode}")
    public Object balance(
            @PathVariable String transServiceCode,
            @RequestParam String CARDNO,
            @RequestParam String CUSTNO,
            @RequestParam String ONLNBL,
            @RequestParam String AVAILBL,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return debitTransactionService.balance(transServiceCode,CARDNO,CUSTNO,ONLNBL,AVAILBL,u_ani,u_connid);
    }

    /**
     * 借记卡卡号查询验证(pcva.trade.b023.01)
     */
    @ApiOperation("个人借记卡开户行查询")
    @GetMapping("/card/check/{transServiceCode}")
    public Object cardCheck(
            @PathVariable String transServiceCode,
            @RequestParam String debit_cardno,
            @RequestParam String credit_idno,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return debitTransactionService.cardCheck(transServiceCode,debit_cardno,credit_idno,u_ani,u_connid);
    }


    /**
     * 借记卡账单查询(pcva.trade.m019.01)
     */
    @ApiOperation("借记卡账单查询(pcva.trade.m019.01)")
    @GetMapping("/menu/{transServiceCode}")
    public Object cardMenu(
            @PathVariable String transServiceCode,
            @RequestParam String CARDNO,
            @RequestParam String day,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return debitTransactionService.cardMenu(transServiceCode,CARDNO,day,u_ani,u_connid);
    }

    /**
     * 对公卡号是否一致(pcva.trade.b200.01)
     */
    @ApiOperation("对公卡号是否一致(pcva.trade.b200.01)")
    @GetMapping("/phone/compare/{transServiceCode}")
    public Object phoneCompare(
            @PathVariable String transServiceCode,
            @RequestParam String ECIFCUSTNO,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return debitTransactionService.phoneCompare(transServiceCode,ECIFCUSTNO,u_ani,u_connid);
    }


    /**
     * 判断卡号是否为借记卡
     */
    @ApiOperation("判断卡号是否为借记卡")
    @GetMapping("/checkCardLength")
    public Object checkCardLength(
            @RequestParam String cardNo) {

        // 调用通用服务处理交易
        return debitTransactionService.checkCardLength(cardNo);
    }


}
