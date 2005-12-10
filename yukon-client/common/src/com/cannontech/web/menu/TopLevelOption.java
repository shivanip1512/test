package com.cannontech.web.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.iterators.FilterIterator;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Represents a menu option that when selected should show a sub menu
 * with additional options to choose from.
 */
public class TopLevelOption extends BaseMenuOption {
    private List optionChildren = new ArrayList(4);

    public TopLevelOption(String topOptionName) {
        linkName = topOptionName;
    }

    public void addSubLevelOption(SimpleMenuOption subLevelOption) {
        optionChildren.add(subLevelOption);
    }

    /**
     * @return List<SimpleMenuOption>
     */
    public List getSubLevelOptions() {
        return optionChildren;
    }

    /**
     * @param user the current user logged in to the session
     * @return Iterator<SimpleMenuOption>
     */
    public Iterator getValidSubLevelOptions(LiteYukonUser user) {
        return new FilterIterator(optionChildren.iterator(), new CheckUserPredicate(user));
    }

}
