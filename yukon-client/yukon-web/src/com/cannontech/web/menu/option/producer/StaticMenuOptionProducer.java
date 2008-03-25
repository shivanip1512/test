package com.cannontech.web.menu.option.producer;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.iterators.FilterIterator;

import com.cannontech.i18n.YukonMessageSourceResolvableImpl;
import com.cannontech.i18n.YukonMessageSourceResovable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.CheckUserPredicate;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionAction;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;
import com.cannontech.web.menu.option.SubMenuOption;

/**
 * Menu option producer class used for all static menu options
 */
public class StaticMenuOptionProducer extends MenuOptionProducerBase {

    private List<MenuOptionProducer> childProducerList;
    private YukonMessageSourceResovable menuText;

    private String script;
    private String url;

    @Override
    public List<MenuOption> getMenuOptions(YukonUserContext userContext) {

        MenuOption option = null;

        if (script != null) {
            // Menu option with script
            SimpleMenuOptionAction tempOption = new SimpleMenuOptionAction(menuText);
            tempOption.setScript(script);
            tempOption.setId(this.getId());
            option = tempOption;
        } else if (url != null) {
            // Menu option with url
            SimpleMenuOptionLink tempOption = new SimpleMenuOptionLink(menuText);
            tempOption.setLinkUrl(url);
            tempOption.setId(this.getId());
            option = tempOption;
        } else {
            // Plain menu option
            SubMenuOption tempOption = new SubMenuOption(menuText);
            tempOption.setId(this.getId());
            option = tempOption;
        }

        return Collections.singletonList(option);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<MenuOptionProducer> getChildren(YukonUserContext userContext) {

        return new FilterIterator(childProducerList.iterator(),
                                  new CheckUserPredicate(userContext));

    }

    public void setKey(String key) {
        this.menuText = new YukonMessageSourceResolvableImpl(key);
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void setLinkUrl(String url) {
        this.url = url;
    }

    public void setChildren(List<MenuOptionProducer> childProducerList) {
        this.childProducerList = childProducerList;
    }

    @Override
    public boolean hasChildren(YukonUserContext userContext) {

        if (childProducerList == null || childProducerList.size() == 0) {
            return false;
        }

        for (MenuOptionProducer producer : childProducerList) {
            if (producer.isValidForUser(userContext)) {
                return true;
            }
        }

        return false;
    }

}
