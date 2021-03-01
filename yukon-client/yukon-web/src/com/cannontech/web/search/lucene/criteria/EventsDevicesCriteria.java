package com.cannontech.web.search.lucene.criteria;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.lucene.search.BooleanClause;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;

/**
 * Criteria used to filter devices and return only devices that are meters or have support for RFN_EVENTS.
 * 
 */
public class EventsDevicesCriteria extends YukonObjectCriteriaHelper {
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    @PostConstruct
    public void init() {
        addCriteria("isMeter", "true", BooleanClause.Occur.SHOULD);

        Set<PaoType> rfnEventsPaoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.RFN_EVENTS);
        for (PaoType paoType : rfnEventsPaoTypes) {
            typeShouldOccur(paoType);
        }
    }
}