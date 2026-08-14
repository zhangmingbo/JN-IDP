package com.egoo.idp.service;

import com.alibaba.fastjson.JSONObject;

public interface RedirectService {

     JSONObject getRedirectInfo(String ANI);

     JSONObject elder(String transServiceCode,String u_ani, String u_connid);
}
