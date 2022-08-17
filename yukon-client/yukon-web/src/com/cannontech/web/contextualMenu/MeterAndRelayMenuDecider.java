package com.cannontech.web.contextualMenu;

import com.cannontech.common.pao.PaoIdentifier;

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
public class MeterAndRelayMenuDecider implements MenuTypeApplicabilityDecider {

    @Override
    public boolean isApplicable(PaoIdentifier paoIdentifier) {
        return paoIdentifier.getPaoType().isMeter() || paoIdentifier.getPaoType().isRfRelay();
    }
}
