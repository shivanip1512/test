package com.cannontech.web.menu.option.producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;
import com.cannontech.web.menu.option.SubMenuOption;

/**
 * Dynamic MenuOptionProducer for demand response details
 */
public class DemandResponseDetailsMenuOptionProducer extends DynamicMenuOptionProducer {

    private RolePropertyDao rolePropertyDao;
    
    @Override
    public List<MenuOption> getMenuOptions(YukonUserContext userContext) {

        LiteYukonUser yukonUser = userContext.getYukonUser();
        boolean showControlAreas = 
            rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_CONTROL_AREAS, yukonUser);
        boolean showScenarios = 
            rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_SCENARIOS, yukonUser);
        
        String controlAreaLinkString = "/spring/dr/controlArea/list";
        String scenarioLinkString = "/spring/dr/scenario/list";

        // Setup details link
        SubMenuOption detailsLink = 
            new SubMenuOption("details", "yukon.web.menu.config.dr.details", true);
        if(showControlAreas) {
            detailsLink.setLinkUrl(controlAreaLinkString);
        } else if(showScenarios) {
            detailsLink.setLinkUrl(scenarioLinkString);
        } else {
            detailsLink.setLinkUrl("/spring/dr/program/list");
        }
        
        // Add sub links
        List<MenuOptionProducer> subOptions = new ArrayList<MenuOptionProducer>();
        if(showScenarios) {
            // Add scenario sub link
            MenuOptionProducer scenarioLink = 
                createLink("scenarios",
                           "yukon.web.menu.config.dr.details.scenarios", 
                           scenarioLinkString);
            subOptions.add(scenarioLink);
        }

        if(showControlAreas) {
		    // Add control area sub link
		    MenuOptionProducer controlAreaLink = 
		        createLink("controlareas",
		                   "yukon.web.menu.config.dr.details.controlareas", 
		                   controlAreaLinkString);
		    subOptions.add(controlAreaLink);
		}
        
        
        // Always add program sub link
        MenuOptionProducer programLink = createLink("programs",
                                                    "yukon.web.menu.config.dr.details.programs", 
                                                    "/spring/dr/program/list");
        subOptions.add(programLink);

        // Always add load group sub link
        MenuOptionProducer loadGroupLink = createLink("loadgroups",
                                                      "yukon.web.menu.config.dr.details.loadgroups", 
                                                      "/spring/dr/loadGroup/list");
        subOptions.add(loadGroupLink);
		
		detailsLink.setSubOptions(subOptions);
         
        return Collections.singletonList((MenuOption)detailsLink);
    }
    
    private MenuOptionProducer createLink(String id, String menuTextKey, String link) {
        SimpleMenuOptionLink menuOption = new SimpleMenuOptionLink(id, menuTextKey);
        menuOption.setLinkUrl(link);
        StaticMenuOptionProducer menuOptionProducer = 
            new StaticMenuOptionProducer(menuOption, NullUserChecker.getInstance());

        return menuOptionProducer;
    }
    
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

}
