package com.cannontech.stars.dr.loadgroup.model;

import java.util.List;

public class LoadGroup {
    int loadGroupId;
    String loadGroupName;
    List<Integer> programIds;
    
    public LoadGroup (){}
    public LoadGroup (int loadGroupId, String loadGroupName, List<Integer> programIds){
        this.loadGroupId = loadGroupId;
        this.loadGroupName = loadGroupName;
        this.programIds = programIds;
    }
    
    public int getLoadGroupId() {
        return loadGroupId;
    }
    public void setLoadGroupId(int loadGroupId) {
        this.loadGroupId = loadGroupId;
    }
    public String getLoadGroupName() {
        return loadGroupName;
    }
    public void setLoadGroupName(String loadGroupName) {
        this.loadGroupName = loadGroupName;
    }
    public List<Integer> getProgramIds() {
        return programIds;
    }
    public void setProgramIds(List<Integer> programIds) {
        this.programIds = programIds;
    }
    
}
