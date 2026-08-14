package com.egoo.idp.controller.transaction.credit;

import com.egoo.idp.service.CreditTransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "交易接口管理")
@RestController
@RequestMapping("/if/v1/transaction/credit")
public class CreditTransactionController {

    @Autowired
    private CreditTransactionService creditTransactionService;

    /**
     * 信用卡申请进度查询(pcva.ccard.ccd002.01)
     */
    @ApiOperation("信用卡申请进度查询")
    @GetMapping("/progress/{transServiceCode}")
    public Object processTransaction(
            @PathVariable String transServiceCode,
            @RequestParam String IDNO,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return creditTransactionService.creditProgress(transServiceCode, IDNO,u_ani,u_connid);
    }


    /**
     * 卡号或身份证查询
     */
    @ApiOperation("卡号或身份证查询")
    @GetMapping("/idOrCardInquire")
    public Object idOrCardInquire(
            @RequestParam String var_input,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return creditTransactionService.idOrCardInquire(var_input,u_ani,u_connid);
    }


    /**
     * 查询主副卡状态
     */
    @ApiOperation("查询主副卡状态pcva.ccard.ccd046.01")
    @GetMapping("/cardStatus/{transServiceCode}")
    public Object cardStatus(
            @PathVariable String transServiceCode,
            @RequestParam String CARDNO,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return creditTransactionService.cardStatus(transServiceCode,CARDNO,u_ani,u_connid);
    }


    /**
     * 获取面签信息手机号
     */
    @ApiOperation("获取面签信息手机号pcva.ccard.ccd036.01")
    @GetMapping("/getVisaInterview/{transServiceCode}")
    public Object getVisaInterview(
            @PathVariable String transServiceCode,
            @RequestParam String CARDNO,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return creditTransactionService.getVisaInterview(transServiceCode,CARDNO,u_ani,u_connid);
    }


    /**
     * 加密cvv2
     */
    @ApiOperation("加密cvv2")
    @GetMapping("/encryptcvv2")
    public Object encryptcvv2(
            @RequestParam String var_input,
            @RequestParam String opt_type) {

        // 调用通用服务处理交易
        return creditTransactionService.encryptcvv2(var_input,opt_type);
    }

    /**
     * 查询激活结果(pcva.ccard.ccd037.01)
     */
    @ApiOperation("查询激活结果")
    @GetMapping("/getActivaRes/{transServiceCode}")
    public Object getActivaRes(
            @PathVariable String transServiceCode,
            @RequestParam String CARDNO,
            @RequestParam String IDNO,
            @RequestParam String validdate,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return creditTransactionService.getActivaRes(transServiceCode,CARDNO,IDNO,validdate,u_ani,u_connid);
    }


    /**
     * 校验cvv2有效期pcva.ccard.ccd051.01"
     */
    @ApiOperation("校验cvv2有效期")
    @GetMapping("/checkcvv2ValidDate/{transServiceCode}")
    public Object checkcvv2ValidDate(
            @PathVariable String transServiceCode,
            @RequestParam String CARDNO,
            @RequestParam String IDNO,
            @RequestParam String var_cvv2_encrypt,
            @RequestParam String var_validdate_encrypt,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return creditTransactionService.checkcvv2ValidDate(transServiceCode,CARDNO,IDNO,var_cvv2_encrypt,var_validdate_encrypt,u_ani,u_connid);
    }

    /**
     * 获取额度播报
     */
    @ApiOperation("获取额度播报pcva.trade.ccd014.01")
    @GetMapping("/getCreditLimit/{transServiceCode}")
    public Object getCreditLimit(
            @PathVariable String transServiceCode,
            @RequestParam String IDNO,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return creditTransactionService.getCreditLimit(transServiceCode,IDNO,u_ani,u_connid);
    }

    /**
     * 借记卡号验证
     */
    @ApiOperation("借记卡号验证pcva.trade.b023.01")
    @GetMapping("/cardVerifi/{transServiceCode}")
    public Object cardVerifi(
            @PathVariable String transServiceCode,
            @RequestParam String CARDNO,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return creditTransactionService.cardVerifi(transServiceCode, CARDNO, u_ani, u_connid);
    }

    /**
     * 自动还款设置
     */
    @ApiOperation("自动还款设置pcva.trade.ccd023.01")
    @GetMapping("/automaticPay/{transServiceCode}")
    public Object automaticPay(
            @PathVariable String transServiceCode,
            @RequestParam String ACCNO,
            @RequestParam String DEBITCARD,
            @RequestParam String AUTPMTTYPE,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return creditTransactionService.automaticPay(transServiceCode,ACCNO,DEBITCARD,AUTPMTTYPE,u_ani,u_connid);
    }


    /**
     * 信用卡查询单日明细
     */
    @ApiOperation("信用卡查询单日明细pcva.trade.ccd024.01")
    @GetMapping("/tranDetailSing/{transServiceCode}")
    public Object tranDetailSing(
            @PathVariable String transServiceCode,
            @RequestParam String CARDNO,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return creditTransactionService.tranDetailSing(transServiceCode,CARDNO,u_ani,u_connid);
    }

    /**
     * 信用卡查询多日明细
     */
    @ApiOperation("信用卡查询多日明细pcva.ccard.ccd019.01")
    @GetMapping("/tranDetailMult/{transServiceCode}")
    public Object tranDetailMult(
            @PathVariable String transServiceCode,
            @RequestParam String ACCNO,
            @RequestParam String day,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return creditTransactionService.tranDetailMult(transServiceCode,ACCNO,day,u_ani,u_connid);
    }

}
