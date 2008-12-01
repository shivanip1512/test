package com.cannontech.yukon.api.stars;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.stars.dr.account.exception.StarsAccountNotFoundException;
import com.cannontech.stars.util.StarsInvalidArgumentException;
import com.cannontech.stars.ws.dto.StarsControllableDeviceDTO;
import com.cannontech.stars.ws.helper.StarsControllableDeviceHelper;
import com.cannontech.yukon.api.stars.ControllableDevicesRequestEndPoint.ErrorCodeMapper;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;

public class ControllableDevicesRequestEndPointTest {

    private ControllableDevicesRequestEndPoint impl;
    private StarsControllableDeviceHelper mockHelper;

    // Test Resource files for the Request xml
    private static final String newDeviceReqResource = "ControllableDevicesRequestNew.xml";
    private static final String updateDeviceReqResource = "ControllableDevicesRequestUpdate.xml";
    private static final String removeDeviceReqResource = "ControllableDevicesRequestRemove.xml";

    private static final String ACCOUNT_NUM_NOT_FOUND = ErrorCodeMapper.AccountNotFound.name();
    private static final String ACCOUNT_NUM_INVALID_ARG = ErrorCodeMapper.InvalidArgument.name();
    private static final String ACCOUNT_NUM_ERROR = ErrorCodeMapper.ProcessingError.name();
    private static final String ACCOUNT_NUM_VALID = "Valid";

    private static final String SERIAL_NUM_NOT_FOUND = ErrorCodeMapper.AccountNotFound.name();
    private static final String SERIAL_NUM_INVALID_ARG = ErrorCodeMapper.InvalidArgument.name();
    private static final String SERIAL_NUM_ERROR = ErrorCodeMapper.ProcessingError.name();
    private static final String SERIAL_NUM_VALID = "Valid";

    private static NodeToElementMapperWrapper<ControllableDeviceResult> deviceResultElementMapper;

    // Response elements
    static final String newDeviceRespElementStr;
    static final String updateDeviceRespElementStr;
    static final String removeDeviceRespElementStr;

    static final String RESP_XML_VERSION_1_0 = "1.0";    
    static final String newDevicesRespStr = "/y:newControllableDevicesResponse";
    static final String updateDevicesRespStr = "/y:updateControllableDevicesResponse";
    static final String removeDevicesRespStr = "/y:removeControllableDevicesResponse";
    static final String respVersionStr = "//@version";    
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
        
