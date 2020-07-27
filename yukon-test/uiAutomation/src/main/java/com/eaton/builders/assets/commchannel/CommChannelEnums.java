package com.eaton.builders.assets.commchannel;

public class CommChannelEnums {

    enum Type {
        LOCAL_SHARED, TCCPORT, TSERVER_SHARED, UDPPORT;
    }
    
    enum BaudRate {
        BAUD_300, BAUD_1200, BAUD_2400, BAUD_4800, BAUD_9600, BAUD_14400, BAUD_38400, BAUD_57600, BAUD_115200;
    }
}
