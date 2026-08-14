package com.egoo.idp.entity.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DBResponse {

    private int retCode;

    private int requestNum;

    private String msg;

    private Object body;

    public DBResponse(int retCode,String msg){
        this.retCode = retCode;
        this.msg = msg;
    }
}
