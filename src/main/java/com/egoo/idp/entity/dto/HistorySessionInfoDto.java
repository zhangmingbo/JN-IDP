package com.egoo.idp.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("record_info")
public class     HistorySessionInfoDto implements Serializable {


    @TableField(value = "agent_id")
    private String agentId;

}
