package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.device.commands.CommandRequestExecutionType;


public class CommandRequestExecutionTypeType extends BaseEnumeratedType<CommandRequestExecutionType> {

    private List<InputOption> optionList = new ArrayList<InputOption>();
    
    public List<InputOption> getOptionList() {

        // re-get available routes
        optionList = new ArrayList<InputOption>();
        CommandRequestExecutionType[] types = CommandRequestExecutionType.values();
        for (CommandRequestExecutionType type : types) {
            
            String typeName = type.getShortName();
            
            InputOption option = new InputOption();
            option.setText(typeName);
            option.setValue(type.name());
            optionList.add(option);
        }
        
        return optionList;
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
