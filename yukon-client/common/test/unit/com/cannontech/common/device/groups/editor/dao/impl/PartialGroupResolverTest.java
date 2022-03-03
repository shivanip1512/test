package com.cannontech.common.device.groups.editor.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cannontech.common.device.groups.dao.DeviceGroupPermission;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public class PartialGroupResolverTest {
    private List<PartialDeviceGroup> partialGroupList;
    private List<StoredDeviceGroup> storedGroupList;
    private int nextId = 0;

    private StoredDeviceGroup root = createDeviceGroup(nextId++, null, "");  // -- /
    private StoredDeviceGroup a1 = createDeviceGroup(nextId++, root, "A1");  // -- /a1
    private StoredDeviceGroup a2 = createDeviceGroup(nextId++, a1, "A2");  // -- /a1/a2
    private StoredDeviceGroup a3 = createDeviceGroup(nextId++, a2, "A3");  // -- /a1/a2/a3
    private StoredDeviceGroup b1 = createDeviceGroup(nextId++, root, "B1");  // -- /b1
    private StoredDeviceGroup b2 = createDeviceGroup(nextId++, b1, "B2");  // -- /b1/b2
    private StoredDeviceGroup c1 = createDeviceGroup(nextId++, root, "C1");  // -- /c1
    private StoredDeviceGroup c2 = createDeviceGroup(nextId++, c1, "C2");  // -- /c1/c2
    private StoredDeviceGroup c3 = createDeviceGroup(nextId++, c2, "C3");  // -- /c1/c2/c3
    private StoredDeviceGroup c4 = createDeviceGroup(nextId++, c3, "C4");  // -- /c1/c2/c3/c4
    private StoredDeviceGroup c5 = createDeviceGroup(nextId++, c1, "C5");  // -- /c1/c5
    private StoredDeviceGroup c6 = createDeviceGroup(nextId++, c1, "C6");  // -- /c1/c6
    
    @BeforeEach
    public void setUp() throws Exception {
        partialGroupList = new ArrayList<PartialDeviceGroup>();
        storedGroupList = new ArrayList<StoredDeviceGroup>();
    }
    
    private void addToCache(StoredDeviceGroup... groups) {
        for (StoredDeviceGroup group : groups) {
            StoredDeviceGroup clone1 = cloneGroup(group);
            storedGroupList.add(clone1);
            
            PartialDeviceGroup partialDeviceGroup = createPartial(group);
            partialGroupList.add(partialDeviceGroup);
        }
    }

    private StoredDeviceGroup createDeviceGroup(Integer id, StoredDeviceGroup parent, String name) {
        StoredDeviceGroup sdg = new StoredDeviceGroup();
        sdg.setId(id);
        sdg.setType(DeviceGroupType.STATIC);
        sdg.setName(name);
        sdg.setParent(parent);
        sdg.setPermission(DeviceGroupPermission.EDIT_MOD);
        
        return sdg;
    }
    
    private StoredDeviceGroup cloneGroup(StoredDeviceGroup group) {
        StoredDeviceGroup sdg = new StoredDeviceGroup();
        sdg.setId(group.getId());
        sdg.setType(group.getType());
        sdg.setName(group.getName());
        sdg.setParent(group.getParent());
        sdg.setPermission(group.getPermission());
        
        return sdg;
    }

    @Test
    public void test_perfect_order() {
        addToCache(root);
        addToCache(a1);
        addToCache(a2);
        addToCache(a3);
        addToCache(b1);
        addToCache(b2);
        addToCache(c1);
        addToCache(c2);
        addToCache(c3);
        addToCache(c4);
        addToCache(c5);
        addToCache(c6);
        
        PartialGroupResolver partialGroupResolver = new PartialGroupResolver(new PartialDeviceGroupDao() {
            @Override
            public Set<PartialDeviceGroup> getPartialGroupsById(Set<Integer> neededIds) {
                throw new RuntimeException("All parents were supplied, this shouldn't be called");
            }
        }, root);
        
        Set<StoredDeviceGroup> result = new HashSet<StoredDeviceGroup>();
        
        partialGroupResolver.resolvePartials(partialGroupList, result);
        
        HashSet<StoredDeviceGroup> expected = new HashSet<StoredDeviceGroup>(storedGroupList);
        
        assertEquals(expected, result);
        
    }

    @Test
    public void test_worst_case_order() {
        addToCache(c6);
        addToCache(c5);
        addToCache(c4);
        addToCache(c3);
        addToCache(c2);
        addToCache(c1);
        addToCache(b2);
        addToCache(b1);
        addToCache(a3);
        addToCache(a2);
        addToCache(a1);
        addToCache(root);
        
        PartialGroupResolver partialGroupResolver = new PartialGroupResolver(new PartialDeviceGroupDao() {
            @Override
            public Set<PartialDeviceGroup> getPartialGroupsById(Set<Integer> neededIds) {
                throw new RuntimeException("All parent's were supplied, this shouldn't be called");
            }
        }, root);
        
        Set<StoredDeviceGroup> result = new HashSet<StoredDeviceGroup>();
        
        partialGroupResolver.resolvePartials(partialGroupList, result);
        
        HashSet<StoredDeviceGroup> expected = new HashSet<StoredDeviceGroup>(storedGroupList);
        
        assertEquals(expected, result);
        
    }
    
    @Test
    public void test_worst_case_order_missing_root() {
        addToCache(c6);
        addToCache(c5);
        addToCache(c4);
        addToCache(c3);
        addToCache(c2);
        addToCache(c1);
        addToCache(b2);
        addToCache(b1);
        addToCache(a3);
        addToCache(a2);
        addToCache(a1);
        
        PartialGroupResolver partialGroupResolver = new PartialGroupResolver(new PartialDeviceGroupDao() {
            @Override
            public Set<PartialDeviceGroup> getPartialGroupsById(Set<Integer> neededIds) {
                Set<PartialDeviceGroup> result = new HashSet<PartialDeviceGroup>();
                for (Integer id : neededIds) {
                    if (id == root.getId()) {
                        result.add(createPartial(root));
                    } else {
                        throw new RuntimeException("This should only have been called for root");
                    }
                }
                return result;
            }
        }, root);
        
        Set<StoredDeviceGroup> result = new HashSet<StoredDeviceGroup>();
        
        partialGroupResolver.resolvePartials(partialGroupList, result);
        
        HashSet<StoredDeviceGroup> expected = new HashSet<StoredDeviceGroup>(storedGroupList);
        
        assertEquals(expected, result);
    }
    
    @Test
    public void test_random_order_just_leafs() {
        addToCache(b2);
        addToCache(c6);
        addToCache(c5);
        addToCache(a3);
        addToCache(c4);
        
        final AtomicInteger callCount = new AtomicInteger(0); // using as a mutable
        
        PartialGroupResolver partialGroupResolver = new PartialGroupResolver(new PartialDeviceGroupDao() {
            @Override
            public Set<PartialDeviceGroup> getPartialGroupsById(Set<Integer> neededIds) {
                callCount.incrementAndGet();
                Set<PartialDeviceGroup> result = new HashSet<PartialDeviceGroup>();
                for (Integer id : neededIds) {
                    if (id == root.getId()) {
                        result.add(createPartial(root));
                    } else if (id == a1.getId()) {
                        result.add(createPartial(a1));
                    } else if (id == a2.getId()) {
                        result.add(createPartial(a2));
                    } else if (id == b1.getId()) {
                        result.add(createPartial(b1));
                    } else if (id == c1.getId()) {
                        result.add(createPartial(c1));
                    } else if (id == c2.getId()) {
                        result.add(createPartial(c2));
                    } else if (id == c3.getId()) {
                        result.add(createPartial(c3));
                    } else {
                        throw new RuntimeException("This should only have been called for root");
                    }
                }
                return result;
            }
        }, root);
        
        Set<StoredDeviceGroup> result = new HashSet<StoredDeviceGroup>();
        
        partialGroupResolver.resolvePartials(partialGroupList, result);
        
        HashSet<StoredDeviceGroup> expected = new HashSet<StoredDeviceGroup>(storedGroupList);
        
        assertEquals(expected, result);
        assertEquals(2, callCount.get()); // this is 2 due to the depth of the groups
    }
    @Test
    public void test_leaf_with_initialization() {
        addToCache(b2);
        addToCache(c6);
        addToCache(c5);
        addToCache(a3);
        addToCache(c4);
        
        final AtomicInteger callCount = new AtomicInteger(0); // using as a mutable
        
        PartialGroupResolver partialGroupResolver = new PartialGroupResolver(new PartialDeviceGroupDao() {
            @Override
            public Set<PartialDeviceGroup> getPartialGroupsById(Set<Integer> neededIds) {
                callCount.incrementAndGet();
                Set<PartialDeviceGroup> result = new HashSet<PartialDeviceGroup>();
                for (Integer id : neededIds) {
                    if (id == c2.getId()) {
                        result.add(createPartial(c2));
                    } else if (id == c3.getId()) {
                        result.add(createPartial(c3));
                    } else {
                        throw new RuntimeException("This should only have been called for c2 and c3: " + id);
                    }
                }
                return result;
            }
        }, root);
        
        partialGroupResolver.addKnownGroups(a1, a2, b1, c1);
        
        Set<StoredDeviceGroup> result = new HashSet<StoredDeviceGroup>();
        
        partialGroupResolver.resolvePartials(partialGroupList, result);
        
        HashSet<StoredDeviceGroup> expected = new HashSet<StoredDeviceGroup>(storedGroupList);
        
        assertEquals(expected, result);
        assertEquals(2, callCount.get()); // this is 2 due to the depth of the groups
    }
    
    private PartialDeviceGroup createPartial(StoredDeviceGroup group) {
        StoredDeviceGroup clone = cloneGroup(group);
        StoredDeviceGroup parent = (StoredDeviceGroup) clone.getParent();
        clone.setParent(null);
        PartialDeviceGroup partialDeviceGroup = new PartialDeviceGroup();
        partialDeviceGroup.setStoredDeviceGroup(clone);
        partialDeviceGroup.setParentGroupId(parent != null ? parent.getId() : null);
        
        return partialDeviceGroup;
    }
    
}
