package com.egoo.idp.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME, startTime);

        StringBuilder requestLog = new StringBuilder();
        requestLog.append("Request URL: ").append(request.getRequestURL()).append("\n");
        requestLog.append("Request Method: ").append(request.getMethod()).append("\n");
        requestLog.append("Client IP: ").append(request.getRemoteAddr()).append("\n");

        // 打印请求参数
        Map<String, Object> paramMap = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            paramMap.put(paramName, request.getParameter(paramName));
        }
        requestLog.append("Request Parameters: ").append(JSON.toJSONString(paramMap)).append("\n");

        log.info(requestLog.toString());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        // 可以在这里处理视图渲染前的逻辑
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        long startTime = (Long) request.getAttribute(START_TIME);
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;

        StringBuilder responseLog = new StringBuilder();
        responseLog.append("Response Status: ").append(response.getStatus()).append("\n");
        responseLog.append("Request Processing Time: ").append(executeTime).append("ms").append("\n");

        if (ex != null) {
            responseLog.append("Exception: ").append(ex.getMessage()).append("\n");
            log.error(responseLog.toString(), ex);
        } else {
            log.info(responseLog.toString());
        }
    }
}
