package com.cannontech.database.model;

import java.util.Iterator;
import java.util.List;

import com.cannontech.database.cache.functions.DeviceConfigurationFuncs;
import com.cannontech.database.cache.functions.DeviceConfigurationFuncsImpl;
import com.cannontech.database.data.device.configuration.Category;
import com.cannontech.database.data.device.configuration.Type;
import com.cannontech.database.data.lite.LiteDeviceConfigurationCategory;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * Tree Model for device configuration category. The tree structure for device
 * configuration category is: Root node is 'Device Configuration Categories'
 * with a child node for each device configuration type. Each configuration type
 * node has a child for each configuration category type included in that
 * configuration type. Each category type node has a child node for each
 * category of that category type.
 */
public class DeviceConfigurationCategoryTreeModel extends DBTreeModel {

    public DeviceConfigurationCategoryTreeModel() {
        super(new DBTreeNode("Device Configuration Categories"));
    }

    public boolean isLiteTypeSupported(int liteType) {
        return liteType == LiteTypes.DEVICE_CONFIGURATION_CATEGORY;
    }

    public void update() {

        DBTreeNode rootNode = (DBTreeNode) getRoot();
        rootNode.removeAllChildren();

        DeviceConfigurationFuncs funcs = new DeviceConfigurationFuncsImpl();

        List<Type> configTypeList = funcs.getConfigTypes();
        Iterator<Type> configTypeIter = configTypeList.iterator();
        while (configTypeIter.hasNext()) {

            Type configType = configTypeIter.next();

            DummyTreeNode configTypeNode = new DummyTreeNode(configType.getName());

            List<Type> categoryTypeList = funcs.getCategoryTypesForConfigType(configType.getId());
            Iterator<Type> categoryTypeIter = categoryTypeList.iterator();
            while (categoryTypeIter.hasNext()) {

                Type categoryType = categoryTypeIter.next();

                if (!"TOU Schedule".equals(categoryType.getName())) {
                    DummyTreeNode categoryTypeNode = new DummyTreeNode(categoryType.getName());

                    List<Category> categoryList = funcs.getCategoriesForType(categoryType.getId());
                    Iterator<Category> categoryIter = categoryList.iterator();
                    while (categoryIter.hasNext()) {
                        Category category = categoryIter.next();

                        LiteDeviceConfigurationCategory liteCategory = new LiteDeviceConfigurationCategory(category.getId(),
                                                                                                           category.getName());

                        DBTreeNode categoryNode = new DBTreeNode(liteCategory);
                        categoryTypeNode.add(categoryNode);
                    }
                    configTypeNode.add(categoryTypeNode);
                }
            }
            rootNode.add(configTypeNode);
        }

    }

}
