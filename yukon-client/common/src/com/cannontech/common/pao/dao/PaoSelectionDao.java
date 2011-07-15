package com.cannontech.common.pao.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.definition.model.PaoData;
import com.cannontech.common.pao.definition.model.PaoData.OptionalField;

public interface PaoSelectionDao {
    /**
     * Populate fields specified in neededData on each PaoData object in paosNeedingData.  Prior
     * to calling this method, the paoIdentifier in each PaoData needs to be populated.
     */
    public void addNeededData(List<PaoData> paosNeedingData, Set<OptionalField> neededData);
}
