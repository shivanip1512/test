package com.cannontech.web.contextualMenu.model.menu;

import com.cannontech.web.contextualMenu.CollectionCategory;

public class SingleDeviceMenu extends ContextualMenu {
    
    @Override
    public CollectionCategory getCollectionCategory() {
        return CollectionCategory.PAO_ID;
    }

}
