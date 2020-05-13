package com.cannontech.rest.api.documentation.commChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.model.MockPaoType;

public class TcpTerminalServerApiDocumentationTest extends CommChannelApiDocBase {

    private String portId = null;

    private static List<FieldDescriptor> buildTCPTerminalServerPortDescriptor() {
        List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(portBaseFields()));
        list.addAll(Arrays.asList(terminalServerFields()));
        return list;
    }

    @Test
    public void Test_TcpTerminalServer_01_Create() {
        portId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_TcpTerminalServer_01_Create" })
    public void Test_TcpTerminalServer_02_Update() {
        portId = updateDoc();
    }

    @Test(dependsOnMethods = { "Test_TcpTerminalServer_01_Create" })
    public void Test_TcpTerminalServer_03_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_TcpTerminalServer_01_Create" })
    public void Test_TcpTerminalServer_04_Delete() {
        deleteDoc();
    }

    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return buildTCPTerminalServerPortDescriptor();
    }

    @Override
    protected MockPaoType getMockPaoType() {
        return MockPaoType.TSERVER_SHARED;
    }

    @Override
    protected String getPortId() {
        return portId;
    }
}