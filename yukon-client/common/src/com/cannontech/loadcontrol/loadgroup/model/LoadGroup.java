package com.cannontech.loadcontrol.loadgroup.model;

import java.util.List;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;

public class LoadGroup implements DisplayablePao {
    private PaoIdentifier paoIdentifier;
    private String loadGroupName;
    private List<Integer> programIds;
    
    public LoadGroup (PaoIdentifier paoIdentifier, String loadGroupName, List<Integer> programIds){
        this.paoIdentifier = paoIdentifier;
        this.loadGroupName = loadGroupName;
        this.programIds = programIds;
    }
    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
    
    @Override
    public String getName() {
        return loadGroupName;
    }
    
    public int getLoadGroupId() {
        return paoIdentifier.getPaoId();
    }

    public List<Integer> getProgramIds() {
        return programIds;
    }
    public void setProgramIds(List<Integer> programIds) {
        this.programIds = programIds;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((loadGroupName == null) ? 0 : loadGroupName.hashCode());
        result = prime * result
                + ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
        result = prime * result
                + ((programIds == null) ? 0 : programIds.hashCode());
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
        LoadGroup other = (LoadGroup) obj;
        if (loadGroupName == null) {
            if (other.loadGroupName != null)
                return false;
        } else if (!loadGroupName.equals(other.loadGroupName))
            return false;
        if (paoIdentifier == null) {
            if (other.paoIdentifier != null)
                return false;
        } else if (!paoIdentifier.equals(other.paoIdentifier))
            return false;
        if (programIds == null) {
            if (other.programIds != null)
                return false;
        } else if (!programIds.equals(other.programIds))
            return false;
        return true;
    }
}
