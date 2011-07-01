package com.cannontech.common.pao.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.definition.model.PaoData;
import com.cannontech.common.pao.definition.model.PaoData.OptionalField;

public interface PaoSelectionDao {
    public void addNeededData(List<PaoData> paosNeedingData, Set<OptionalField> neededData);
}
