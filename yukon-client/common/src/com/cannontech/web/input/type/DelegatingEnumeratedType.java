package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.Errors;

import com.cannontech.web.input.InputSource;
import com.cannontech.web.input.validate.InputValidator;

/**
 * Implementation of input type which represents a list input type. This class
 * gets it's property editor and type class from the input type member.
 */
public class DelegatingEnumeratedType<T> extends BaseEnumeratedType<T> {

    private List<String> optionList = new ArrayList<String>();
    private InputType<T> enumeratedType;

    public List<String> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<String> optionList) {
        this.optionList = optionList;
    }

    public InputType<T> getEnumeratedType() {
        return enumeratedType;
    }

    public void setEnumeratedType(InputType<T> enumeratedType) {
        this.enumeratedType = enumeratedType;
    }

    public Class<T> getTypeClass() {
        return enumeratedType.getTypeClass();
    }

    public InputValidator<T> getValidator() {
        return new InputValidator<T>() {

            public void validate(String path, InputSource field, T value, Errors errors) {

                String valueString = value.toString();

                if (!optionList.contains(valueString)) {
                    errors.rejectValue(path,
                                       "error.invalidOption",
                                       "The value was not a valid option.");
                }

            }
        };
    }

    public PropertyEditor getPropertyEditor() {
        return enumeratedType.getPropertyEditor();
    }

}
