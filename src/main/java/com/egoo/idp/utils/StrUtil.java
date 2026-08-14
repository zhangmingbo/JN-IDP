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
            result.put("prompt", prompt);
        } else {
            result.put("ReturnCode", "multiple");
            result.put("prompt", prompt.substring(0, PROMPT_LENGTH));

            int remainingLength = prompt.length() - PROMPT_LENGTH;
            int endIndex = PROMPT_LENGTH + Math.min(remainingLength, PROMPT_LENGTH);
            result.put("prompt2", prompt.substring(PROMPT_LENGTH, endIndex));
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
