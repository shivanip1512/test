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
public class CountOverridesTowardsLimitRequestEndpoint {

	private OptOutService optOutService;
    private Namespace ns = YukonXml.getYukonNamespace();
	private AuthDao authDao;

    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="countOverridesTowardsLimitRequest")
    public Element invoke(Element countOverridesTowardsLimitRequest, LiteYukonUser user) throws Exception {
        
    	XmlVersionUtils.verifyYukonMessageVersion(countOverridesTowardsLimitRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
    	
    	// init response
        Element resp = new Element("countOverridesTowardsLimitResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // run service
        Element resultElement;
        try {
            // Check authorization
            authDao.verifyTrueProperty(user,
                                       ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT);
            optOutService.changeOptOutCountStateForToday(user, true);
            resultElement = XmlUtils.createStringElement("success", ns, "");
        } catch (NotAuthorizedException e) {
            resultElement = XMLFailureGenerator.generateFailure(countOverridesTowardsLimitRequest,
                                                                e,
                                                                "UserNotAuthorized",
                                                                "The user is not authorized to change count state of overrides.");
        }

        // return response
        resp.addContent(resultElement);
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
