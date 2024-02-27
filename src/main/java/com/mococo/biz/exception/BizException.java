package com.mococo.biz.exception;

/**
 * BizException
 * @author mococo
 *
 */
public class BizException extends RuntimeException {
	/**
	 * serialVersionUID
	 */
    private static final long serialVersionUID = 8312099212145487238L;
    
    /**
     * DEFAULT_ERR_CODE
     */
    private static final String DEFAULT_ERR_CODE = "error.default";
    
    /**
     * code
     */
    private final String code;

    /**
     * BizException
     */
    public BizException() {
        super();
        this.code = DEFAULT_ERR_CODE;
    }
    
    
    /**
     * BizException
     * @param e
     */
    public BizException(final Exception excep) {
        super(excep);
        this.code = DEFAULT_ERR_CODE;
    }
    
    
    /**
     * BizException
     * @param code
     */
    public BizException(final String code) {
        super();
        this.code = code;
    }
    
    /**
     * getCode
     * @return
     */
    public String getCode() {
        return code;
    }
}