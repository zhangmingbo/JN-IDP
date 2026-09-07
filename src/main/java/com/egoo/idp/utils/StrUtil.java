package com.egoo.idp.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Slf4j
@Component
public class StrUtil {

    private static int PROMPT_LENGTH;

    @Value("${gateway.transaction.promptLength}")
    public void setPromptLength(int promptLength) {
        PROMPT_LENGTH = promptLength;
    }

    public static String paseStrUTF8(String garbledText){
        String originalText = null;
        try {
            byte[] isoBytes = garbledText.getBytes("ISO-8859-1");
            originalText = new String(isoBytes, "UTF-8");
        } catch (Exception e) {
            log.error("转换格式异常",e);
        }
        return originalText;
    }

    public static void capturePrompt(String prompt, JSONObject result) {
        if (prompt == null) {
            result.put("prompt", "");
            return;
        }

        if (prompt.length() <= PROMPT_LENGTH) {
            // 小于等于380字符，整段放入prompt
            result.put("prompt", prompt);
        } else {
            // 超过380字符，平分为3段，每段不超过380字符，超过1140字符的部分放弃
            result.put("ReturnCode", "multiple");
            
            int totalLength = Math.min(prompt.length(), PROMPT_LENGTH * 3); // 最多1140字符
            int segmentLength = totalLength / 3; // 每段长度（平分）
            
            result.put("prompt", prompt.substring(0, segmentLength));
            result.put("prompt2", prompt.substring(segmentLength, segmentLength * 2));
            result.put("prompt3", prompt.substring(segmentLength * 2, totalLength));
        }
    }

    public static String reverSal(String validdate){
        try {
            validdate = validdate.substring(2) + validdate.substring(0,2);
            return validdate;
        } catch (Exception e) {
            log.error("转换validdate异常",e);
            return validdate;
        }
    }
}
