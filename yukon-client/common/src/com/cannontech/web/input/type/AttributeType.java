package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;

/**
 * Implementation of input type which represents an Attribute input type
 */
public class AttributeType extends DefaultValidatedType<Attribute> {
	
	private AttributeService attributeService;

    private String renderer = null;

    public AttributeType() {
        setRenderer("StringType.jsp");
    }

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public Class<Attribute> getTypeClass() {
        return Attribute.class;
    }

    public PropertyEditor getPropertyEditor() {

    	PropertyEditor attrPropEditor = new PropertyEditorSupport() {
    		
            @Override
            public void setAsText(String attr) throws IllegalArgumentException {
            	
                if (StringUtils.isNotBlank(attr)) {
                    Attribute attribute = attributeService.resolveAttributeName(attr);
                    setValue(attribute);
                }
            }
            
            @Override
            public String getAsText() {
            	
            	Attribute attribute = (Attribute) getValue();
            	if (attribute != null) {
            	    return attribute.getKey();
            	}
            	return "";
            }
        };
        return attrPropEditor;
    }
    
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
		this.attributeService = attributeService;
	}

}