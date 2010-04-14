package com.cannontech.web.stars.dr.operator.model;

import com.cannontech.web.stars.dr.operator.hardware.model.SwitchAssignment;
import com.google.common.base.Supplier;

public class SwitchAssignmentSupplier implements Supplier<SwitchAssignment> {
    
    @Override
    public SwitchAssignment get() {
        return new SwitchAssignment();
    }

}
