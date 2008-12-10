package com.cannontech.yukon.api.loadManagement;

import javax.annotation.PostConstruct;

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
public class CancelAllCurrentOverridesRequestEndpoint {

	private OverrideService overrideService;
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="cancelAllCurrentOverridesRequest")
    public Element invoke(Element cancelAllCurrentOverridesRequest, LiteYukonUser user) throws Exception {
        
    	XmlVersionUtils.verifyYukonMessageVersion(cancelAllCurrentOverridesRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
    	
    	// init response
        Element resp = new Element("cancelAllCurrentOverridesResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // run service
        //TODO decide what exception(s) this might throw, add error code details
        try {
        	overrideService.cancelAllCurrentOverrides(user);
        } catch (Exception e) {
        	
        	Element fe = XMLFailureGenerator.generateFailure(cancelAllCurrentOverridesRequest, e, "ERROR_CODE", "ERROR_DESCRIPTION");
            resp.addContent(fe);
            return resp;
        }
        
        // build response
        resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        
        return resp;
    }
    
    @Autowired
    public void setOverrideService(OverrideService overrideService) {
		this.overrideService = overrideService;
	}
}
