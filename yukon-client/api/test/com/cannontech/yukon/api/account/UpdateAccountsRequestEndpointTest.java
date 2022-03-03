package com.cannontech.yukon.api.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.transform.JDOMSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Node;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.UserNameUnavailableException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.exception.AccountNumberUnavailableException;
import com.cannontech.stars.dr.account.exception.InvalidAccountNumberException;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.dr.account.service.AccountServiceHelper;
import com.cannontech.yukon.api.account.endpoint.UpdateAccountsRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockAccountEventLogService;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.utils.TestUtils;

public class UpdateAccountsRequestEndpointTest {
	
	private static final LiteYukonUser AUTH_USER = MockRolePropertyDao.getAuthorizedUser();
	
    private UpdateAccountsRequestEndpoint impl;

    @BeforeEach
    public void setUp() throws Exception {
        impl = new UpdateAccountsRequestEndpoint();
        impl.setAccountService(new AccountServiceAdapter() {

            @Override
            public void updateAccount(UpdatableAccount updatableAccount, LiteYukonUser user) throws InvalidAccountNumberException {
                if(updatableAccount.getAccountNumber().equalsIgnoreCase("INVALID ACCOUNT #")) {
                    throw new InvalidAccountNumberException("INVALID ACCOUNT #");
                }
                
                if(updatableAccount.getAccountNumber().equalsIgnoreCase("INVALID ACCOUNT # TRY ADD")) {
                    throw new InvalidAccountNumberException("INVALID ACCOUNT # TRY ADD");
                }
            }
            
            @Override
            public int addAccount(UpdatableAccount updatableAccount, LiteYukonUser operator) throws AccountNumberUnavailableException, UserNameUnavailableException {
                if(updatableAccount.getAccountNumber().equalsIgnoreCase("INVALID ACCOUNT #")) {
                    throw new AccountNumberUnavailableException("INVALID ACCOUNT #");
                }
                String userName = updatableAccount.getAccountDto().getUserName();
                if(StringUtils.isNotBlank(userName)){
                    if(userName.equalsIgnoreCase("DUPLICATE USERNAME")) {
                        throw new UserNameUnavailableException("DUPLICATE USERNAME");
                    }
                }
                return -1;
            }
        });
        
        impl.setAccountServiceHelper(new AccountServiceHelper() {
            @Override
            public UpdatableAccount buildFullDto(UpdatableAccount workingAccount, LiteYukonUser user) {
                
                return workingAccount;
            }
        });
        impl.setAccountEventLogService(new MockAccountEventLogService());
    }

    @Test
    public void testInvoke() throws Exception {
        /*
         * Test the success file 
         */
        
        // Load schemas
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/account/schemas/UpdateAccountsRequest.xsd", this.getClass());
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/account/schemas/UpdateAccountsResponse.xsd", this.getClass());
        
        Resource successResource = new ClassPathResource("successfulUpdateAccountsRequest.xml", UpdateAccountsRequestEndpointTest.class);
        Element successInputElement = XmlUtils.createElementFromResource(successResource);
        
        // Test request against schema definition
        TestUtils.validateAgainstSchema(successInputElement, reqSchemaResource);
        
        Element successOutputElement = impl.invoke(successInputElement, AUTH_USER);
        
        // Test response against schema definition
        TestUtils.validateAgainstSchema(successOutputElement, respSchemaResource);
        
        assertNotNull(successOutputElement, "Missing output element from UpdateAccountsResponse.");
        
        new XMLOutputter(Format.getPrettyFormat()).output(successOutputElement, System.out);

        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(new JDOMSource(successOutputElement));
        template.setNamespaces(YukonXml.getYukonNamespaceAsProperties());

        List<Node> successResultList = template.evaluateAsNodeList("//y:accountResultList/y:accountResult");
        
        assertNotNull(successResultList, "Missing AccountResult list from UpdateAccountsResponse.");
        assertTrue(successResultList.size() > 0, "AccountResult list from UpdateAccountsResponse should not be empty.");
        
        for(Node node : successResultList) {
            assertEquals(2, node.getChildNodes().getLength(), "Invalid result node structure.");
            Node successNode = node.getChildNodes().item(1);
            assertEquals("success", successNode.getLocalName(), "Missing success node.");
        }

        /*
         * Test the failure files
         */
        
        Resource dupAccountNumResource = new ClassPathResource("invalidUpdateAccountsRequest.xml", UpdateAccountsRequestEndpointTest.class);
        Element dupAccountNumInputElement = XmlUtils.createElementFromResource(dupAccountNumResource);
        
        // Test request against schema definition
        TestUtils.validateAgainstSchema(dupAccountNumInputElement, reqSchemaResource);
        
        Element dupAccountNumOutputElement = impl.invoke(dupAccountNumInputElement, AUTH_USER);
        
        // Test response against schema definition
        TestUtils.validateAgainstSchema(dupAccountNumOutputElement, respSchemaResource);
        
        assertNotNull(dupAccountNumOutputElement, "Missing output element from UpdateAccountsResponse.");

        new XMLOutputter(Format.getPrettyFormat()).output(dupAccountNumOutputElement, System.out);

        template.setContext(new JDOMSource(dupAccountNumOutputElement));
        template.setNamespaces(YukonXml.getYukonNamespaceAsProperties());

        List<Node> dupAccountNumResultList = template.evaluateAsNodeList("//y:accountResultList/y:accountResult");
        
        assertNotNull(dupAccountNumResultList, "Missing AccountResult list from UpdateAccountsResponse.");
        assertTrue(dupAccountNumResultList.size() > 0, "AccountResult list from UpdateAccountsResponse should not be empty.");
        
        for(Node node : dupAccountNumResultList) {
            assertEquals(2, node.getChildNodes().getLength(), "Invalid result node structure.");
            Node failureNode = node.getChildNodes().item(1);
            assertEquals("failure", failureNode.getLocalName(), "Missing failure node.");
        }
        
    }
}
