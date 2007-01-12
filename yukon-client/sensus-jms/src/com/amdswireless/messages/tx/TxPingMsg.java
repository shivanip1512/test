package com.amdswireless.messages.tx;



public class TxPingMsg extends TxMsg {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private short pingType;

    public TxPingMsg() {
        super();
        try {
            super.setCommand((short)0x54);
            super.setLength((short)0x24);
            super.setAppCode((short)0x7);
            super.setMiscTx((short)0x1);
            super.setCommandType((short)0x8);
            super.setNcId((short)0x01);
            super.setToi(new java.util.Date().getTime()/1000);
        } catch ( Exception ex ) {
            System.err.println("ERROR --- "+ex);
        }
        pingType=0x0;
    }

    public void setPingType(short cmdType) throws PingTypeInvalid {
        switch(cmdType) {
            case 0x0: pingType=0x0; break;  // Normal Ping
            case 0x1: pingType=0x1; break;  // Configuration Ping
            case 0x2: pingType=0x2; break;  // Demand Read Ping
            case 0x3: pingType=0x3; break;  // Handheld mPass Ping (Buddy Allowed)
            case 0x4: pingType=0x4; break;  // Handheld mPass Ping (Buddy Disabled)
            case 0x5: pingType=0x5; break;  // Alarm Ping
            case 0x6: pingType=0x6; break;  // handheld mPass Alarm Ping (Buddy Disabled)
            case 0x7: pingType=0x7; break;  // handheld mPass Demand Read Ping (Buddy Disabled)
            case 0x8: pingType=0x8; break;  // Meter Read Message
            case 0x9: pingType=0x9; break;  // Full Tier Data Meter Read
            case 0xA: pingType=0xA; break;  // Full Tier Data Meter Read
            case 0xB: pingType=0xB; break;  // Single Tier Data Meter Read
            default: throw new PingTypeInvalid();
        }
    }

    public short getPingType() {
        return pingType;
    }

    public String getMsgText() {
        super.updateMsg(pingType, 29);
        return super.getMsgText();
    }
}
