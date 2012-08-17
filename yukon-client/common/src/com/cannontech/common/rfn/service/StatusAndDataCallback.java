package com.cannontech.common.rfn.service;

import org.springframework.context.MessageSourceResolvable;

/**
 * Callback to handle the request with two responses technique where the first
 * response indicates whether the second response potentially containing the
 * desired data should be expected.
 */
public interface StatusAndDataCallback<S, D> extends DataCallback<D> {
    
    /**
     * Handle the reponses that indicate the request was accepted and that a data
     * reponse should be expected.
     */
    public void receivedStatus(S status);
    
    /**
     * Handle the reponses that indicate the request was not accepted and that a data
     * should not be expected.
     */
    public void receivedStatusError(MessageSourceResolvable message);

}