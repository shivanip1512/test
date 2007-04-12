package com.cannontech.database.model;

import java.util.Iterator;
import java.util.List;

import com.cannontech.common.device.configuration.model.DeviceConfiguration;
import com.cannontech.common.device.configuration.model.Type;
import com.cannontech.common.device.configuration.service.DeviceConfigurationFuncs;
import com.cannontech.common.device.configuration.service.DeviceConfigurationFuncsImpl;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteDeviceConfiguration;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * Tree model for DeviceConfigurations. The tree structure for device
 * configurations is: Root node is 'Device Configurations' with a child node for
 * each configuration type. Each type node has a child for each configuration of
 * that type
 */
public class DeviceConfigurationTreeModel extends DBTreeModel {

    public DeviceConfigurationTreeModel() {
        super(new DBTreeNode("Device Configurations"));
    }

    public boolean isLiteTypeSupported(int liteType) {
        return liteType == LiteTypes.DEVICE_CONFIGURATION;
    }

    public void update() {

        DBTreeNode rootNode = (DBTreeNode) getRoot();
        rootNode.removeAllChildren();

        DeviceConfigurationFuncs funcs = new DeviceConfigurationFuncsImpl();

        List<Type> typeList = funcs.getConfigTypes();
        Iterator typeIter = typeList.iterator();
        while (typeIter.hasNext()) {

            Type type = (Type) typeIter.next();

            DummyTreeNode typeNode = new DummyTreeNode(type.getName());

            List<DeviceConfiguration> configList = funcs.getConfigsForType(type.getId());
            Iterator configIter = configList.iterator();
            while (configIter.hasNext()) {
                DeviceConfiguration config = (DeviceConfiguration) configIter.next();

                LiteDeviceConfiguration liteConfig = new LiteDeviceConfiguration(config.getId(),
                                                                                 config.getName());

                DBTreeNode deviceNode = new DBTreeNode(liteConfig);
                typeNode.add(deviceNode);
            }

            rootNode.add(typeNode);
        }

    }

    @Override
    /**
     * This method was overridden to insert new device configurations at the
     * correct level in the tree
     */
    public boolean insertTreeObject(LiteBase lb) {
        if (lb == null || !isLiteTypeSupported(lb.getLiteType()))
            return false;

        // Load the device configuration to get the configuration type
        DeviceConfigurationFuncs funcs = new DeviceConfigurationFuncsImpl();
        DeviceConfiguration configuration = funcs.loadConfig(lb.getLiteID());
        Type type = configuration.getType();

        DBTreeNode rootNode = (DBTreeNode) getRoot();

        // Loop through each of the configuration type nodes until we find the
        // correct type and then insert the new configuration as a child to that
        // node
        for (int index = 0; index < rootNode.getChildCount(); index++) {

            DummyTreeNode child = (DummyTreeNode) rootNode.getChildAt(index);
            if (((String) child.getUserObject()).equals(type.getName())) {

                DBTreeNode deviceNode = new DBTreeNode(lb);

                int[] ind = { child.getChildCount() };

                child.insert(deviceNode, ind[0]);

                nodeChanged(child);
                nodesWereInserted(child, ind);

                return true;
            }
        }

        return false;
    }
}
