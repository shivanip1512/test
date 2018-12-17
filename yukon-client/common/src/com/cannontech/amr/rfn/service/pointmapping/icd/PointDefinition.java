package com.cannontech.amr.rfn.service.pointmapping.icd;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PointDefinition extends BasePointDefinition {
    boolean coincident = false;
    @JsonProperty("Mapped")
    public boolean mapped = true;  //  default to true

    Set<PaoType> excludedTypes = EnumSet.noneOf(PaoType.class);
    
    @JsonProperty("Modifiers")
    public void setModifiers(List<String> modifiers) {
        modifiers.forEach(this::addModifier);
    }

    private void addModifier(String modifierName) {
        Modifiers modifier = Modifiers.getByCommonName(modifierName);
        
        if (modifier.isCoincident()) {
            coincident = true;
        } else if ( ! modifier.isSiPrefix()) {
            //  Only add non-scaling modifiers
            super.addModifier(modifier);
        }
    }
    
    public boolean isCoincident() {
        return coincident;
    }
    public boolean isMapped() {
        return mapped && 
                Optional.ofNullable(getUnit())
                    .map(Units::isMetric)
                    .orElse(true);
    }
    public PointDefinition() {
        super();
    }
    public PointDefinition(Units unit, Set<Modifiers> modifiers) {
        super(unit, modifiers);
    }
    public PointDefinition(Units unit, Set<Modifiers> modifiers, boolean mapped) {
        super(unit, modifiers);
        this.mapped = mapped;
    }
    public PointDefinition(Units unit, Set<Modifiers> modifiers, boolean mapped, boolean coincident) {
        super(unit, modifiers);
        this.mapped = mapped;
        this.coincident = coincident;
    }
}