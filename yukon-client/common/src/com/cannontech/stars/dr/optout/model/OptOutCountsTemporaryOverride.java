package com.cannontech.stars.dr.optout.model;

import java.util.Comparator;

public class OptOutCountsTemporaryOverride extends OptOutTemporaryOverride {
    private OptOutCounts counting;

    public OptOutCounts getOptOutCounts() {
        return this.counting;
    }

    public void setCounting(OptOutCounts counting) {
        this.counting = counting;
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