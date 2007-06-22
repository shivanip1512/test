package com.cannontech.amr.deviceread.dao.impl;

import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.util.OneLessSet;


public class SetCoveringSolver {
    /**
     * This method tries to find the cheapest subset of the allPossible input
     * that satisfies all of the needed items. This is likely not at all efficient
     * if the allPossible set is larger than 10-20.
     * 
     * Possible speed improvements include making calculateWeight and isCovered cache
     * interim results and to give getMinimalSet a lower bound so that it could stop
     * if it reached a known minimum (like one).
     * @param <T>
     * @param <V>
     * @param allPossible
     * @param needed
     * @return
     */
    public static <T,V extends HasWeight<T>> Set<V> getMinimalSet(Set<V> allPossible, Set<T> needed) {
        
        if (!isCovered(allPossible, needed)) {
            return null;
        }
        
        float smallestWeight = calculateWeight(allPossible);
        Set<V> cheapestSet = allPossible;
        
        for (V aCommand : allPossible) {
            // remove from set
            Set<V> oneLessSet = new OneLessSet<V>(allPossible, aCommand);
            // make a recursive call
            Set<V> minimalSet = getMinimalSet(oneLessSet, needed);
            if (minimalSet != null) {
                // check if we've found something smaller
                float totalWeight = calculateWeight(minimalSet);
                if (totalWeight < smallestWeight) {
                    smallestWeight = totalWeight;
                    cheapestSet = minimalSet;
                }
            }
        }
        
        
        return cheapestSet;
    }
    
    private static <V extends HasWeight> float calculateWeight(Set<V> minimalCommands) {
        float sum = 0;
        for (HasWeight item : minimalCommands) {
            sum += item.getWeight();
        }
        return sum;
    }
    
    private static <T,V extends HasWeight<T>> boolean isCovered(Set<V> allPossible, Set<T> needed) {
        Set<T> unLocated = new HashSet<T>(needed);
        for (V aSet : allPossible) {
            unLocated.removeAll(aSet.getAffected());
        }
        return unLocated.isEmpty();
    }
    
    public static interface HasWeight<T> {
        public float getWeight();
        public Set<T> getAffected();
    }
    
}
