package com.cannontech.yukon.api.stars.endpoint;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Node;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.enrollment.model.EnrolledDevicePrograms;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEventLoggingData;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelperHolder;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.utils.TestUtils;
import com.google.common.collect.Lists;

public class EnrolledDevicesByAccountNumberRequestEndpointTest {

    private static final LiteYukonUser AUTH_USER = MockRolePropertyDao.getAuthorizedUser();

    private Namespace ns;
    private EnrolledDevicesByAccountNumberRequestEndpoint impl;

    private static final String ACCOUNT_NUM_NOT_FOUND = "Not found";
    private static final String ACCOUNT_NUM_EMPTY = "Empty";
    private static final String ACCOUNT_NUM_VALID = "Valid";

    private static EnrolledDeviceMapper enrolledDeviceMapper = new EnrolledDeviceMapper();

    // Response elements
    static final String enrolledDeviceRespElementStr;

    static final String enrolledDevicesRespStr = "/y:enrolledDevicesByAccountNumberResponse";
    static final String enrolledDevicesListStr = "/y:enrolledDevicesList";
    static final String enrolledDeviceStr = "/y:enrolledDevice";
    static final String serialNumberRespStr = "y:serialNumber";
    static final String failErrorCodeStr = enrolledDevicesRespStr + "/y:failure/y:errorCode";
    static final String failErrorDescStr = enrolledDevicesRespStr + "/y:failure/y:errorDescription";

    static {
        enrolledDeviceRespElementStr = enrolledDevicesRespStr + enrolledDevicesListStr + enrolledDeviceStr;
    }

    @Before
    public void setUp() throws Exception {
        ns = YukonXml.getYukonNamespace();
        impl = new EnrolledDevicesByAccountNumberRequestEndpoint();
        impl.setEnrollmentHelperService(new MockEnrollmentHelperService());
    }

    private class MockEnrollmentHelperService implements
            EnrollmentHelperService {

        @Override
        public List<EnrolledDevicePrograms> getEnrolledDeviceProgramsByAccountNumber(
                String accountNumber, Date startDate, Date stopDate,
                LiteYukonUser user) throws AccountNotFoundException {
            if (accountNumber.equals(ACCOUNT_NUM_NOT_FOUND)) {
                throw new AccountNotFoundException("Account not found");
            } else if (accountNumber.equals(ACCOUNT_NUM_EMPTY)) {
                return Collections.EMPTY_LIST;
            } else {
                // valid case, return 1 result now;
                List<EnrolledDevicePrograms> result = Lists.newArrayList();
                result.add(new EnrolledDevicePrograms("Valid",
                    Collections.EMPTY_MAP));
                return result;
            }
        }

        @Override
        public void doEnrollment(EnrollmentHelper enrollmentHelper,
                EnrollmentEnum enrollmentEnum, LiteYukonUser user) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateProgramEnrollments(
                List<ProgramEnrollment> programEnrollments, int accountId,
                YukonUserContext userContext) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public EnrollmentEventLoggingData getEventLoggingData(ProgramEnrollment programEnrollment) {
            throw new UnsupportedOperationException();
        }

		@Override
		public void doEnrollment(
				EnrollmentHelperHolder enrollmentHelperHolder,
				EnrollmentEnum enrollmentEnum, LiteYukonUser user) {
			throw new UnsupportedOperationException();			
		}

        @Override
        public void doEnrollment(EnrollmentHelper enrollmentHelper, EnrollmentEnum enrollmentEnum, LiteYukonUser user,
                CustomerAccount ca) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void makeDisconnectMetersOnAccountEnrollable(int accountId) {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void testInvokeEnrolledDevicesAccountNotFound() throws Exception {

        // init
        Element reqElement = buildRequest(ACCOUNT_NUM_NOT_FOUND);

        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/stars/schemas/EnrolledDevicesByAccountNumberRequest.xsd",
                                                           this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);

        // invoke test with authorized user; NOTE: user doesn't appear to ever actually be read
        Element respElement = impl.invoke(reqElement, AUTH_USER);

        // verify the respElement is valid according to schema
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/stars/schemas/EnrolledDevicesByAccountNumberResponse.xsd",
                                                            this.getClass());
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template,
                                      enrolledDevicesRespStr,
                                      XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        Assert.assertTrue("Should have failed with not found error",
                          template.evaluateAsNode(failErrorCodeStr) != null);
        Assert.assertTrue("Should have failed with not found error",
                          template.evaluateAsNode(failErrorDescStr) != null);
        Assert.assertTrue("Should have failed with not found error",
                          template.evaluateAsString(failErrorCodeStr)
                                  .equals("InvalidAccountNumber"));
    }

