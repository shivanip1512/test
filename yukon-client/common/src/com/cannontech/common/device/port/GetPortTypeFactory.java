package com.cannontech.common.device.port;

import com.cannontech.common.pao.PaoType;

public class GetPortTypeFactory {
    public PortBase getModel(PaoType paoType) {
        PortBase portBase = null;
        switch (paoType) {
        case TCPPORT :
            portBase = new TcpPortDetail();
            break;
        case UDPPORT : 
            portBase = new UdpPortDetail();
            break;
        case TSERVER_SHARED : 
            portBase = new TcpSharedPortDetail();
            break;
        case LOCAL_SHARED : 
            portBase = new LocalSharedPortDetail();
            break;
        }
        return portBase;
    }
}
