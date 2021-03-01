package com.cannontech.rest.api.documentation.commChannel;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPaoType;

public class TcpPortApiDoc extends CommChannelApiDocBase {

    private String portId = null;

    private static List<FieldDescriptor> buildPortBaseDescriptor() {
        List<FieldDescriptor> portBaseDescriptor = new ArrayList<>(Arrays.asList(portBaseFields()));
        return portBaseDescriptor;
    }

    @Test
    public void Test_TcpPort_01_Create() {
        portId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_TcpPort_01_Create" })
    public void Test_TcpPort_02_Update() {
        portId = updatePartialDoc();
    }

    @Test(dependsOnMethods = { "Test_TcpPort_01_Create" })
    public void Test_TcpPort_03_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_TcpPort_01_Create" })
    public void Test_TcpPort_04_Delete() {
        deleteDoc();
    }

    @Test
    public void Test_AllPorts_05_Get() {
        List<FieldDescriptor> allPortsDescriptor = Arrays.asList(
            fieldWithPath("[].id").type(JsonFieldType.NUMBER).description(idDescStr),
            fieldWithPath("[].name").type(JsonFieldType.STRING).description("Comm Channel Name"),
            fieldWithPath("[].enable").type(JsonFieldType.BOOLEAN).description("Status"),
            fieldWithPath("[].type").type(JsonFieldType.STRING).description("Type"));
        String url = ApiCallHelper.getProperty("getAllCommChannels");
        getAllDoc(allPortsDescriptor, url);
    }

    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return buildPortBaseDescriptor();
    }

    @Override
    protected MockPaoType getMockPaoType() {
        return MockPaoType.TCPPORT;
    }
    
    @Override
    protected String getPortId() {
        return portId;
    }
}