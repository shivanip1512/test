package com.cannontech.web.menu.renderer;

import java.io.Writer;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ecs.html.OptGroup;
import org.apache.ecs.html.Option;
import org.apache.ecs.html.Select;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.ModuleConfigException;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOption;
import com.cannontech.web.menu.option.SubMenuOption;

public class SelectMenuOptionRenderer {
	
	public void renderSelect(Select select, Writer out) {
	    select.output(out);
	}
	
	public void renderSelect(SelectMenuConfiguration selectMenuConfiguration, Writer out, MessageSourceAccessor messageSourceAccessor, YukonUserContext userContext) {
		
	    Select select = generateSelect(selectMenuConfiguration, messageSourceAccessor, userContext);
	    renderSelect(select, out);
	}

	public Select generateSelect(SelectMenuConfiguration selectMenuConfiguration, MessageSourceAccessor messageSourceAccessor, YukonUserContext userContext) {
		
		List<MenuOption> menuOptions = selectMenuConfiguration.getMenuBase().getMenuOptions(userContext);
	    Select select = new Select();
	    if (StringUtils.isNotBlank(selectMenuConfiguration.getOnChange())) {
	    	select.setOnChange(e(selectMenuConfiguration.getOnChange()));
	    }
	    if (StringUtils.isNotBlank(selectMenuConfiguration.getHeaderKey())) {
	    	select.addElement(new Option("").addElement(messageSourceAccessor.getMessage(selectMenuConfiguration.getHeaderKey())).setSelected(true));
	    }
	    OptGroup optGroup = null;

	    for (MenuOption menuOption : menuOptions) {

	        if (menuOption instanceof SubMenuOption) {
	            SubMenuOption parentOption = (SubMenuOption) menuOption;
	            List<MenuOption> subMenuOptions = parentOption.getMenuOptions(userContext);
	            if (subMenuOptions.isEmpty()) {
	                // we don't want useless headers
	            } else {
	                optGroup = new OptGroup();
	                String menuName = messageSourceAccessor.getMessage(parentOption.getMenuText());
	                optGroup.addAttribute("label", e(menuName));

	                for (MenuOption subMenuOption : subMenuOptions) {
	                    if (subMenuOption instanceof SimpleMenuOption) {
	                        Option portalOption = createPortalOption((SimpleMenuOption) subMenuOption, messageSourceAccessor);
	                        optGroup.addElement(portalOption);
	                    } else {
	                        // multiple menu levels aren't supported here
	                        throw new ModuleConfigException("StandardMenuRenderer quick links only support two level menus");
	                    }
	                }
	                select.addElement(optGroup);
	            }
	        } else if (menuOption instanceof SimpleMenuOption) {
	            Option portalOption = createPortalOption((SimpleMenuOption) menuOption, messageSourceAccessor);
	            select.addElement(portalOption);
	        }
	    }
		
	    return select;
	}
	
	private String e(String input) {
        return StringEscapeUtils.escapeHtml4(input);
    }
    
    private Option createPortalOption(SimpleMenuOption subMenuOption, MessageSourceAccessor messageSourceAccessor) {
        SimpleMenuOption simpleMenuOption = subMenuOption;
        Option portalOption = new Option(simpleMenuOption.getUrl());
        String linkName = messageSourceAccessor.getMessage(simpleMenuOption.getMenuText());
        portalOption.addElement(e(linkName));
        return portalOption;
    }
}
