package com.cannontech.web.stars.dr.operator;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.stars.dr.general.dao.FaqDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;


@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_FAQ)
public class OperatorFaqController {

    private FaqDao faqDao;
    
	// FAQ
	@RequestMapping(value = "/operator/faq")
    public String faq(ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
	
	    Map<String, Map<String, String>> questions = faqDao.getQuestionMap(userContext);
	    modelMap.addAttribute("questions", questions);
	    
	    return "operator/faq.jsp";
	    
	}
	

	@Autowired
	public void setFaqDao(FaqDao faqDao) {
        this.faqDao = faqDao;
    }
}
