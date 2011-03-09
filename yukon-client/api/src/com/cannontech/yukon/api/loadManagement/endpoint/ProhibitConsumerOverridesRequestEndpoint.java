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
public class ProhibitConsumerOverridesRequestEndpoint {

    private OptOutService optOutService;
    private StarsEventLogService starsEventLogService;
    private Namespace ns = YukonXml.getYukonNamespace();
	private RolePropertyDao rolePropertyDao;
    
	private String programNameExpressionStr = "/y:prohibitConsumerOverridesRequest/y:programName";
	
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="prohibitConsumerOverridesRequest")
    public Element invoke(Element prohibitConsumerOverridesRequest, LiteYukonUser user) throws Exception {
        
        XmlVersionUtils.verifyYukonMessageVersion(prohibitConsumerOverridesRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0, XmlVersionUtils.YUKON_MSG_VERSION_1_1);
        String version = XmlVersionUtils.getYukonMessageVersion(prohibitConsumerOverridesRequest);
        
        SimpleXPathTemplate requestTemplate = XmlApiUtils.getXPathTemplateForElement(prohibitConsumerOverridesRequest);
        String programName = requestTemplate.evaluateAsString(programNameExpressionStr);
        
        // init response
        Element resp = new Element("prohibitConsumerOverridesResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, version);
        
        // run service
        Element resultElement;
        try {
            // Check authorization
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT, user);
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WS_LM_CONTROL_ACCESS, user);
            
            if (StringUtils.isBlank(programName)) {
                starsEventLogService.disablingOptOutUsageForTodayAttemptedByApi(user);
                optOutService.changeOptOutEnabledStateForToday(user, false);

            } else {
                starsEventLogService.disablingOptOutUsageForTodayByProgramAttemptedByApi(user, programName);
                optOutService.changeOptOutEnabledStateForTodayByProgramName(user, false, programName);
            }
            
            resultElement = XmlUtils.createStringElement("success", ns, "");
            
        } catch (NotAuthorizedException e) {
            resultElement = XMLFailureGenerator.generateFailure(prohibitConsumerOverridesRequest,
                                                                e,
                                                                "UserNotAuthorized",
                                                                "The user is not authorized to prohibit overrides.");
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