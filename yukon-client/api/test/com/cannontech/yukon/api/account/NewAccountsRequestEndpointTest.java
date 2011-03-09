package com.cannontech.yukon.api.account;

import java.util.List;

import org.apache.commons.lang.StringUtils;
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

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.core.dao.UserNameUnavailableException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.exception.AccountNumberUnavailableException;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.yukon.api.account.endpoint.NewAccountsRequestEndpoint;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.YukonXml;
import com.cannontech.yukon.api.utils.TestUtils;

public class NewAccountsRequestEndpointTest {
    private NewAccountsRequestEndpoint impl;

    @Before
    public void setUp() throws Exception {
        impl = new NewAccountsRequestEndpoint();
        impl.setAccountService(new AccountServiceAdapter() {

            @Override
            public int addAccount(UpdatableAccount updatableAccount, LiteYukonUser operator) throws AccountNumberUnavailableException, UserNameUnavailableException {
                if(updatableAccount.getAccountNumber().equalsIgnoreCase("DUPLICATE ACCOUNT #")) {
                    throw new AccountNumberUnavailableException("DUPLICATE ACCOUNT #");
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

    }

    @Test
    public void testInvoke() throws Exception {
        /*
         * Test the success file 
         */
        
        // Load schemas
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/account/schemas/NewAccountsRequest.xsd", this.getClass());
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/account/schemas/NewAccountsResponse.xsd", this.getClass());
        
        Resource successResource = new ClassPathResource("successfulNewAccountsRequest.xml", NewAccountsRequestEndpointTest.class);
        Element successInputElement = XmlUtils.createElementFromResource(successResource);
        
        // Test request against schema definition
        TestUtils.validateAgainstSchema(successInputElement, reqSchemaResource);
        
        LiteYukonUser user = new LiteYukonUser();
        Element successOutputElement = impl.invoke(successInputElement, user);
        
        // Test response against schema definition
        TestUtils.validateAgainstSchema(successOutputElement, respSchemaResource);
        
        Assert.assertNotNull("Missing output element from NewAccountsResponse.", successOutputElement);
        
        new XMLOutputter(Format.getPrettyFormat()).output(successOutputElement, System.out);

        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(new JDOMSource(successOutputElement));
        template.setNamespaces(YukonXml.getYukonNamespaceAsProperties());

        List<Node> successResultList = template.evaluateAsNodeList("//y:accountResultList/y:accountResult");
        
        Assert.assertNotNull("Missing CustomerAccountResult list from NewAccountsResponse.", successResultList);
        Assert.assertTrue("CustomerAccountResult list from NewAccountsResponse should not be empty.", successResultList.size() > 0);
        
        for(Node node : successResultList) {
            Assert.assertEquals("Invalid result node structure.", 2, node.getChildNodes().getLength());
            Node successNode = node.getChildNodes().item(1);
            Assert.assertEquals("Missing success node.", "success", successNode.getLocalName());
        }

        /*
         * Test the failure files
         */
        
        Resource dupAccountNumResource = new ClassPathResource("duplicateAccountNumberNewAccountsRequest.xml", NewAccountsRequestEndpointTest.class);
        Element dupAccountNumInputElement = XmlUtils.createElementFromResource(dupAccountNumResource);
        
        // Test request against schema definition
        TestUtils.validateAgainstSchema(dupAccountNumInputElement, reqSchemaResource);
        
        Element dupAccountNumOutputElement = impl.invoke(dupAccountNumInputElement, user);
        
        // Test request against schema definition
        TestUtils.validateAgainstSchema(dupAccountNumOutputElement, respSchemaResource);
        
        Assert.assertNotNull("Missing output element from NewAccountsResponse.", dupAccountNumOutputElement);

        new XMLOutputter(Format.getPrettyFormat()).output(dupAccountNumOutputElement, System.out);

        template.setContext(new JDOMSource(dupAccountNumOutputElement));
        template.setNamespaces(YukonXml.getYukonNamespaceAsProperties());

        List<Node> dupAccountNumResultList = template.evaluateAsNodeList("//y:accountResultList/y:accountResult");
        
        Assert.assertNotNull("Missing CustomerAccountResult list from NewAccountsResponse.", dupAccountNumResultList);
        Assert.assertTrue("CustomerAccountResult list from NewAccountsResponse should not be empty.", dupAccountNumResultList.size() > 0);
        
        for(Node node : dupAccountNumResultList) {
            Assert.assertEquals("Invalid result node structure.", 2, node.getChildNodes().getLength());
            Node failureNode = node.getChildNodes().item(1);
            Assert.assertEquals("Missing failure node.", "failure", failureNode.getLocalName());
        }
        
        Resource dupUsernameResource = new ClassPathResource("duplicateUsernameNewAccountsRequest.xml", NewAccountsRequestEndpointTest.class);
        Element dupUsernameInputElement = XmlUtils.createElementFromResource(dupUsernameResource);
        Element dupUsernameOutputElement = impl.invoke(dupUsernameInputElement, user);
        
        Assert.assertNotNull("Missing output element from NewAccountsResponse.", dupUsernameOutputElement);

        new XMLOutputter(Format.getPrettyFormat()).output(dupUsernameOutputElement, System.out);

        template.setContext(new JDOMSource(dupUsernameOutputElement));
        template.setNamespaces(YukonXml.getYukonNamespaceAsProperties());

        List<Node> dupUsernameResultList = template.evaluateAsNodeList("//y:accountResultList/y:accountResult");
        
        Assert.assertNotNull("Missing CustomerAccountResult list from NewAccountsResponse.", dupUsernameResultList);
        Assert.assertTrue("CustomerAccountResult list from NewAccountsResponse should not be empty.", dupUsernameResultList.size() > 0);
        
        for(Node node : dupUsernameResultList) {
            Assert.assertEquals("Invalid result node structure.", 2, node.getChildNodes().getLength());
            Node successNode = node.getChildNodes().item(1);
            Assert.assertEquals("Missing failure node.", "failure", successNode.getLocalName());
        }
    }
}
