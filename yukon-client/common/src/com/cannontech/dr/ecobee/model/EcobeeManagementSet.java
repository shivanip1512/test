package com.cannontech.dr.ecobee.model;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Object representing the path of an Ecobee management hierarchy set.
 * For example, to specify the set "/levelOne/levelTwo" you would call: 
 * <code>new EcobeeManagementSetPath(levelOne, levelTwo)</code>
 */
public final class EcobeeManagementSet {
    private final List<String> pathElements;
    
    public EcobeeManagementSet(String... pathElements) {
        this.pathElements = Lists.newArrayList(pathElements);
    }
    
    public String get(int index) {
        return pathElements.get(index);
    }
    
    /**
     * @return A String containing all the path elements (except the last element) concatenated with forward
     * slashes.
     */
    public String getPath() {
        List<String> subList = pathElements.subList(0, pathElements.size() - 1);
        return Joiner.on("/").join(subList);
    }
    
    /**
     * @return The last element in the path, which represents the name of the management hierarchy set.
     */
    public String getName() {
        return pathElements.get(pathElements.size() - 1);
    }
    
    @Override
    public String toString() {
        return Joiner.on("/").join(pathElements);
    }
}
