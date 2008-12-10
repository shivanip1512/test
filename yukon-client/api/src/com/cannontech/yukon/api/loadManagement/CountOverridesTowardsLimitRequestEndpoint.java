package com.cannontech.yukon.api.loadManagement;

import javax.annotation.PostConstruct;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.OverrideService;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class CountOverridesTowardsLimitRequestEndpoint {

	private OverrideService overrideService;
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="countOverridesTowardsLimitRequest")
    public Element invoke(Element countOverridesTowardsLimitRequest, LiteYukonUser user) throws Exception {
        
    	XmlVersionUtils.verifyYukonMessageVersion(countOverridesTowardsLimitRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
    	
    	// init response
        Element resp = new Element("countOverridesTowardsLimitResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);
        
        // run service
        //TODO decide what exception(s) this might throw, add error code details
        try {
        	overrideService.countOverridesTowardsLimit(user);
        } catch (Exception e) {
        	
        	Element fe = XMLFailureGenerator.generateFailure(countOverridesTowardsLimitRequest, e, "ERROR_CODE", "ERROR_DESCRIPTION");
            resp.addContent(fe);
            return resp;
        }
        
        resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        
        return resp;
    }
    
    @Autowired
    public void setOverrideService(OverrideService overrideService) {
		this.overrideService = overrideService;
	}
}
