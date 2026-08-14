package com.egoo.idp.entity.common;

import com.alibaba.fastjson.JSON;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FastjsonMapGeneratorCorrected {

    public static String generateRequestJsonWithMap(String query,String knowledgeBase) {
        // Get current date and time
        LocalDateTime now = LocalDateTime.now();

        // Define date and time formatters
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");

        // Format date and time strings
        String msgDate = now.format(dateFormatter);
        String msgTime = now.format(timeFormatter);
        String requestDate = msgDate; // Same rule as msgDate
        String requestTime = msgTime; // Same rule as msgTime

        // Generate 6 random digits
        Random random = new Random();
        int randomNumber = random.nextInt(1000000); // Generates a number from 0 to 999999
        String randomDigits = String.format("%06d", randomNumber); // Format to ensure 6 digits with leading zeros

        // Construct dynamic fields based on rules
        String msgId = "377" + msgDate + msgTime + randomDigits;
        String consumerSeqNo = "377" + msgDate + msgTime + randomDigits; // Same rule as msgId

        // --- Build the JSON structure using Maps ---

        // messages object (inside bizBody)
        Map<String, Object> messages = new HashMap<>();
        messages.put("query", query); // Set the input query here
        messages.put("knowledge_base", knowledgeBase);
        messages.put("busitype", "00");

        // bizBody object (inside request)
        Map<String, Object> bizBody = new HashMap<>();
        bizBody.put("usertype", "00");
        bizBody.put("usernum", "88888");
        bizBody.put("channelnum", "377");
        bizBody.put("chattype", "01");
        bizBody.put("streamtype", "0");
        bizBody.put("knowledge_base", knowledgeBase);
        bizBody.put("tasknumber", msgId); // Use generated msgId
        bizBody.put("model", "qwen-72b");
        bizBody.put("messages", messages); // Put the messages map
        bizBody.put("max_tokens", "2048");
        bizBody.put("flag", "0");

        // SYS_HEAD object (inside bizHeader)
        Map<String, Object> sysHeadForBizHeader = new HashMap<>(); // Name it differently to avoid confusion
        sysHeadForBizHeader.put("TransServiceScene", "");
        sysHeadForBizHeader.put("RequestDate", requestDate); // Use generated requestDate
        sysHeadForBizHeader.put("ConsumerSeqNo", consumerSeqNo); // Use generated consumerSeqNo
        sysHeadForBizHeader.put("TdgBrah", "80888");
        sysHeadForBizHeader.put("ConsumerId", "377");
        sysHeadForBizHeader.put("ChannelCode", "377");
        sysHeadForBizHeader.put("RequestTime", requestTime); // Use generated requestTime

        // bizHeader object (inside request)
        Map<String, Object> bizHeader = new HashMap<>();
        bizHeader.put("SYS_HEAD", sysHeadForBizHeader); // Put the SYS_HEAD map

        // request object (inside Body)
        Map<String, Object> request = new HashMap<>();
        request.put("bizBody", bizBody); // Put the bizBody map
        request.put("bizHeader", bizHeader); // Put the bizHeader map

        // Body object (inside Transaction)
        Map<String, Object> body = new HashMap<>();
        body.put("request", request); // Put the request map

        // sysHeader object (inside Header)
        Map<String, Object> sysHeader = new HashMap<>();
        sysHeader.put("ver", "1.0");
        sysHeader.put("pinIndex", "");
        sysHeader.put("bizType", "");
        sysHeader.put("authContext", "");
        sysHeader.put("msgId", msgId); // Use generated msgId
        sysHeader.put("clientCd", "377");
        sysHeader.put("pinValue", "");
        sysHeader.put("authId", "");
        sysHeader.put("authPara", "");
        sysHeader.put("orgCode", "");
        sysHeader.put("serverCd", "757");
        sysHeader.put("msgTime", msgTime); // Use generated msgTime
        sysHeader.put("serviceCd", "P00001061653");
        sysHeader.put("operation", "llm.answer.0003.05");
        sysHeader.put("msgDate", msgDate); // Use generated msgDate

        // Header object (inside Transaction)
        Map<String, Object> header = new HashMap<>();
        header.put("sysHeader", sysHeader); // Put the sysHeader map

        // Transaction object (This map holds Header and Body)
        Map<String, Object> transactionContent = new HashMap<>();
        transactionContent.put("Header", header); // Put the Header map
        transactionContent.put("Body", body); // Put the Body map

        // Create the absolute root map that contains the "Transaction" key
        Map<String, Object> rootMap = new HashMap<>();
        rootMap.put("Transaction", transactionContent); // Put the transactionContent map under the "Transaction" key

        // Convert the rootMap to a JSON string using Fastjson
        // Use JSON.toJSONString with features for pretty printing if desired
        String jsonOutput = JSON.toJSONString(rootMap, true); // `true` enables pretty printing

        return jsonOutput;
    }
}
