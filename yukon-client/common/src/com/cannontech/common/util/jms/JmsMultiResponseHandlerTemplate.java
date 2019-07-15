package com.cannontech.common.util.jms;

import com.cannontech.common.rfn.message.JmsMultiResponse;

/**
 * Partial implementation of JmsMultiResponseHandler.
 */
public abstract class JmsMultiResponseHandlerTemplate<T extends JmsMultiResponse> implements JmsMultiResponseHandler<T> {
    
    private final Class<T> expectedType;
    
    protected JmsMultiResponseHandlerTemplate(Class<T> expectedType) {
        this.expectedType = expectedType;
    }
    
    @Override
    public Class<T> getExpectedType() {
        return expectedType;
    }
}
