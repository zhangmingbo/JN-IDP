package com.egoo.idp.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Util
{
  public static String swtichAPPLICATIONSTATUS(String APPLICATIONSTATUS)
  {
    switch (APPLICATIONSTATUS) {
    case "0":
      return "申请未受理";
    case "1":
      return "申请已受理待审批";
    case "2":
      return "审批中";
    case "3":
      return "审批通过待制卡";
    case "4":
      return "系统取消办卡 (客户通过征审系统取消办卡)";
    case "5":
      return "审批拒绝";
    case "6":
      return "审批通过制卡中";
    case "7":
      return "客服取消办卡 (客户通过客服系统取消办卡)";
    case "8":
      return "审批通过制卡成功";
    }
    return "未知";
  }

  public static String getYYYYMMbefore(int m_ago)
  {
    SimpleDateFormat SDF = new SimpleDateFormat("yyyyMM");
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());

    cal.add(2, -m_ago - 1);
    Date m = cal.getTime();
    return SDF.format(m);
  }

  public static String switchUserLevel(String UserLevel)
  {
    switch (UserLevel) {
    case "00":
      return "0";
    case "10":
      return "0";
    case "11":
      return "1";
    case "12":
      return "2";
    case "13":
      return "3";
    case "251":
      return "0";
    case "14":
      return "4";
    case "20":
      return "0";
    case "241":
      return "0";
    case "242":
      return "0";
    case "243":
      return "0";
    case "252":
      return "0";
    case "253":
      return "0";
    case "261":
      return "0";
    case "31":
      return "0";
    }
    return "0";
  }

  public static String switchACCKIND2(String ACCKIND2)
  {
    switch (ACCKIND2) {
    case "301":
      return "一类户";
    case "302":
      return "二类户";
    case "303":
      return "三类户";
    }
    return "未知";
  }

  public static String switchACCKIND1(String ACCKIND1)
  {
    switch (ACCKIND1) {
    case "0001":
      return "基本户";
    case "0002":
      return "一般户";
    case "0003":
      return "临时户";
    case "0004":
      return "专户";
    case "0006":
      return "NRA账户";
    case "0007":
      return "外币账户";
    case "0008":
      return "NRA基本户";
    case "0009":
      return "NRA一般户";
    case "0010":
      return "NRA专户";
    case "0101":
      return "单位非结算户";
    case "0102":
      return "单位委托存款户";
    case "0201":
      return "同业非结算户";
    case "1001":
      return "结构性存款(对公)";
    case "1201":
      return "结构性存款(对私)";
    case "1102":
      return "个人结算账户";
    case "1101":
      return "个人非结算户";
    case "0131":
      return "外汇结算户";
    case "0132":
      return "发票池回款专户";
    case "0133":
      return "出口收汇待核查户";
    case "0134":
      return "资本金专户";
    case "0135":
      return "国内外汇贷款专户";
    case "0136":
      return "还贷专户";
    case "0137":
      return "外债专户";
    case "0138":
      return "捐赠户";
    case "0139":
      return "离岸账户OSA";
    case "0140":
      return "保税区账户B";
    case "0141":
      return "出口加工区账户C";
    case "0142":
      return "其它非结算户";
    case "0143":
      return "前期费用外汇账户";
    case "0144":
      return "境内资产变现账户";
    case "0145":
      return "境内再投资专用账户";
    case "0146":
      return "境外汇入保证金专用外汇账户";
    case "0147":
      return "境内划入保证金专用外汇账户";
    case "0148":
      return "境外资产变现账户";
    case "0149":
      return "境外放款专用账户";
    case "XXX":
      return "默认值";
    case "ZZZ":
      return "组合账户";
    }
    return "未知";
  }

  public static String switchACCSTATE(String ACCSTATE)
  {
    String str = ACCSTATE; int i = -1; switch (str.hashCode()) { case 65:
      if (!str.equals("A")) break; i = 0; break;
    case 67:
      if (!str.equals("C")) break; i = 1; break;
    case 66:
      if (!str.equals("B")) break; i = 2; break;
    case 68:
      if (!str.equals("D")) break; i = 3; break;
    case 73:
      if (!str.equals("I")) break; i = 4; break;
    case 72:
      if (!str.equals("H")) break; i = 5; break;
    case 71:
      if (!str.equals("G")) break; i = 6; break;
    case 89:
      if (!str.equals("Y")) break; i = 7; break;
    case 74:
      if (!str.equals("J")) break; i = 8;
    case 69:
    case 70:
    case 75:
    case 76:
    case 77:
    case 78:
    case 79:
    case 80:
    case 81:
    case 82:
    case 83:
    case 84:
    case 85:
    case 86:
    case 87:
    case 88: } switch (i) {
    case 0:
      return "正常";
    case 1:
      return "销户";
    case 2:
      return "不动户";
    case 3:
      return "久悬户";
    case 4:
      return "转营业外收入";
    case 5:
      return "待启用";
    case 6:
      return "未启用";
    case 7:
      return "预销户";
    case 8:
      return "控制";
    }
    return "未知";
  }

  public static String switchDCMTTP(String DCMTTP)
  {
    switch (DCMTTP) {
    case "701":
      return "存款证明书(本外币)";
    case "704":
      return "外币携带证 -";
    case "731":
      return "活期储蓄存款存折";
    case "732":
      return "常州市财政涉农专用存折";
    case "734":
      return "一证通存折(本外币)";
    case "737":
      return "储蓄存单(本外币)";
    case "738":
      return "江南借记卡个人卡";
    case "739":
      return "江南借记卡员工卡";
    case "740":
      return "江南借记卡单位卡 -";
    case "741":
      return "市民卡 -";
    case "742":
      return "江南财缘卡";
    case "743":
      return "江南财缘卡员工卡";
    case "744":
      return "江南卡";
    case "745":
      return "大丰江南卡";
    case "746":
      return "龙城通卡";
    case "748":
      return "省社保卡";
    case "749":
      return "工会卡";
    case "750":
      return "DIY卡";
    case "752":
      return "人行支付系统专用凭证";
    case "753":
      return "小额支付系统专用凭证";
    case "754":
      return "电子汇兑补充凭证";
    case "755":
      return "盐城市支付清算专用凭证";
    case "756":
      return "常州同城交换补充凭证(本外币)";
    case "757":
      return "VIP金卡";
    case "758":
      return "VIP白金卡";
    case "759":
      return "VIP钻石卡";
    case "760":
      return "江南盈卡";
    case "771":
      return "单位定期存单(本外币)";
    case "772":
      return "单位定期开户证实书(本外币)";
    case "773":
      return "农信银全国银行汇票";
    case "774":
      return "银行本票";
    case "776":
      return "银行承兑汇票";
    case "777":
      return "商业承兑汇票";
    case "778":
      return "华东三省一市汇票";
    case "779":
      return "对公存折 -";
    case "781":
      return "现金支票";
    case "782":
      return "转账支票";
    case "783":
      return "支票(上海)";
    case "784":
      return "贷记凭证(上海)";
    case "801":
      return "现金调拨单";
    case "802":
      return "江苏省代收罚没款收据";
    case "803":
      return "电费收据";
    case "804":
      return "假币收缴凭证";
    case "805":
      return "股权证书";
    case "806":
      return "手机银行卡";
    case "807":
      return "印鉴卡(上海)";
    case "808":
      return "黄金账户卡";
    case "810":
      return "上海支付结算专用凭证";
    case "811":
      return "太平洋保险借意险保单";
    case "812":
      return "中国人寿借意险保单";
    case "813":
      return "USBKEY个人版";
    case "814":
      return "USBKEY企业版";
    case "815":
      return "光大永明人寿保险单";
    case "816":
      return "富友预付费卡(记名卡)";
    case "817":
      return "富友预付费卡(不记名卡)";
    case "818":
      return "聚金理财协议书";
    case "819":
      return "便民通服务协议";
    case "820":
      return "特惠商户三方合作协议";
    case "821":
      return "珠联璧合理财非保本协议书";
    case "822":
      return "金梅花理财协议书";
    case "823":
      return "商务通服务协议";
    case "824":
      return "旅通预付费卡";
    case "825":
      return "泰富卡(500)";
    case "826":
      return "泰富卡(1000)";
    case "827":
      return "泰富卡(不定额)";
    case "828":
      return "USBKEY个人版(二代)";
    case "829":
      return "USBKEY企业版(二代)";
    case "830":
      return "代收印花税凭证";
    case "831":
      return "现金支票(人行)";
    case "832":
      return "现金支票(工行)";
    case "833":
      return "现金支票(农行)";
    case "834":
      return "现金支票(中行)";
    case "835":
      return "现金支票(建行)";
    case "836":
      return "现金支票(商行)";
    case "837":
      return "现金支票(招行)";
    case "838":
      return "现金支票(交行)";
    case "839":
      return "现金支票(兴业)";
    case "840":
      return "现金支票(华夏)";
    case "841":
      return "现金支票(中信)";
    case "842":
      return "现金支票(浦发)";
    case "843":
      return "现金支票(民生)";
    case "844":
      return "现金支票(邮储)";
    case "845":
      return "转账支票(人行)";
    case "846":
      return "现金支票(行)";
    case "849":
      return "支票(农商行)";
    case "901":
      return "收回贷款凭证";
    case "902":
      return "收回利息凭证";
    case "099":
      return "其它重空凭证";
    case "850":
      return "阶梯电价电费收据";
    case "851":
      return "珠联璧合理财保本协议书";
    case "852":
      return "鸿富理财非保本协议书";
    case "853":
      return "机构信用代码证";
    case "854":
      return "悦富保证收益型理财协议书";
    case "855":
      return "聚富保本浮动收益型理财协议书";
    case "856":
      return "平安人意险";
    case "857":
      return "异地支行同城交换补充凭证";
    case "858":
      return "太平洋保险公司保单";
    case "859":
      return "东吴人寿保险公司保单";
    case "860":
      return "华泰人寿保险公司保单";
    case "861":
      return "中国人寿银保通";
    case "862":
      return "东吴人寿银保通";
    case "863":
      return "音频KEY";
    }
    return "未知";
  }

  public static String resolveReturnMessage(String ReturnMessage)
  {
    String result = "";

    String regEx = "错误信息.*";
    Pattern pattern = Pattern.compile(regEx);
    Pattern pattern_credit = Pattern.compile("信用卡核心返回.*");
    Pattern pattern_credit2 = Pattern.compile("授权返回.*");

    Pattern pattern_debit = Pattern.compile("(\\]\\[111\\]).*");

    Matcher matcher = pattern.matcher(ReturnMessage);
    if (matcher.find()) {
      result = matcher.group().substring(5);
      Matcher matcher_credit = pattern_credit.matcher(result);
      Matcher matcher_credit2 = pattern_credit2.matcher(result);
      Matcher matcher_debit = pattern_debit.matcher(result);
      if (matcher_credit.find())
        result = matcher_credit.group().substring(8);
      else if (matcher_credit2.find())
        result = matcher_credit2.group().substring(5);
      else while (matcher_debit.find()) {
          System.out.println(matcher_debit.toMatchResult());
          result = matcher_debit.group().trim().substring(6);
        }
    }
    else {
      result = "未知错误";
    }
    return result;
  }

  /**
   * 构建基础请求JSON（通用部分）
   */
  public static JSONObject getBasicJson(String transServiceCode, String u_ani, String u_connid) {
    JSONObject json = new JSONObject();
    try {
      Random rand = new Random();
      SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
      SimpleDateFormat sdf2 = new SimpleDateFormat("HHmmss");
      Calendar calendar = Calendar.getInstance();
      Date date = calendar.getTime();
      String sdate = sdf1.format(date);
      String stime = sdf2.format(date);

      JSONObject SYS_HEAD = new JSONObject();
      JSONObject APP_HEAD = new JSONObject();

      String mmid = getMMID();

      SYS_HEAD.put("ConsumerId", "C002");
      SYS_HEAD.put("RequestDate", sdate);
      SYS_HEAD.put("DeviceNum", u_ani);
      SYS_HEAD.put("TransServiceCode", transServiceCode);
      SYS_HEAD.put("RequestTime", stime);
      SYS_HEAD.put("ConsumerSeqNo", "C00201" + sdate + stime + mmid + "A" + rand.nextInt(999998));
      SYS_HEAD.put("AgentId", "00000");
      SYS_HEAD.put("AgentLevel", "0");
      SYS_HEAD.put("TranTellerNo", "jnuser");

      APP_HEAD.put("CHANNELCODE", "C002");
      APP_HEAD.put("MMDATE", sdate);
      APP_HEAD.put("MMID", mmid);

      json.put("SYS_HEAD", SYS_HEAD);
      json.put("APP_HEAD", APP_HEAD);

    } catch (Exception e) {
      log.error("获取统一请求异常", e);
      throw new RuntimeException("构建请求失败", e);
    }
    return json;
  }

  private static String getMMID() {
    // 生成UUID并移除连字符
    String uuid = UUID.randomUUID().toString().replace("-", "");
    // 取前16个字符
    return uuid.substring(0, 16);
  }

  public static String getPlasticcdStr(String plasticcd) {
    if("U".equals(plasticcd)){
       return "false";
    }else if(" ".equals(plasticcd)){
      return "true";
    }else {
      return "unknown";
    }
  }


  public static String change(String strmoney) {
    double inputMonney = Double.parseDouble(strmoney);
    if(inputMonney < 0){
      inputMonney = Math.abs(inputMonney);
    }
    if(inputMonney > 0){
      int decimalDigit = 2;//人名币保留2位小数到分
      char[] data = {'零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'};
      char[] units = {'分', '角', '元', '拾', '佰', '仟', '万', '拾', '佰', '仟', '亿', '拾', '佰', '仟','兆', '拾', '佰', '仟'};
      int uint = 0;
      long money = (long)(inputMonney * Math.pow(10, decimalDigit + 1));
      if (money % 10 > 4) {
        money = (money / 10) + 1;
      } else {
        money = money / 10;
      }
      StringBuffer sbf = new StringBuffer();
      while (money != 0) {
        sbf.insert(0, units[uint++]);//插入人名币单位
        sbf.insert(0, data[(int) (money % 10)]);//插入单位所对应的值
        money = money / 10;
      }
      return sbf.toString().replaceAll("零[仟佰拾]", "零").replaceAll("零+万", "万").replaceAll("零+亿", "亿").replaceAll("亿万", "亿零").replaceAll("零+", "零").replaceAll("零元", "元").replaceAll("零[角分]", "");
    }else {
      return "零元";
    }

  }

  public static String changeDate(String prompt){
    if (prompt == null || prompt.isEmpty()) {
      return prompt;
    }
    return prompt.replace("Y", "年").replace("M", "月");
  }

  public static Integer getBigNum(int num){
    try {
      if(num >=10){
        return 10;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return num;
  }

}
