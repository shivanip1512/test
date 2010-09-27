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
    
}
