package com.cannontech.yukon.api.stars.endpoint;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Node;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.dr.account.exception.StarsAccountNotFoundException;
import com.cannontech.stars.util.StarsClientRequestException;
import com.cannontech.stars.util.StarsInvalidArgumentException;
import com.cannontech.stars.ws.LmDeviceDto;
import com.cannontech.stars.ws.StarsControllableDeviceHelper;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.stars.endpoint.ControllableDevicesRequestEndPoint.ErrorCodeMapper;
import com.cannontech.yukon.api.stars.mocks.MockHardwareEventLogService;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class ControllableDevicesRequestEndPointTest {

    private static final LiteYukonUser AUTH_USER = MockRolePropertyDao.getAuthorizedUser();
    private static final LiteYukonUser NOT_AUTH_USER = MockRolePropertyDao.getUnAuthorizedUser();

    private ControllableDevicesRequestEndPoint impl;
    private StarsControllableDeviceHelper mockHelper;

    // Test Resource files for the Request xml
    private static final String newDeviceReqResource = "ControllableDevicesRequestNew.xml";
    private static final String updateDeviceReqResource = "ControllableDevicesRequestUpdate.xml";
    private static final String removeDeviceReqResource = "ControllableDevicesRequestRemove.xml";

    private static final String ACCOUNT_NUM_NOT_FOUND = ErrorCodeMapper.AccountNotFound.name();
    private static final String ACCOUNT_NUM_INVALID_ARG = ErrorCodeMapper.InvalidArgument.name();
    private static final String ACCOUNT_NUM_ERROR = ErrorCodeMapper.ClientRequestError.name();
    private static final String ACCOUNT_NUM_VALID = "Valid";

    private static final String SERIAL_NUM_NOT_FOUND = ErrorCodeMapper.AccountNotFound.name();
    private static final String SERIAL_NUM_INVALID_ARG = ErrorCodeMapper.InvalidArgument.name();
    private static final String SERIAL_NUM_ERROR = ErrorCodeMapper.ClientRequestError.name();
    private static final String SERIAL_NUM_VALID = "Valid";

    private static ControllableDeviceResultMapper deviceResultElementMapper = new ControllableDeviceResultMapper();

    // Response elements
    static final String newDeviceRespElementStr;
    static final String updateDeviceRespElementStr;
    static final String removeDeviceRespElementStr;

    static final String newDevicesRespStr = "/y:newControllableDevicesResponse";
    static final String updateDevicesRespStr = "/y:updateControllableDevicesResponse";
    static final String removeDevicesRespStr = "/y:removeControllableDevicesResponse";
    static final String controllableDeviceResultListStr = "/y:controllableDeviceResultList";
    static final String controllableDeviceResultStr = "/y:controllableDeviceResult";
    static final String accountNumberRespStr = "y:accountNumber";
    static final String serialNumberRespStr = "y:serialNumber";
    static final String successStr = "y:success";
    static final String failErrorCodeStr = "y:failure/y:errorCode";
    static final String failErrorDescStr = "y:failure/y:errorDescription";

    static {
        newDeviceRespElementStr = newDevicesRespStr + controllableDeviceResultListStr + controllableDeviceResultStr;
        updateDeviceRespElementStr = updateDevicesRespStr + controllableDeviceResultListStr + controllableDeviceResultStr;
        removeDeviceRespElementStr = removeDevicesRespStr + controllableDeviceResultListStr + controllableDeviceResultStr;
    }

    @Before
    public void setUp() throws Exception {
        mockHelper = new MockControllableDeviceHelper();

        impl = new ControllableDevicesRequestEndPoint();
        impl.setStarsControllableDeviceHandler(mockHelper);
        impl.setHardwareEventLogService(new MockHardwareEventLogService());
        ReflectionTestUtils.setField(impl, "rolePropertyDao", new MockRolePropertyDao());
    }

    
    private class MockControllableDeviceHelper implements
            StarsControllableDeviceHelper {

        @Override
        public LiteInventoryBase addDeviceToAccount(
                LmDeviceDto deviceInfo, LiteYukonUser user) {
            if (deviceInfo.getAccountNumber().equals(ACCOUNT_NUM_NOT_FOUND)) {
                throw new StarsAccountNotFoundException("Account not found");
            } else if (deviceInfo.getAccountNumber()
                                 .equals(ACCOUNT_NUM_INVALID_ARG)) {
                throw new StarsInvalidArgumentException("Account Number is required");
            } else if (deviceInfo.getAccountNumber().equals(ACCOUNT_NUM_ERROR)) {
                throw new StarsClientRequestException("Client Request Error");
            } else {
                // valid case, return null for now; result is not read back
                return null;
            }
        }

        @Override
        public LiteInventoryBase updateDeviceOnAccount(LmDeviceDto deviceInfo, LiteYukonUser user,
                boolean isEIMRequest) {
            if (deviceInfo.getAccountNumber().equals(ACCOUNT_NUM_NOT_FOUND)) {
                throw new StarsAccountNotFoundException("Account not found");
            } else if (deviceInfo.getAccountNumber()
                                 .equals(ACCOUNT_NUM_INVALID_ARG)) {
                throw new StarsInvalidArgumentException("Account Number is required");
            } else if (deviceInfo.getAccountNumber().equals(ACCOUNT_NUM_ERROR)) {
                throw new StarsClientRequestException("Client Request Error");
            } else {
                // valid case, return null for now; result is not read back
                return null;
            }
        }

        @Override
        public void removeDeviceFromAccount(
                LmDeviceDto deviceInfo, LiteYukonUser user) {
            if (deviceInfo.getAccountNumber().equals(ACCOUNT_NUM_NOT_FOUND)) {
                throw new StarsAccountNotFoundException("Account not found");
            } else if (deviceInfo.getAccountNumber()
                                 .equals(ACCOUNT_NUM_INVALID_ARG)) {
                throw new StarsInvalidArgumentException("Account Number is required");
            } else if (deviceInfo.getAccountNumber().equals(ACCOUNT_NUM_ERROR)) {
                throw new StarsClientRequestException("Client Request Error");
            } else {
                // valid case, no exceptions
            }
        }

        @Override
        public boolean isOperationAllowedForDevice(LmDeviceDto dto, LiteYukonUser user) {
            return true;
        }
    }

    @Test
    public void testInvokeAddDeviceAuthUser() throws Exception {

        // init
        Resource resource = new ClassPathResource(newDeviceReqResource, this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);
        
        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/stars/schemas/NewControllableDevicesRequest.xsd", this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);
        
        //invoke test with authorized user
        Element respElement = impl.invokeAddDevice(reqElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/stars/schemas/NewControllableDevicesResponse.xsd", this.getClass());
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, newDevicesRespStr, XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        List<ControllableDeviceResult> deviceResults = template.evaluate(newDeviceRespElementStr,
                                                                         deviceResultElementMapper);

        // verify data in the response
        assertTrue("Incorrect resultSize", deviceResults != null && deviceResults.size() == 4);
        
        // results verified by positional check, which validates node-mapper as well
        // Not found case
        ControllableDeviceResult deviceResult = deviceResults.get(0);
        assertTrue("Incorrect accountNumber",
                   deviceResult.getAccountNumber().equals(ACCOUNT_NUM_NOT_FOUND));                
        assertTrue("Incorrect serialNumber",
                   deviceResult.getSerialNumber().equals(SERIAL_NUM_NOT_FOUND));
        assertTrue("Success should be false", !deviceResult.isSuccess());
        assertTrue("Incorrect errorCode",
                   deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode()
                                                                          .equals(ACCOUNT_NUM_NOT_FOUND));
        assertTrue("Missing errorDescription", deviceResult.getFailErrorDesc() != null);
        
        //Invalid argument case
        deviceResult = deviceResults.get(1);
        assertTrue("Incorrect accountNumber",
                   deviceResult.getAccountNumber().equals(ACCOUNT_NUM_INVALID_ARG));                
        assertTrue("Incorrect serialNumber",
                   deviceResult.getSerialNumber().equals(SERIAL_NUM_INVALID_ARG));
        assertTrue("Success should be false", !deviceResult.isSuccess());
        assertTrue("Incorrect errorCode",
                   deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode()
                                                                          .equals(ACCOUNT_NUM_INVALID_ARG));
        assertTrue("Missing errorDescription", deviceResult.getFailErrorDesc() != null);
        
        // error case        
        deviceResult = deviceResults.get(2);
        assertTrue("Incorrect accountNumber",
                   deviceResult.getAccountNumber().equals(ACCOUNT_NUM_ERROR));                
        assertTrue("Incorrect serialNumber",
                   deviceResult.getSerialNumber().equals(SERIAL_NUM_ERROR));
        assertTrue("Success should be false", !deviceResult.isSuccess());
        assertTrue("Incorrect errorCode",
                   deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode()
                                                                          .equals(ACCOUNT_NUM_ERROR));
        assertTrue("Missing errorDescription", deviceResult.getFailErrorDesc() != null);
        
        // valid case, no exceptions        
        deviceResult = deviceResults.get(3);        
        assertTrue("Incorrect accountNumber",
                   deviceResult.getAccountNumber().equals(ACCOUNT_NUM_VALID));                
        assertTrue("Incorrect serialNumber",
                   deviceResult.getSerialNumber().equals(SERIAL_NUM_VALID));
        assertTrue("Success should be true", deviceResult.isSuccess());
        assertTrue("Error Code should be blank", StringUtils.isBlank(deviceResult.getFailErrorCode()));
        assertTrue("Error Description should be blank", StringUtils.isBlank(deviceResult.getFailErrorDesc()));
    }

    @Test(expected=NotAuthorizedException.class)
    public void testInvokeAddDeviceUnauthUser() throws Exception {

        // init
        Resource resource = new ClassPathResource(newDeviceReqResource, this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);
        
        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/stars/schemas/NewControllableDevicesRequest.xsd", this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);
        
        //invoke test with unauthorized user
        impl.invokeAddDevice(reqElement, NOT_AUTH_USER);
    }
    
    @Test
    public void testInvokeUpdateDeviceAuthUser() throws Exception {

        // init
        Resource resource = new ClassPathResource(updateDeviceReqResource, this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);
        
        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/stars/schemas/UpdateControllableDevicesRequest.xsd", this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);
        
        //invoke test with authorized user
        Element respElement = impl.invokeUpdateDevice(reqElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/stars/schemas/UpdateControllableDevicesResponse.xsd", this.getClass());
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        // create template and parse response data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, updateDevicesRespStr, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        List<ControllableDeviceResult> deviceResults = template.evaluate(updateDeviceRespElementStr,
                                                                         deviceResultElementMapper);

        // verify data in the response
        assertTrue("Incorrect resultSize", deviceResults != null && deviceResults.size() == 4);
        
        // results verified by positional check, which validates node-mapper as well
        // Not found case
        ControllableDeviceResult deviceResult = deviceResults.get(0);
        assertTrue("Incorrect accountNumber",
                   deviceResult.getAccountNumber().equals(ACCOUNT_NUM_NOT_FOUND));                
        assertTrue("Incorrect serialNumber",
                   deviceResult.getSerialNumber().equals(SERIAL_NUM_NOT_FOUND));
        assertTrue("Success should be false", !deviceResult.isSuccess());
        assertTrue("Incorrect errorCode",
                   deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode()
                                                                          .equals(ACCOUNT_NUM_NOT_FOUND));
        assertTrue("Missing errorDescription", deviceResult.getFailErrorDesc() != null);
        
        //Invalid argument case
        deviceResult = deviceResults.get(1);
        assertTrue("Incorrect accountNumber",
                   deviceResult.getAccountNumber().equals(ACCOUNT_NUM_INVALID_ARG));                
        assertTrue("Incorrect serialNumber",
                   deviceResult.getSerialNumber().equals(SERIAL_NUM_INVALID_ARG));
        assertTrue("Success should be false", !deviceResult.isSuccess());
        assertTrue("Incorrect errorCode",
                   deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode()
                                                                          .equals(ACCOUNT_NUM_INVALID_ARG));
        assertTrue("Missing errorDescription", deviceResult.getFailErrorDesc() != null);
        
        // error case        
        deviceResult = deviceResults.get(2);
        assertTrue("Incorrect accountNumber",
                   deviceResult.getAccountNumber().equals(ACCOUNT_NUM_ERROR));                
        assertTrue("Incorrect serialNumber",
                   deviceResult.getSerialNumber().equals(SERIAL_NUM_ERROR));
        assertTrue("Success should be false", !deviceResult.isSuccess());
        assertTrue("Incorrect errorCode",
                   deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode()
                                                                          .equals(ACCOUNT_NUM_ERROR));
        assertTrue("Missing errorDescription", deviceResult.getFailErrorDesc() != null);
        
        // valid case, no exceptions        
        deviceResult = deviceResults.get(3);        
        assertTrue("Incorrect accountNumber",
                   deviceResult.getAccountNumber().equals(ACCOUNT_NUM_VALID));                
        assertTrue("Incorrect serialNumber",
                   deviceResult.getSerialNumber().equals(SERIAL_NUM_VALID));
        assertTrue("Success should be true", deviceResult.isSuccess());
        assertTrue("Error Code should be blank", StringUtils.isBlank(deviceResult.getFailErrorCode()));
        assertTrue("Error Description should be blank", StringUtils.isBlank(deviceResult.getFailErrorDesc()));
    }
    
    @Test(expected=NotAuthorizedException.class)
    public void testInvokeUpdateDeviceUnauthUser() throws Exception {

        // init
        Resource resource = new ClassPathResource(updateDeviceReqResource, this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);
        
        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/stars/schemas/UpdateControllableDevicesRequest.xsd", this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);
        
        //invoke test with unauthorized user
        impl.invokeUpdateDevice(reqElement, NOT_AUTH_USER);
    }

    @Test
    public void testInvokeRemoveDeviceAuthUser() throws Exception {

        // init
        Resource resource = new ClassPathResource(removeDeviceReqResource, this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);

        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/stars/schemas/RemoveControllableDevicesRequest.xsd", this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);
        
        //invoke test with authorized user
        Element respElement = impl.invokeRemoveDevice(reqElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/stars/schemas/RemoveControllableDevicesResponse.xsd", this.getClass());
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        // create template and parse response data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, removeDevicesRespStr, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        List<ControllableDeviceResult> deviceResults = template.evaluate(removeDeviceRespElementStr,
                                                                         deviceResultElementMapper);

        // verify data in the response
        assertTrue("Incorrect resultSize", deviceResults != null && deviceResults.size() == 4);
        
        // results verified by positional check, which validates node-mapper as well
        // Not found case
        ControllableDeviceResult deviceResult = deviceResults.get(0);
        assertTrue("Incorrect accountNumber",
                   deviceResult.getAccountNumber().equals(ACCOUNT_NUM_NOT_FOUND));                
        assertTrue("Incorrect serialNumber",
                   deviceResult.getSerialNumber().equals(SERIAL_NUM_NOT_FOUND));
        assertTrue("Success should be false", !deviceResult.isSuccess());
        assertTrue("Incorrect errorCode",
                   deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode()
                                                                          .equals(ACCOUNT_NUM_NOT_FOUND));
        assertTrue("Missing errorDescription", deviceResult.getFailErrorDesc() != null);
        
        //Invalid argument case
        deviceResult = deviceResults.get(1);
        assertTrue("Incorrect accountNumber",
                   deviceResult.getAccountNumber().equals(ACCOUNT_NUM_INVALID_ARG));                
        assertTrue("Incorrect serialNumber",
                   deviceResult.getSerialNumber().equals(SERIAL_NUM_INVALID_ARG));
        assertTrue("Success should be false", !deviceResult.isSuccess());
        assertTrue("Incorrect errorCode",
                   deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode()
                                                                          .equals(ACCOUNT_NUM_INVALID_ARG));
        assertTrue("Missing errorDescription", deviceResult.getFailErrorDesc() != null);
        
        // error case        
        deviceResult = deviceResults.get(2);
        assertTrue("Incorrect accountNumber",
                   deviceResult.getAccountNumber().equals(ACCOUNT_NUM_ERROR));                
        assertTrue("Incorrect serialNumber",
                   deviceResult.getSerialNumber().equals(SERIAL_NUM_ERROR));
        assertTrue("Success should be false", !deviceResult.isSuccess());
        assertTrue("Incorrect errorCode",
                   deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode()
                                                                          .equals(ACCOUNT_NUM_ERROR));
        assertTrue("Missing errorDescription", deviceResult.getFailErrorDesc() != null);
        
        // valid case, no exceptions        
        deviceResult = deviceResults.get(3);        
        assertTrue("Incorrect accountNumber",
                   deviceResult.getAccountNumber().equals(ACCOUNT_NUM_VALID));                
        assertTrue("Incorrect serialNumber",
                   deviceResult.getSerialNumber().equals(SERIAL_NUM_VALID));
        assertTrue("Success should be true", deviceResult.isSuccess());
        assertTrue("Error Code should be blank", StringUtils.isBlank(deviceResult.getFailErrorCode()));
        assertTrue("Error Description should be blank", StringUtils.isBlank(deviceResult.getFailErrorDesc()));
    }
    
    @Test(expected=NotAuthorizedException.class)
    public void testInvokeRemoveDeviceUnauthUser() throws Exception {

        // init
        Resource resource = new ClassPathResource(removeDeviceReqResource, this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);

        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/stars/schemas/RemoveControllableDevicesRequest.xsd", this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);
        
        //invoke test with unauthorized user
        impl.invokeRemoveDevice(reqElement, NOT_AUTH_USER);
    }

    private static class ControllableDeviceResultMapper implements
            ObjectMapper<Node, ControllableDeviceResult> {

        @Override
        public ControllableDeviceResult map(Node from) throws ObjectMappingException {
            // create template and parse data
            SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(from);

            ControllableDeviceResult deviceResult = new ControllableDeviceResult();
            deviceResult.setAccountNumber(template.evaluateAsString(accountNumberRespStr));
            deviceResult.setSerialNumber(template.evaluateAsString(serialNumberRespStr));
            deviceResult.setSuccess(template.evaluateAsNode(successStr) != null);
            deviceResult.setFailErrorCode(template.evaluateAsString(failErrorCodeStr));
            deviceResult.setFailErrorDesc(template.evaluateAsString(failErrorDescStr));

            return deviceResult;
        }
    }

    // Test class to test out the Device result elements in the response
    private static class ControllableDeviceResult {

        private String accountNumber;
        private String serialNumber;
        private boolean success;
        private String failErrorCode;
        private String failErrorDesc;

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getFailErrorCode() {
            return failErrorCode;
        }

        public void setFailErrorCode(String failErrorCode) {
            this.failErrorCode = failErrorCode;
        }

        public String getFailErrorDesc() {
            return failErrorDesc;
        }

        public void setFailErrorDesc(String failErrorDesc) {
            this.failErrorDesc = failErrorDesc;
        }
    }
}
