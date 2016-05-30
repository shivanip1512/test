package com.cannontech.yukon.api.loadManagement;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
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
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.EnergyCompany.Builder;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.yukon.api.loadManagement.adapters.CustomerAccountDaoAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.EnergyCompanyDaoAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.LmHardwareBaseDaoAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.OptOutServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.OverrideRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockAccountEventLogService;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.utils.TestUtils;

public class OverrideRequestEndpointTest {
    
    private static final String KNOWN_ACCOUNT_NUMBER_OPTOUTS_AVAILABLE = "A";
    private static final String KNOWN_ACCOUNT_NUMBER_NO_OPTOUTS_AVAILABLE = "B";
    private static final String UNKNOWN_ACCOUNT_NUMBER = "C";
    
    private static final LiteYukonUser AUTH_USER = MockRolePropertyDao.getAuthorizedUser();
    private static final LiteYukonUser NOT_AUTH_USER = MockRolePropertyDao.getUnAuthorizedUser();
    
    private static Namespace ns = YukonXml.getYukonNamespace();
    private static final String RESP_ELEMENT_NAME = "overrideResponse";
    private static final String REQU_ELEMENT_NAME = "overrideRequest";
    
    private Resource requestSchemaResource = 
        new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/OverrideRequest.xsd", this.getClass());
    private Resource responseSchemaResource = 
        new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/OverrideResponse.xsd", this.getClass());
 
    private OverrideRequestEndpoint impl;
    
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
        impl = new OverrideRequestEndpoint();
        
        impl.setCustomerAccountDao(new MockCustomerAccountDao());
        impl.setLmHardwareBaseDao(new MockLmHardwareBaseDao());
        impl.setOptOutService(new MockOptOutService());
        impl.setRolePropertyDao(new MockRolePropertyDao());
        impl.setEnergyCompanyDao(new MockEnergyCompanyDao());
        impl.setAccountEventLogService(new MockAccountEventLogService());
    }
    
    @Test
    public void testInvoke() throws Exception {
        
        // test with unauthorized user
        // ========================================================================================
        validate(KNOWN_ACCOUNT_NUMBER_OPTOUTS_AVAILABLE, TestInventory.NOT_OPTED_OUT_AND_NOT_SCHEDULED, 
                 null, 1, null, NOT_AUTH_USER, "UserNotAuthorized", false);
       
        // test with unknown account number
        // ========================================================================================
        validate(UNKNOWN_ACCOUNT_NUMBER, TestInventory.NOT_OPTED_OUT_AND_NOT_SCHEDULED, 
                 null, 1, null, AUTH_USER, "InvalidAccountNumber", false);
        
        // test with unknown serial number
        // ========================================================================================
        validate(KNOWN_ACCOUNT_NUMBER_OPTOUTS_AVAILABLE, TestInventory.UNKNOWN, 
                 null, 1, null, AUTH_USER, "InvalidSerialNumber", false);
        
        // test passed start time
        // ======================================================================================== 
        validate(KNOWN_ACCOUNT_NUMBER_OPTOUTS_AVAILABLE, TestInventory.NOT_OPTED_OUT_AND_NOT_SCHEDULED, 
                 "2011-02-20T10:00:00Z", 1, null, AUTH_USER, "InvalidStartDate", false);
        
        // test instant opt out on not opted out device
        // ======================================================================================== 
        validate(KNOWN_ACCOUNT_NUMBER_OPTOUTS_AVAILABLE, TestInventory.NOT_OPTED_OUT_AND_NOT_SCHEDULED, 
                 null, 1, null, AUTH_USER, null, true);
      
        // test instant opt out on opted out device
        // ======================================================================================== 
        validate(KNOWN_ACCOUNT_NUMBER_OPTOUTS_AVAILABLE, TestInventory.OPTED_OUT_AND_NOT_SCHEDULED, 
                 null, 1, null, AUTH_USER, "OverrideAlreadyActive", false);
        
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);//2012-01-01T10:00:00Z
        // test schedule opt out on device with no schedules
        // ========================================================================================
        validate(KNOWN_ACCOUNT_NUMBER_OPTOUTS_AVAILABLE, TestInventory.OPTED_OUT_AND_NOT_SCHEDULED, 
                 fmt.format((new DateTime()).plusDays(1).toDate()), 1, null, AUTH_USER, null, true);
        
        // test schedule opt out on device with schedule
        // ======================================================================================== 
        validate(KNOWN_ACCOUNT_NUMBER_OPTOUTS_AVAILABLE, TestInventory.OPTED_OUT_AND_SCHEDULED, 
                 fmt.format((new DateTime()).plusDays(1).toDate()), 1, null, AUTH_USER, "OverrideAlreadyScheduled", false);
        
        // test counting opting out on account with no opt outs remaining 
        // ======================================================================================== 
        validate(KNOWN_ACCOUNT_NUMBER_NO_OPTOUTS_AVAILABLE, TestInventory.NOT_OPTED_OUT_AND_NOT_SCHEDULED, 
                 null, 1, null, AUTH_USER, "OverrideLimitReached", false);
    }
    
    private void validate(String accountNumber, TestInventory inventory, String startDate, long durationInHours, 
                          Boolean counts, LiteYukonUser user, String expectedResponse, boolean expectedSuccess) 
    throws Exception {
        Element requestElement = createRequest(accountNumber, inventory.toString(), startDate, durationInHours, counts);
        SimpleXPathTemplate responseTemplate = validateAndReturnResponse(requestElement, user);
        if(expectedSuccess) {
            TestUtils.runSuccessAssertion(responseTemplate, RESP_ELEMENT_NAME);
        } else {
            TestUtils.runFailureAssertions(responseTemplate, RESP_ELEMENT_NAME, expectedResponse);
        }
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
        public CustomerAccount getByAccountNumber(String accountNumber, 
                                                  Iterable<? extends YukonEnergyCompany> energyCompany) {
            if (accountNumber.equals(UNKNOWN_ACCOUNT_NUMBER)) {
                throw new NotFoundException("Unknown account");
            }
            MockCustomerAccount account = new MockCustomerAccount();
            account.setAccountId(1);
            account.setAccountNumber(accountNumber);
            if (accountNumber.equals(KNOWN_ACCOUNT_NUMBER_NO_OPTOUTS_AVAILABLE)) {
                account.hasOptOutsLeft = false;
            }
            return account;
        }
    }
    
    private class MockEnergyCompanyDao extends EnergyCompanyDaoAdapter{

        @Override
        public EnergyCompany getEnergyCompanyByOperator(LiteYukonUser operator) {
            Builder ecBuilder = new EnergyCompany.Builder();
            ecBuilder.addEnergyCompany(1, "test energy company", new LiteYukonUser(122, "yukon") , 1, null);
            return ecBuilder.build().get(1);
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
