package com.cannontech.yukon.api.loadManagement;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;
import com.cannontech.yukon.api.utils.TestUtils;

public class CountOverridesTowardsLimitRequestEndpointTest {

	//TODO add any required services interface vars
    private CountOverridesTowardsLimitRequestEndpoint impl;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @Before
    public void setUp() throws Exception {
        
    	impl = new CountOverridesTowardsLimitRequestEndpoint();
    	//TODO set any mocked required services on endpoint
        impl.initialize();
    }
    
    //TODO mock any required services
    
    @Test
    public void testInvoke() throws Exception {
        
        // init
        Element requestElement = null;
        Attribute versionAttribute = null;
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;
        Resource requestSchemaResource = new ClassPathResource("../schemas/loadManagement/CountOverridesTowardsLimitRequest.xsd", this.getClass());
        Resource responseSchemaResource = new ClassPathResource("../schemas/loadManagement/CountOverridesTowardsLimitResponse.xsd", this.getClass());
        
        // test
        //==========================================================================================
        requestElement = new Element("countOverridesTowardsLimitRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
        
        // run and validate response against xsd
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // outputs
        //TODO tests for success/failure
    }

}
