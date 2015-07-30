package com.cannontech.web.dr.cc.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

public enum CiEventType {
    
    ACCOUNTING("genericAccounting"),
    DIRECT("genericDirect"),
    ECONOMIC("genericEconomic"),
    NOTIFICATION("genericNotification"),
    
    ISOC_DIRECT("isocDirect"),
    ISOC_SUPERCEDE_DIRECT("isocSupercedeDirect"),
    ISOC_NOTIFICATION("isocNotification"),
    ISOC_SUPERCEDE_NOTIFICATION("isocSupercedeNotification"),
    ISOC_SAME_DAY("isocSameDay"),
    ;
    
    private static List<CiEventType> hoursConstrainedTypes = ImmutableList.of(ISOC_DIRECT, ISOC_NOTIFICATION, ISOC_SAME_DAY);
    
    private String strategyBeanName;
    
    private CiEventType(String strategyBeanName) {
        this.strategyBeanName = strategyBeanName;
    }
    
    public String getStrategyBeanName() {
        return strategyBeanName;
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
        return this == ACCOUNTING;
    }
    
    public boolean isEconomic() {
        return this == ECONOMIC ||
               this == ISOC_SAME_DAY;
    }
    
    public boolean isNotification() {
        return !isAccounting() && !isEconomic();
    }
    
    /**
     * @return True if the event type uses lm direct programs, otherwise false.
     */
    public boolean isDirect() {
        return this == DIRECT ||
               this == ISOC_DIRECT ||
               this == ISOC_SUPERCEDE_DIRECT;
    }
    
    public boolean isHoursConstrained() {
        return hoursConstrainedTypes.contains(this);
    }
}
