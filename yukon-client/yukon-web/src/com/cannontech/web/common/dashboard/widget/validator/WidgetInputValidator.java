package com.cannontech.web.common.dashboard.widget.validator;

import com.cannontech.web.common.dashboard.exception.WidgetParameterValidationException;

/**
 * Interface for widget input validators. These validators may check anything about the input object, and may be used
 * to validate inputs on multiple widgets.
 */
public interface WidgetInputValidator {
    
    /**
     * Check that the input object meets the acceptance criteria of this validator. If the input is invalid, a 
     * WidgetParameterValidationException is thrown, detailing the problem.
     */
    void validate(String inputName, Object input) throws WidgetParameterValidationException;
    
}
