package com.cannontech.rest.api.documentation.commChannel;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;


import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import com.cannontech.rest.api.commChannel.helper.CommChannelHelper;
import com.cannontech.rest.api.commChannel.request.MockPortBase;
import com.cannontech.rest.api.documentation.DocumentationBase;
import com.cannontech.rest.api.documentation.DocumentationFields;
import com.cannontech.rest.api.documentation.DocumentationFields.*;

public abstract class CommChannelApiDocBase extends DocumentationBase {

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

    private MockPortBase getMockObject() {
        return CommChannelHelper.buildCommChannel(getMockPaoType());
    }
    
    /**
     * Return the request fieldDescriptors
     */
    protected abstract List<FieldDescriptor> getFieldDescriptors();
    
    protected abstract String getPortId();
    
    @Override
    protected Create buildCreateFields() {
        List<FieldDescriptor> requestFields  = getFieldDescriptors();
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        return new DocumentationFields.Create(requestFields, responseFields, idStr, idDescStr, getMockObject(), "createPort");
    }
    
    @Override
    protected Update buildUpdateFields() {
        List<FieldDescriptor> requestFields  = getFieldDescriptors();
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        return new DocumentationFields.Update(requestFields, responseFields, idStr, idDescStr, getMockObject(), "updatePort", getPortId());
    }
    
    @Override
    protected Get buildGetFields() {
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        return new DocumentationFields.Get(responseFields, "getPort", getPortId());
    }
    
    @Override
    protected Delete buildDeleteFields() {
        return new DocumentationFields.Delete("deletePort", getPortId());
    }
    
    @Override
    protected Copy buildCopyFields() {
        return null;
    }
}