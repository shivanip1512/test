package com.cannontech.web.menu.option.producer;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;

/**
 * Dynamic MenuOptionProducer for trends
 */
public class OptOutMenuOptionProducer extends DynamicMenuOptionProducer {

    private OptOutStatusService optOutStatusService;

    public void setOptOutStatusService(OptOutStatusService optOutStatusService) {
		this.optOutStatusService = optOutStatusService;
	}

    @Override
    public List<MenuOption> doGetMenuOptions(YukonUserContext userContext) {

    	List<MenuOption> menuOptions = new ArrayList<MenuOption>();
    	
        // Generate a menu option if opt out is enabled
    	boolean optOutEnabled = optOutStatusService.getOptOutEnabled(userContext.getYukonUser()).isOptOutEnabled();
    	if(optOutEnabled) {
    		YukonMessageSourceResolvable resolvable = 
    			new YukonMessageSourceResolvable("yukon.web.menu.config.consumer.programs.optOut");
    		
    		SimpleMenuOptionLink optionLink = new SimpleMenuOptionLink("optOut", resolvable);
    		optionLink.setLinkUrl("/stars/consumer/optout");
    		
    		menuOptions.add(optionLink);
    	}
         
        return menuOptions;
    }

}
