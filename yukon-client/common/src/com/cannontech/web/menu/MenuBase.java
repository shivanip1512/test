package com.cannontech.web.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.iterators.FilterIterator;

import com.cannontech.database.data.lite.LiteYukonUser;

public class MenuBase {
    private List topLevelOptions = new ArrayList();

    public List getTopLevelOptions() {
        return topLevelOptions;
    }

    public void setTopLevelOptions(List topLevelOptions) {
        this.topLevelOptions = topLevelOptions;
    }

    public Iterator getValidTopLevelOptions(LiteYukonUser user) {
        return new FilterIterator(topLevelOptions.iterator(), new CheckUserPredicate(user));
    }
    
    public void addTopLevelOption(BaseMenuOption topLevelOption) {
        topLevelOptions.add(topLevelOption);
    }

}
