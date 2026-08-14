package com.egoo.idp.entity.eurm;

public enum ResultCode {

    // 成功状态码
    SUCCESS(200, "操作成功"),
    // 客户端错误状态码
    BAD_REQUEST(400, "操作失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "数据未找到未找到"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}
