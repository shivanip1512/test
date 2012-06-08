package com.cannontech.common.rfn.message;

/**
 * Use this interface on message classes.
 * For model object classes use {@link YukonRfn}
 */
public interface RfnIdentifingMessage {
    
    public RfnIdentifier getRfnIdentifier();
    
}