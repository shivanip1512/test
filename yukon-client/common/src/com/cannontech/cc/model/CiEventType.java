package com.cannontech.cc.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

public enum CiEventType {
    
    ACCOUNTING("genericAccounting", CurtailmentProgramType.ACCOUNTING),
    DIRECT("genericDirect", CurtailmentProgramType.DIRECT_CONTROL),
    ECONOMIC("genericEconomic", CurtailmentProgramType.ECONOMIC),
    NOTIFICATION("genericNotification", CurtailmentProgramType.CAPACITY_CONTINGENCY),
    
    ISOC_DIRECT("isocDirect", CurtailmentProgramType.DIRECT_CONTROL),
    ISOC_SUPERSEDE_DIRECT("isocSupersedeDirect", CurtailmentProgramType.DIRECT_CONTROL),
    ISOC_NOTIFICATION("isocNotification", CurtailmentProgramType.CAPACITY_CONTINGENCY),
    ISOC_SUPERSEDE_NOTIFICATION("isocSupersedeNotification", CurtailmentProgramType.CAPACITY_CONTINGENCY),
    ISOC_SAME_DAY("isocSameDay", CurtailmentProgramType.ECONOMIC),
    ;
    
    private static List<CiEventType> hoursConstrainedTypes = ImmutableList.of(ISOC_DIRECT, ISOC_NOTIFICATION, ISOC_SAME_DAY);
    
    private String strategyBeanName;
    private CurtailmentProgramType programType;
    
    private CiEventType(String strategyBeanName, CurtailmentProgramType programType) {
        this.strategyBeanName = strategyBeanName;
        this.programType = programType;
    }
    
    public String getStrategyBeanName() {
        return strategyBeanName;
    }
    
    public CurtailmentProgramType getProgramType() {
        return programType;
    }
    
    public static CiEventType of(String strategyBeanName) {
        for (CiEventType type : values()) {
            if (type.strategyBeanName.equals(strategyBeanName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown strategy bean name: " + strategyBeanName);
    }
    
    public boolean isAccounting() {
        return this.programType == CurtailmentProgramType.ACCOUNTING;
    }
    
    public boolean isEconomic() {
        return this.programType == CurtailmentProgramType.ECONOMIC;
    }
    
    // is this correct??? Or should it be this.programType = CurtailmentProgramType.CAPACITY_CONTINGENCY ???
    public boolean isNotification() {
        return !isAccounting() && !isEconomic();
    }
    
    /**
     * @return True if the event type uses lm direct programs, otherwise false.
     */
    public boolean isDirect() {
        return this.programType == CurtailmentProgramType.DIRECT_CONTROL;
    }
    
    public boolean isHoursConstrained() {
        return hoursConstrainedTypes.contains(this);
    }
}
