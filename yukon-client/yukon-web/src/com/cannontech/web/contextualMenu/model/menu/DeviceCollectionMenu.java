package com.cannontech.web.contextualMenu.model.menu;

import com.cannontech.web.contextualMenu.CollectionCategory;

public class DeviceCollectionMenu extends ContextualMenu {
    
    @Override
    public CollectionCategory getCollectionCategory() {
        return CollectionCategory.DEVICE_COLLECTION;
    }
}
