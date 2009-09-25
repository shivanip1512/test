package com.cannontech.dr.program.service;

import java.util.List;

public class ConstraintViolations {
    private List<String> violations = null;

    public ConstraintViolations(List<String> violations) {
        this.violations = violations;
    }

    public List<String> getViolations() {
        return violations;
    }
}
