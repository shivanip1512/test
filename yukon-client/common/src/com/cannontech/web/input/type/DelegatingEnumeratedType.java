package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Implementation of input type which represents a list input type. This class
 * gets it's property editor and type class from the input type member.
 */
public class DelegatingEnumeratedType<T> extends BaseEnumeratedType<T> {

    private List<? extends InputOptionProvider> optionList = Lists.newArrayList();
    private InputType<T> enumeratedType;

    public List<? extends InputOptionProvider> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<? extends InputOptionProvider> optionList) {
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
