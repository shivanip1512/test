package com.cannontech.thirdparty.exception;

import org.springframework.context.MessageSourceResolvable;

public class ZigbeeCommissionException extends RuntimeException {
    
    private MessageSourceResolvable messageSourceResolvable;
    
    public ZigbeeCommissionException(MessageSourceResolvable descriptionMsr) {
        this.messageSourceResolvable = descriptionMsr;
    }
    
    public MessageSourceResolvable getDescription() {
        return messageSourceResolvable;
    }
}
