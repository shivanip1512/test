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
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class DecrementOverrideLimitRequestEndpoint {

    private OptOutService optOutService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private static String accountNumberStr = "/y:decrementDeviceOverrideLimitRequest/y:accountNumber";
    private static String serialNumberStr = "/y:decrementDeviceOverrideLimitRequest/y:serialNumber";
    
    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="totalOverriddenDevicesByAccountNumberRequest")
    public Element invokeDecrementCountToLimitBySerialNumberAndAccount(
    		Element decrementDeviceOverrideLimitRequest, LiteYukonUser user) throws Exception {
        
        //Verify Request message version
        XmlVersionUtils.verifyYukonMessageVersion(decrementDeviceOverrideLimitRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(decrementDeviceOverrideLimitRequest);
        
        String accountNumber = template.evaluateAsString(accountNumberStr);
        String serialNumber = template.evaluateAsString(serialNumberStr);
        
        // init response
        Element resp = new Element("totalOverriddenDevicesByAccountNumberResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // run service
        optOutService.allowAdditionalOptOuts(accountNumber, serialNumber, 1, user);
        
        // build response
        resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        
        return resp;
    }
    
    @Autowired
    public void setOptOutService(OptOutService optOutService) {
		this.optOutService = optOutService;
	}
    
}

