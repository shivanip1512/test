package com.cannontech.common.pao.definition.attribute.lookup;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointTemplate;
import com.cannontech.common.search.FilterType;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.NotFoundException;

public class MappedAttributeDefinition extends AttributeDefinition {

    private ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao;
    private FilterType filterType;
    
    public MappedAttributeDefinition(BuiltInAttribute attribute, FilterType filterType, 
                                     ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao) {
        super(attribute);
        this.filterType = filterType;
        this.extraPaoPointAssignmentDao = extraPaoPointAssignmentDao;
    }

    @Override
    public PaoPointIdentifier getPointIdentifier(YukonPao pao) {
        PaoPointIdentifier paoPointIdentifier = extraPaoPointAssignmentDao.getPaoPointIdentifier(pao, attribute);
        return paoPointIdentifier;
    }

    @Override
    public PaoPointIdentifier findActualPointIdentifier(YukonPao pao) {
        try {
            PaoPointIdentifier paoPointIdentifier = getPointIdentifier(pao);
            return paoPointIdentifier;
        } catch (NotFoundException e) {
            return null;
        }
    }

    @Override
    public int getPointId(YukonPao pao) throws NotFoundException {
        try {
            int pointId = extraPaoPointAssignmentDao.getPointId(pao, attribute);
            return pointId;
        } catch (IllegalUseOfAttribute e) {
            throw new NotFoundException("Point not found for attribute: " + attribute.getDescription());
        }
    }

    @Override
    public PaoPointTemplate getPointTemplate(YukonPao pao) {
        return null;
    }

    @Override
    public boolean isPointTemplateAvailable() {
        return false;
    }

    public FilterType getFilterType() {
        return filterType;
    }

}