package com.cannontech.analysis.tablemodel;

import java.util.Set;

public interface CapControlFilterable {

    public abstract void setCapBankIdsFilter(Set<Integer> capBankIds);

    public abstract void setFeederIdsFilter(Set<Integer> feederIds);

    public abstract void setSubbusIdsFilter(Set<Integer> subbusIds);
    
    public abstract void setSubstationIdsFilter(Set<Integer> substationIds);
    
    public abstract void setAreaIdsFilter(Set<Integer> areaIds);

}