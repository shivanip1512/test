package com.cannontech.web.menu.option.producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;
import com.cannontech.web.menu.option.SubMenuOption;

/**
 * Dynamic MenuOptionProducer for thermostats
 */
public class DemandResponseOptOutMenuOptionProducer extends DynamicMenuOptionProducer {
    private final static String adminUrl = "/stars/operator/optOut/admin";
    private final static String scheduledEventsUrl = "/stars/operator/optOut/admin/viewScheduled";
    private final static String surveyListUrl = "/stars/optOutSurvey/list";
    private final static String baseKey = "yukon.web.menu.config.dr.optout";

    private RolePropertyDao rolePropertyDao;

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}

    @Override
    public List<MenuOption> doGetMenuOptions(YukonUserContext userContext) {

        // Generate legal menu options the demand response opt out menu.

        boolean adminAllowed =
            rolePropertyDao.checkAnyProperties(userContext.getYukonUser(),
                                               YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_STATUS,
                                               YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE,
                                               YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CANCEL_CURRENT,
                                               YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS);
        boolean scheduledEventsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_VIEW_OPT_OUT_EVENTS,
                                          userContext.getYukonUser());
        boolean surveyListAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_OPT_OUT_SURVEY_EDIT,
                                          userContext.getYukonUser());
        if (!adminAllowed && !scheduledEventsAllowed && !surveyListAllowed) {
            return Collections.emptyList();
        }

        SubMenuOption option = new SubMenuOption("optout", baseKey, false);
        option.setLinkUrl(adminAllowed ? adminUrl
                : (scheduledEventsAllowed ? scheduledEventsUrl : surveyListUrl));

        List<MenuOptionProducer> subOptions = new ArrayList<MenuOptionProducer>(2);
        if (adminAllowed) {
            subOptions.add(createLink("admin", adminUrl));
        }
        if (scheduledEventsAllowed) {
            subOptions.add(createLink("scheduledevents", scheduledEventsUrl));
        }
        if (surveyListAllowed) {
            subOptions.add(createLink("surveyList", surveyListUrl));
        }
        option.setSubOptions(subOptions);

        List<MenuOption> optionList = new ArrayList<MenuOption>();
        optionList.add(option);
        return optionList;
    }

    private MenuOptionProducer createLink(String labelKey, String linkUrl) {
        SimpleMenuOptionLink optionLink = new SimpleMenuOptionLink(labelKey,
                                                                   baseKey + "." + labelKey);
        optionLink.setLinkUrl(linkUrl);
        return new StaticMenuOptionProducer(optionLink, NullUserChecker.getInstance());
    }
}
