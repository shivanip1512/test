package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Implementation of input type which represents a list of integer input type
 */
@SuppressWarnings("unchecked")
public class IntegerListType extends DefaultValidatedType<List> {
    private String renderer = "stringType.jsp";

    @Override
    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    @Override
    public Class<List> getTypeClass() {
        return List.class;
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        PropertyEditor intPropEditor = new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                String[] split = StringUtils.split(text, ",");
                List<Integer> result = new ArrayList<Integer>();
                for (int index = 0; index < split.length; index++) {
                    int parseInt = Integer.parseInt(split[index]);
                    result.add(parseInt);
                }
                setValue(result);
            }

            @Override
            public String getAsText() {
                return StringUtils.join((List) getValue(), ",");
            }
        };
        return intPropEditor;
    }
}
