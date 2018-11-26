package com.cannontech.amr.rfn.service.pointmapping;

import com.google.common.collect.ImmutableSet;

public class PointMapper {
    private String name;
    private String uom;
    private double multiplier = 1.0d;
    private boolean siPrefixParsing = false;
    private ImmutableSet<ModifiersMatcher> modifiersMatchers;
    
    /*
     * Both baseUom and baseModifiersMatchers will be null if this point mapper is 
     * NOT for mapping to a point of coinicidental measurements.
     * Example: the var measurement at a peak demand event, the baseOum and baseModifiersMatchers
     * will be those of the peak demand channel data. 
     */
    private String baseUom;
    private ImmutableSet<ModifiersMatcher> baseModifiersMatchers;
    
    public PointMapper(String name, 
                       String uom, 
                       double multiplier, 
                       boolean siPrefixParsing,
                       Iterable<ModifiersMatcher> modifiersMatchers, 
                       String baseUom, 
                       Iterable<ModifiersMatcher> baseModifiersMatchers) {
        
        this.name = name;
        this.uom = uom;
        this.multiplier = multiplier;
        this.siPrefixParsing = siPrefixParsing;
        this.modifiersMatchers = ImmutableSet.copyOf(modifiersMatchers);
        this.baseUom = baseUom;
        if (baseModifiersMatchers == null) {
            this.baseModifiersMatchers = ImmutableSet.of();
        } else {
            this.baseModifiersMatchers = ImmutableSet.copyOf(baseModifiersMatchers);
        }
    }
    public String getName() {
        return name;
    }
    public String getUom() {
        return uom;
    }
    public double getMultiplier() {
        return multiplier;
    }
    public boolean isSiPrefixParsing() {
        return siPrefixParsing;
    }
    public ImmutableSet<ModifiersMatcher> getModifiersMatchers() {
        return modifiersMatchers;
    }
    public String getBaseUom() {
        return baseUom;
    }
    /*
     * Will never be null. If there are no base modifier matchers this will be an empty ImmutableSet
     */
    public ImmutableSet<ModifiersMatcher> getBaseModifiersMatchers() {
        return baseModifiersMatchers;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("PointMapper [uom=%s, name=%s, multiplier=%s, siPrefixParsing=%s, modifiersMatchers=%s, baseUom=%s, baseModifiersMatchers=%s]",
                             uom,
                             name,
                             multiplier,
                             siPrefixParsing,
                             modifiersMatchers,
                             baseUom,
                             baseModifiersMatchers);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((baseModifiersMatchers == null) ? 0 : baseModifiersMatchers.hashCode());
        result = prime * result + ((baseUom == null) ? 0 : baseUom.hashCode());
        result = prime * result + ((modifiersMatchers == null) ? 0 : modifiersMatchers.hashCode());
        result = prime * result + (siPrefixParsing ? 1231 : 1237);
        result = prime * result + ((uom == null) ? 0 : uom.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PointMapper other = (PointMapper) obj;
        if (baseModifiersMatchers == null) {
            if (other.baseModifiersMatchers != null)
                return false;
        } else if (!baseModifiersMatchers.equals(other.baseModifiersMatchers))
            return false;
        if (baseUom == null) {
            if (other.baseUom != null)
                return false;
        } else if (!baseUom.equals(other.baseUom))
            return false;
        if (modifiersMatchers == null) {
            if (other.modifiersMatchers != null)
                return false;
        } else if (!modifiersMatchers.equals(other.modifiersMatchers))
            return false;
        if (siPrefixParsing != other.siPrefixParsing)
            return false;
        if (uom == null) {
            if (other.uom != null)
                return false;
        } else if (!uom.equals(other.uom))
            return false;
        //  do not compare name
        return true;
    }
}