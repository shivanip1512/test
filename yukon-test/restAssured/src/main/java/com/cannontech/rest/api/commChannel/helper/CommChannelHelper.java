package com.cannontech.rest.api.commChannel.helper;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import com.cannontech.rest.api.commChannel.request.MockBaudRate;
import com.cannontech.rest.api.commChannel.request.MockLocalSharedPortDetail;
import com.cannontech.rest.api.commChannel.request.MockPaoType;
import com.cannontech.rest.api.commChannel.request.MockPortBase;
import com.cannontech.rest.api.commChannel.request.MockPortSharing;
import com.cannontech.rest.api.commChannel.request.MockPortTiming;
import com.cannontech.rest.api.commChannel.request.MockProtocolWrap;
import com.cannontech.rest.api.commChannel.request.MockSharedPortType;
import com.cannontech.rest.api.commChannel.request.MockTcpPortDetail;
import com.cannontech.rest.api.commChannel.request.MockTcpSharedPortDetail;
import com.cannontech.rest.api.commChannel.request.MockUDPPortDetails;

public class CommChannelHelper {
    public final static String CONTEXT_PORT_ID = "id";

    public final static MockPortBase buildCommChannel(MockPaoType paoType) {
        MockPortBase tcpPort = null;
        switch (paoType) {
        case TCPPORT:
            tcpPort = MockTcpPortDetail.builder()
                    .type(paoType)
                    .name(getTcpPortName(paoType))
                    .enable(true)
                    .baudRate(MockBaudRate.BAUD_2400)
                    .timing(buildPortTiming())
                    .build();
            break;
        case TSERVER_SHARED:
            tcpPort = MockTcpSharedPortDetail.builder()
                    .type(paoType)
                    .name(getTcpPortName(paoType))
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
                    .name(getTcpPortName(paoType))
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
                    .name(getTcpPortName(paoType))
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

    public static FieldDescriptor[] portBaseFields() {
        return new FieldDescriptor[] {
                fieldWithPath("type").type(JsonFieldType.STRING).description("Channel Type"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("Comm Channel Name"),
                fieldWithPath("enable").type(JsonFieldType.BOOLEAN).description("Enable Channel"),
                fieldWithPath("baudRate").type(JsonFieldType.STRING)
                        .description("Baud Rate Possible values for Baud Rate are : BAUD_300," +
                                "    BAUD_1200" +
                                "    BAUD_2400," +
                                "    BAUD_4800," +
                                "    BAUD_9600," +
                                "    BAUD_14400," +
                                "    BAUD_28800," +
                                "    BAUD_38400," +
                                "    BAUD_57600," +
                                "    BAUD_115200"),
                fieldWithPath("timing.preTxWait").type(JsonFieldType.NUMBER).description("Pre Tx Wait").optional(),
                fieldWithPath("timing.rtsToTxWait").type(JsonFieldType.NUMBER).description("RTS To Tx Wait").optional(),
                fieldWithPath("timing.postTxWait").type(JsonFieldType.NUMBER).description("Post Tx Wait").optional(),
                fieldWithPath("timing.receiveDataWait").type(JsonFieldType.NUMBER).description("Receive Data Wait").optional(),
                fieldWithPath("timing.extraTimeOut").type(JsonFieldType.NUMBER).description("Additional Time Out").optional(), };
    }

    public static FieldDescriptor[] buildTerminalServerPortDescriptor() {
        return new FieldDescriptor[] {
                fieldWithPath("sharing.sharedPortType").type(JsonFieldType.STRING).description("Shared Port Type Possible values of Shared Port Type are : NONE,ACS,ILEX").optional(),
                fieldWithPath("sharing.sharedSocketNumber").type(JsonFieldType.NUMBER).description("Shared Socket Number").optional(),
                fieldWithPath("portNumber").type(JsonFieldType.NUMBER).description("Port Number").optional(),
                fieldWithPath("carrierDetectWaitInMilliseconds").type(JsonFieldType.NUMBER).description("Carrier Detect Wait In MiliSeconds").optional(),
                fieldWithPath("protocolWrap").type(JsonFieldType.STRING).description("Protocol Wrap Possible values of Protocol Wrap are None,IDLC").optional(),
                fieldWithPath("ipAddress").type(JsonFieldType.STRING).description("IP Address(In case Of UDP Port ipAddress value is UDP)").optional(),
        };
    }

    public static FieldDescriptor[] buildLocalPortDescriptor() {
        return new FieldDescriptor[] {
                fieldWithPath("sharing.sharedPortType").type(JsonFieldType.STRING).description("Shared Port Type Possible values of Shared Port Type are : NONE,ACS,ILEX").optional(),
                fieldWithPath("sharing.sharedSocketNumber").type(JsonFieldType.NUMBER).description("Shared Socket Number").optional(),
                fieldWithPath("carrierDetectWaitInMilliseconds").type(JsonFieldType.NUMBER).description("Carrier Detect Wait In MiliSeconds").optional(),
                fieldWithPath("protocolWrap").type(JsonFieldType.STRING).description("Protocol Wrap Possible values of Protocol Wrap are None,IDLC").optional(),
                fieldWithPath("physicalPort").type(JsonFieldType.STRING).description("Physical Port").optional(),
        };
    }
    
    public static FieldDescriptor[] buildGetAllPortsDescriptor() {
        return new FieldDescriptor[] {
                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("Port Id"),
                fieldWithPath("[].name").type(JsonFieldType.STRING).description("Comm Channel Name"),
                fieldWithPath("[].enable").type(JsonFieldType.BOOLEAN).description("Status"),
                fieldWithPath("[].type").type(JsonFieldType.STRING).description("Type"),
        };
    }
    

    public static List<FieldDescriptor> buildTCPTerminalServerPortDescriptor() {
        List<FieldDescriptor> tcpTerminalServerPortDescriptor = Arrays.asList(portBaseFields());
        List<FieldDescriptor> list = new ArrayList<>(tcpTerminalServerPortDescriptor);
        list.addAll(Arrays.asList(buildTerminalServerPortDescriptor()));
        return list;
    }

    public static List<FieldDescriptor> buildUDPTerminalServerPortDescriptor() {
        List<FieldDescriptor> udpTerminalServerPortDescriptor = Arrays.asList(portBaseFields());
        List<FieldDescriptor> list = new ArrayList<>(udpTerminalServerPortDescriptor);
        list.addAll(Arrays.asList(buildTerminalServerPortDescriptor()));
        list.add(15, fieldWithPath("keyInHex").type(JsonFieldType.STRING).description("Key In Hex"));
        return list;
    }

    public static List<FieldDescriptor> buildLocalSharedPortDescriptor() {
        List<FieldDescriptor> localSharedPortDescriptor = Arrays.asList(portBaseFields());
        List<FieldDescriptor> list = new ArrayList<>(localSharedPortDescriptor);
        list.addAll(Arrays.asList(buildLocalPortDescriptor()));
        return list;
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

    public static String getTcpPortName(MockPaoType paoType) {
        return buildFriendlyName(paoType, "TCPPort", "Test Port");
    }

    public static String buildFriendlyName(MockPaoType paoType, String removePrefix, String addSuffix) {
        return StringUtils.remove(WordUtils.capitalizeFully(StringUtils.removeStartIgnoreCase(paoType.name(), removePrefix), '_'),
                "_") + addSuffix;
    }
}