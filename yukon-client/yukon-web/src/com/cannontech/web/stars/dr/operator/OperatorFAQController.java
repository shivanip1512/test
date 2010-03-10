package com.cannontech.web.stars.dr.operator;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.stars.dr.general.dao.FAQDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;


@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_FAQ)
public class OperatorFAQController {

    private FAQDao faqDao;
    private RolePropertyDao rolePropertyDao;
    
	// FAQ
	@RequestMapping(value = "/operator/faq")
    public String faq(int accountId, int energyCompanyId, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
	
	    String webLinkFAQ = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.OPERATOR_WEB_LINK_FAQ,
	                                                               userContext.getYukonUser());
	    
	    if (!StringUtils.isBlank(webLinkFAQ) && !webLinkFAQ.equalsIgnoreCase("(none)")) {
	        return "redirect:"+webLinkFAQ;
	    }

	    Map<String, Map<String, String>> questions = faqDao.getQuestionMap(userContext);
	    modelMap.addAttribute("questions", questions);
	    
	    return "operator/faq.jsp";
	    
	}
	

	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
	
	@Autowired
	public void setFaqDao(FAQDao faqDao) {
        this.faqDao = faqDao;
    }
}
