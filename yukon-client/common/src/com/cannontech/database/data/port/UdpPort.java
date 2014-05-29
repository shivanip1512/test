package com.cannontech.database.data.port;

import com.cannontech.common.pao.PaoType;

public class UdpPort extends TerminalServerSharedPortBase {
    
    public UdpPort() {
        super(PaoType.UDPPORT);
    }
}