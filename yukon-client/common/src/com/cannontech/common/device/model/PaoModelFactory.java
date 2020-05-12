package com.cannontech.common.device.model;

import com.cannontech.common.device.port.LocalSharedPortDetail;
import com.cannontech.common.device.port.TcpPortDetail;
import com.cannontech.common.device.port.TcpSharedPortDetail;
import com.cannontech.common.device.port.UdpPortDetail;
import com.cannontech.common.pao.PaoType;

public class PaoModelFactory {

    public static Object getModel(PaoType paoType) {
        Object portBase = null;
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
