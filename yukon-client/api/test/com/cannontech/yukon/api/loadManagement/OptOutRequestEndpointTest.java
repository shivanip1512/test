package com.cannontech.yukon.api.loadManagement;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.optout.exception.AlreadyOptedOutException;
import com.cannontech.stars.dr.optout.exception.InvalidOptOutStartDateException;
import com.cannontech.stars.dr.optout.exception.OptOutAlreadyScheduledException;
import com.cannontech.stars.dr.optout.exception.OptOutCountLimitException;
import com.cannontech.stars.dr.optout.exception.OptOutException;
import com.cannontech.stars.dr.optout.model.OptOutCounts;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.yukon.api.loadManagement.adapters.CustomerAccountDaoAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.LmHardwareBaseDaoAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.OptOutServiceAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.YukonEnergyCompanyServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.OptOutRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockAccountEventLogService;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.util.YukonXml;
import com.cannontech.yukon.api.utils.TestUtils;

public class OptOutRequestEndpointTest {
    
    private static final String KNOWN_ACCOUNT_NUMBER_OPTOUTS_AVAILABLE = "A";
    private static final String KNOWN_ACCOUNT_NUMBER_NO_OPTOUTS_AVAILABLE = "B";
    private static final String UNKNOWN_ACCOUNT_NUMBER = "C";
    
    private static Namespace ns = YukonXml.getYukonNamespace();
    private static final String RESP_ELEMENT_NAME = "optOutResponse";
    private static final String REQU_ELEMENT_NAME = "optOutRequest";
    
    private Resource requestSchemaResource = 
        new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/OptOutRequest.xsd", this.getClass());
    private Resource responseSchemaResource = 
        new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/OptOutResponse.xsd", this.getClass());
 
    private OptOutRequestEndpoint impl;
    
    private enum TestInventory {
        OPTED_OUT_AND_SCHEDULED (1, true, true), 
        OPTED_OUT_AND_NOT_SCHEDULED (2, true, false),
        NOT_OPTED_OUT_AND_SCHEDULED (3, false, true),
        NOT_OPTED_OUT_AND_NOT_SCHEDULED (4, false, false),
        UNKNOWN (5, false, false);
     
        private int inventoryId;
        
        private boolean isOptedOut;
        private boolean isScheduled;

        private TestInventory(int inventoryId, boolean isOptedOut, boolean isScheduled) {
            this.inventoryId = inventoryId;
            this.isOptedOut = isOptedOut;
            this.isScheduled = isScheduled;
        }
        
        private static TestInventory getInventoryForId(int inventoryId) {
            for (TestInventory test : values()){
                if (test.inventoryId == inventoryId) {
                    return test;
                }
            }
            return null;
        }
    }
    
    @Before
    public void setUp() throws Exception {
        impl = new OptOutRequestEndpoint();
        
        impl.setCustomerAccountDao(new MockCustomerAccountDao());
        impl.setLmHardwareBaseDao(new MockLmHardwareBaseDao());
        impl.setOptOutService(new MockOptOutService());
        impl.setRolePropertyDao(new MockRolePropertyDao());
        impl.setYukonEnergyCompanyService(new MockYukonEnergyCompanyService());
        impl.setAccountEventLogService(new MockAccountEventLogService());
    }
    
    @Test
    public void testInvoke() throws Exception {
        Element requestElement = null;
        SimpleXPathTemplate outputTemplate = null;
        LiteYukonUser user = new LiteYukonUser();
  
        // test with unauthorized user
        // ========================================================================================
        requestElement = createRequest(KNOWN_ACCOUNT_NUMBER_OPTOUTS_AVAILABLE, TestInventory.NOT_OPTED_OUT_AND_NOT_SCHEDULED.toString(), null, 1, null);
        outputTemplate = validateAndReturnResponse(requestElement, MockRolePropertyDao.getUnAuthorizedUser());
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "UserNotAuthorized");
        
