package com.cannontech.rest.api.commChannel;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cannontech.rest.api.commChannel.helper.CommChannelHelper;
import com.cannontech.rest.api.commChannel.request.MockBaudRate;
import com.cannontech.rest.api.commChannel.request.MockPortTiming;
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
        assertTrue("Status code should be 201", createResponse.statusCode() == 201);
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

        ExtractableResponse<?> getResponse = ApiCallHelper.patch("updatePort", tcpPort,
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
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Name is required."),
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
                        "Name must not contain any of the following characters: / \\ , ' \" |."),
                "Expected code in response is not correct");
    }
    
    /**
     * Test case to validate TCPPort comm channel cannot be created as Baud Rate with null and gets valid error message
     * in response
     */
    @Test
    public void tcpPort_09_BaudRateCannotBeNull() {
        MockTcpPortDetail mockTcpPort = (MockTcpPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TCPPORT);

        mockTcpPort.setBaudRate(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpPort);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "baudRate", "Baud Rate is required."),
                "Expected code in response is not correct");
    }
    
    /**
     * Test case to validate TCPPort comm channel cannot be created as Type with null and gets valid error message
     * in response
     */
    @Test
    public void tcpPort_10_TypeCannotBeNull() {
        
        String expectedMessage = "type is not found in the request.";
        MockTcpPortDetail mockTcpPort = (MockTcpPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TCPPORT);

        mockTcpPort.setType(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpPort);
        assertTrue(createResponse.statusCode() == 400, "Status code should be 400");
        MockApiError error = createResponse.as(MockApiError.class);
        assertTrue("Expected error message Should be : " + expectedMessage, expectedMessage.equals(error.getMessage()));
    }
    
    /**
     * Test case to validate TCPPort comm channel cannot be created as Name is Duplicated and gets valid error message
     * in response
     */
    @Test
    public void tcpPort_11_DuplicateName() {
        
        MockTcpPortDetail mockTcpPort = (MockTcpPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TCPPORT);

        mockTcpPort.setName("Test Port");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpPort);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Name already exists"),
                "Expected code in response is not correct");
    }
    
    /**
     * Test case to validate TCPPort comm channel is created with default timing values when Timings field is passed as null
     */
    @Test
    public void tcpPort_12_WhenTimingAsNullVerifyDefaultValues() {
        
        MockTcpPortDetail mockTcpPort = (MockTcpPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TCPPORT);

        mockTcpPort.setTiming(null);
        mockTcpPort.setName("TimingAsNull");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpPort);
        String portId = createResponse.path(CommChannelHelper.CONTEXT_PORT_ID).toString();
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Port Id should not be Null", portId != null);
        
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getPort",
                portId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockTcpPortDetail tcpPortDetail = getResponse.as(MockTcpPortDetail.class);
        
        Integer preTxWait = 25;
        Integer rtsToTxWait = 0;
        Integer postTxWait = 0;
        Integer receiveDataWait = 0;
        Integer extraTimeOut = 0;

        assertTrue("Additional Time Out Should be : " + preTxWait,
                preTxWait.equals(tcpPortDetail.getTiming().getPreTxWait()));
        assertTrue("Additional Time Out Should be : " + rtsToTxWait,
                rtsToTxWait.equals(tcpPortDetail.getTiming().getRtsToTxWait()));
        assertTrue("Additional Time Out Should be : " + postTxWait,
                postTxWait.equals(tcpPortDetail.getTiming().getPostTxWait()));
        assertTrue("Additional Time Out Should be : " + receiveDataWait,
                receiveDataWait.equals(tcpPortDetail.getTiming().getReceiveDataWait()));
        assertTrue("Additional Time Out Should be : " + extraTimeOut,
                extraTimeOut.equals(tcpPortDetail.getTiming().getExtraTimeOut()));
        
    }
    
    /**
     * Test case to validate TCPPort comm channel cannot be deleted when invalid Port Id is passed in delete request and validate
     * in response error message
     */
    @Test
    public void tcpPort_13_DeleteWithInvalidPortID(ITestContext context) {
        String expectedMessage = "Port Id not found";
        ExtractableResponse<?> response = ApiCallHelper.delete("deletePort", "10");
        assertTrue("Status code should be 400", response.statusCode() == 400);

        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected error message Should be : " + expectedMessage, expectedMessage.equals(error.getMessage()));
    }
    
    /**
     * Test case to validate TCPPort comm channel cannot be created as Timing fields Exceed Max Value and gets valid error message
     * in response
     */
    @Test
    public void tcpPort_14_TimingExceedMaxValue(ITestContext context) {
        
        MockTcpPortDetail mockTcpPort = (MockTcpPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TCPPORT);

        MockPortTiming timingValues = MockPortTiming.builder()
        .preTxWait(10000001)
        .rtsToTxWait(10000001)
        .postTxWait(10000001)
        .receiveDataWait(1001)
        .extraTimeOut(1000)
        .build();
        
        mockTcpPort.setTiming(timingValues);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpPort);
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
     * Test case to validate TCPPort comm channel cannot be created as Timing fields Exceed Min Value and gets valid error message
     * in response
     */
    @Test
    public void tcpPort_15_TimingBelowMinValue(ITestContext context) {
        
        MockTcpPortDetail mockTcpPort = (MockTcpPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TCPPORT);

        MockPortTiming timingValues = MockPortTiming.builder()
        .preTxWait(-1)
        .rtsToTxWait(-1)
        .postTxWait(-1)
        .receiveDataWait(-1)
        .extraTimeOut(-1)
        .build();
        
        mockTcpPort.setTiming(timingValues);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpPort);
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
     * Test case to validate TCPPort comm channel cannot be updated with same name as other comm channel, as each comm channel is unique and gets valid error message
     * in response
     */
    @Test(dependsOnMethods = { "tcpPort_01_Create" })
    public void tcpPort_16_UpdateCommChannelWithExistingCommChannelName(ITestContext context) {
        String existingCommChannelName = "Test Port11112346144";

        MockTcpPortDetail mockTcpPort = (MockTcpPortDetail) CommChannelHelper
                .buildCommChannel(MockPaoType.TCPPORT);

        mockTcpPort.setName("OtherCommChannel1111");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockTcpPort);
        String portId = createResponse.path(CommChannelHelper.CONTEXT_PORT_ID).toString();
        assertTrue("Status code should be 201", createResponse.statusCode() == 201);
        assertTrue("Port Id should not be Null", portId != null);
        
        mockTcpPort.setName(existingCommChannelName);
        
        ExtractableResponse<?> getResponse = ApiCallHelper.post("updatePort", mockTcpPort,
                portId);
                       
        assertTrue(getResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(getResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(getResponse, "name", "Name already exists"),
                "Expected code in response is not correct");
    }
}
