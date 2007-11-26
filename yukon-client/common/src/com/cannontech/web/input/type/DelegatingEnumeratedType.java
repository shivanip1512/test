package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of input type which represents a list input type. This class
 * gets it's property editor and type class from the input type member.
 */
public class DelegatingEnumeratedType<T> extends BaseEnumeratedType<T> {

    private List<InputOption> optionList = new ArrayList<InputOption>();
    private InputType<T> enumeratedType;

    public List<InputOption> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<InputOption> optionList) {
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

    public PropertyEditor getPropertyEditor() {
        return enumeratedType.getPropertyEditor();
    }

}
