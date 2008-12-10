package com.cannontech.yukon.api.loadManagement;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;
import com.cannontech.yukon.api.utils.TestUtils;

public class CancelAllCurrentOverridesRequestEndpointTest {

    private CancelAllCurrentOverridesRequestEndpoint impl;
    private MockOverrideService mockOverrideService; 
    
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @Before
    public void setUp() throws Exception {
        
    	mockOverrideService = new MockOverrideService();
    	
    	impl = new CancelAllCurrentOverridesRequestEndpoint();
    	impl.setOverrideService(mockOverrideService);
        impl.initialize();
    }
    
    private class MockOverrideService extends OverrideServiceAdapter {
    	
    	@Override
    	public void cancelAllCurrentOverrides(LiteYukonUser user) {

    		
    	}
    }
    
    @Test
    public void testInvoke() throws Exception {
        
        // init
        Element requestElement = null;
        Attribute versionAttribute = null;
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;
        Resource requestSchemaResource = new ClassPathResource("../schemas/loadManagement/CancelAllCurrentOverridesRequest.xsd", this.getClass());
        Resource responseSchemaResource = new ClassPathResource("../schemas/loadManagement/CancelAllCurrentOverridesResponse.xsd", this.getClass());
        
        // test
        //==========================================================================================
        requestElement = new Element("cancelAllCurrentOverridesRequest", ns);
        XmlVersionUtils.addVersionAttribute(requestElement, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
        
        // run and validate response against xsd
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // outputs
        //TODO tests for success/failure
    }

}
