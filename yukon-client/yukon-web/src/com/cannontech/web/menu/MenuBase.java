package com.cannontech.web.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.iterators.FilterIterator;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.producer.MenuOptionProducer;

public class MenuBase {
    private List<MenuOptionProducer> topLevelOptions = new ArrayList<MenuOptionProducer>();

    public List<MenuOptionProducer> getTopLevelOptions() {
        return topLevelOptions;
    }

    public void setTopLevelOptions(List<MenuOptionProducer> topLevelOptions) {
        this.topLevelOptions = topLevelOptions;
    }

    @SuppressWarnings("unchecked")
    public Iterator<MenuOptionProducer> getValidTopLevelOptions(YukonUserContext userContext) {
        return new FilterIterator(topLevelOptions.iterator(), new CheckUserPredicate(userContext));
    }
    
    public void addTopLevelOption(MenuOptionProducer topLevelOption) {
        topLevelOptions.add(topLevelOption);
    }

}
