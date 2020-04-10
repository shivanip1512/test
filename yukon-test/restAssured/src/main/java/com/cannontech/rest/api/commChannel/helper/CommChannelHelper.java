package com.cannontech.rest.api.commChannel.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import com.cannontech.rest.api.commChannel.request.MockBaudRate;
import com.cannontech.rest.api.commChannel.request.MockPaoType;
import com.cannontech.rest.api.commChannel.request.MockPortBase;
import com.cannontech.rest.api.commChannel.request.MockPortTiming;
import com.cannontech.rest.api.commChannel.request.MockTcpPortDetail;

public class CommChannelHelper {
    public final static String CONTEXT_PORT_ID = "portId";

    public final static MockPortBase buildCommChannel(MockPaoType paoType) {
        MockPortBase tcpPort = null;
        switch (paoType) {
        case TCPPORT:
            MockPortTiming tcpTiming = MockPortTiming.builder()
                    .postTxWait(89)
                    .preTxWait(87)
                    .receiveDataWait(76)
                    .rtsToTxWait(823)
                    .extraTimeOut(98)
                    .build();

            tcpPort = MockTcpPortDetail.builder()
                    .type(paoType)
                    .name(getTcpPortName(paoType))
                    .disable(true)
                    .baudRate(MockBaudRate.BAUD_2400)
                    .timing(tcpTiming)
                    .build();
            break;

        case UDPPORT:
            // TODO for UDPPORT
            break;

        default:
            break;
        }

        return tcpPort;
    }

    public static String getTcpPortName(MockPaoType paoType) {
        return buildFriendlyName(paoType, "TCPPort", "Test TCP Port");
    }

    public static String buildFriendlyName(MockPaoType paoType, String removePrefix, String addSuffix) {
        return StringUtils.remove(WordUtils.capitalizeFully(StringUtils.removeStartIgnoreCase(paoType.name(), removePrefix), '_'),
                "_") + addSuffix;
    }
}