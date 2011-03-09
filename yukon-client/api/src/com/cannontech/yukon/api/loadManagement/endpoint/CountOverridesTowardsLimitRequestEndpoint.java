package com.cannontech.yukon.api.loadManagement.endpoint;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlApiUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class CountOverridesTowardsLimitRequestEndpoint {

	private OptOutService optOutService;
	private StarsEventLogService starsEventLogService;
    private Namespace ns = YukonXml.getYukonNamespace();
	private RolePropertyDao rolePropertyDao;

    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="countOverridesTowardsLimitRequest")
    public Element invoke(Element countOverridesTowardsLimitRequest, LiteYukonUser user) throws Exception {
        
    	XmlVersionUtils.verifyYukonMessageVersion(countOverridesTowardsLimitRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0, XmlVersionUtils.YUKON_MSG_VERSION_1_1);
    	String version = XmlVersionUtils.getYukonMessageVersion(countOverridesTowardsLimitRequest);
    	
    	SimpleXPathTemplate template = XmlApiUtils.getXPathTemplateForElement(countOverridesTowardsLimitRequest);
    	String programName = template.evaluateAsString("/y:countOverridesTowardsLimitRequest/y:programName");
    	
    	// init response
        Element resp = new Element("countOverridesTowardsLimitResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, version);

        // run service
        Element resultElement;
        try {
        	
        	// Check authorization
        	rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT, user);
        	rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WS_LM_DATA_ACCESS, user);
        	
        	if (StringUtils.isBlank(programName)) {
        	    starsEventLogService.countTowardOptOutLimitTodayAttemptedByApi(user);
        		optOutService.changeOptOutCountStateForToday(user, true);
        	} else {
        	    starsEventLogService.countTowardOptOutLimitTodayByProgramAttemptedByApi(user, programName);
        		optOutService.changeOptOutCountStateForTodayByProgramName(user, true, programName);
        	}
            
            resultElement = XmlUtils.createStringElement("success", ns, "");
            
        } catch (NotAuthorizedException e) {
            resultElement = XMLFailureGenerator.generateFailure(countOverridesTowardsLimitRequest, e, "UserNotAuthorized", "The user is not authorized to change count state of overrides.");
        } catch (ProgramNotFoundException e) {
        	resultElement = XMLFailureGenerator.generateFailure(countOverridesTowardsLimitRequest, e, "ProgramNotFound", "Unknown program name.");
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
    public void setStarsEventLogService(StarsEventLogService starsEventLogService) {
        this.starsEventLogService = starsEventLogService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
    
}