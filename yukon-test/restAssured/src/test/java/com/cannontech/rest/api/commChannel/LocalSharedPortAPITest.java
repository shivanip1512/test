package com.cannontech.rest.api.commChannel;

import static org.junit.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cannontech.rest.api.commChannel.helper.CommChannelHelper;
import com.cannontech.rest.api.commChannel.request.MockBaudRate;
import com.cannontech.rest.api.commChannel.request.MockLocalSharedPortDetail;
import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.response.ExtractableResponse;

public class LocalSharedPortAPITest {
    MockLocalSharedPortDetail localSharedPortDetail = null;

    @BeforeClass
    public void setUp() {
        localSharedPortDetail = (MockLocalSharedPortDetail) CommChannelHelper.buildCommChannel(MockPaoType.LOCAL_SHARED);
    }

    @Test
    public void localSharedPortCommChannel_01_Create(ITestContext context) {
        Log.startTestCase("localSharedPortCommChannel_01_Create");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", localSharedPortDetail);
        String portId = createResponse.path(CommChannelHelper.CONTEXT_PORT_ID).toString();
        context.setAttribute(CommChannelHelper.CONTEXT_PORT_ID, portId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Port Id should not be Null", portId != null);
        Log.endTestCase("localSharedPortCommChannel_01_Create");
    }

    @Test(dependsOnMethods = { "localSharedPortCommChannel_01_Create" })
    public void localSharedPortCommChannel_02_Get(ITestContext context) {
        Log.startTestCase("localSharedPortCommChannel_02_Get");

        String portId = context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString();

        Log.info("Port Id of LocalSharedPort CommChannel created is : " + portId);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getPort", portId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockLocalSharedPortDetail localSharedPortDetail = getResponse.as(MockLocalSharedPortDetail.class);
        context.setAttribute("LocalSharedPortName", localSharedPortDetail.getName());

        assertTrue("Name Should be : " + localSharedPortDetail.getName(),
                localSharedPortDetail.getName().equals(localSharedPortDetail.getName()));
        assertTrue("Type Should be : " + localSharedPortDetail.getType(),
                localSharedPortDetail.getType() == localSharedPortDetail.getType());
        assertTrue("Baud Rate Should be : " + localSharedPortDetail.getBaudRate(),
                localSharedPortDetail.getBaudRate().equals(localSharedPortDetail.getBaudRate()));
        assertTrue("Additional Time Out Should be : " + localSharedPortDetail.getTiming().getExtraTimeOut(),
                localSharedPortDetail.getTiming().getExtraTimeOut().equals(localSharedPortDetail.getTiming().getExtraTimeOut()));
        Log.endTestCase("localSharedPortCommChannel_02_Get");
    }

    @Test(dependsOnMethods = { "localSharedPortCommChannel_02_Get" })
    public void localSharedPortCommChannel_03_Update(ITestContext context) {
        Log.startTestCase("localSharedPortCommChannel_03_Update");
        String name = "Test Local Shared Port Update";
        context.setAttribute("LocalSharedPortName_Update", name);

        String portId = context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString();
        localSharedPortDetail.setName(name);
        localSharedPortDetail.setBaudRate(MockBaudRate.BAUD_115200);
        localSharedPortDetail.setEnable(true);

        ExtractableResponse<?> getResponse = ApiCallHelper.post("updatePort", localSharedPortDetail, portId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);
        Log.endTestCase("localSharedPortCommChannel_03_Update");
    }

    @Test(dependsOnMethods = { "localSharedPortCommChannel_03_Update" })
    public void localSharedPortCommChannel_04_Delete(ITestContext context) {
        Log.startTestCase("localSharedPortCommChannel_04_Delete");
        ExtractableResponse<?> response = ApiCallHelper.delete("deletePort",
                context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        Log.startTestCase("localSharedPortCommChannel_04_Delete");
    }
}
