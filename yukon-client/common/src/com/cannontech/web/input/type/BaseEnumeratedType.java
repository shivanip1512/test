package com.cannontech.web.input.type;

import java.util.List;

import org.springframework.validation.Errors;

import com.cannontech.web.input.validate.InputValidator;

/**
 * Base class for input types which represent a list input type.  This represents any enumerated type, not
 * just those based on a java enum.
 */
public abstract class BaseEnumeratedType<T> implements InputType<T> {

    private String renderer = "enumeratedType.jsp";

    public abstract List<? extends InputOptionProvider> getOptionList();

    public BaseEnumeratedType() {
        super();
    }

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public InputValidator<T> getValidator() {
        return new InputValidator<T>() {

            public void validate(String path, String displayName, T value, Errors errors) {

                if (value == null) {
                    return;
                }
                
                String valueString = value.toString();

                InputOptionProvider option = 
                        getOptionList().stream()
                            .filter(opt -> valueString.equalsIgnoreCase(opt.getValue()))
                            .findAny()
                            .orElse(null);

                if (option == null) {
                    // Not in the option list
                    errors.rejectValue(path,
                                       "yukon.web.input.error.invalidOption",
                                       new Object[] { displayName, value },
                                       "The value is not a valid option.");
                } else if (!option.isEnabled()) {
                    // Not enabled in the option list
                    errors.rejectValue(path,
                                       "yukon.web.input.error.disabledOption",
                                       new Object[] { displayName, value },
                                       "The value is not an enabled option.");
                }
            }

            public String getDescription() {
                return "";
            }
        };
    }

}