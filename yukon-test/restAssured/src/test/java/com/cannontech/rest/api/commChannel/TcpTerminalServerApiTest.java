package com.cannontech.rest.api.commChannel;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cannontech.rest.api.commChannel.helper.CommChannelHelper;
import com.cannontech.rest.api.commChannel.request.MockBaudRate;
import com.cannontech.rest.api.commChannel.request.MockPortTiming;
import com.cannontech.rest.api.commChannel.request.MockSharedPortType;
import com.cannontech.rest.api.commChannel.request.MockTcpPortDetail;
import com.cannontech.rest.api.commChannel.request.MockTcpSharedPortDetail;
import com.cannontech.rest.api.commChannel.request.MockUDPPortDetails;
import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockApiError;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class TcpTerminalServerApiTest {
    MockTcpSharedPortDetail tcpTerminal = null;

    @BeforeClass
    public void setUp() {
        tcpTerminal = (MockTcpSharedPortDetail) CommChannelHelper.buildCommChannel(MockPaoType.TSERVER_SHARED);
    }

    /**
     * This test case validates creation of TCP Terminal Server comm channel
     */
    @Test
    public void tcpTerminalServer_01_Create(ITestContext context) {

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", tcpTerminal);
        String portId = createResponse.path(CommChannelHelper.CONTEXT_PORT_ID).toString();
        context.setAttribute(CommChannelHelper.CONTEXT_PORT_ID, portId);
        assertTrue("Status code should be 201", createResponse.statusCode() == 201);
        assertTrue("Port Id should not be Null", portId != null);
    }

    /**
     * This test case validates retrieval(Get) of TCP Terminal Server comm channel and validates response
     */
    @Test(dependsOnMethods = { "tcpTerminalServer_01_Create" })
    public void tcpTerminalServer_02_Get(ITestContext context) {
        Log.info("Port Id of TCPTerminal CommChannel created is : "
                + context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getPort",
                context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockTcpSharedPortDetail tcpTerminalDetail = getResponse.as(MockTcpSharedPortDetail.class);

        assertTrue("Name Should be : " + tcpTerminal.getName(), tcpTerminal.getName().equals(tcpTerminalDetail.getName()));
        assertTrue("Type Should be : " + tcpTerminal.getType(), tcpTerminal.getType() == tcpTerminalDetail.getType());
        assertTrue("Baud Rate Should be : " + tcpTerminal.getBaudRate(),
                tcpTerminal.getBaudRate().equals(tcpTerminalDetail.getBaudRate()));
        assertTrue("Port Number Should be : " + tcpTerminal.getPortNumber(),
                tcpTerminal.getPortNumber().equals(tcpTerminalDetail.getPortNumber()));
        assertTrue("Additional Time Out Should be : " + tcpTerminal.getTiming().getExtraTimeOut(),
                tcpTerminal.getTiming().getExtraTimeOut().equals(tcpTerminalDetail.getTiming().getExtraTimeOut()));
    }

    /**
     * This test case validates updation of TCP Terminal Server comm channel created via tcpTerminalServer_01_Create
     */
    @Test(dependsOnMethods = { "tcpTerminalServer_01_Create" })
    public void tcpTerminalServer_03_Update(ITestContext context) {
        String name = "Test TCP Terminal Update";

        tcpTerminal.setName(name);
        tcpTerminal.setBaudRate(MockBaudRate.BAUD_115200);
        tcpTerminal.setIpAddress("locahost");
        tcpTerminal.setPortNumber(6734);
        tcpTerminal.getSharing().setSharedPortType(MockSharedPortType.ILEX);

        ExtractableResponse<?> getResponse = ApiCallHelper.patch("updatePort", tcpTerminal,
                context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getPort",
                context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockTcpSharedPortDetail updatedTcpTerminalResponse = getupdatedResponse.as(MockTcpSharedPortDetail.class);
        context.setAttribute("tcpTerminalName", updatedTcpTerminalResponse.getName());

        assertTrue("Name Should be : " + tcpTerminal.getName(),
                tcpTerminal.getName().equals(updatedTcpTerminalResponse.getName()));
        assertTrue("Type Should be : " + tcpTerminal.getType(), tcpTerminal.getType() == updatedTcpTerminalResponse.getType());
        assertTrue("Baud Rate Should be : " + tcpTerminal.getBaudRate(),
                tcpTerminal.getBaudRate().equals(updatedTcpTerminalResponse.getBaudRate()));
        assertTrue("Port Number Should be : " + tcpTerminal.getPortNumber(),
                tcpTerminal.getPortNumber().equals(updatedTcpTerminalResponse.getPortNumber()));
        assertTrue("IP Address Should be : " + tcpTerminal.getIpAddress(),
                tcpTerminal.getIpAddress().equals(updatedTcpTerminalResponse.getIpAddress()));
        assertTrue("Shared port type Should be : " + tcpTerminal.getSharing().getSharedPortType(),
                tcpTerminal.getSharing().getSharedPortType().equals(updatedTcpTerminalResponse.getSharing().getSharedPortType()));
    }

    /**
     * This test case validates deletion of TCP Terminal Server comm channel
     */
    @Test(dependsOnMethods = { "tcpTerminalServer_02_Get" })
    public void tcpTerminalServer_04_Delete(ITestContext context) {
        String expectedMessage = "Port Id not found";

        ExtractableResponse<?> response = ApiCallHelper.delete("deletePort",
                context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        // Get request to validate TCP Terminal comm channel is deleted
        ExtractableResponse<?> getDeletedResponse = ApiCallHelper.get("getPort",
                context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());
        assertTrue("Status code should be 400", getDeletedResponse.statusCode() == 400);

        MockApiError error = getDeletedResponse.as(MockApiError.class);
        assertTrue("Expected error message Should be : " + expectedMessage, expectedMessage.equals(error.getMessage()));
    }

    /**
     * Test case to validate TCP Terminal Server comm channel cannot be created with name as null and gets valid error message
     * in response
     */
    @Test
    public void tcpTerminalServer_05_NameCannotBeNull() {
        MockTcpSharedPortDetail mockTcpTerminal = (MockTcpSharedPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TSERVER_SHARED);

        mockTcpTerminal.setName(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Name is required."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate TCP Terminal Server comm channel cannot be created with empty name and gets valid error message in
     * response
     */
    @Test
    public void tcpTerminalServer_06_NameCannotBeEmpty() {
        MockTcpSharedPortDetail mockTcpTerminal = (MockTcpSharedPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TSERVER_SHARED);

        mockTcpTerminal.setName("");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Name is required."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate TCP Terminal Server comm channel cannot be created with name having more than 60 characters and
     * validates valid error message in response
     */
    @Test
    public void tcpTerminalServer_07_NameGreaterThanMaxLength() {

        MockTcpSharedPortDetail mockTcpTerminal = (MockTcpSharedPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TSERVER_SHARED);

        mockTcpTerminal.setName("TestTerminalTestTerminalTestTerminalTestTerminalTestTerminalCreate");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Exceeds maximum length of 60."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate TCP Terminal Server comm channel cannot be created with name having special characters and validates
     * valid error message in response
     */
    @Test
    public void tcpTerminalServer_08_NameWithSpecialChars() {

        MockTcpSharedPortDetail mockTcpTerminal = (MockTcpSharedPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TSERVER_SHARED);

        mockTcpTerminal.setName("Test,//Test");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "name",
                        "Name must not contain any of the following characters: / \\ , ' \" |"),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate TCP Terminal Server comm channel cannot be created with empty IpAddress and gets valid error message
     * in response
     */
    @Test
    public void tcpTerminalServer_09_IpAddressCannotBeEmpty() {

        MockTcpSharedPortDetail mockTcpTerminal = (MockTcpSharedPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TSERVER_SHARED);

        mockTcpTerminal.setIpAddress("");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "ipAddress", "IP Address is required."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate TCP Terminal Server comm channel cannot be created with IpAddress with null and gets valid error
     * message in response
     */
    @Test
    public void tcpTerminalServer_10_IpAddressCannotBeNull() {
        MockTcpSharedPortDetail mockTcpTerminal = (MockTcpSharedPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TSERVER_SHARED);

        mockTcpTerminal.setIpAddress(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "ipAddress", "IP Address is required."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate TCP Terminal Server comm channel cannot be created with Port number as null and gets valid error
     * message in response
     */
    @Test
    public void tcpTerminalServer_11_PortNoCannotBeNull() {
        MockTcpSharedPortDetail mockTcpTerminal = (MockTcpSharedPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TSERVER_SHARED);

        mockTcpTerminal.setPortNumber(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "portNumber", "Port Number must be between 1 and 65,535."),
                "Expected code in response is not correct");
    }
    
    /**
     * Test case to validate TCP Terminal Server comm channel cannot be created with invalid IpAddress and gets valid error
     * message in response
     */
    @Test
    public void tcpTerminalServer_12_InvalidIpAddress() {
        MockTcpSharedPortDetail mockTcpTerminal = (MockTcpSharedPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TSERVER_SHARED);

        mockTcpTerminal.setIpAddress("123#$%^^&");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "ipAddress", "Invalid IP/Host Name."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate TCP Terminal Server comm channel cannot be created with Port number max range 65,535 and gets valid
     * error message in response
     */
    @Test
    public void tcpTerminalServer_13_PortNumberRange() {
        MockTcpSharedPortDetail mockTcpTerminal = (MockTcpSharedPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TSERVER_SHARED);

        mockTcpTerminal.setPortNumber(65536);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "portNumber", "Port Number must be between 1 and 65,535."),
                "Expected code in response is not correct");
    }
    
    /**
     * Test case to validate TCP Terminal Server comm channel cannot be created as Timing fields Exceed Max Value and gets valid
     * error message in response
     */
    @Test
    public void tcpTerminalServer_14_TimingExceedMaxValue(ITestContext context) {
        
        MockTcpSharedPortDetail mockTcpTerminal = (MockTcpSharedPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TSERVER_SHARED);

        MockPortTiming timingValues = MockPortTiming.builder()
        .preTxWait(10000001)
        .rtsToTxWait(10000001)
        .postTxWait(10000001)
        .receiveDataWait(1001)
        .extraTimeOut(1000)
        .build();
        
        mockTcpTerminal.setTiming(timingValues);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "timing.preTxWait", "Pre Tx Wait must be between 0 and 10,000,000."),
                "Expected code in response is not correct");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "timing.rtsToTxWait", "RTS To Tx Wait must be between 0 and 10,000,000."),
                "Expected code in response is not correct");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "timing.postTxWait", "Post Tx Wait must be between 0 and 10,000,000."),
                "Expected code in response is not correct");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "timing.receiveDataWait", "Receive Data Wait must be between 0 and 1,000."),
                "Expected code in response is not correct");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "timing.extraTimeOut", "Additional Time Out must be between 0 and 999."),
                "Expected code in response is not correct");
    }
    
    /**
     * Test case to validate TCP Terminal Server comm channel cannot be created as Timing fields Exceed Min Value and gets valid
     * error message in response
     */
    @Test
    public void tcpTerminalServer_15_TimingBelowMinValue(ITestContext context) {
        
        MockTcpSharedPortDetail mockTcpTerminal = (MockTcpSharedPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TSERVER_SHARED);

        MockPortTiming timingValues = MockPortTiming.builder()
        .preTxWait(-1)
        .rtsToTxWait(-1)
        .postTxWait(-1)
        .receiveDataWait(-1)
        .extraTimeOut(-1)
        .build();
        
        mockTcpTerminal.setTiming(timingValues);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "timing.preTxWait", "Pre Tx Wait must be between 0 and 10,000,000."),
                "Expected code in response is not correct");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "timing.rtsToTxWait", "RTS To Tx Wait must be between 0 and 10,000,000."),
                "Expected code in response is not correct");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "timing.postTxWait", "Post Tx Wait must be between 0 and 10,000,000."),
                "Expected code in response is not correct");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "timing.receiveDataWait", "Receive Data Wait must be between 0 and 1,000."),
                "Expected code in response is not correct");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "timing.extraTimeOut", "Additional Time Out must be between 0 and 999."),
                "Expected code in response is not correct");
    }
}
