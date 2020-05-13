package com.cannontech.rest.api.commChannel.helper;

import com.cannontech.rest.api.commChannel.request.MockBaudRate;
import com.cannontech.rest.api.commChannel.request.MockLocalSharedPortDetail;
import com.cannontech.rest.api.commChannel.request.MockPortBase;
import com.cannontech.rest.api.commChannel.request.MockPortSharing;
import com.cannontech.rest.api.commChannel.request.MockPortTiming;
import com.cannontech.rest.api.commChannel.request.MockProtocolWrap;
import com.cannontech.rest.api.commChannel.request.MockSharedPortType;
import com.cannontech.rest.api.commChannel.request.MockTcpPortDetail;
import com.cannontech.rest.api.commChannel.request.MockTcpSharedPortDetail;
import com.cannontech.rest.api.commChannel.request.MockUDPPortDetails;
import com.cannontech.rest.api.common.ApiUtils;
import com.cannontech.rest.api.common.model.MockPaoType;

public class CommChannelHelper {
    public final static String CONTEXT_PORT_ID = "id";

    public final static MockPortBase buildCommChannel(MockPaoType paoType) {
        MockPortBase tcpPort = null;
        String name = ApiUtils.buildFriendlyName(paoType, "TCPPort", "Test Port");
        switch (paoType) {
        case TCPPORT:
            tcpPort = MockTcpPortDetail.builder()
                    .type(paoType)
                    .name(name)
                    .enable(true)
                    .baudRate(MockBaudRate.BAUD_2400)
                    .timing(buildPortTiming())
                    .build();
            break;
        case TSERVER_SHARED:
            tcpPort = MockTcpSharedPortDetail.builder()
                    .type(paoType)
                    .name(name)
                    .enable(true)
                    .baudRate(MockBaudRate.BAUD_2400)
                    .timing(buildPortTiming())
                    .sharing(buildPortSharing())
                    .portNumber(1234)
                    .carrierDetectWaitInMilliseconds(123)
                    .protocolWrap(MockProtocolWrap.IDLC)
                    .ipAddress("127.0.0.1")
                    .build();
            break;
        case UDPPORT:
            tcpPort = MockUDPPortDetails.builder()
                    .type(paoType)
                    .name(name)
                    .enable(true)
                    .baudRate(MockBaudRate.BAUD_2400)
                    .timing(buildPortTiming())
                    .sharing(buildPortSharing())
                    .portNumber(5534)
                    .carrierDetectWaitInMilliseconds(544)
                    .protocolWrap(MockProtocolWrap.IDLC)
                    .keyInHex("00112233445566778899aabbccddeeff")
                    .build();
            break;
        case LOCAL_SHARED:
            tcpPort = MockLocalSharedPortDetail.builder()
                    .type(paoType)
                    .name(name)
                    .enable(true)
                    .baudRate(MockBaudRate.BAUD_2400)
                    .timing(buildPortTiming())
                    .sharing(buildPortSharing())
                    .carrierDetectWaitInMilliseconds(544)
                    .protocolWrap(MockProtocolWrap.IDLC)
                    .physicalPort("com2")
                    .build();
            break;
        default:
            break;
        }

        return tcpPort;
    }

    private static MockPortTiming buildPortTiming() {
        return MockPortTiming.builder()
                .postTxWait(89)
                .preTxWait(87)
                .receiveDataWait(76)
                .rtsToTxWait(823)
                .extraTimeOut(98)
                .build();
    }

    private static MockPortSharing buildPortSharing() {
        return MockPortSharing.builder()
                .sharedPortType(MockSharedPortType.ACS)
                .sharedSocketNumber(100).build();
    }
}