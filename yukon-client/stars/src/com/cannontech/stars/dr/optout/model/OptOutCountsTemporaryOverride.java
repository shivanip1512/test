package com.cannontech.stars.dr.optout.model;

import java.util.Comparator;

public class OptOutCountsTemporaryOverride extends OptOutTemporaryOverride {
    private int optOutValue;
    
    public int getOptOutValue() {
        return optOutValue;
    }

    public OptOutCounts getOptOutCounts() {
        return OptOutCounts.valueOf(optOutValue);
    }

    public void setOptOutValue(int optOutValue) {
        this.optOutValue = optOutValue;
    }
    
    public static Comparator<OptOutCountsTemporaryOverride> getStartTimeComparator() {
        
        Comparator<OptOutCountsTemporaryOverride> c = new Comparator<OptOutCountsTemporaryOverride>() {
            
            @Override
            public int compare(OptOutCountsTemporaryOverride o1, OptOutCountsTemporaryOverride o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        };
        
        return c;
    }
    
}