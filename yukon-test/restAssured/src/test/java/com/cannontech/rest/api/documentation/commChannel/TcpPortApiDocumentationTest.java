package com.cannontech.rest.api.documentation.commChannel;

import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.testng.annotations.Test;

import com.cannontech.rest.api.commChannel.helper.CommChannelHelper;
import com.cannontech.rest.api.commChannel.request.MockPaoType;
import com.cannontech.rest.api.commChannel.request.MockPortBase;

public class TcpPortApiDocumentationTest extends CommChannelApiDocBase {

    private String portId = null;
    private MockPortBase tcpPort = CommChannelHelper.buildCommChannel(MockPaoType.TCPPORT);

    private static List<FieldDescriptor> buildPortBaseDescriptor() {
        List<FieldDescriptor> portBaseDescriptor = Arrays.asList(portBaseFields());
        return portBaseDescriptor;
    }

    @Test
    public void Test_TcpPort_01_Create() {
        portId = create(buildPortBaseDescriptor(), tcpPort);
    }

    @Test(dependsOnMethods = { "Test_TcpPort_01_Create" })
    public void Test_TcpPort_02_Update() {
        portId = update(buildPortBaseDescriptor(), tcpPort, portId);
    }

    @Test(dependsOnMethods = { "Test_TcpPort_01_Create" })
    public void Test_TcpPort_03_Get() {
        getOne(buildPortBaseDescriptor(), portId);
    }

    @Test(dependsOnMethods = { "Test_TcpPort_01_Create" })
    public void Test_TcpPort_04_Delete() {
        delete(portId);
    }

    @Test
    public void Test_AllPorts_05_Get() {
        getAll(buildAllPortsDescriptor());
    }
}