        deviceResultElementMapper = new NodeToElementMapperWrapper<ControllableDeviceResult>(new ControllableDeviceResultMapper());        
    }

    @Before
    public void setUp() throws Exception {
        mockHelper = new MockControllableDeviceHelper();

        impl = new ControllableDevicesRequestEndPoint();
        impl.setStarsControllableDeviceHandler(mockHelper);
        impl.initialize();
    }

    private class MockControllableDeviceHelper implements
            StarsControllableDeviceHelper {

        @Override
        public LiteInventoryBase addDeviceToAccount(
                StarsControllableDeviceDTO deviceInfo, LiteYukonUser user) {
            if (deviceInfo.getAccountNumber().equals(ACCOUNT_NUM_NOT_FOUND)) {
                throw new StarsAccountNotFoundException("Account not found");
            } else if (deviceInfo.getAccountNumber()
                                 .equals(ACCOUNT_NUM_INVALID_ARG)) {
                throw new StarsInvalidArgumentException("Account Number is required");
            } else if (deviceInfo.getAccountNumber().equals(ACCOUNT_NUM_ERROR)) {
                throw new RuntimeException("Processing Error");
            } else {
                // valid case, return null for now; result is not read back
                return null;
            }
        }

        @Override
        public LiteInventoryBase updateDeviceOnAccount(
                StarsControllableDeviceDTO deviceInfo, LiteYukonUser user) {
            if (deviceInfo.getAccountNumber().equals(ACCOUNT_NUM_NOT_FOUND)) {
                throw new StarsAccountNotFoundException("Account not found");
            } else if (deviceInfo.getAccountNumber()
                                 .equals(ACCOUNT_NUM_INVALID_ARG)) {
                throw new StarsInvalidArgumentException("Account Number is required");
            } else if (deviceInfo.getAccountNumber().equals(ACCOUNT_NUM_ERROR)) {
                throw new RuntimeException("Processing Error");
            } else {
                // valid case, return null for now; result is not read back
                return null;
            }
        }

        @Override
        public void removeDeviceFromAccount(
                StarsControllableDeviceDTO deviceInfo, LiteYukonUser user) {
            if (deviceInfo.getAccountNumber().equals(ACCOUNT_NUM_NOT_FOUND)) {
                throw new StarsAccountNotFoundException("Account not found");
            } else if (deviceInfo.getAccountNumber()
                                 .equals(ACCOUNT_NUM_INVALID_ARG)) {
                throw new StarsInvalidArgumentException("Account Number is required");
            } else if (deviceInfo.getAccountNumber().equals(ACCOUNT_NUM_ERROR)) {
                throw new RuntimeException("Processing Error");
            } else {
                // valid case, no exceptions
            }
        }
    }

    @Test
    public void testInvokeAddDevice() throws Exception {

        // init
        Resource resource = new ClassPathResource(newDeviceReqResource, this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);  

        Element respElement = impl.invokeAddDevice(reqElement, null);

        // create template and parse response data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(respElement);
        assertTrue(template.evaluateAsString(respVersionStr).equals(RESP_XML_VERSION_1_0));
        List<ControllableDeviceResult> deviceResults = template.evaluate(newDeviceRespElementStr,
                                                                         deviceResultElementMapper);

        // verify data in the response
        assertTrue(deviceResults != null && deviceResults.size() == 4);
        for (ControllableDeviceResult deviceResult : deviceResults) {
            if (deviceResult.getAccountNumber().equals(ACCOUNT_NUM_NOT_FOUND)) {

                assertTrue(deviceResult.getSerialNumber()
                                       .equals(SERIAL_NUM_NOT_FOUND));
                assertTrue(deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode().equals(ACCOUNT_NUM_NOT_FOUND));
                assertTrue(deviceResult.getFailErrorDesc() != null);
            } else if (deviceResult.getAccountNumber()
                                   .equals(ACCOUNT_NUM_INVALID_ARG)) {
                assertTrue(deviceResult.getSerialNumber()
                                       .equals(SERIAL_NUM_INVALID_ARG));
                assertTrue(deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode().equals(ACCOUNT_NUM_INVALID_ARG));
                assertTrue(deviceResult.getFailErrorDesc() != null);

            } else if (deviceResult.getAccountNumber()
                                   .equals(ACCOUNT_NUM_ERROR)) {
                assertTrue(deviceResult.getSerialNumber()
                                       .equals(SERIAL_NUM_ERROR));
                assertTrue(deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode().equals(ACCOUNT_NUM_ERROR));
                assertTrue(deviceResult.getFailErrorDesc() != null);
            } else if (deviceResult.getAccountNumber()
                                   .equals(ACCOUNT_NUM_VALID)) {
                // valid case, no exceptions
                assertTrue(deviceResult.getSerialNumber()
                                       .equals(SERIAL_NUM_VALID));
                assertTrue(deviceResult.getSuccess() != null);
                assertTrue(StringUtils.isBlank(deviceResult.getFailErrorCode()));
                assertTrue(StringUtils.isBlank(deviceResult.getFailErrorDesc()));
            }
        }
    }

    @Test
    public void testInvokeUpdateDevice() throws Exception {

        // init
        Resource resource = new ClassPathResource(updateDeviceReqResource, this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);

        Element respElement = impl.invokeUpdateDevice(reqElement, null);

        // create template and parse response data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(respElement);
        assertTrue(template.evaluateAsString(respVersionStr).equals(RESP_XML_VERSION_1_0));        
        List<ControllableDeviceResult> deviceResults = template.evaluate(updateDeviceRespElementStr,
                                                                         deviceResultElementMapper);

        // verify data in the response
        assertTrue(deviceResults != null && deviceResults.size() == 4);
        for (ControllableDeviceResult deviceResult : deviceResults) {
            if (deviceResult.getAccountNumber().equals(ACCOUNT_NUM_NOT_FOUND)) {

                assertTrue(deviceResult.getSerialNumber()
                                       .equals(SERIAL_NUM_NOT_FOUND));
                assertTrue(deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode().equals(ACCOUNT_NUM_NOT_FOUND));
                assertTrue(deviceResult.getFailErrorDesc() != null);
            } else if (deviceResult.getAccountNumber()
                                   .equals(ACCOUNT_NUM_INVALID_ARG)) {
                assertTrue(deviceResult.getSerialNumber()
                                       .equals(SERIAL_NUM_INVALID_ARG));
                assertTrue(deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode().equals(ACCOUNT_NUM_INVALID_ARG));
                assertTrue(deviceResult.getFailErrorDesc() != null);

            } else if (deviceResult.getAccountNumber()
                                   .equals(ACCOUNT_NUM_ERROR)) {
                assertTrue(deviceResult.getSerialNumber()
                                       .equals(SERIAL_NUM_ERROR));
                assertTrue(deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode().equals(ACCOUNT_NUM_ERROR));
                assertTrue(deviceResult.getFailErrorDesc() != null);
            } else if (deviceResult.getAccountNumber()
                                   .equals(ACCOUNT_NUM_VALID)) {
                // valid case, no exceptions
                assertTrue(deviceResult.getSerialNumber()
                                       .equals(SERIAL_NUM_VALID));
                assertTrue(deviceResult.getSuccess() != null);
                assertTrue(StringUtils.isBlank(deviceResult.getFailErrorCode()));
                assertTrue(StringUtils.isBlank(deviceResult.getFailErrorDesc()));
            }
        }
    }

    @Test
    public void testInvokeRemoveDevice() throws Exception {

        // init
        Resource resource = new ClassPathResource(removeDeviceReqResource, this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);

        Element respElement = impl.invokeRemoveDevice(reqElement, null);

        // create template and parse response data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(respElement);
        assertTrue(template.evaluateAsString(respVersionStr).equals(RESP_XML_VERSION_1_0));        
        List<ControllableDeviceResult> deviceResults = template.evaluate(removeDeviceRespElementStr,
                                                                         deviceResultElementMapper);

        // verify data in the response
        assertTrue(deviceResults != null && deviceResults.size() == 4);
        for (ControllableDeviceResult deviceResult : deviceResults) {
            if (deviceResult.getAccountNumber().equals(ACCOUNT_NUM_NOT_FOUND)) {

                assertTrue(deviceResult.getSerialNumber()
                                       .equals(SERIAL_NUM_NOT_FOUND));
                assertTrue(deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode().equals(ACCOUNT_NUM_NOT_FOUND));
                assertTrue(deviceResult.getFailErrorDesc() != null);
            } else if (deviceResult.getAccountNumber()
                                   .equals(ACCOUNT_NUM_INVALID_ARG)) {
                assertTrue(deviceResult.getSerialNumber()
                                       .equals(SERIAL_NUM_INVALID_ARG));
                assertTrue(deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode().equals(ACCOUNT_NUM_INVALID_ARG));
                assertTrue(deviceResult.getFailErrorDesc() != null);

            } else if (deviceResult.getAccountNumber()
                                   .equals(ACCOUNT_NUM_ERROR)) {
                assertTrue(deviceResult.getSerialNumber()
                                       .equals(SERIAL_NUM_ERROR));
                assertTrue(deviceResult.getFailErrorCode() != null && deviceResult.getFailErrorCode().equals(ACCOUNT_NUM_ERROR));
                assertTrue(deviceResult.getFailErrorDesc() != null);
            } else if (deviceResult.getAccountNumber()
                                   .equals(ACCOUNT_NUM_VALID)) {
                // valid case, no exceptions
                assertTrue(deviceResult.getSerialNumber()
                                       .equals(SERIAL_NUM_VALID));
                assertTrue(deviceResult.getSuccess() != null);
                assertTrue(StringUtils.isBlank(deviceResult.getFailErrorCode()));
                assertTrue(StringUtils.isBlank(deviceResult.getFailErrorDesc()));
            }
        }
    }

    private static class ControllableDeviceResultMapper implements
            ObjectMapper<Element, ControllableDeviceResult> {

        @Override
        public ControllableDeviceResult map(Element from) throws ObjectMappingException {
            // create template and parse data
            SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(from);

            ControllableDeviceResult deviceResult = new ControllableDeviceResult();
            deviceResult.setAccountNumber(template.evaluateAsString(accountNumberRespStr));
            deviceResult.setSerialNumber(template.evaluateAsString(serialNumberRespStr));
            deviceResult.setSuccess(template.evaluateAsString(successStr));
            deviceResult.setFailErrorCode(template.evaluateAsString(failErrorCodeStr));
            deviceResult.setFailErrorDesc(template.evaluateAsString(failErrorDescStr));

            return deviceResult;
        }
    }

    // Test class to test out the Device result elements in the response
    private static class ControllableDeviceResult {

        private String accountNumber;
        private String serialNumber;
        private String success;
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

        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
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
