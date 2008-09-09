package com.cannontech.core.service;

public interface PorterRequestCancelService {
    
    /**
     * Will send a cancel command to cancel all commands that have the givin groupMessageId.
     * Returnes the number of commands that were canceled.
     * @param groupMessageId Commands sent with this ID will be canceled.
     * @return The number of commands canceled.
     */
    public long cancelRequests(int groupMessageId);
}
