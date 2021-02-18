package com.eaton.builders.assets.commchannel;

import java.util.Random;

public class CommChannelTypes {
    
    public enum CommChannelType {
        TCPPORT("TCPPORT"),
        LOCAL_SHARED("LOCAL_SHARED"),
        UDPPORT("UDPPORT"),
        TSERVER_SHARED("TSERVER_SHARED");
        
        private final String commType;
        
        CommChannelType(String commType) {
            this.commType = commType;
        }
        
        public String getCommChannelType() {
            return this.commType;
        }        
    }

    public enum BaudRateType {
        BAUD_300("BAUD_300"),
        BAUD_1200("BAUD_1200"),
        BAUD_2400("BAUD_2400"),
        BAUD_9600("BAUD_9600"),
        BAUD_14400("BAUD_14400"),
        BAUD_38400("BAUD_38400"),
        BAUD_57600("BAUD_57600"),
        BAUD_115200("BAUD_115200");
        
        private final String baudRate;
        
        BaudRateType(String baudRate) {
            this.baudRate = baudRate;
        }
        
        public String getBaudRate() {
            return this.baudRate;
        }
        
        public static BaudRateType getRandomBaudRate() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
    
    public enum ProtocolWrapType {
        NONE("NONE"),
        IDLC("IDLC");
        
        private final String protocolWrap;
        
        ProtocolWrapType(String protocolWrap) {
            this.protocolWrap = protocolWrap;
        }
        
        public String getProtocolWrap() {
            return this.protocolWrap;
        }
        
        public static ProtocolWrapType getRandomProtocolWrap() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
    
    public enum SharedPortType {
        
        ILEX("ILEX"),
        ACS("ACS"),
        NONE("None");
        
        private final String sharedPort;
        
        SharedPortType(String sharedPort) {
            this.sharedPort = sharedPort;
        }
        
        public String getSharedPortType() {
            return this.sharedPort;
        }
        
        public static SharedPortType getRandomSharedPort() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
    
    public enum PhysicalPortType {
        COM1("com1"),
        COM2("com2"),
        COM3("com3"),
        COM4("com4"),
        COM5("com5"),
        COM6("com6"),
        COM7("com7"),
        COM8("com8"),
        COM50("com50"),
        COM99("com99"),
        OTHER("Other");
        
        private final String physicalPort;
        
        PhysicalPortType(String physicalPort) {
            this.physicalPort = physicalPort;
        }
        
        public String getPhysicalPort() {
            return this.physicalPort;
        }
        
        public static PhysicalPortType getRandomPhysicalPort() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
}
