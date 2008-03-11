package com.cannontech.i18n;

import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * Yukon extension of DefaultMessageSourceResolvable with convienience
 * constructor
 */
public class YukonMessageSourceResolvable extends
        DefaultMessageSourceResolvable {

    public YukonMessageSourceResolvable(String code, String... args) {
        super(new String[] { code }, args);
    }

}
