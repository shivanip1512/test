package com.cannontech.web.menu.option.producer;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;

public class EnrollmentMenuOptionProducer extends DynamicMenuOptionProducer {

    private OptOutStatusService optOutStatusService;

    public void setOptOutStatusService(OptOutStatusService optOutStatusService) {
        this.optOutStatusService = optOutStatusService;
    }

    @Override
    public List<MenuOption> doGetMenuOptions(YukonUserContext userContext) {

        List<MenuOption> menuOptions = new ArrayList<MenuOption>();
        
        // Generate a menu option if opt out is enabled
        if(optOutStatusService.getOptOutEnabled(userContext.getYukonUser()).isCommunicationEnabled()) {
            YukonMessageSourceResolvable resolvable = 
                new YukonMessageSourceResolvable("yukon.web.menu.config.consumer.programs.enrollment");
            
            SimpleMenuOptionLink optionLink = new SimpleMenuOptionLink("enrollment", resolvable);
            optionLink.setLinkUrl("/stars/consumer/enrollment");
            
            menuOptions.add(optionLink);
        }
         
        return menuOptions;
    }

}
