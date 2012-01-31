package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.List;

import com.cannontech.common.device.DeviceRequestType;
import com.google.common.collect.Lists;


public class CommandRequestExecutionTypeType extends BaseEnumeratedType<DeviceRequestType> {

    private List<InputOptionProvider> optionList = Lists.newArrayList();
    
    public List<InputOptionProvider> getOptionList() {

        // re-get available routes
        optionList = Lists.newArrayList();
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
