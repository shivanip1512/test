package com.cannontech.database.data.capcontrol;

import com.cannontech.common.search.FilterType;
import com.cannontech.core.dao.ExtraPaoPointMapping;
import com.cannontech.enums.RegulatorPointMapping;

public class VoltageRegulatorPointMapping implements Comparable<VoltageRegulatorPointMapping>{

    private ExtraPaoPointMapping extraPaoPointMapping;
    private String paoName; /* This is the name of the pao that the point is actually attached to, not the pao it's being mapped to. */
    private String pointName;
    private int index;
    private FilterType filterType;
    
    public String getPaoName() {
        return paoName;
    }
    
    public void setPaoName(String paoName) {
        this.paoName = paoName;
    }
    
    public String getPointName() {
        return pointName;
    }
    
    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }
    
    public FilterType getFilterType() {
        return filterType;
    }
    
    /**
     * Returns the String value of the FilterType Enum
     */
    public String getFilterName() {
    	return filterType.name();
    }
    
    /**
     * This is only needed since JSF needs to set bean properties as part of it's lifecycle.
     * Calling this function will not set the FilterName.
     * @param name
     */
    public void setFilterName(String name) {
    	
    }
    
    public RegulatorPointMapping getRegulatorPointMapping() {
        return extraPaoPointMapping.getRegulatorPointMapping();
    }
    
    public void setRegulatorPointMapping(RegulatorPointMapping regulatorPointMapping) {
        this.extraPaoPointMapping.setRegulatorPointMapping(regulatorPointMapping);
    }
    
    public int getPointId() {
        return extraPaoPointMapping.getPointId();
    }
    
    public void setPointId(int pointId) {
        this.extraPaoPointMapping.setPointId(pointId);
    }

    public ExtraPaoPointMapping getExtraPaoPointMapping() {
        return extraPaoPointMapping;
    }

    public void setExtraPaoPointMapping(ExtraPaoPointMapping extraPaoPointMapping) {
        this.extraPaoPointMapping = extraPaoPointMapping;
    }

    @Override
    public int compareTo(VoltageRegulatorPointMapping o) {
        return getRegulatorPointMapping().getDescription().compareTo(o.getRegulatorPointMapping().getDescription());
    }
}