package com.cannontech.i18n;

import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * Yukon extension of DefaultMessageSourceResolvable with convienience
 * constructor
 */
public class YukonMessageSourceResolvableImpl extends
        DefaultMessageSourceResolvable implements YukonMessageSourceResovable {

    private String code;

    public YukonMessageSourceResolvableImpl(String code, String... args) {
        super(new String[] { code }, args);
        this.code = code;
    }

    public YukonMessageSourceResolvableImpl(String code, Object[] args, String defaultMessage) {
        super(new String[]{ code }, args, defaultMessage);
        this.code = code;
    }
    
    @Override
    public String getCode() {
        return this.code;
    }

}
