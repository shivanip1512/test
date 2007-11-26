package com.cannontech.web.input.type;

import java.util.List;

import org.springframework.validation.Errors;

import com.cannontech.web.input.validate.InputValidator;

/**
 * Base class for input types which represent a list input type.
 */
public abstract class BaseEnumeratedType<T> implements InputType<T> {

    private String renderer = "enumeratedType.jsp";

    public abstract List<InputOption> getOptionList();

    public BaseEnumeratedType() {
        super();
    }

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public InputValidator getValidator() {
        return new InputValidator() {

            public void validate(String path, String displayName, Object value, Errors errors) {

                String valueString = value.toString();

                List<InputOption> optionList = getOptionList();
                for (InputOption option : optionList) {
                    if (valueString.equalsIgnoreCase(option.getValue())) {
                        // is valid option - in option list
                        return;
                    }
                }
                
                // Not in the option list
                errors.rejectValue(path,
                                   "error.invalidOption",
                                   new Object[] { displayName, value },
                                   "The value is not a valid option.");
            }

            public String getDescription() {
                return "";
            }
        };
    }

}