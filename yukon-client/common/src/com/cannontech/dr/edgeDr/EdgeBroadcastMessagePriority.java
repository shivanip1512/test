package com.cannontech.dr.edgeDr;

public enum EdgeBroadcastMessagePriority {
    IMMEDIATE(com.cannontech.messaging.serialization.thrift.generated.EdgeBroadcastMessagePriority.IMMEDIATE),
    NON_REAL_TIME(com.cannontech.messaging.serialization.thrift.generated.EdgeBroadcastMessagePriority.NON_REAL_TIME),
    HIGH(com.cannontech.messaging.serialization.thrift.generated.EdgeBroadcastMessagePriority.IMMEDIATE),
    LOW(com.cannontech.messaging.serialization.thrift.generated.EdgeBroadcastMessagePriority.NON_REAL_TIME),
    ;
    
    private com.cannontech.messaging.serialization.thrift.generated.EdgeBroadcastMessagePriority thriftPriority;
    
    private EdgeBroadcastMessagePriority(com.cannontech.messaging.serialization.thrift.generated.EdgeBroadcastMessagePriority priority) {
        thriftPriority = priority;
    }
    
    public com.cannontech.messaging.serialization.thrift.generated.EdgeBroadcastMessagePriority getThriftEquivalent() {
        return thriftPriority;
    }
}
