package com.cannontech.dr.scenario.model;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;

public class Scenario implements DisplayablePao{

    private PaoIdentifier paoIdentifier;
    private String name;
    private int programCount;
    
    public Scenario(PaoIdentifier paoIdentifier, String name, int programCount){
        this.paoIdentifier = paoIdentifier;
        this.name = name;
        this.programCount = programCount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }

    public int getProgramCount() {
        return programCount;
    }
    public void setProgramCount(int programCount) {
        this.programCount = programCount;
    }

    public boolean isHasPrograms() {
        return programCount > 0 ? true : false;
    }
}
