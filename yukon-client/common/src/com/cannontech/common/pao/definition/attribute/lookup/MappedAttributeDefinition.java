package com.cannontech.common.pao.definition.attribute.lookup;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointTemplate;
import com.cannontech.common.search.FilterType;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;

public class MappedAttributeDefinition extends AttributeDefinition {

    private ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao;
    private FilterType filterType;
    
    public MappedAttributeDefinition(Attribute attribute, FilterType pointType, ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao) {
        super(attribute);
        this.filterType = pointType;
        this.extraPaoPointAssignmentDao = extraPaoPointAssignmentDao;
    }

    @Override
    public PaoPointIdentifier getPointIdentifier(YukonPao pao) {
        PaoPointIdentifier paoPointIdentifier = extraPaoPointAssignmentDao.getPaoPointIdentifier(pao, attribute);
        return paoPointIdentifier;
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