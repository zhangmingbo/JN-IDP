package com.egoo.idp.controller.transaction.menu;

import com.egoo.idp.service.MenuTransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "账单管理")
@RestController
@RequestMapping("/if/v1/transaction/menu")
public class MenuTransactionController {

    @Autowired
    private MenuTransactionService menuTransactionService;

    /**
     * 查询卡号是否有查询密码(pcva.ccard.ccd070.01)
     */
    @ApiOperation("查询最终卡号")
    @GetMapping("/queryPass/{transServiceCode}")
    public Object queryPass(
            @PathVariable String transServiceCode,
            @RequestParam String CARDNO,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return menuTransactionService.queryPass(transServiceCode, CARDNO,u_ani,u_connid);
    }

    /**
     * 修改账单信息(pcva.ccard.ccd076.01)
     */
    @ApiOperation("修改账单日")
    @GetMapping("/update/date/{transServiceCode}")
    public Object updateDate(
            @PathVariable String transServiceCode,
            @RequestParam String ACCNO,
            @RequestParam String BILLCYCLE,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return menuTransactionService.updateDate(transServiceCode, ACCNO,BILLCYCLE,u_ani,u_connid);
    }

    /**
     * 修改账单信息(pcva.ccard.ccd076.01)
     */
    @ApiOperation("修改账单地址")
    @GetMapping("/update/address/{transServiceCode}")
    public Object updateAdress(
            @PathVariable String transServiceCode,
            @RequestParam String ACCNO,
            @RequestParam String BILLADDRCD,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return menuTransactionService.updateAdress(transServiceCode, ACCNO,BILLADDRCD,u_ani,u_connid);
    }


    /**
     * 已出账单(pcva.ccard.ccd088.01)
     */
    @ApiOperation("已出账单")
    @GetMapping("/outstandingBill/{transServiceCode}")
    public Object outstandingBill(
            @PathVariable String transServiceCode,
            @RequestParam String CARDNO,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return menuTransactionService.outstandingBill(transServiceCode, CARDNO,u_ani,u_connid);
    }



    /**
     * 未出账单(pcva.ccard.ccd018.01)
     */
    @ApiOperation("未出账单")
    @GetMapping("/unbilled/{transServiceCode}")
    public Object unbilled(
            @PathVariable String transServiceCode,
            @RequestParam String ACCNO,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return menuTransactionService.unbilled(transServiceCode, ACCNO,u_ani,u_connid);
    }


    /**
     * 获取账单地址(pcva.trade.ccd014.01)
     */
    @ApiOperation("获取账单地址")
    @GetMapping("/getAddress/{transServiceCode}")
    public Object getAddress(
            @PathVariable String transServiceCode,
            @RequestParam String IDTYPE,
            @RequestParam String IDNO,
            @RequestParam String ACCNO,
            @RequestParam String u_ani,
            @RequestParam String u_connid) {

        // 调用通用服务处理交易
        return menuTransactionService.getAddress(transServiceCode,IDTYPE, IDNO,ACCNO,u_ani,u_connid);
    }



}
