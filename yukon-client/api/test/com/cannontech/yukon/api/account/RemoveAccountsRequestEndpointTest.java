package com.cannontech.yukon.api.account;

import java.util.List;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Node;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.exception.InvalidAccountNumberException;
import com.cannontech.yukon.api.account.endpoint.RemoveAccountsRequestEndpoint;
import com.cannontech.yukon.api.utils.TestUtils;

public class RemoveAccountsRequestEndpointTest {
    private RemoveAccountsRequestEndpoint impl;

    @Before
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
        
        LiteYukonUser user = new LiteYukonUser();
        Element successOutputElement = impl.invoke(successInputElement, user);
        
        // Test response against schema definition
        TestUtils.validateAgainstSchema(successOutputElement, respSchemaResource);
        
        Assert.assertNotNull("Missing output element from RemoveAccountsResponse.", successOutputElement);
        
        new XMLOutputter(Format.getPrettyFormat()).output(successOutputElement, System.out);

        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(new JDOMSource(successOutputElement));
        template.setNamespaces(YukonXml.getYukonNamespaceAsProperties());

        List<Node> successResultList = template.evaluateAsNodeList("//y:accountResultList/y:accountResult");
        
        Assert.assertNotNull("Missing AccountResult list from RemoveAccountsResponse.", successResultList);
        Assert.assertTrue("AccountResult list from RemoveAccountsResponse should not be empty.", successResultList.size() > 0);
        
        for(Node node : successResultList) {
            Assert.assertEquals("Invalid result node structure.", 2, node.getChildNodes().getLength());
            Node successNode = node.getChildNodes().item(1);
            Assert.assertEquals("Missing success node.", "success", successNode.getLocalName());
        }

        /*
         * Test the failure files
         */
        
        Resource invalidAccountNumResource = new ClassPathResource("invalidAccountNumberRemoveAccountsRequest.xml", RemoveAccountsRequestEndpointTest.class);
        Element invalidAccountNumInputElement = XmlUtils.createElementFromResource(invalidAccountNumResource);
        
        // Test request against schema definition
        TestUtils.validateAgainstSchema(invalidAccountNumInputElement, reqSchemaResource);
        
        Element invalidAccountNumOutputElement = impl.invoke(invalidAccountNumInputElement, user);
        
        // Test response against schema definition
        TestUtils.validateAgainstSchema(invalidAccountNumOutputElement, respSchemaResource);
        
        Assert.assertNotNull("Missing output element from RemoveAccountsResponse.", invalidAccountNumOutputElement);

        new XMLOutputter(Format.getPrettyFormat()).output(invalidAccountNumOutputElement, System.out);

        template.setContext(new JDOMSource(invalidAccountNumOutputElement));
        template.setNamespaces(YukonXml.getYukonNamespaceAsProperties());

        List<Node> invalidAccountNumResultList = template.evaluateAsNodeList("//y:accountResultList/y:accountResult");
        
        Assert.assertNotNull("Missing AccountResult list from RemoveAccountsResponse.", invalidAccountNumResultList);
        Assert.assertTrue("AccountResult list from RemoveAccountsResponse should not be empty.", invalidAccountNumResultList.size() > 0);
        
        for(Node node : invalidAccountNumResultList) {
            Assert.assertEquals("Invalid result node structure.", 2, node.getChildNodes().getLength());
            Node failureNode = node.getChildNodes().item(1);
            Assert.assertEquals("Missing failure node.", "failure", failureNode.getLocalName());
        }
        
    }
}

