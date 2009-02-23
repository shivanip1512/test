package com.cannontech.core.service;

public interface PorterRequestCancelService {
    
    /**
     * Will send a cancel command to cancel all commands that have the givin groupMessageId.
     * Returns the number of commands that were canceled.
     * @param groupMessageId Commands sent with this ID will be canceled.
     * @param priority Priority of the cancel command. Should be less than or equal to the priority of the command/group that
     * is being canceled.
     * @return The number of commands canceled.
     */
    public long cancelRequests(int groupMessageId, int priority);
}
