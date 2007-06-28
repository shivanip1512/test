package com.cannontech.common.device.groups.service;

import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.groups.dao.DeviceGroupDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.core.dao.NotFoundException;

public class DeviceGroupTreeFactory {
    private DeviceGroupDao deviceGroupDao;
    
    public TreeNode getRootNode() {
        DeviceGroup rootGroup = deviceGroupDao.getRootGroup();
        TreeNode root = createNode(rootGroup);
        return root;
    }
    
    public TreeModel getModel() {
        DefaultTreeModel model = new DefaultTreeModel(getRootNode(), true);
        return model;
    }
    
    private DefaultMutableTreeNode createNode(DeviceGroup deviceGroup) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(deviceGroup);
        //node.setParent(parent);
        
        List<? extends DeviceGroup> childGroups = deviceGroupDao.getChildGroups(deviceGroup);
        for (DeviceGroup child : childGroups) {
            DefaultMutableTreeNode childNode = createNode(child);
            node.add(childNode);
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
