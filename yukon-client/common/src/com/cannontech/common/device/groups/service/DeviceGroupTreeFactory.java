package com.cannontech.common.device.groups.service;

import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.model.DBTreeModel;
import com.cannontech.database.model.LiteBaseTreeModel;

public class DeviceGroupTreeFactory {
    public final class LiteBaseModel extends DBTreeModel {
        private final boolean devices;

        public LiteBaseModel(TreeNode root, boolean devices) {
            super(root);
            this.devices = devices;
        }

        public boolean isLiteTypeSupported(int liteType) {
            return false;
        }

        public void update() {
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getRootNode(devices);
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

    private DeviceGroupDao deviceGroupDao;
    
    public TreeNode getRootNode(boolean includeDevices) {
        DeviceGroup rootGroup = deviceGroupDao.getRootGroup();
        TreeNode root = createNode(rootGroup, includeDevices);
        return root;
    }
    
    public LiteBaseTreeModel getLiteBaseModel(final boolean includeDevices) {
        DBTreeModel model = new LiteBaseModel(null, includeDevices);
        return model;
    }
    
    public TreeModel getModel() {
        DefaultTreeModel model = new DefaultTreeModel(getRootNode(false), true);
        return model;
    }
    
    private DefaultMutableTreeNode createNode(DeviceGroup deviceGroup, boolean includeDevices) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(deviceGroup);
        
        List<? extends DeviceGroup> childGroups = deviceGroupDao.getChildGroups(deviceGroup);
        for (DeviceGroup child : childGroups) {
            DefaultMutableTreeNode childNode = createNode(child, includeDevices);
            node.add(childNode);
        }
        if (includeDevices) {
            List<YukonDevice> childDevices = deviceGroupDao.getChildDevices(deviceGroup);
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
    public void setDeviceGroupDao(DeviceGroupDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }
    
}
