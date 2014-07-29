package com.cannontech.dr.estimatedload.dao;

import com.cannontech.core.dao.NotFoundException;

public class FormulaNotFoundException extends NotFoundException {
    public FormulaNotFoundException(String message) {
        super(message);
    }

    public FormulaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
