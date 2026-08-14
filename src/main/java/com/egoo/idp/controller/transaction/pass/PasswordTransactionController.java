package com.egoo.idp.controller.transaction.pass;


import com.egoo.idp.service.PasswordTransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Api(tags = "交易密码管理")
@RestController
@RequestMapping("/if/v1/transaction/password")
public class PasswordTransactionController {

    @Autowired
    private PasswordTransactionService passwordTransactionService;


    /**
     * 密码校验
     */
    @ApiOperation("密码重复校验")
    @GetMapping("/checkout")
    public Object processTransaction(
            @RequestParam String password,
            @RequestParam String confirmPassword
    ) {

        Map<String, Object> res = null;
        Boolean isRight = false;
        try {
            res = new HashMap<>();
            if(!ObjectUtils.isEmpty(password) && !ObjectUtils.isEmpty(confirmPassword)){
                 if(password.equals(confirmPassword)){
                     isRight = true;
                 }
            }
            res.put("isRight",isRight);
        } catch (Exception e) {
            log.error("校验密码异常:{}",e);
            res.put("isRight",isRight);
        }
        return res;
    }

    /**
     * 交易密码加密(pcva.trade.b013_1.01)
     */
    @ApiOperation("交易密码加密")
    @GetMapping("/encrypt/{transServiceCode}")
    public Object passEncrypt(
            @PathVariable String transServiceCode,
            @RequestParam String CARDNO,
            @RequestParam String PINBLOCK,
            @RequestParam(required = false) String opt_type,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return passwordTransactionService.passEncrypt(transServiceCode, CARDNO , PINBLOCK,opt_type,u_ani,u_connid);
    }


    /**
     * 校验
     */
    @ApiOperation("弱密码校验")
    @GetMapping("/checkWeakPassword")
    public Object checkWeakPassword(
            @RequestParam String u_ani,
            @RequestParam String IDNO,
            @RequestParam String CARDNO,
            @RequestParam String PINBLOCK) {

        // 调用通用服务处理交易
        return passwordTransactionService.checkWeakPassword(u_ani,IDNO,CARDNO,PINBLOCK);
    }


    /**
     * 信用卡交易密码校验(pcva.ccard.ccd050.01)
     */
    @ApiOperation("信用卡交易密码校验")
    @GetMapping("/credit/check/{transServiceCode}")
    public Object creditCheck(
            @PathVariable String transServiceCode,
            @RequestParam String CARDNO,
            @RequestParam String PINBLOCK,
            @RequestParam String u_ani,
            @RequestParam String u_connid,
            @RequestParam(required = false) Integer num) {

        // 调用通用服务处理交易
        return passwordTransactionService.creditCheck(transServiceCode,CARDNO,PINBLOCK,u_ani,u_connid,num);
    }


    /**
     * 信用卡交易密码上传(pcva.ccard.ccd005.01)
     */
    @ApiOperation("信用卡交易密码上传")
    @GetMapping("/credit/{transServiceCode}")
    public Object creditSet(
            @PathVariable String transServiceCode,
            @RequestParam String u_ani,
            @RequestParam String u_connid,
            @RequestParam String CARDNO,
            @RequestParam String IDNO,
            @RequestParam String PINBLOCKFLAG,
            @RequestParam String PINBLOCK,
            @RequestParam(required = false) String PINBLOCK_old,
            @RequestParam(required = false) String cvalId) {

        // 调用通用服务处理交易
        return passwordTransactionService.creditSet(transServiceCode,u_ani,u_connid,CARDNO,IDNO,PINBLOCKFLAG,PINBLOCK,PINBLOCK_old,cvalId);
    }



    /**
     * 信用卡查询密码校验(pcva.ccard.ccd052.01)
     */
    @ApiOperation("信用卡查询密码校验")
    @GetMapping("/credit/query/check/{transServiceCode}")
    public Object creditQueryCheck(
            @PathVariable String transServiceCode,
            @RequestParam String u_ani,
            @RequestParam String u_connid,
            @RequestParam String CARDNO,
            @RequestParam String PINBLOCK,
            @RequestParam(required = false) Integer num) {

        // 调用通用服务处理交易
        return passwordTransactionService.creditQueryCheck(transServiceCode,CARDNO,PINBLOCK,u_ani,u_connid,num);
    }


