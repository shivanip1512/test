package com.cannontech.rest.api.commChannel;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cannontech.rest.api.commChannel.helper.CommChannelHelper;
import com.cannontech.rest.api.commChannel.request.MockBaudRate;
import com.cannontech.rest.api.commChannel.request.MockTcpPortDetail;
import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockApiError;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class TcpPortApiTest {

    MockTcpPortDetail tcpPort = null;

    @BeforeClass
    public void setUp() {
        tcpPort = (MockTcpPortDetail) CommChannelHelper.buildCommChannel(MockPaoType.TCPPORT);
    }

    @Test
    public void tcpPort_01_Create(ITestContext context) {
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", tcpPort);
        String portId = createResponse.path(CommChannelHelper.CONTEXT_PORT_ID).toString();
        context.setAttribute(CommChannelHelper.CONTEXT_PORT_ID, portId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Port Id should not be Null", portId != null);
    }

    @Test(dependsOnMethods = { "tcpPort_01_Create" })
    public void tcpPort_02_Get(ITestContext context) {

        Log.info("Port Id of TcpPort CommChannel created is : "
                + context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getPort",
                context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockTcpPortDetail tcpPortDetail = getResponse.as(MockTcpPortDetail.class);

        assertTrue("Name Should be : " + tcpPort.getName(), tcpPort.getName().equals(tcpPortDetail.getName()));
        assertTrue("Type Should be : " + tcpPort.getType(), tcpPort.getType() == tcpPortDetail.getType());
        assertTrue("Baud Rate Should be : " + tcpPort.getBaudRate(),
                tcpPort.getBaudRate().equals(tcpPortDetail.getBaudRate()));
        assertTrue("Additional Time Out Should be : " + tcpPort.getTiming().getExtraTimeOut(),
                tcpPort.getTiming().getExtraTimeOut().equals(tcpPortDetail.getTiming().getExtraTimeOut()));
    }

    @Test(dependsOnMethods = { "tcpPort_01_Create" })
    public void tcpPort_03_Update(ITestContext context) {
        String name = "Test TCP Port Update";

        tcpPort.setName(name);
        tcpPort.setBaudRate(MockBaudRate.BAUD_115200);

        ExtractableResponse<?> getResponse = ApiCallHelper.post("updatePort", tcpPort,
                context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getPort",
                context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockTcpPortDetail updatedTcpPortResponse = getupdatedResponse.as(MockTcpPortDetail.class);
        context.setAttribute("tcpTerminalName", updatedTcpPortResponse.getName());

        assertTrue("Name Should be : " + tcpPort.getName(),
                tcpPort.getName().equals(updatedTcpPortResponse.getName()));
        assertTrue("Type Should be : " + tcpPort.getType(), tcpPort.getType() == updatedTcpPortResponse.getType());
        assertTrue("Baud Rate Should be : " + tcpPort.getBaudRate(),
                tcpPort.getBaudRate().equals(updatedTcpPortResponse.getBaudRate()));
    }

    @Test(dependsOnMethods = { "tcpPort_02_Get" })
    public void tcpPort_04_Delete(ITestContext context) {
        String expectedMessage = "Port Id not found";
        ExtractableResponse<?> response = ApiCallHelper.delete("deletePort",
                context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        // Get request to validate TCP Port comm channel is deleted
        ExtractableResponse<?> getDeletedResponse = ApiCallHelper.get("getPort",
                context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());
        assertTrue("Status code should be 400", getDeletedResponse.statusCode() == 400);

        MockApiError error = getDeletedResponse.as(MockApiError.class);
        assertTrue("Expected error message Should be : " + expectedMessage, expectedMessage.equals(error.getMessage()));
    }

    /**
     * Test case to validate TCPPort comm channel cannot be created as name with null and gets valid error message
     * in response
     */
    @Test
    public void tcpPort_05_NameCannotBeNull() {
        MockTcpPortDetail mockTcpPort = (MockTcpPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TCPPORT);

        mockTcpPort.setName(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpPort);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Name is required."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate TCPPort comm channel cannot be created with empty name and gets valid error message in
     * response
     */
    @Test
    public void tcpPort_06_NameCannotBeEmpty() {
        MockTcpPortDetail mockTcpPort = (MockTcpPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TCPPORT);

        mockTcpPort.setName("");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpPort);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Cannot be blank."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate TCPPort comm channel cannot be created with name having more than 60 characters and
     * validates valid error message in response
     */
    @Test
    public void tcpPort_07_NameGreaterThanMaxLength() {
        MockTcpPortDetail mockTcpPort = (MockTcpPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TCPPORT);

        mockTcpPort.setName("TestTcpPortTestTcpPortTestTcpPortTestTcpPortTestTcpPortCreate");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpPort);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Exceeds maximum length of 60."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate TCPPort comm channel cannot be created with name having special characters and validates
     * valid error message in response
     */
    @Test
    public void tcpPort_08_NameWithSpecialChars() {
        MockTcpPortDetail mockTcpPort = (MockTcpPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TCPPORT);

        mockTcpPort.setName("Test,//Test");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpPort);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "name",
                        "Cannot be blank or include any of the following characters: / \\ , ' \" |"),
                "Expected code in response is not correct");
    }
}