        // test with unknown account number
        // ========================================================================================       
        requestElement = createRequest(UNKNOWN_ACCOUNT_NUMBER, TestInventory.NOT_OPTED_OUT_AND_NOT_SCHEDULED.toString(), null, 1, null);
        outputTemplate = validateAndReturnResponse(requestElement, user);
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "NotFound");
        
        // test with unknown serial number
        // ========================================================================================       
        requestElement = createRequest(KNOWN_ACCOUNT_NUMBER_OPTOUTS_AVAILABLE, TestInventory.UNKNOWN.toString(), null, 1, null);
        outputTemplate = validateAndReturnResponse(requestElement, user);
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "NotFound");
        
        // test passed start time
        // ======================================================================================== 
        requestElement = createRequest(KNOWN_ACCOUNT_NUMBER_OPTOUTS_AVAILABLE, TestInventory.NOT_OPTED_OUT_AND_NOT_SCHEDULED.toString(), 
                                       "2011-02-20T10:00:00Z", 1, null);
        outputTemplate = validateAndReturnResponse(requestElement, user);
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "InvalidStartDate");
        
        // test instant opt out on not opted out device
        // ======================================================================================== 
        requestElement = createRequest(KNOWN_ACCOUNT_NUMBER_OPTOUTS_AVAILABLE, TestInventory.NOT_OPTED_OUT_AND_NOT_SCHEDULED.toString(), 
                                       null, 1, null);
        outputTemplate = validateAndReturnResponse(requestElement, user);
        TestUtils.runSuccessAssertion(outputTemplate, RESP_ELEMENT_NAME);
        
        // test instant opt out on opted out device
        // ======================================================================================== 
        requestElement = createRequest(KNOWN_ACCOUNT_NUMBER_OPTOUTS_AVAILABLE, TestInventory.OPTED_OUT_AND_NOT_SCHEDULED.toString(), 
                                       null, 1, null);
        outputTemplate = validateAndReturnResponse(requestElement, user);
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "DeviceAlreadyOptedOut");
        
        // test schedule opt out on device with no schedules
        // ======================================================================================== 
        requestElement = createRequest(KNOWN_ACCOUNT_NUMBER_OPTOUTS_AVAILABLE, TestInventory.OPTED_OUT_AND_NOT_SCHEDULED.toString(), 
                                       "2012-01-01T10:00:00Z", 1, null);
        outputTemplate = validateAndReturnResponse(requestElement, user);
        TestUtils.runSuccessAssertion(outputTemplate, RESP_ELEMENT_NAME);
        
        // test schedule opt out on device with schedule
        // ======================================================================================== 
        requestElement = createRequest(KNOWN_ACCOUNT_NUMBER_OPTOUTS_AVAILABLE, TestInventory.OPTED_OUT_AND_SCHEDULED.toString(), 
                                       "2012-01-01T10:00:00Z", 1, null);
        outputTemplate = validateAndReturnResponse(requestElement, user);
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "OptOutAlreadyScheduled");
        
        // test counting opting out on account with no opt outs remaining 
        // ========================================================================================   
        requestElement = createRequest(KNOWN_ACCOUNT_NUMBER_NO_OPTOUTS_AVAILABLE, TestInventory.NOT_OPTED_OUT_AND_NOT_SCHEDULED.toString(), 
                                       null, 1, null);
        outputTemplate = validateAndReturnResponse(requestElement, user);
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "OptOutLimitReached");
       
    }
    
    private SimpleXPathTemplate validateAndReturnResponse(Element requestElement, LiteYukonUser user) throws Exception{
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);

        Element responseElement = impl.invoke(requestElement, user);
        
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        return YukonXml.getXPathTemplateForElement(responseElement);
        
    }
    
    private Element createRequest(String accountNumber, String serialNumber, String startDate, long durationInHours, Boolean counts) {
        Element requestElement = new Element(REQU_ELEMENT_NAME, ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);

        Element elem_AccountNumber = XmlUtils.createStringElement("accountNumber", ns, accountNumber);
        requestElement.addContent(elem_AccountNumber);
        Element elem_serialNumber = XmlUtils.createStringElement("serialNumber", ns, serialNumber);
        requestElement.addContent(elem_serialNumber);
        if (startDate != null) {
            Element elem_startDate = XmlUtils.createStringElement("startDate", ns, startDate);
            requestElement.addContent(elem_startDate);
        }
        
        Element elem_durationInHours = XmlUtils.createLongElement("durationInHours", ns, durationInHours);
        requestElement.addContent(elem_durationInHours);

        
        if (counts != null) {
            Element elem_Counts = XmlUtils.createBooleanElement("counts", ns, counts);
            requestElement.addContent(elem_Counts);
        }
        
        return requestElement;
    }
    

    
    private class MockLmHardwareBaseDao extends LmHardwareBaseDaoAdapter {

        @Override
        public LMHardwareBase getBySerialNumber(String serialNumber) throws DataAccessException {
            TestInventory test = TestInventory.valueOf(serialNumber);
            if (test == null || test == TestInventory.UNKNOWN) {
                throw new NotFoundException("Unknown Inventory");
            }

            LMHardwareBase hardware = new LMHardwareBase();
            hardware.setInventoryId(test.inventoryId);
            hardware.setManufacturerSerialNumber(serialNumber);
            return hardware;
        }

    }
    
    private class MockCustomerAccount extends CustomerAccount {
        private boolean hasOptOutsLeft = true;
    }
    
    private class MockCustomerAccountDao extends CustomerAccountDaoAdapter {

        @Override
        public CustomerAccount getByAccountNumberForDescendentsOfEnergyCompany(String accountNumber,
                                                                               YukonEnergyCompany energyCompany) {
            if (accountNumber.equals(UNKNOWN_ACCOUNT_NUMBER)) {
                throw new NotFoundException("Unknown account");
            } else {
                MockCustomerAccount account = new MockCustomerAccount();
                account.setAccountId(1);
                account.setAccountNumber(accountNumber);
                if (accountNumber.equals(KNOWN_ACCOUNT_NUMBER_NO_OPTOUTS_AVAILABLE)) {
                    account.hasOptOutsLeft = false;
                }
                return account;
            }
        }

    }
    
    private class MockYukonEnergyCompanyService extends YukonEnergyCompanyServiceAdapter{

        @Override
        public YukonEnergyCompany getEnergyCompanyByOperator(LiteYukonUser operator) {
            return null;
        }
    }
    
    private class MockOptOutService extends OptOutServiceAdapter {
        
        @Override
        public void optOutWithValidation(CustomerAccount customerAccount, OptOutRequest request,
                                              LiteYukonUser user, OptOutCounts optOutCounts)
                throws CommandCompletionException, OptOutException {
            TestInventory test = TestInventory.getInventoryForId(request.getInventoryIdList().get(0));
            
            
            if (request.getStartDate() != null) {
                Instant currentTime = new Instant();
                Instant startTime = new Instant(request.getStartDate());

                if (startTime.isBefore(currentTime)) {
                    throw new InvalidOptOutStartDateException();
                }
            }
            
            if (request.getStartDate() == null && test.isOptedOut) {
                throw new AlreadyOptedOutException(test.toString());
            }
            
            if (request.getStartDate() != null && test.isScheduled) {
                throw new OptOutAlreadyScheduledException(test.toString());
            }
            
            MockCustomerAccount mockAccount = (MockCustomerAccount) customerAccount;
            
            if (optOutCounts == OptOutCounts.COUNT && !mockAccount.hasOptOutsLeft) {
                throw new OptOutCountLimitException(test.toString());
            }
        }
    }
    
    

 
}
