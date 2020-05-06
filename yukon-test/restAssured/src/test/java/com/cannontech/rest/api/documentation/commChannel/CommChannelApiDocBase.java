package com.cannontech.rest.api.documentation.commChannel;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.Test;

import com.cannontech.rest.api.commChannel.request.MockPortBase;
import com.cannontech.rest.api.documentation.DocumentationBase;

public class CommChannelApiDocBase extends DocumentationBase {

    public final static String idStr = "id";
    public final static String idDescStr = "Port Id";

    protected static FieldDescriptor[] portBaseFields() {
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
    
    protected static FieldDescriptor[] terminalServerFields() {
        return new FieldDescriptor[] {
                fieldWithPath("sharing.sharedPortType").type(JsonFieldType.STRING)
                        .description("Shared Port Type Possible values of Shared Port Type are : NONE,ACS,ILEX").optional(),
                fieldWithPath("sharing.sharedSocketNumber").type(JsonFieldType.NUMBER).description("Shared Socket Number")
                        .optional(),
                fieldWithPath("portNumber").type(JsonFieldType.NUMBER).description("Port Number").optional(),
                fieldWithPath("carrierDetectWaitInMilliseconds").type(JsonFieldType.NUMBER)
                        .description("Carrier Detect Wait In MiliSeconds").optional(),
                fieldWithPath("protocolWrap").type(JsonFieldType.STRING)
                        .description("Protocol Wrap Possible values of Protocol Wrap are None,IDLC").optional(),
                fieldWithPath("ipAddress").type(JsonFieldType.STRING).description("IP Address(In case Of UDP Port ipAddress value is 'UDP')").optional(),
        };
    }

    protected static FieldDescriptor[] allPortsFields() {
        return new FieldDescriptor[] {
                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description(idDescStr),
                fieldWithPath("[].name").type(JsonFieldType.STRING).description("Comm Channel Name"),
                fieldWithPath("[].enable").type(JsonFieldType.BOOLEAN).description("Status"),
                fieldWithPath("[].type").type(JsonFieldType.STRING).description("Type"),
        };
    }
    
    protected static List<FieldDescriptor> buildAllPortsDescriptor() {
        List<FieldDescriptor> portBaseDescriptor = Arrays.asList(allPortsFields());
        return portBaseDescriptor;
    }
    
    protected String create(List<FieldDescriptor> descriptor, MockPortBase portBase) {
        List<FieldDescriptor> requestFields  = descriptor;
        List<FieldDescriptor> responseFields = new ArrayList<>(descriptor);
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        
        return createDoc(requestFields, responseFields, idStr, idDescStr, portBase, "createPort");
    }

    protected String update(List<FieldDescriptor> descriptor, MockPortBase portBase, String portId) {
        List<FieldDescriptor> requestFields =  descriptor;
        List<FieldDescriptor> responseFields = new ArrayList<>(descriptor);
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        return updateDoc(requestFields, responseFields, idStr,  idDescStr, portBase, "updatePort", portId);
    }

    protected void getOne(List<FieldDescriptor> descriptor, String portId) {
        List<FieldDescriptor> responseFields = new ArrayList<>(descriptor);
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        getOneDoc(responseFields, "getPort", portId);
    }

    public void getAll(List<FieldDescriptor> descriptor) {
        List<FieldDescriptor> responseFields = new ArrayList<>(descriptor);
        getAllDoc(responseFields, "getAllCommChannels");
    }

    
    protected void delete (String portId) {
        deleteDoc("deletePort", portId);
    }
}