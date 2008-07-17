package com.cannontech.common.device.groups.service;

import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.predicate.Predicate;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.model.DBTreeModel;
import com.cannontech.database.model.LiteBaseTreeModel;

public class DeviceGroupTreeFactory {
    private final class NullPredicate implements Predicate<DeviceGroup> {
        public boolean evaluate(DeviceGroup object) {
            return true;
        }
    }

    public final class LiteBaseModel extends DBTreeModel {
        private final boolean devices;
        private final NullPredicate nullPredicate;

        public LiteBaseModel(TreeNode root, boolean devices, NullPredicate nullPredicate) {
            super(root);
            this.devices = devices;
            this.nullPredicate = nullPredicate;
        }

        public boolean isLiteTypeSupported(int liteType) {
            return false;
        }

        public void update() {
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getRootNode(devices, nullPredicate);
            // this is lame, but this is life... blame TreeViewPanel
            DefaultMutableTreeNode newRoot = new DefaultMutableTreeNode("Device Groups");
            Enumeration enumeration = rootNode.children();
            while (enumeration.hasMoreElements()) {
                MutableTreeNode object = (MutableTreeNode) enumeration.nextElement();
                newRoot.add(object);
            }
            setRoot(newRoot);
        }

        @Override
        public String toString() {
            return "Device Groups";
        }
    }

    private DeviceGroupProviderDao deviceGroupDao;
    private DeviceGroupService deviceGroupService;
    
    public TreeNode getRootNode(boolean includeDevices, Predicate<DeviceGroup> deviceGroupFilter) {
        DeviceGroup rootGroup = deviceGroupDao.getRootGroup();
        TreeNode root = createNode(rootGroup, includeDevices, deviceGroupFilter);
        return root;
    }
    
    public LiteBaseTreeModel getLiteBaseModel(final boolean includeDevices) {
        DBTreeModel model = new LiteBaseModel(null, includeDevices, new NullPredicate());
        return model;
    }
    
    public TreeModel getModel() {
        return getModel(new NullPredicate());
    }
    
    public TreeModel getStaticOnlyModel() {
        final DeviceGroup systemDeviceGrp = deviceGroupService.resolveGroupName(SystemGroupEnum.SYSTEM.getFullPath());   
        return getModel(new Predicate<DeviceGroup>() {
            public boolean evaluate(DeviceGroup object) {
                return object.getType().equals(DeviceGroupType.STATIC) && !object.isEqualToOrDescendantOf(systemDeviceGrp); 
            }
        });
    }
    
    public TreeModel getModel(Predicate<DeviceGroup> deviceGroupFilter) {
        DefaultTreeModel model = new DefaultTreeModel(getRootNode(false, deviceGroupFilter), true);
        return model;
    }
    
    private DefaultMutableTreeNode createNode(DeviceGroup deviceGroup, boolean includeDevices, Predicate<DeviceGroup> deviceGroupFilter) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(deviceGroup);
        
        List<? extends DeviceGroup> childGroups = deviceGroupDao.getChildGroups(deviceGroup);
        for (DeviceGroup child : childGroups) {
            if (deviceGroupFilter.evaluate(child)) {
                DefaultMutableTreeNode childNode = createNode(child, includeDevices, deviceGroupFilter);
                node.add(childNode);
            }
        }
        if (includeDevices) {
            Set<YukonDevice> childDevices = deviceGroupDao.getChildDevices(deviceGroup);
            for (YukonDevice child : childDevices) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
                node.add(childNode);
            }
        }
        return node;
    }
        
    public TreePath getPathForGroup(TreeNode from, DeviceGroup group) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) from;
        DefaultMutableTreeNode node = null;
        Enumeration e = root.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode current = (DefaultMutableTreeNode)e.nextElement();
            if (group.equals(current.getUserObject())) {
                node = current;
                break;
            }
        }
        
        if (node == null) {
            throw new NotFoundException("Group " + group + " was not found under " + from);
        }
        
        TreeNode[] pathArray = node.getPath();
        TreePath path = new TreePath(pathArray);
        return path;
    }
    
    public DeviceGroup getGroupForPath(TreePath path) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        return (DeviceGroup) node.getUserObject();
    }
    
    @Required
    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }
    
    @Required
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
}
