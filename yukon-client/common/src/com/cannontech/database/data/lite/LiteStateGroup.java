package com.cannontech.database.data.lite;

import java.util.ArrayList;
import java.util.List;

public class LiteStateGroup extends LiteBase {
    
    private String stateGroupName;
    private List<LiteState> statesList;
    private String groupType;
    
    public LiteStateGroup(int stateGroupId) {
        super();
        setLiteID(stateGroupId);
        setLiteType(LiteTypes.STATEGROUP);
    }
    
    public LiteStateGroup(int stateGroupId, String name) {
        this(stateGroupId);
        setStateGroupName(name);
    }
    
    public LiteStateGroup(int stateGroupId, String name, String groupType) {
        super();
        setLiteID(stateGroupId);
        stateGroupName = new String(name);
        setLiteType(LiteTypes.STATEGROUP);
        setGroupType(groupType);
    }
    
    public LiteStateGroup(int stateGroupId, String name, List<LiteState> states) {
        super();
        setLiteID(stateGroupId);
        stateGroupName = new String(name);
        statesList = new ArrayList<LiteState>(states);
        setLiteType(LiteTypes.STATEGROUP);
    }
    
    public int getStateGroupID() {
        return getLiteID();
    }
    
    public String getStateGroupName() {
        return stateGroupName;
    }
    
    public List<LiteState> getStatesList() {
        if( statesList == null )
            statesList = new ArrayList<LiteState>(6);
        return statesList;
    }
    
    public void setStateGroupID(int newValue) {
        setLiteID(newValue);
    }

    public void setStateGroupName(String newValue) {
        this.stateGroupName = new String(newValue);
    }

    public void setStatesList(List<LiteState> newList) {
        this.statesList = new ArrayList<LiteState>(newList);
    }

    @Override
    public String toString() {
        return stateGroupName;
    }
    /**
     * Returns the groupType.
     * @return String
     */
    public String getGroupType() {
        return groupType;
    }

    /**
     * Sets the groupType.
     * @param groupType The groupType to set
     */
    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

}
