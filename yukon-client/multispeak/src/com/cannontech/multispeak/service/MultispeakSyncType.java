package com.cannontech.multispeak.service;

import java.util.Set;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.Sets;

public enum MultispeakSyncType implements DisplayableEnum {

    SUBSTATION_AND_BILLING_CYCLE(Sets.immutableEnumSet(MultispeakSyncTypeProcessorType.SUBSTATION, 
            MultispeakSyncTypeProcessorType.BILLING_CYCLE)),
    SUBSTATION(Sets.immutableEnumSet(MultispeakSyncTypeProcessorType.SUBSTATION)),
    BILLING_CYCLE(Sets.immutableEnumSet(MultispeakSyncTypeProcessorType.BILLING_CYCLE)),
    ENROLLMENT(Sets.immutableEnumSet(MultispeakSyncTypeProcessorType.ENROLLMENT)),
    ;
    
    private Set<MultispeakSyncTypeProcessorType> processorTypes;
    
    MultispeakSyncType(Set<MultispeakSyncTypeProcessorType> processorTypes) {
        this.processorTypes = processorTypes;
    }
    
    public Set<MultispeakSyncTypeProcessorType> getProcessorTypes() {
        return this.processorTypes;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.adminSetup.multispeakSync.multispeakSyncType." + this;
    }
}

