package com.egoo.idp.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.egoo.idp.entity.dto.HistorySessionInfoDto;
import com.egoo.idp.mapper.HistorySessionMapper;
import com.egoo.idp.service.HistorySessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class HistorySessionServiceImpl implements HistorySessionService {

    @Autowired
    private HistorySessionMapper historySessionMapper;

    @Override
    public HistorySessionInfoDto getAgentIdByPhone(String phoneNum,String tenantId) {

        HistorySessionInfoDto historySessionInfoDto = null;
        try {
            QueryWrapper<HistorySessionInfoDto> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("agent_id").eq("record_id", phoneNum)
                    .eq("tenant_id",tenantId)
                    .apply("trunc(gmt_create) = trunc(sysdate)")
                    .orderByDesc("gmt_create")
                    .last("limit 1");
            historySessionInfoDto = historySessionMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("查询网点电话失败",e);
        }
        return historySessionInfoDto;
    }

    public static void main(String[] args) {
        Map<Object, Object> res = new HashMap<>();
        res.put("UserName", "12014404");
        res.put("UserId", "12014404");
        res.put("DisplayName", "王婧");
        String s = JSON.toJSONString(res);
        log.info("需要加密{}",s);
        String s1 = Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
        log.info("加密后:{}",s1);
        String s2 = new String(Base64.getDecoder().decode(s1));
        log.info("解密后{}",s2);
    }
}
