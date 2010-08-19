package com.cannontech.database.data.capcontrol;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.search.FilterType;
import com.cannontech.core.dao.ExtraPaoPointMapping;

public class LtcPointMapping implements Comparable<LtcPointMapping>{

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
    
    //Hiding the FilterType Enum from JSF so it doesn't error on the submit.
    public String getFilterName() {
    	return filterType.name();
    }
    
    //Hiding the FilterType Enum from JSF so it doesn't error on the submit.
    public void setFilterName(String name) {
    	
    }
    
    public Attribute getAttribute() {
        return extraPaoPointMapping.getAttribute();
    }
    
    public void setAttribute(Attribute attribute) {
        this.extraPaoPointMapping.setAttribute(attribute);
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
    public int compareTo(LtcPointMapping o) {
        return getAttribute().getDescription().compareTo(o.getAttribute().getDescription());
    }
}