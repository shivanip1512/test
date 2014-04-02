package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

/**
 * Implementation of input type which represents an integer input type
 */
@SuppressWarnings("unchecked")
public class AttributeSetType extends DefaultValidatedType<Set> {

    private String renderer = null;

    public AttributeSetType() {
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
                Set<Attribute> result = new HashSet<Attribute>();
                for (int i = 0; i < split.length; ++i) {
                    
                    Attribute attribute = BuiltInAttribute.valueOf(split[i]);
                    result.add(attribute);
                }
                setValue(result);
            }
            @Override
            public String getAsText() {
                Collection value2 = (Collection) getValue();
                return StringUtils.join(value2, ",");
            }
        };
        return intPropEditor;
    }

}