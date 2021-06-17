package com.cannontech.yukon.api.loadManagement;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.model.OptOutEventState;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.energyCompany.model.EnergyCompany.Builder;
import com.cannontech.yukon.api.loadManagement.adapters.CustomerAccountDaoAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.EnergyCompanyDaoAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.LmHardwareBaseDaoAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.OptOutEventDaoAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.OptOutServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.CancelActiveOverrideRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockAccountEventLogService;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.utils.TestUtils;

public class CancelActiveOverrideRequestEndpointTest {
    private static final LiteYukonUser AUTH_USER = MockRolePropertyDao.getAuthorizedUser();
    private static final LiteYukonUser NOT_AUTH_USER = MockRolePropertyDao.getUnAuthorizedUser();
    
    private static final String KNOWN_ACCOUNT_NUMBER = "A";
    private static final String UNKNOWN_ACCOUNT_NUMBER = "B";
    
    private static Namespace ns = YukonXml.getYukonNamespace();
    private static final String RESP_ELEMENT_NAME = "cancelActiveOverrideResponse";
    private static final String REQU_ELEMENT_NAME = "cancelActiveOverrideRequest";
    
    private Resource requestSchemaResource = 
        new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/CancelActiveOverrideRequest.xsd", this.getClass());
    private Resource responseSchemaResource = 
        new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/CancelActiveOverrideResponse.xsd", this.getClass());
 
    private CancelActiveOverrideRequestEndpoint impl;
    
    private enum TestInventory {
        OPTED_OUT(1, true),
        NOT_OPTED_OUT(2, false),
        UNKNOWN (3, false);
     
        private int inventoryId;
        
        private boolean isOptedOut;

        private TestInventory(int inventoryId, boolean isOptedOut) {
            this.inventoryId = inventoryId;
            this.isOptedOut = isOptedOut;
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
    
    @BeforeEach
    public void setUp() throws Exception {
        impl = new CancelActiveOverrideRequestEndpoint();
        
        impl.setCustomerAccountDao(new MockCustomerAccountDao());
        impl.setLmHardwareBaseDao(new MockLmHardwareBaseDao());
        impl.setOptOutService(new MockOptOutService());
        impl.setRolePropertyDao(new MockRolePropertyDao());
        impl.setEnergyCompanyDao(new MockEnergyCompanyDao());
        impl.setAccountEventLogService(new MockAccountEventLogService());
        impl.setOptOutEventDao(new MockOptOutEventDao());
    }
    
    @Test
    public void testInvoke() throws Exception {

        // test with unauthorized user
        // ========================================================================================
        validate(KNOWN_ACCOUNT_NUMBER, TestInventory.OPTED_OUT, NOT_AUTH_USER, "UserNotAuthorized", false);
        
        // test with unknown account number
        // ========================================================================================    
        validate(UNKNOWN_ACCOUNT_NUMBER, TestInventory.OPTED_OUT, AUTH_USER, "InvalidAccountNumber", false);
        
        // test with unknown serial number
        // ========================================================================================       
        validate(KNOWN_ACCOUNT_NUMBER, TestInventory.UNKNOWN, AUTH_USER, "InvalidSerialNumber", false);
        
        // test with opted out device
        // ========================================================================================
        validate(KNOWN_ACCOUNT_NUMBER, TestInventory.OPTED_OUT, AUTH_USER, null, true);
        
        // test with not opted out device
        // ========================================================================================
        validate(KNOWN_ACCOUNT_NUMBER, TestInventory.NOT_OPTED_OUT, AUTH_USER, "CancelFailed", false);
    }
    
    
    private void validate(String accountNumber, TestInventory inventory, LiteYukonUser user, String expectedResponse, boolean expectedSuccess) 
    throws Exception {
        Element requestElement = createRequest(accountNumber, inventory.toString());
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
    
    private Element createRequest(String accountNumber, String serialNumber) {
        Element requestElement = new Element(REQU_ELEMENT_NAME, ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);

        Element elem_AccountNumber = XmlUtils.createStringElement("accountNumber", ns, accountNumber);
        requestElement.addContent(elem_AccountNumber);
        Element elem_serialNumber = XmlUtils.createStringElement("serialNumber", ns, serialNumber);
        requestElement.addContent(elem_serialNumber);
        
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
    
    private class MockCustomerAccountDao extends CustomerAccountDaoAdapter {
        @Override
        public CustomerAccount getByAccountNumber(String accountNumber,
                Iterable<? extends YukonEnergyCompany> energyCompany) {
            if (accountNumber.equals(UNKNOWN_ACCOUNT_NUMBER)) {
                throw new NotFoundException("Unknown account");
            }
            CustomerAccount account = new CustomerAccount();
            account.setAccountId(1);
            account.setAccountNumber(accountNumber);
            return account;
        }
        
    }
    
    private class MockEnergyCompanyDao extends EnergyCompanyDaoAdapter {

        @Override
        public EnergyCompany getEnergyCompanyByOperator(LiteYukonUser operator) {
            Builder ecBuilder = new EnergyCompany.Builder();
            ecBuilder.addEnergyCompany(1, "test energy company", new LiteYukonUser(122, "yukon") , 1, null);
            return ecBuilder.build().get(1);
        }
    }
    
    private class MockOptOutService extends OptOutServiceAdapter {
        @Override
        public void cancelOptOut(List<Integer>eventIdList, LiteYukonUser user) {
            // Don't need to do no nothing but don't need to throw no exception neither.
        }
    }
    
    private class MockOptOutEventDao extends OptOutEventDaoAdapter {
        
        @Override
        public List<OptOutEventDto> getCurrentOptOuts(int customerAccountId, int inventoryId) {
            TestInventory test = TestInventory.getInventoryForId(inventoryId);
            ArrayList<OptOutEventDto> events = new ArrayList<OptOutEventDto>();
            if (test.isOptedOut) {
                OptOutEventDto event = new OptOutEventDto();
                event.setState(OptOutEventState.START_OPT_OUT_SENT);
                events.add(event);
            }
            
            return events;
        }
    }
}