    /**
     * 信用卡查询密码修改(pcva.ccard.ccd006.01)
     */
    @ApiOperation("信用卡查询密码修改")
    @GetMapping("/credit/query/set/{transServiceCode}")
    public Object creditQuerySet(
            @PathVariable String transServiceCode,
            @RequestParam String u_ani,
            @RequestParam String u_connid,
            @RequestParam String CARDNO,
            @RequestParam String IDNO,
            @RequestParam String PINBLOCK,
            @RequestParam String PINBLOCK_old,
            @RequestParam String PINBLOCKFLAG) {

        // 调用通用服务处理交易
        return passwordTransactionService.creditQuerySet(transServiceCode,CARDNO,IDNO,PINBLOCK,PINBLOCK_old,u_ani,u_connid,PINBLOCKFLAG);
    }


    /**
     * 借记卡交易密码验证(pcva.trade.b175.01)
     */
    @ApiOperation("借记卡交易密码验证")
    @GetMapping("/debitCard/check/{transServiceCode}")
    public Object debitCheck(
            @PathVariable String transServiceCode,
            @RequestParam String CARDNO,
            @RequestParam String PINBLOCK,
            @RequestParam(required = false) Integer num,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return passwordTransactionService.debitCheck(transServiceCode, CARDNO , PINBLOCK,num,u_ani,u_connid);
    }


    /**
     * 借记卡交易密码上传(pcva.trade.b059.01)
     */
    @ApiOperation("借记卡交易密码上传")
    @GetMapping("/debitCard/set/{transServiceCode}")
    public Object debitSet(
            @PathVariable String transServiceCode,
            @RequestParam String u_ani,
            @RequestParam String u_connid,
            @RequestParam String CARDNO,
            @RequestParam String CUSTIDNO,
            @RequestParam String IDNO,
            @RequestParam String PINBLOCK) {

        // 调用通用服务处理交易
        return passwordTransactionService.debitSet(transServiceCode,u_ani,u_connid,CARDNO,CUSTIDNO,IDNO,PINBLOCK);
    }


    /**
     * 借记卡查询密码校验(pcva.trade.b175.01)
     */
    @ApiOperation("借记卡查询密码校验")
    @GetMapping("/debit/query/check/{transServiceCode}")
    public Object debitQueryCheck(
            @PathVariable String transServiceCode,
            @RequestParam String u_ani,
            @RequestParam String u_connid,
            @RequestParam String CARDNO,
            @RequestParam String PINBLOCK,
            @RequestParam(required = false) Integer num) {

        // 调用通用服务处理交易
        return passwordTransactionService.debitQueryCheck(transServiceCode,CARDNO,PINBLOCK,u_ani,u_connid,num);
    }


    /**
     * 借记卡交易密码修改(pcva.trade.b058.01)
     */
    @ApiOperation("借记卡交易密码修改")
    @GetMapping("/debit/query/set/{transServiceCode}")
    public Object debitQuerySet(
            @PathVariable String transServiceCode,
            @RequestParam String u_ani,
            @RequestParam String u_connid,
            @RequestParam String CARDNO,
            @RequestParam String CUSTIDNO,
            @RequestParam String IDNO,
            @RequestParam String MIMAZLEII,
            @RequestParam String PINBLOCK,
            @RequestParam(required = false) String PINBLOCK_old) {

        // 调用通用服务处理交易
        return passwordTransactionService.debitQuerySet(transServiceCode,CARDNO,CUSTIDNO,IDNO,MIMAZLEII,PINBLOCK,PINBLOCK_old,u_ani,u_connid);
    }

    /**
     * 借记卡查询密码重置(pcva.trade.b037.01)
     */
    @ApiOperation("借记卡查询密码重置")
    @GetMapping("/debit/query/reset/{transServiceCode}")
    public Object debitQueryReset(
            @PathVariable String transServiceCode,
            @RequestParam String u_ani,
            @RequestParam String u_connid,
            @RequestParam String CARDNO,
            @RequestParam String ECIFCUSTNO,
            @RequestParam String IDNO,
            @RequestParam String PINBLOCK) {

        // 调用通用服务处理交易
        return passwordTransactionService.debitQueryReset(transServiceCode,CARDNO,ECIFCUSTNO,IDNO,PINBLOCK,u_ani,u_connid);
    }


    /**
     * 获取卡信息(pcva.trade.b054.01)
     */
    @ApiOperation("获取卡信息")
    @GetMapping("/getEcif/{transServiceCode}")
    public Object getEcif(
            @PathVariable String transServiceCode,
            @RequestParam String u_ani,
            @RequestParam String u_connid,
            @RequestParam String CARDNO) {

        // 调用通用服务处理交易
        return passwordTransactionService.getEcif(transServiceCode,CARDNO,u_ani,u_connid);
    }



}
