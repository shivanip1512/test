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
import com.cannontech.rest.api.commChannel.request.MockUDPPortDetails;
import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockApiError;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class UdpTerminalServerApiTest {
    MockUDPPortDetails udpTerminal = null;

    @BeforeClass
    public void setUp() {
        udpTerminal = (MockUDPPortDetails) CommChannelHelper.buildCommChannel(MockPaoType.UDPPORT);
    }

    /**
     * This test case validates creation of UDP Terminal Server comm channel
     */
    @Test
    public void udpTerminalServer_01_Create(ITestContext context) {

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", udpTerminal);
        String portId = createResponse.path(CommChannelHelper.CONTEXT_PORT_ID).toString();
        context.setAttribute(CommChannelHelper.CONTEXT_PORT_ID, portId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Port Id should not be Null", portId != null);
    }

    /**
     * This test case validates retrieval(Get) of UDP Terminal Server comm channel and validates response
     */
    @Test(dependsOnMethods = { "udpTerminalServer_01_Create" })
    public void udpTerminalServer_02_Get(ITestContext context) {
        Log.info("Port Id of udpTerminal CommChannel created is : "
                + context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getPort",
                context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockUDPPortDetails udpTerminalDetail = getResponse.as(MockUDPPortDetails.class);

        assertTrue("Name Should be : " + udpTerminal.getName(), udpTerminal.getName().equals(udpTerminalDetail.getName()));
        assertTrue("Type Should be : " + udpTerminal.getType(), udpTerminal.getType() == udpTerminalDetail.getType());
        assertTrue("Baud Rate Should be : " + udpTerminal.getBaudRate(),
                udpTerminal.getBaudRate().equals(udpTerminalDetail.getBaudRate()));
        assertTrue("Port Number Should be : " + udpTerminal.getPortNumber(),
                udpTerminal.getPortNumber().equals(udpTerminalDetail.getPortNumber()));
        assertTrue("Additional Time Out Should be : " + udpTerminal.getTiming().getExtraTimeOut(),
                udpTerminal.getTiming().getExtraTimeOut().equals(udpTerminalDetail.getTiming().getExtraTimeOut()));
    }

    /**
     * This test case validates updation of UDP Terminal Server comm channel created via udpTerminalServer_01_Create
     */
    @Test(dependsOnMethods = { "udpTerminalServer_01_Create" })
    public void udpTerminalServer_03_Update(ITestContext context) {
        String name = "Test UDP Terminal Update";

        udpTerminal.setName(name);
        udpTerminal.setBaudRate(MockBaudRate.BAUD_115200);
        udpTerminal.setPortNumber(6734);
        udpTerminal.getSharing().setSharedPortType(MockSharedPortType.NONE);
        udpTerminal.getSharing().setSharedSocketNumber(1025);
        udpTerminal.setKeyInHex("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");

        ExtractableResponse<?> getResponse = ApiCallHelper.patch("updatePort", udpTerminal,
                context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getPort",
                context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockUDPPortDetails updatedUdpTerminalResponse = getupdatedResponse.as(MockUDPPortDetails.class);
        context.setAttribute("udpTerminalName", updatedUdpTerminalResponse.getName());

        assertTrue("Name Should be : " + udpTerminal.getName(),
                udpTerminal.getName().equals(updatedUdpTerminalResponse.getName()));
        assertTrue("Type Should be : " + udpTerminal.getType(), udpTerminal.getType() == updatedUdpTerminalResponse.getType());
        assertTrue("Baud Rate Should be : " + udpTerminal.getBaudRate(),
                udpTerminal.getBaudRate().equals(updatedUdpTerminalResponse.getBaudRate()));
        assertTrue("Port Number Should be : " + udpTerminal.getPortNumber(),
                udpTerminal.getPortNumber().equals(updatedUdpTerminalResponse.getPortNumber()));
        assertTrue("Shared port type Should be : " + udpTerminal.getSharing().getSharedPortType(),
                udpTerminal.getSharing().getSharedPortType().equals(updatedUdpTerminalResponse.getSharing().getSharedPortType()));
        assertTrue("Socket number Should be : " + udpTerminal.getSharing().getSharedSocketNumber(),
                udpTerminal.getSharing().getSharedSocketNumber()
                        .equals(updatedUdpTerminalResponse.getSharing().getSharedSocketNumber()));
        assertTrue("Key in Hex Should be : " + udpTerminal.getKeyInHex(),
                udpTerminal.getKeyInHex().equals(updatedUdpTerminalResponse.getKeyInHex()));
    }

    /**
     * This test case validates deletion of UDP Terminal Server comm channel
     */
    @Test(dependsOnMethods = { "udpTerminalServer_02_Get" })
    public void udpTerminalServer_04_Delete(ITestContext context) {
        String expectedMessage = "Port Id not found";

        ExtractableResponse<?> response = ApiCallHelper.delete("deletePort",
                context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        // Get request to validate UDP terminal comm channel is deleted
        ExtractableResponse<?> getDeletedResponse = ApiCallHelper.get("getPort",
                context.getAttribute(CommChannelHelper.CONTEXT_PORT_ID).toString());
        assertTrue("Status code should be 400", getDeletedResponse.statusCode() == 400);

        MockApiError error = getDeletedResponse.as(MockApiError.class);
        assertTrue("Expected error message Should be : " + expectedMessage, expectedMessage.equals(error.getMessage()));
    }

    /**
     * Test case to validate UDP Terminal Server comm channel cannot be created with name as null and gets valid error message
     * in response
     */
    @Test
    public void udpTerminalServer_05_NameCannotBeNull() {
        MockUDPPortDetails mockUdpTerminal = (MockUDPPortDetails) CommChannelHelper
                .buildCommChannel(MockPaoType.UDPPORT);

        mockUdpTerminal.setName(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockUdpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Name is required."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate UDP Terminal Server comm channel cannot be created with empty name and gets valid error message in
     * response
     */
    @Test
    public void udpTerminalServer_06_NameCannotBeEmpty() {
        MockUDPPortDetails mockUdpTerminal = (MockUDPPortDetails) CommChannelHelper
                .buildCommChannel(MockPaoType.UDPPORT);

        mockUdpTerminal.setName("");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockUdpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Name is required."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate UDP Terminal Server comm channel cannot be created with name having more than 60 characters and
     * validates valid error message in response
     */
    @Test
    public void udpTerminalServer_07_NameGreaterThanMaxLength() {

        MockUDPPortDetails mockUdpTerminal = (MockUDPPortDetails) CommChannelHelper
                .buildCommChannel(MockPaoType.UDPPORT);

        mockUdpTerminal.setName("TestTerminalTestTerminalTestTerminalTestTerminalTestTerminalCreate");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockUdpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Exceeds maximum length of 60."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate UDP Terminal Server comm channel cannot be created with name having special characters and validates
     * valid error message in response
     */
    @Test
    public void udpTerminalServer_08_NameWithSpecialChars() {
        MockUDPPortDetails mockUdpTerminal = (MockUDPPortDetails) CommChannelHelper
                .buildCommChannel(MockPaoType.UDPPORT);

        mockUdpTerminal.setName("Test,//Test");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockUdpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "name",
                        "Name must not contain any of the following characters: / \\ , ' \" |"),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate UDP Terminal Server comm channel cannot be created with Port number max range 65,535 and gets valid
     * error
     * message in response
     */
    @Test
    public void udpTerminalServer_09_PortNumberRange() {
        MockUDPPortDetails mockUdpTerminal = (MockUDPPortDetails) CommChannelHelper
                .buildCommChannel(MockPaoType.UDPPORT);

        mockUdpTerminal.setPortNumber(99999999);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockUdpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "portNumber", "Port Number must be between 1 and 65,535."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate UDP Terminal Server comm channel cannot be created with Port number as null and gets valid error
     * message in response
     */
    @Test
    public void udpTerminalServer_10_PortNoCannotBeNull() {
        MockUDPPortDetails mockUdpTerminal = (MockUDPPortDetails) CommChannelHelper
                .buildCommChannel(MockPaoType.UDPPORT);

        mockUdpTerminal.setPortNumber(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockUdpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "portNumber", "Port Number must be between 1 and 65,535."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate UDP Terminal Server comm channel cannot be created with KeyInHex as empty and gets valid error
     * message in response
     */
    @Test
    public void udpTerminalServer_11_KeyInHexEmpty() {
        MockUDPPortDetails mockUdpTerminal = (MockUDPPortDetails) CommChannelHelper
                .buildCommChannel(MockPaoType.UDPPORT);

        mockUdpTerminal.setKeyInHex(" ");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockUdpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "keyInHex",
                        "Encryption Key must be in Hex format and 16 bytes long (32 hex values)."),
                "Expected code in response is not correct");
    }
    
    /**
     * Test case to validate UDP Terminal Server comm channel cannot be created with KeyInHex invalid character (Valid hex
     * characters includes 0-9 and A-F) and gets valid error message in response
     */
    @Test
    public void udpTerminalServer_12_KeyInHexInvalidHexChar() {
        MockUDPPortDetails mockUdpTerminal = (MockUDPPortDetails) CommChannelHelper
                .buildCommChannel(MockPaoType.UDPPORT);

        mockUdpTerminal.setKeyInHex("1313666666abcdef1111222233abcdeG");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockUdpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "keyInHex",
                        "Encryption Key must be in Hex format and 16 bytes long (32 hex values)."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate UDP Terminal Server comm channel cannot be created with KeyInHex less than 16 bytes
     * and gets valid error message in response
     */
    @Test
    public void udpTerminalServer_13_KeyInHexLessThan16Bytes() {
        MockUDPPortDetails mockUdpTerminal = (MockUDPPortDetails) CommChannelHelper
                .buildCommChannel(MockPaoType.UDPPORT);

        mockUdpTerminal.setKeyInHex("abcdef1313666666abcdef111122223");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockUdpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "keyInHex",
                        "Encryption Key must be 16 bytes long (32 hex values)."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate UDP Terminal Server comm channel cannot be created with KeyInHex greater than 16 bytes
     * and gets valid error message in response
     */
    @Test
    public void udpTerminalServer_14_KeyInHexGreaterThan16Bytes() {
        MockUDPPortDetails mockUdpTerminal = (MockUDPPortDetails) CommChannelHelper
                .buildCommChannel(MockPaoType.UDPPORT);

        mockUdpTerminal.setKeyInHex("abcdef1313666666abcdef11112222345");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockUdpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "keyInHex",
                        "Encryption Key must be 16 bytes long (32 hex values)."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate UDP Terminal Server comm channel cannot be created with SharedSocketNum exceeds Max Range
     * (1-65535) and gets valid error message in response
     */
    @Test
    public void udpTerminalServer_15_SharedSocNumExceedsMaxRange() {
        MockUDPPortDetails mockUdpTerminal = (MockUDPPortDetails) CommChannelHelper
                .buildCommChannel(MockPaoType.UDPPORT);

        mockUdpTerminal.getSharing().setSharedSocketNumber(65536);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockUdpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "sharing.sharedSocketNumber",
                "Socket Number must be between 1 and 65,535."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate UDP Terminal Server comm channel cannot be created with SharedSocketNum exceeds Min Range (1-65535)
     * and gets valid error message in response
     */
    @Test
    public void udpTerminalServer_16_SharedSocNumExceedsMinRange() {
        MockUDPPortDetails mockUdpTerminal = (MockUDPPortDetails) CommChannelHelper
                .buildCommChannel(MockPaoType.UDPPORT);

        mockUdpTerminal.getSharing().setSharedSocketNumber(0);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockUdpTerminal);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "sharing.sharedSocketNumber",
                        "Socket Number must be between 1 and 65,535."),
                        "Expected code in response is not correct");
    }
    
    /**
     * Test case to validate UDP Terminal Server comm channel cannot be created as as Timing fields Exceed Max Value and gets
     * valid error message in response
     */
    @Test
    public void udpTerminalServer__17_TimingExceedMaxValue() {
        MockUDPPortDetails mockUdpTerminal = (MockUDPPortDetails) CommChannelHelper
                .buildCommChannel(MockPaoType.UDPPORT);

        MockPortTiming timingValues = MockPortTiming.builder()
                .preTxWait(10000001)
                .rtsToTxWait(10000001)
                .postTxWait(10000001)
                .receiveDataWait(1001)
                .extraTimeOut(1000)
                .build();

        mockUdpTerminal.setTiming(timingValues);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockUdpTerminal);
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
     * Test case to validate UDP Terminal Server comm channel cannot be created as as Timing fields Exceed Min Value and gets
     * valid error message in response
     */
    @Test
    public void udpTerminalServer__18_TimingBlelowMinValue() {
        MockUDPPortDetails mockUdpTerminal = (MockUDPPortDetails) CommChannelHelper
                .buildCommChannel(MockPaoType.UDPPORT);

        MockPortTiming timingValues = MockPortTiming.builder()
                .preTxWait(-1)
                .rtsToTxWait(-1)
                .postTxWait(-1)
                .receiveDataWait(-1)
                .extraTimeOut(-1)
                .build();

        mockUdpTerminal.setTiming(timingValues);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPort", mockUdpTerminal);
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
