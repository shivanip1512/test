package com.cannontech.rest.api.documentation.commChannel;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.model.MockPaoType;

public class UdpTerminalServerApiDoc extends CommChannelApiDocBase {

    private String portId = null;

    private static List<FieldDescriptor> buildUDPTerminalServerPortDescriptor() {
        List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(portBaseFields()));
        list.addAll(Arrays.asList(terminalServerFields()));
        list.add(15, fieldWithPath("keyInHex").type(JsonFieldType.STRING).description("Key In Hex"));
        return list;
    }

    
    @Test
    public void Test_UDPTerminalServer_01_Create() {
        portId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_UDPTerminalServer_01_Create" })
    public void Test_UDPTerminalServer_02_Update() {
        portId = updateDoc();
    }

    @Test(dependsOnMethods = { "Test_UDPTerminalServer_01_Create" })
    public void Test_UDPTerminalServer_03_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_UDPTerminalServer_01_Create" })
    public void Test_UDPTerminalServer_04_Delete() {
        deleteDoc();
    }

    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return buildUDPTerminalServerPortDescriptor();
    }

    @Override
    protected MockPaoType getMockPaoType() {
        return MockPaoType.UDPPORT;
    }
    
    @Override
    protected String getPortId() {
        return portId;
    }
}