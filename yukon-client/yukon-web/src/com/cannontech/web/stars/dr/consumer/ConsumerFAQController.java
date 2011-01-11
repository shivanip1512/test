package com.cannontech.web.stars.dr.consumer;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.stars.dr.general.dao.FaqDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_QUESTIONS_FAQ)
@Controller
@RequestMapping("/consumer/faq")
public class ConsumerFAQController extends AbstractConsumerController {

    private FaqDao faqDao;

    @RequestMapping(method = RequestMethod.GET)
    public String view(YukonUserContext yukonUserContext, ModelMap map) {

        Map<String, Map<String, String>> questions = faqDao.getQuestionMap(yukonUserContext);
        map.addAttribute("questions", questions);
        
        return "consumer/faq.jsp";
    }
    
    // DI Setter
    @Autowired
    public void setFaqDao(FaqDao faqDao) {
        this.faqDao = faqDao;
    }
}
