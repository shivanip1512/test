package com.cannontech.amr.meter.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.google.common.collect.Sets;

public class MeterEventLookupService {

    @Autowired AttributeService attributeService;
    
    public <T extends YukonPao> Set<Attribute> getAvailableEventAttributes(List<T> paos) {
        Set<PaoType> paoTypes = Sets.newHashSet();
        for (YukonPao pao : paos) {
            paoTypes.add(pao.getPaoIdentifier().getPaoType());
        }

        Set<Attribute> availableAttributes = Sets.newHashSet();
        for (PaoType paoType : paoTypes) {
            availableAttributes.addAll(attributeService.getAvailableAttributes(paoType));
        }

        Set<Attribute> attributes = getAvailableAttributesFromAll(availableAttributes);
        return attributes;
    }
    
    private Set<Attribute> getAvailableAttributesFromAll(Set<Attribute> availableAttributes) {
        Set<? extends Attribute> allEventAttributes = Sets.newHashSet(MeterEventStatusTypeGroupings.getAll());
        Set<Attribute> availableEventAttributes = Sets.newHashSet(allEventAttributes);
        
        for (Attribute attr : allEventAttributes) {
            if (!availableAttributes.contains(attr)) {
                availableEventAttributes.remove(attr);
            }
        }
        return availableEventAttributes;
    }
}
