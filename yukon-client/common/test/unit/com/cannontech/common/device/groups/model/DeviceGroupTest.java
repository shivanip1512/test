package com.cannontech.common.device.groups.model;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.common.device.groups.dao.DeviceGroupType;

public class DeviceGroupTest {

    private MutableDeviceGroup root;
    private MutableDeviceGroup a;
    private MutableDeviceGroup b;
    private MutableDeviceGroup b2;
    private MutableDeviceGroup c;
    private MutableDeviceGroup cDupe;

    @Before
    public void setUp() throws Exception {
        root = new TestDeviceGroup();
        root.setParent(null);
        root.setName("");
        root.setType(DeviceGroupType.STATIC);
        
        a = new TestDeviceGroup();
        a.setParent(root);
        a.setName("a");
        a.setType(DeviceGroupType.STATIC);
        
        b = new TestDeviceGroup();
        b.setParent(a);
        b.setName("b");
        b.setType(DeviceGroupType.STATIC);
        
        b2 = new TestDeviceGroup();
        b2.setParent(a);
        b2.setName("b2");
        b2.setType(DeviceGroupType.STATIC);
        
        c = new TestDeviceGroup();
        c.setParent(b);
        c.setName("c");
        c.setType(DeviceGroupType.STATIC);

        cDupe = new TestDeviceGroup();
        cDupe.setParent(b);
        cDupe.setName("c");
        cDupe.setType(DeviceGroupType.STATIC);
        
    }
    
    @Test
    public void testFullName() {
        Assert.assertEquals("/a/b/c", c.getFullName());
        Assert.assertEquals("/a/b2", b2.getFullName());
        Assert.assertEquals("/", root.getFullName());
    }
    
    @Test
    public void testEquality() {
        Assert.assertTrue(c.equals(cDupe));
        Assert.assertTrue(c.hashCode() == cDupe.hashCode());
        Assert.assertTrue(cDupe.equals(c));
        Assert.assertFalse(b.equals(b2));
    }
    
    @Test
    public void testIsDescendantOf() {
        Assert.assertTrue(c.isDescendantOf(b));
        Assert.assertTrue(b2.isDescendantOf(a));
        Assert.assertTrue(b.isDescendantOf(a));
        Assert.assertTrue(a.isDescendantOf(root));
        Assert.assertTrue(c.isDescendantOf(root));
        
        Assert.assertFalse(b.isDescendantOf(c));
        Assert.assertFalse(b.isDescendantOf(b2));
        Assert.assertFalse(a.isDescendantOf(b));
        Assert.assertFalse(root.isDescendantOf(a));
        Assert.assertFalse(root.isDescendantOf(c));
        
        Assert.assertFalse(root.isDescendantOf(root));
        Assert.assertFalse(c.isDescendantOf(c));
        
        Assert.assertFalse(c.isDescendantOf(b2));
        Assert.assertFalse(b2.isDescendantOf(c));
    }
    
    @Test
    public void testIsChildOf() {
        Assert.assertTrue(c.isChildOf(b));
        Assert.assertTrue(b2.isChildOf(a));
        Assert.assertTrue(b.isChildOf(a));
        Assert.assertTrue(a.isChildOf(root));
        Assert.assertFalse(c.isChildOf(root));
        
        Assert.assertFalse(b.isChildOf(c));
        Assert.assertFalse(b.isChildOf(b2));
        Assert.assertFalse(a.isChildOf(b));
        Assert.assertFalse(root.isChildOf(a));
        Assert.assertFalse(root.isChildOf(c));

        Assert.assertFalse(root.isChildOf(root));
        Assert.assertFalse(c.isChildOf(c));
        
        Assert.assertFalse(c.isChildOf(b2));
        Assert.assertFalse(b2.isChildOf(c));
    }
    
    private class TestDeviceGroup extends MutableDeviceGroup {

        @Override
        public boolean isEditable() {
            return false;
        }

        @Override
        public boolean isModifiable() {
            return false;
        }
        
        @Override
        public boolean isHidden() {
            return false;
        }
        
    }

}
