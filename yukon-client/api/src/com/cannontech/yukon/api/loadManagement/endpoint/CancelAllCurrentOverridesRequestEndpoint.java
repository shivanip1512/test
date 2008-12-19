package com.cannontech.yukon.api.loadManagement.endpoint;

import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class CancelAllCurrentOverridesRequestEndpoint {

	private OptOutService optOutService;
    private Namespace ns = YukonXml.getYukonNamespace();
	private AuthDao authDao;
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="cancelAllCurrentOverridesRequest")
    public Element invoke(Element cancelAllCurrentOverridesRequest, LiteYukonUser user) throws Exception {
        
    	XmlVersionUtils.verifyYukonMessageVersion(cancelAllCurrentOverridesRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
    	
    	// init response
        Element resp = new Element("cancelAllCurrentOverridesResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // Check authorization
        try {
        	authDao.verifyTrueProperty(user, ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT);
        } catch (NotAuthorizedException e) {
        	Element fe = XMLFailureGenerator.generateFailure(
        			cancelAllCurrentOverridesRequest, 
        			e, 
        			"UserNotAuthorized", 
        	"The user is not authorized to cancel all current overrides.");
        	resp.addContent(fe);
        	return resp;
        }
        
        // run service
        optOutService.cancelAllOptOuts(user);
        
        // build response
        resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        
        return resp;
    }
    
    @Autowired
    public void setOptOutService(OptOutService optOutService) {
		this.optOutService = optOutService;
	}
    
    @Autowired
    public void setAuthDao(AuthDao authDao) {
		this.authDao = authDao;
	}
}
