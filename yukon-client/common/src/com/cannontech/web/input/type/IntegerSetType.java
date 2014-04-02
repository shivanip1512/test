package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * Implementation of input type which represents an integer input type
 */
@SuppressWarnings("unchecked")
public class IntegerSetType extends DefaultValidatedType<Set> {

    private String renderer = null;

    public IntegerSetType() {
        setRenderer("stringType.jsp");
    }

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    @SuppressWarnings("unchecked")
    public Class<Set> getTypeClass() {
        return Set.class;
    }

    public PropertyEditor getPropertyEditor() {
        PropertyEditor intPropEditor = new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                String[] split = StringUtils.split(text, ",");
                List<Integer> result = new ArrayList<Integer>();
                for (int i = 0; i < split.length; ++i) {
                    int parseInt = Integer.parseInt(split[i]);
                    result.add(parseInt);
                }
                setValue(result);
            }
            @Override
            public String getAsText() {
                Collection value2 = (Collection) getValue();  //TODO should be in an if for better error checking
                return StringUtils.join(value2, ",");
            }
        };
        return intPropEditor;
    }

}