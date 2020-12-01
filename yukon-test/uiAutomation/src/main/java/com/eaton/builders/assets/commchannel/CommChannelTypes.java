package com.eaton.builders.assets.commchannel;

import java.util.Random;

public class CommChannelTypes {

    public enum BaudRate {
        
        BAUD_300("BAUD_300"),
        BAUD_1200("BAUD_1200"),
        BAUD_2400("BAUD_2400"),
        BAUD_9600("BAUD_9600"),
        BAUD_14400("BAUD_14400"),
        BAUD_38400("BAUD_38400"),
        BAUD_57600("BAUD_57600"),
        BAUD_115200("BAUD_115200");
        
        private final String baudRate;
        
        BaudRate(String baudRate) {
            this.baudRate = baudRate;
        }
        
        public String getBaudRate() {
            return this.baudRate;
        }
        
        public static BaudRate getRandomBaudRate() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
    
    public enum ProtocolWrap {

        PROTOCOL_WRAP("Protocol Wrap"),
        NONE("None"),
        IDLC("IDLC");
        
        private final String protocolWrap;
        
        ProtocolWrap(String protocolWrap) {
            this.protocolWrap = protocolWrap;
        }
        
        public String getProtocolWrap() {
            return this.protocolWrap;
        }
        
        public static ProtocolWrap getRandomBaudRate() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
    
    public enum SharedPortType {
        
        ILEX("ILEX"),
        ACS("ACS"),
        NONE("NONE");
        
        private final String sharedPortType;
        
        SharedPortType(String sharedPortType) {
            this.sharedPortType = sharedPortType;
        }
        
        public String getSharedPortType() {
            return this.sharedPortType;
        }
        
        public static SharedPortType getRandomBaudRate() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
}
