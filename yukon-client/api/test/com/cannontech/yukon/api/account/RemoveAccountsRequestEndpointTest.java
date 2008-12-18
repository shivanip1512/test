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

import com.cannontech.common.bulk.field.impl.AccountDto;
import com.cannontech.common.bulk.field.impl.UpdatableAccount;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.exception.AccountNumberUnavailableException;
import com.cannontech.stars.dr.account.exception.InvalidAccountNumberException;
import com.cannontech.stars.dr.account.exception.UserNameUnavailableException;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.yukon.api.account.endpoint.RemoveAccountsRequestEndpoint;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;

public class RemoveAccountsRequestEndpointTest {
    private RemoveAccountsRequestEndpoint impl;

    @Before
    public void setUp() throws Exception {
        impl = new RemoveAccountsRequestEndpoint();
        impl.setAccountService(new AccountService() {

            @Override
            public void addAccount(UpdatableAccount updatableAccount, LiteYukonUser operator) throws AccountNumberUnavailableException, UserNameUnavailableException {
            }

            @Override
            public void deleteAccount(String accountNumber, LiteYukonUser user) {
                if(accountNumber.equalsIgnoreCase("INVALID ACCOUNT #")) {
                    throw new InvalidAccountNumberException("INVALID ACCOUNT #");
                }
                
            }

            @Override
            public AccountDto getAccountDto(String accountNumber, LiteYukonUser yukonUser) {
                return null;
            }

            @Override
            public void updateAccount(UpdatableAccount updatableAccount, LiteYukonUser user) throws NotFoundException {
            }

        });

    }

    @Test
    public void testInvoke() throws Exception {
        /*
         * Test the success file 
         */
        Resource successResource = new ClassPathResource("successfulRemoveAccountsRequest.xml", RemoveAccountsRequestEndpointTest.class);
        Element successInputElement = XmlUtils.createElementFromResource(successResource);
        LiteYukonUser user = new LiteYukonUser();
        Element successOutputElement = impl.invoke(successInputElement, user);
        
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
        Element invalidAccountNumOutputElement = impl.invoke(invalidAccountNumInputElement, user);
        
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

