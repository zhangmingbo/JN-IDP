package com.egoo.idp.service;

import com.egoo.idp.entity.common.DBResponse;

public interface KbpService {

    void sendKbpRequestRsp(String queryPrefix, String querySuffix, String knowledgeBase, DBResponse dbResponse,Integer num);
}
