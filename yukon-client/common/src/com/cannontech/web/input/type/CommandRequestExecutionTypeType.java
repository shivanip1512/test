package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.device.DeviceRequestType;


public class CommandRequestExecutionTypeType extends BaseEnumeratedType<DeviceRequestType> {

    private List<InputOption> optionList = new ArrayList<InputOption>();
    
    public List<InputOption> getOptionList() {

        // re-get available routes
        optionList = new ArrayList<InputOption>();
        DeviceRequestType[] types = DeviceRequestType.values();
        for (DeviceRequestType type : types) {
            
            String typeName = type.getShortName();
            
            InputOption option = new InputOption();
            option.setText(typeName);
            option.setValue(type.name());
            optionList.add(option);
        }
        
        return optionList;
    }
    
    public Class<DeviceRequestType> getTypeClass() {
        return DeviceRequestType.class;
    }

    public PropertyEditor getPropertyEditor() {

        PropertyEditor attrPropEditor = new PropertyEditorSupport() {
            
            @Override
            public void setAsText(String typeStr) throws IllegalArgumentException {
                
                setValue(DeviceRequestType.valueOf(typeStr));
            }
            
            @Override
            public String getAsText() {
                
                DeviceRequestType type = (DeviceRequestType)getValue();
                return type.name();
            }
        };
        return attrPropEditor;
    }
    
}
