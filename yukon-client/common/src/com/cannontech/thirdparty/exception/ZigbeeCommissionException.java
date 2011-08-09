package com.cannontech.thirdparty.exception;

import org.springframework.context.MessageSourceResolvable;

public class ZigbeeCommissionException extends RuntimeException {
    
    private MessageSourceResolvable messageSourceResolvable;
    
    public ZigbeeCommissionException(MessageSourceResolvable messageSourceResolvable) {
        this.messageSourceResolvable = messageSourceResolvable;
    }
    
    public MessageSourceResolvable getMessageSourceResolvable() {
        return messageSourceResolvable;
    }
}
