package com.cannontech.i18n;

import org.springframework.context.MessageSourceResolvable;

/**
 * Yukon extension of MessageSourceResolvable
 */
public interface YukonMessageSourceResovable extends MessageSourceResolvable {

    /**
     * Method to get the primary message code for this resolvable
     * @return Primary code
     */
    public String getCode();
}
