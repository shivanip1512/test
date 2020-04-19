package com.cannontech.rest.api.commChannel;

import static org.junit.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cannontech.rest.api.commChannel.helper.CommChannelHelper;
import com.cannontech.rest.api.commChannel.request.MockBaudRate;
import com.cannontech.rest.api.commChannel.request.MockPaoType;
import com.cannontech.rest.api.commChannel.request.MockTcpPortDetail;
import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.response.ExtractableResponse;

public class TcpPortAPITest {

    MockTcpPortDetail tcpPort = null;

    @BeforeClass
    public void setUp() {

        tcpPort = (MockTcpPortDetail) CommChannelHelper.buildCommChannel(MockPaoType.TCPPORT);
    }

    @Test
    public void tcpPortCommChannel_01_Create(ITestContext context) {
        Log.startTestCase("tcpPortCommChannel_01_Create");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", tcpPort);
        String portId = createResponse.path(CommChannelHelper.CONTEXT_PORT_ID).toString();
        context.setAttribute(CommChannelHelper.CONTEXT_PORT_ID, portId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Port Id should not be Null", portId != null);
        Log.endTestCase("tcpPortCommChannel_01_Create");
    }

    @Test(dependsOnMethods = { "tcpPortCommChannel_01_Create" })
    public void tcpPortCommChannel_02_Get(ITestContext context) {
        Log.startTestCase("tcpPortCommChannel_02_Get");

        String portId = context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString();

        Log.info("Port Id of TcpPort CommChannel created is : " + portId);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getPort", portId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockTcpPortDetail tcpPortDetail = getResponse.as(MockTcpPortDetail.class);
        context.setAttribute("TCPPortName", tcpPortDetail.getName());

        assertTrue("Name Should be : " + tcpPort.getName(), tcpPort.getName().equals(tcpPortDetail.getName()));
        assertTrue("Type Should be : " + tcpPort.getType(), tcpPort.getType() == tcpPortDetail.getType());
        assertTrue("Baud Rate Should be : " + tcpPort.getBaudRate(),
                tcpPort.getBaudRate().equals(tcpPortDetail.getBaudRate()));
        assertTrue("Additional Time Out Should be : " + tcpPort.getTiming().getExtraTimeOut(), tcpPort.getTiming().getExtraTimeOut().equals(tcpPortDetail.getTiming().getExtraTimeOut()));

        Log.endTestCase("tcpPortCommChannel_02_Get");
    }

    @Test(dependsOnMethods = { "tcpPortCommChannel_01_Create" })
    public void tcpPortCommChannel_03_Update(ITestContext context) {
        Log.startTestCase("tcpPortCommChannel_03_Update");
        String name = "Test TCP Port Update";
        context.setAttribute("TCPPortName_Update", name);

        String portId = context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString();
        tcpPort.setName(name);
        tcpPort.setBaudRate(MockBaudRate.BAUD_115200);
        tcpPort.setEnable(true);

        ExtractableResponse<?> getResponse = ApiCallHelper.post("updatePort", tcpPort, portId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);
        Log.endTestCase("tcpPortCommChannel_03_Update");
    }

    @Test(dependsOnMethods = { "tcpPortCommChannel_02_Get" })
    public void tcpPortCommChannel_04_Delete(ITestContext context) {
        Log.startTestCase("tcpPortCommChannel_04_Delete");
        ExtractableResponse<?> response = ApiCallHelper.delete("deletePort",
                context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        Log.startTestCase("tcpPortCommChannel_04_Delete");
    }
}
