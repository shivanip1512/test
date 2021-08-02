package com.cannontech.common.device.model;

import com.cannontech.common.device.port.LocalSharedPortDetail;
import com.cannontech.common.device.port.TcpPortDetail;
import com.cannontech.common.device.port.TcpSharedPortDetail;
import com.cannontech.common.device.port.UdpPortDetail;
import com.cannontech.common.device.virtualDevice.VirtualDeviceModel;
import com.cannontech.common.device.virtualDevice.VirtualMeterModel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;

public class PaoModelFactory {

    public static YukonPao getModel(PaoType paoType) {
        if (paoType == null) {
            return null;
        }
        YukonPao yukonPaoModel = null;
        switch (paoType) {
        case TCPPORT:
            yukonPaoModel = new TcpPortDetail();
            break;
        case UDPPORT:
            yukonPaoModel = new UdpPortDetail();
            break;
        case TSERVER_SHARED:
            yukonPaoModel = new TcpSharedPortDetail();
            break;
        case LOCAL_SHARED:
            yukonPaoModel = new LocalSharedPortDetail();
            break;
        case VIRTUAL_SYSTEM:
            yukonPaoModel = new VirtualDeviceModel();
            break;
        case VIRTUAL_METER:
            yukonPaoModel = new VirtualMeterModel();
            break;
        default:
            break;
        }
        return yukonPaoModel;
    }
}
