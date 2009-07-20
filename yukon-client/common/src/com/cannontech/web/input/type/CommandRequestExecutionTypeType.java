package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import com.cannontech.common.device.commands.CommandRequestExecutionType;


public class CommandRequestExecutionTypeType extends DefaultValidatedType<CommandRequestExecutionType> {

	private String renderer = null;

    public CommandRequestExecutionTypeType() {
        setRenderer("StringType.jsp");
    }
    
    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public Class<CommandRequestExecutionType> getTypeClass() {
        return CommandRequestExecutionType.class;
    }

    public PropertyEditor getPropertyEditor() {

    	PropertyEditor attrPropEditor = new PropertyEditorSupport() {
    		
            @Override
            public void setAsText(String typeStr) throws IllegalArgumentException {
            	
                setValue(CommandRequestExecutionType.valueOf(typeStr));
            }
            
            @Override
            public String getAsText() {
            	
            	CommandRequestExecutionType type = (CommandRequestExecutionType)getValue();
                return type.name();
            }
        };
        return attrPropEditor;
    }
}
