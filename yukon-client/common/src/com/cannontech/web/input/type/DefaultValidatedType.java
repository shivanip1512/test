package com.cannontech.web.input.type;

import com.cannontech.web.input.validate.DefaultValidator;
import com.cannontech.web.input.validate.InputValidator;

public abstract class DefaultValidatedType<T> implements InputType<T> {

    @Override
    public InputValidator<T> getValidator() {
        return DefaultValidator.getInstance();
    }

}
