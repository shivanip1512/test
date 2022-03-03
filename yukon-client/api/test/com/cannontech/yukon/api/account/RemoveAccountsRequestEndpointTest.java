package com.cannontech.yukon.api.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.exception.InvalidAccountNumberException;
import com.cannontech.yukon.api.account.endpoint.RemoveAccountsRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockAccountEventLogService;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.utils.TestUtils;

public class RemoveAccountsRequestEndpointTest {
	
	private static final LiteYukonUser AUTH_USER = MockRolePropertyDao.getAuthorizedUser();
	
	private RemoveAccountsRequestEndpoint impl;

    @BeforeEach
    public void setUp() throws Exception {
        impl = new RemoveAccountsRequestEndpoint();
        impl.setAccountService(new AccountServiceAdapter() {

            @Override
            public void deleteAccount(String accountNumber, LiteYukonUser user) {
                if(accountNumber.equalsIgnoreCase("INVALID ACCOUNT #")) {
                    throw new InvalidAccountNumberException("INVALID ACCOUNT #");
                }
                
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
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/account/schemas/RemoveAccountsRequest.xsd", this.getClass());
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/account/schemas/RemoveAccountsResponse.xsd", this.getClass());
        
        Resource successResource = new ClassPathResource("successfulRemoveAccountsRequest.xml", RemoveAccountsRequestEndpointTest.class);
        Element successInputElement = XmlUtils.createElementFromResource(successResource);
        
        // Test request against schema definition
        TestUtils.validateAgainstSchema(successInputElement, reqSchemaResource);
        
        Element successOutputElement = impl.invoke(successInputElement, AUTH_USER);
        
        // Test response against schema definition
        TestUtils.validateAgainstSchema(successOutputElement, respSchemaResource);
        
        assertNotNull(successOutputElement, "Missing output element from RemoveAccountsResponse.");
        
        new XMLOutputter(Format.getPrettyFormat()).output(successOutputElement, System.out);

        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(new JDOMSource(successOutputElement));
        template.setNamespaces(YukonXml.getYukonNamespaceAsProperties());

        List<Node> successResultList = template.evaluateAsNodeList("//y:accountResultList/y:accountResult");
        
        assertNotNull(successResultList, "Missing AccountResult list from RemoveAccountsResponse.");
        assertTrue(successResultList.size() > 0, "AccountResult list from RemoveAccountsResponse should not be empty.");
        
        for(Node node : successResultList) {
            assertEquals(2, node.getChildNodes().getLength(), "Invalid result node structure.");
            Node successNode = node.getChildNodes().item(1);
            assertEquals("success", successNode.getLocalName(), "Missing success node.");
        }

        /*
         * Test the failure files
         */
        
        Resource invalidAccountNumResource = new ClassPathResource("invalidAccountNumberRemoveAccountsRequest.xml", RemoveAccountsRequestEndpointTest.class);
        Element invalidAccountNumInputElement = XmlUtils.createElementFromResource(invalidAccountNumResource);
        
        // Test request against schema definition
        TestUtils.validateAgainstSchema(invalidAccountNumInputElement, reqSchemaResource);
        
        Element invalidAccountNumOutputElement = impl.invoke(invalidAccountNumInputElement, AUTH_USER);
        
        // Test response against schema definition
        TestUtils.validateAgainstSchema(invalidAccountNumOutputElement, respSchemaResource);
        
        assertNotNull(invalidAccountNumOutputElement, "Missing output element from RemoveAccountsResponse.");

        new XMLOutputter(Format.getPrettyFormat()).output(invalidAccountNumOutputElement, System.out);

        template.setContext(new JDOMSource(invalidAccountNumOutputElement));
        template.setNamespaces(YukonXml.getYukonNamespaceAsProperties());

        List<Node> invalidAccountNumResultList = template.evaluateAsNodeList("//y:accountResultList/y:accountResult");
        
        assertNotNull(invalidAccountNumResultList, "Missing AccountResult list from RemoveAccountsResponse.");
        assertTrue(invalidAccountNumResultList.size() > 0, "AccountResult list from RemoveAccountsResponse should not be empty.");
        
        for(Node node : invalidAccountNumResultList) {
            assertEquals(2, node.getChildNodes().getLength(), "Invalid result node structure.");
            Node failureNode = node.getChildNodes().item(1);
            assertEquals("failure", failureNode.getLocalName(), "Missing failure node.");
        }
        
    }
}

