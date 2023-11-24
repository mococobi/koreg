package com.mococo.biz.exception;

public class BizException extends RuntimeException {
    private static final long serialVersionUID = 8312099212145487238L;
    private static final String DEFAULT_ERR_CODE = "error.default";
    private final String code;

    public BizException() {
        super();
        this.code = DEFAULT_ERR_CODE;
    }

    public BizException(final Exception e) {
        super(e);
        this.code = DEFAULT_ERR_CODE;
    }

    public BizException(final String code) {
        super();
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}