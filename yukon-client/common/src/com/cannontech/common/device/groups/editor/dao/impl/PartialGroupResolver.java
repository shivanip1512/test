package com.cannontech.common.device.groups.editor.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * The purpose of this class is to efficiently resolve a collection of PartialDeviceGroups
 * into fully populated StoredDeviceGroups. It ensures that the database is hit a minimum amount
 * to retrieve additional parental StoredDeviceGroups that weren't included in the original
 * collection of PartialDeviceGroups. The resolver can be prepopulated with a collection of "known
 * groups" this is useful because often the parent or base group of a query is known ahead of 
 * time and can simply be passed in to prevent an additional database hit to retrieve it and its 
 * ancestors.
 */
public class PartialGroupResolver {
    private PartialDeviceGroupDao partialDeviceGroupDao;
    
    private Map<Integer, StoredDeviceGroup> knownGroupLookup = Maps.newHashMapWithExpectedSize(1);

    /**
     * Instantiates the resolver. 
     * @param partialDeviceGroupDao
     * @param base provides a starting point for the resolver; all groups to be resolved must be a descendent of this group
     */
    public PartialGroupResolver(PartialDeviceGroupDao partialDeviceGroupDao, StoredDeviceGroup base) {
        this.partialDeviceGroupDao = partialDeviceGroupDao;
        addKnownGroups(base);
    }

    /**
     * Used to prepopulate the resolver with any groups that happen to be known ahead
     * of time. This method also causes each groups ancestors to be cached as well.
     * 
     * @param knownGroups
     */
    public void addKnownGroups(StoredDeviceGroup... knownGroups) {
        for (StoredDeviceGroup known : knownGroups) {
            knownGroupLookup.put(known.getId(), known);
        }
    }
    
    /**
     * This method converts a Collection of PartialDeviceGroups into a Collection of
     * StoredDeviceGroups. If the PartialDeviceGroups refer to parents that aren't
     * themselves contained in the input Collection and weren't passed in as a known
     * group, the PartialDeviceGroupDao will be used to lookup the parent in the 
     * database.
     * 
     * @param input - A collection of PartialDeviceGroups to be processed
     * @param output - A collection (typically empty) which the output will be placed in
     */
    public void collectMissingPartials(Collection<PartialDeviceGroup> input,
            Set<Integer> neededParentIds,
            Map<Integer, PartialDeviceGroup> lookupById) {
         
        for (PartialDeviceGroup partialDeviceGroup : input) {
            lookupById.put(partialDeviceGroup.getStoredDeviceGroup().getId(), partialDeviceGroup);
            if (partialDeviceGroup.getParentGroupId() != null) {
                // we don't want null to be a key
                neededParentIds.add(partialDeviceGroup.getParentGroupId());
            }
        }
        
        SetView<Integer> missing = Sets.difference(neededParentIds, lookupById.keySet());
        missing = Sets.difference(missing, knownGroupLookup.keySet());
        // missing now represents all of the ids we know we need to load at
        // this point, there may be other ids that are required, but we won't
        // know about them until we load these (the recursive call will
        // handle them)
        if (missing.isEmpty()) {
            return;
        }
        
        Set<PartialDeviceGroup> nextInput = partialDeviceGroupDao.getPartialGroupsById(missing);
        collectMissingPartials(nextInput, neededParentIds, lookupById);
    }

    public void resolvePartials(Collection<PartialDeviceGroup> input,
            Collection<StoredDeviceGroup> output) {
        Set<Integer> neededParentIds = Sets.newHashSet();
        Map<Integer, PartialDeviceGroup> lookupById = Maps.newHashMap();
        // collect any intermediary partials (groups between root and the
        // groups represented by input)
        collectMissingPartials(input, neededParentIds, lookupById);
        
        // using lookupById as a repository of all required groups,
        // build StoredDeviceGroups for everything in input and add to output
        for (PartialDeviceGroup partialDeviceGroup : input) {
            StoredDeviceGroup group = connectPartial(partialDeviceGroup, lookupById);
            output.add(group);
        }
    }
    
    private StoredDeviceGroup connectPartial(
            PartialDeviceGroup partialDeviceGroup,
            Map<Integer, PartialDeviceGroup> lookupById) {
        Integer parentGroupId = partialDeviceGroup.getParentGroupId();
        if (parentGroupId == null) {
            return partialDeviceGroup.getStoredDeviceGroup();
        }
        // look in knownGroup cache for parent
        StoredDeviceGroup parent = knownGroupLookup.get(parentGroupId);
        if (parent == null) {
            // not already cached, get the partial that represents
            // our parent and recurse to build entire chain
            PartialDeviceGroup parentPartial = lookupById.get(parentGroupId);
            parent = connectPartial(parentPartial, lookupById);
            knownGroupLookup.put(parent.getId(), parent); // cache known parents
        }
        // construct the resulting StoredDeviceGroup and return
        StoredDeviceGroup group = partialDeviceGroup.getStoredDeviceGroup();
        group.setParent(parent);
        return group;
    }

    /**
     * This is a shortcut method for processing a single PartialDeviceGroup.
     * 
     * @param partial - A PartialDeviceGroup to be resolved
     * @return - The resolved StoredDeviceGroup
     */
    public StoredDeviceGroup resolvePartial(PartialDeviceGroup partial) {
        // special case for root group
        if (partial.getParentGroupId() == null) {
            // this saves a lot of memory allocation and happens quite often
            return partial.getStoredDeviceGroup();
        }
        ArrayList<StoredDeviceGroup> result = new ArrayList<StoredDeviceGroup>(1);
        
        resolvePartials(Collections.singletonList(partial), result);
        
        return result.get(0);
    }

    /**
     * This is a shortcut method for processing a Collection of PartialDeviceGroups
     * and returning a List. It can be used instead of creating a separate ArrayList
     * to pass into the input/output version of the resolvePartials method.
     * @param input
     * @return
     */
    public List<StoredDeviceGroup> resolvePartials(Collection<PartialDeviceGroup> input) {
        ArrayList<StoredDeviceGroup> result = new ArrayList<StoredDeviceGroup>(1);
        
        resolvePartials(input, result);
        
        return result;
    }
}
