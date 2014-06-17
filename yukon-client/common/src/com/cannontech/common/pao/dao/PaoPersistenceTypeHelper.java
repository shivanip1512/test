package com.cannontech.common.pao.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.dao.impl.CompletePaoMetaData;
import com.cannontech.common.pao.model.CompleteYukonPao;

public interface PaoPersistenceTypeHelper {
    Set<Class<? extends CompleteYukonPao>> getPaoClasses();
    Set<Class<?>> getPaoPartClasses();

    Map<PaoType, List<CompletePaoMetaData>> getPaoTypeToTableMapping();
    Map<Class<?>, Set<PaoType>> getPaoTypeByClass();
    Map<PaoType, Class<? extends CompleteYukonPao>> getClassByPaoType();
}
