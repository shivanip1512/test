package com.cannontech.dr.nest.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SchemaViolationResponse {
    private NestError error;
    private List<SchemaViolation> violations;
    
    public NestError getError() {
        return error;
    }

    public List<SchemaViolation> getViolations() {
        return violations;
    }
    
    public void setError(NestError error) {
        this.error = error;
    }

    public void setViolations(List<SchemaViolation> violations) {
        this.violations = violations;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}