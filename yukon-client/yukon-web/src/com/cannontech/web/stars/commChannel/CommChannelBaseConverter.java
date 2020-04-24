package com.cannontech.web.stars.commChannel;

import org.apache.logging.log4j.Logger;
import org.springframework.core.convert.converter.Converter;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.TcpPortDetail;
import com.cannontech.common.device.port.TcpSharedPortDetail;
import com.cannontech.common.device.port.UdpPortDetail;
import com.cannontech.common.pao.PaoType;

/**
 * Converter class for port base.
 * This will take port type as string and return the appropriate object.
 * Converter is required when a inherited class have to be passed for a base class input.
 */
public class CommChannelBaseConverter implements Converter<String, PortBase> {
    private static final Logger log = YukonLogManager.getLogger(CommChannelBaseConverter.class);

    @Override
    public PortBase convert(String groupType) {
        PortBase commChannel = null;
        PaoType paoType = null;
        try {
            paoType = PaoType.valueOf(groupType);
        } catch (IllegalArgumentException e) {
            log.error(groupType + " pao type doesn't match with existing pao types", e);
        }
        switch (paoType) {
        case TCPPORT:
            commChannel = new TcpPortDetail();
        case TSERVER_SHARED:
            commChannel = new TcpSharedPortDetail();
            break;
        case UDPPORT:
            commChannel = new UdpPortDetail();
            break;
        }
        return commChannel;
        }
}
