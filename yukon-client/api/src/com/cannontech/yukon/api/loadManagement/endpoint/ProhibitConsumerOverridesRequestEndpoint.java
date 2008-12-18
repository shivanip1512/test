package com.cannontech.yukon.api.loadManagement.endpoint;

import javax.annotation.PostConstruct;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class ProhibitConsumerOverridesRequestEndpoint {

    private OptOutService optOutService;
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="prohibitConsumerOverridesRequest")
    public Element invoke(Element prohibitConsumerOverridesRequest, LiteYukonUser user) throws Exception {
        
        XmlVersionUtils.verifyYukonMessageVersion(prohibitConsumerOverridesRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // init response
        Element resp = new Element("prohibitConsumerOverridesResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // run service
        optOutService.changeOptOutEnabledStateForToday(user, false);
        
        resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        
        return resp;
    }
    
    @Autowired
    public void setOptOutService(OptOutService optOutService) {
		this.optOutService = optOutService;
	}
}

