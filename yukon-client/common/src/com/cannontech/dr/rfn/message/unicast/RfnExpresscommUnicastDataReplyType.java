package com.cannontech.dr.rfn.message.unicast;

public enum RfnExpresscommUnicastDataReplyType {
    
    OK,
    FAILURE,
    NETWORK_TIMEOUT,
    TIMEOUT, // TODO: Is it OK for NM to use this?
}