    @Test
    public void testInvokeEnrolledDevicesEmptyResult() throws Exception {

        // init
        Element reqElement = buildRequest(ACCOUNT_NUM_EMPTY);

        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/stars/schemas/EnrolledDevicesByAccountNumberRequest.xsd",
                                                           this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);

        // invoke test with authorized user; NOTE: user doesn't appear to ever actually be read
        Element respElement = impl.invoke(reqElement, AUTH_USER);

        // verify the respElement is valid according to schema
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/stars/schemas/EnrolledDevicesByAccountNumberResponse.xsd",
                                                            this.getClass());
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template,
                                      enrolledDevicesRespStr,
                                      XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        Assert.assertTrue("Should have passed with empty result",
                          template.evaluateAsNode(failErrorCodeStr) == null);
        Assert.assertTrue("Should have passed with empty result",
                          template.evaluateAsNode(failErrorDescStr) == null);

        List<EnrolledDevicePrograms> enrolledDevices = template.evaluate(enrolledDeviceRespElementStr,
                                                                         enrolledDeviceMapper);
        // verify data in the response
        assertTrue("Incorrect resultSize",
                   enrolledDevices != null && enrolledDevices.size() == 0);

    }

    @Test
    public void testInvokeEnrolledDevicesWithResult() throws Exception {

        // init
        Element reqElement = buildRequest(ACCOUNT_NUM_VALID);

        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/stars/schemas/EnrolledDevicesByAccountNumberRequest.xsd",
                                                           this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);

        // invoke test with authorized user; NOTE: user doesn't appear to ever actually be read
        Element respElement = impl.invoke(reqElement, AUTH_USER);

        // verify the respElement is valid according to schema
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/stars/schemas/EnrolledDevicesByAccountNumberResponse.xsd",
                                                            this.getClass());
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template,
                                      enrolledDevicesRespStr,
                                      XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        Assert.assertTrue("Should have passed with results",
                          template.evaluateAsNode(failErrorCodeStr) == null);
        Assert.assertTrue("Should have passed with results",
                          template.evaluateAsNode(failErrorDescStr) == null);

        List<EnrolledDevicePrograms> enrolledDevices = template.evaluate(enrolledDeviceRespElementStr,
                                                                         enrolledDeviceMapper);
        // verify data in the response
        assertTrue("Incorrect resultSize",
                   enrolledDevices != null && enrolledDevices.size() == 1);

    }

    private Element buildRequest(String accountNumber) {

        Element requestElement = new Element("enrolledDevicesByAccountNumberRequest",
                                             ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);

        if (accountNumber != null) {
            requestElement.addContent(XmlUtils.createStringElement("accountNumber",
                                                                   ns,
                                                                   accountNumber));
        }
        return requestElement;
    }

    private static class EnrolledDeviceMapper implements
            ObjectMapper<Node, EnrolledDevicePrograms> {

        @Override
        public EnrolledDevicePrograms map(Node from)
                throws ObjectMappingException {
            // create template and parse data
            SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(from);

            EnrolledDevicePrograms deviceResult = new EnrolledDevicePrograms(template.evaluateAsString(serialNumberRespStr),
                                                                             Collections.EMPTY_MAP);

            return deviceResult;
        }
    }
}
