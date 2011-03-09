package com.cannontech.yukon.api.loadManagement;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.model.OptOutEventState;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.yukon.api.loadManagement.adapters.CustomerAccountDaoAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.LmHardwareBaseDaoAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.OptOutEventDaoAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.OptOutServiceAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.YukonEnergyCompanyServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.CancelActiveOptOutRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockAccountEventLogService;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlApiUtils;
import com.cannontech.yukon.api.util.YukonXml;
import com.cannontech.yukon.api.utils.TestUtils;

public class CancelActiveOptOutRequestEndpointTest {
    
    private static final String KNOWN_ACCOUNT_NUMBER = "A";
    private static final String UNKNOWN_ACCOUNT_NUMBER = "B";
    
    private static Namespace ns = YukonXml.getYukonNamespace();
    private static final String RESP_ELEMENT_NAME = "cancelActiveOptOutResponse";
    private static final String REQU_ELEMENT_NAME = "cancelActiveOptOutRequest";
    
    private Resource requestSchemaResource = 
        new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/CancelActiveOptOutRequest.xsd", this.getClass());
    private Resource responseSchemaResource = 
        new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/CancelActiveOptOutResponse.xsd", this.getClass());
 
    private CancelActiveOptOutRequestEndpoint impl;
    
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
    
    @Before
    public void setUp() throws Exception {
        impl = new CancelActiveOptOutRequestEndpoint();
        
        impl.setAccountEventLogService(null);
        impl.setCustomerAccountDao(new MockCustomerAccountDao());
        impl.setLmHardwareBaseDao(new MockLmHardwareBaseDao());
        impl.setOptOutService(new MockOptOutService());
        impl.setRolePropertyDao(new MockRolePropertyDao());
        impl.setYukonEnergyCompanyService(new MockYukonEnergyCompanyService());
        impl.setAccountEventLogService(new MockAccountEventLogService());
        impl.setOptOutEventDao(new MockOptOutEventDao());
    }
    
    @Test
    public void testInvoke() throws Exception {
        Element requestElement = null;
        SimpleXPathTemplate outputTemplate = null;
        LiteYukonUser user = new LiteYukonUser();
  
        // test with unauthorized user
        // ========================================================================================
        requestElement = createRequest(KNOWN_ACCOUNT_NUMBER, TestInventory.OPTED_OUT.toString());
        outputTemplate = validateAndReturnResponse(requestElement, MockRolePropertyDao.getUnAuthorizedUser());
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "UserNotAuthorized");
        
        // test with unknown account number
        // ========================================================================================       
        requestElement = createRequest(UNKNOWN_ACCOUNT_NUMBER, TestInventory.OPTED_OUT.toString());
        outputTemplate = validateAndReturnResponse(requestElement, user);
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "NotFound");
        
        // test with unknown serial number
        // ========================================================================================       
        requestElement = createRequest(KNOWN_ACCOUNT_NUMBER, TestInventory.UNKNOWN.toString());
        outputTemplate = validateAndReturnResponse(requestElement, user);
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "NotFound");
        
        // test with opted out device
        // ========================================================================================       
        requestElement = createRequest(KNOWN_ACCOUNT_NUMBER, TestInventory.OPTED_OUT.toString());
        outputTemplate = validateAndReturnResponse(requestElement, user);
        TestUtils.runSuccessAssertion(outputTemplate, RESP_ELEMENT_NAME);
        
        // test with not opted out device
        // ========================================================================================       
        requestElement = createRequest(KNOWN_ACCOUNT_NUMBER, TestInventory.NOT_OPTED_OUT.toString());
        outputTemplate = validateAndReturnResponse(requestElement, user);
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "CancelFailed");
        
       
    }
    
    private SimpleXPathTemplate validateAndReturnResponse(Element requestElement, LiteYukonUser user) throws Exception{
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);

        Element responseElement = impl.invoke(requestElement, user);
        
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        return XmlApiUtils.getXPathTemplateForElement(responseElement);
        
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
        public CustomerAccount getByAccountNumberForDescendentsOfEnergyCompany(String accountNumber,
                                                                               YukonEnergyCompany energyCompany) {
            if (accountNumber.equals(UNKNOWN_ACCOUNT_NUMBER)) {
                throw new NotFoundException("Unknown account");
            } else {
                CustomerAccount account = new CustomerAccount();
                account.setAccountId(1);
                account.setAccountNumber(accountNumber);
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
        public void cancelOptOut(List<Integer>eventIdList, LiteYukonUser user) {
            
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
