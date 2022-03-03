package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;

/**
 * Implementation of input type which represents an Attribute Set input type
 */
@SuppressWarnings("unchecked")
public class AttributeSetType extends DefaultValidatedType<Set> {
    
    @Autowired private AttributeService attributeService;

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
                    
                    Attribute attribute = attributeService.resolveAttributeName(split[i]);
                    result.add(attribute);
                }
                setValue(result);
            }
            @Override
            public String getAsText() {
                Collection<Attribute> atts = (Collection<Attribute>) getValue();
                List<String> result = new ArrayList<>();
                atts.stream().forEach(att -> result.add(att.getKey()));
                return StringUtils.join(result, ",");
            }
        };
        return intPropEditor;
    }

}