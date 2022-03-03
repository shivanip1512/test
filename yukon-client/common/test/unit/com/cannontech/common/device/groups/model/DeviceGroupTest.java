package com.cannontech.common.device.groups.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cannontech.common.device.groups.dao.DeviceGroupType;

public class DeviceGroupTest {

    private MutableDeviceGroup root;
    private MutableDeviceGroup a;
    private MutableDeviceGroup b;
    private MutableDeviceGroup b2;
    private MutableDeviceGroup c;
    private MutableDeviceGroup cDupe;

    @BeforeEach
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
        assertEquals("/a/b/c", c.getFullName());
        assertEquals("/a/b2", b2.getFullName());
        assertEquals("/", root.getFullName());
    }
    
    @Test
    public void testEquality() {
        assertTrue(c.equals(cDupe));
        assertTrue(c.hashCode() == cDupe.hashCode());
        assertTrue(cDupe.equals(c));
        assertFalse(b.equals(b2));
    }
    
    @Test
    public void testIsDescendantOf() {
        assertTrue(c.isDescendantOf(b));
        assertTrue(b2.isDescendantOf(a));
        assertTrue(b.isDescendantOf(a));
        assertTrue(a.isDescendantOf(root));
        assertTrue(c.isDescendantOf(root));
        
        assertFalse(b.isDescendantOf(c));
        assertFalse(b.isDescendantOf(b2));
        assertFalse(a.isDescendantOf(b));
        assertFalse(root.isDescendantOf(a));
        assertFalse(root.isDescendantOf(c));
        
        assertFalse(root.isDescendantOf(root));
        assertFalse(c.isDescendantOf(c));
        
        assertFalse(c.isDescendantOf(b2));
        assertFalse(b2.isDescendantOf(c));
    }
    
    @Test
    public void testIsChildOf() {
        assertTrue(c.isChildOf(b));
        assertTrue(b2.isChildOf(a));
        assertTrue(b.isChildOf(a));
        assertTrue(a.isChildOf(root));
        assertFalse(c.isChildOf(root));
        
        assertFalse(b.isChildOf(c));
        assertFalse(b.isChildOf(b2));
        assertFalse(a.isChildOf(b));
        assertFalse(root.isChildOf(a));
        assertFalse(root.isChildOf(c));

        assertFalse(root.isChildOf(root));
        assertFalse(c.isChildOf(c));
        
        assertFalse(c.isChildOf(b2));
        assertFalse(b2.isChildOf(c));
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
