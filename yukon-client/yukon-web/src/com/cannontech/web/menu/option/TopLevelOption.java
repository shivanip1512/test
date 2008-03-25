package com.cannontech.web.menu.option;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.iterators.FilterIterator;

import com.cannontech.i18n.YukonMessageSourceResovable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.CheckUserPredicate;

/**
 * Represents a menu option that when selected should show a sub menu with
 * additional options to choose from.
 */
public class TopLevelOption extends BaseMenuOption {

    private List<SimpleMenuOption> optionChildren = new ArrayList<SimpleMenuOption>(4);

    public TopLevelOption(YukonMessageSourceResovable menuText) {
        super(menuText);
    }

    public void addSubLevelOption(SimpleMenuOption subLevelOption) {
        optionChildren.add(subLevelOption);
    }

    /**
     * @return List<SimpleMenuOption>
     */
    public List<SimpleMenuOption> getSubLevelOptions() {
        return optionChildren;
    }

    /**
     * @param user the current user logged in to the session
     * @return Iterator<SimpleMenuOption>
     */
    @SuppressWarnings("unchecked")
    public Iterator<SimpleMenuOption> getValidSubLevelOptions(YukonUserContext userContext) {
        return new FilterIterator(optionChildren.iterator(),
                                  new CheckUserPredicate(userContext));
    }

}
