package com.cannontech.yukon.api.loadManagement.endpoint;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramService;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class CountOverridesTowardsLimitRequestEndpoint {

	private OptOutService optOutService;
    private Namespace ns = YukonXml.getYukonNamespace();
	private RolePropertyDao rolePropertyDao;
	private ProgramService programService;
	private StarsDatabaseCache starsDatabaseCache;

    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="countOverridesTowardsLimitRequest")
    public Element invoke(Element countOverridesTowardsLimitRequest, LiteYukonUser user) throws Exception {
        
    	XmlVersionUtils.verifyYukonMessageVersion(countOverridesTowardsLimitRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
    	
    	SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(countOverridesTowardsLimitRequest);
    	String programName = template.evaluateAsString("/y:countOverridesTowardsLimitRequest/y:programName");
    	
    	// init response
        Element resp = new Element("countOverridesTowardsLimitResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // run service
        Element resultElement;
        try {
        	
        	// Check authorization
        	rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT, user);
        	
        	if (StringUtils.isBlank(programName)) {
        		
        		optOutService.changeOptOutCountStateForToday(user, true);
        	
        	} else {
        		
        		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
        		Program program = programService.getByProgramName(programName, energyCompany);
        		int webpublishingProgramId = program.getProgramId();
        		
        		optOutService.changeOptOutCountStateForTodayByProgramId(user, true, webpublishingProgramId);
        	}
            
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
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
    
    @Autowired
    public void setProgramService(ProgramService programService) {
		this.programService = programService;
	}
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
		this.starsDatabaseCache = starsDatabaseCache;
	}
    
}
