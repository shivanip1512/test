package com.cannontech.common.device.groups.editor.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.util.MapSet;

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
    
    private Map<Integer, StoredDeviceGroup> knownGroupLookup = new HashMap<Integer, StoredDeviceGroup>();
    private MapSet<Integer, PartialDeviceGroup> waitingPartials = new MapSet<Integer, PartialDeviceGroup>();
    private Set<Integer> allSeendIds = new HashSet<Integer>();

    
    public PartialGroupResolver(PartialDeviceGroupDao partialDeviceGroupDao) {
        this.partialDeviceGroupDao = partialDeviceGroupDao;
    }

    /**
     * Used to prepopulate the resolver with any groups that happen to be known ahead
     * of time. This method also causes each groups ancestors to be cached as well.
     * 
     * @param knownGroups
     */
    public void addKnownGroups(StoredDeviceGroup... knownGroups) {
        for (StoredDeviceGroup known : knownGroups) {
            StoredDeviceGroup group = known;
            while (group != null) {
                knownGroupLookup.put(group.getId(), group);
                group = (StoredDeviceGroup) group.getParent(); // a stored parent is always stored
            }
        }
    }
    
    private void recordKnownGroup(StoredDeviceGroup group) {
        knownGroupLookup.put(group.getId(), group);
    }
    
    private boolean processPartial(PartialDeviceGroup partialGroup) {
        boolean foundSomething = false;
        StoredDeviceGroup group = partialGroup.getStoredDeviceGroup();
        allSeendIds.add(group.getId());
        StoredDeviceGroup parentGroup = knownGroupLookup.get(partialGroup.getParentGroupId());
        if (partialGroup.getParentGroupId() == null || parentGroup != null) {
            group.setParent(parentGroup);
            recordKnownGroup(group);
            waitingPartials.removeValue(partialGroup.getParentGroupId(), partialGroup);
            foundSomething = true;
            // since we found something, let's see if anyone's waiting on it
            // use a copy to prevent concurrent modification (although, this is the slowest part
            // of the whole algorithm)
            Set<PartialDeviceGroup> somePartials = new HashSet<PartialDeviceGroup>(waitingPartials.get(group.getId()));
            for (PartialDeviceGroup partialDeviceGroup : somePartials) {
                processPartial(partialDeviceGroup);
            }
        } else {
            waitingPartials.add(partialGroup.getParentGroupId(), partialGroup);
        }
        return foundSomething;
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
    public void resolvePartials(Collection<PartialDeviceGroup> input,
                                Collection<StoredDeviceGroup> output) {
        // loop over input collection once
        // after this, everything will either be waiting or known
        for (PartialDeviceGroup partial : input) {
            processPartial(partial);
        }

        // request anything we haven't seen, but are waiting on
        while (!waitingPartials.isEmpty()) {
            Set<Integer> neededIds = new HashSet<Integer>(waitingPartials.keySet());
            neededIds.removeAll(allSeendIds);

            Set<PartialDeviceGroup> newGroups =
                partialDeviceGroupDao.getPartialGroupsById(neededIds);
            for (PartialDeviceGroup partial : newGroups) {
                processPartial(partial);
            }
        }
        
        // we must resist the temptation to just return knownGroupLookup.values() because
        // that includes other items we've cached but don't want to return, if this ends up
        // sucking, we could just account for things better...
        for (PartialDeviceGroup partial : input) {
            StoredDeviceGroup storedDeviceGroup = knownGroupLookup.get(partial.getStoredDeviceGroup().getId());
            output.add(storedDeviceGroup);
        }

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
