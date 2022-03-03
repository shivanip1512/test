package com.cannontech.web.contextualMenu;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;

/**
 * Used as the applicability decider for all meter MenuEntry's
 * 
 * An example of how it is used and initialized for a specific Menu (in applicationContext.xml):
 * 
 *    <bean id="meterMenuDecider" class="com.cannontech.web.contextualMenu.MeterMenuDecider"/>
 *    
 *    <bean id="menuItemAmrArchivedDataExporter" class="com.cannontech.web.contextualMenu.model.menuEntry.DeviceCollectionMenuAction">
 *        <property name="decider" ref="meterMenuDecider"/>
 *        <constructor-arg value="/tools/data-exporter/view"/>
 *        <constructor-arg value="ARCHIVED_DATA_EXPORT"/>
 *    </bean>
 */
public class EventsMenuDecider implements MenuTypeApplicabilityDecider {
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    @Override
    public boolean isApplicable(PaoIdentifier paoIdentifier) {
        return paoIdentifier.getPaoType().isMeter()
                || paoDefinitionDao.isTagSupported(paoIdentifier.getPaoType(), PaoTag.RFN_EVENTS);
    }
}
