package com.egoo.idp.service;


import com.egoo.idp.entity.dto.HistorySessionInfoDto;

import java.util.List;

public interface HistorySessionService {



    HistorySessionInfoDto getAgentIdByPhone(String phoneNum, String tenantId);
}
