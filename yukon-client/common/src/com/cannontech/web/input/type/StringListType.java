package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Input type representing a list of strings
 */
public class StringListType extends DefaultValidatedType<List> {
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
    	PropertyEditor stringPropEditor = new PropertyEditorSupport() {
    		@Override
    		public void setAsText(String text) throws IllegalArgumentException {
    			String[] stringArray = StringUtils.split(text, ",");
    			List<String> result = Arrays.asList(stringArray);
    			setValue(result);
    		}
    		@Override
    		public String getAsText() {
    			List<String> strings = (List<String>) getValue();
    			String joinedStrings = StringUtils.join(strings, ",");
    			return joinedStrings;
    		}
    	};
    	return stringPropEditor;
    }
}
