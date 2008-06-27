package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.service.DeviceDefinitionService;

/**
 * Implementation of input type which represents a list input type. This class
 * gets it's property editor and type class from the input type member.
 */
public class DeviceTypeEnumeratedType extends BaseEnumeratedType<Integer> {

    
    private DeviceDefinitionService deviceDefinitionService = null;
    private List<InputOption> optionList = new ArrayList<InputOption>();
    private InputType<Integer> enumeratedType;
    
    public List<InputOption> getOptionList() {

        // re-get available routes
        optionList = new ArrayList<InputOption>();

        Map<String, List<DeviceDefinition>> deviceGroupMap = deviceDefinitionService.getDeviceDisplayGroupMap();
        for (String key : deviceGroupMap.keySet()) {
            
            for (DeviceDefinition def :  deviceGroupMap.get(key)) {
                
                InputOption option = new InputOption();
                option.setText(def.getDisplayName());
                option.setValue(def.getDisplayName());
                optionList.add(option);
            }
        }
        
        return optionList;
    }

    public InputType<Integer> getEnumeratedType() {
        return enumeratedType;
    }

    public void setEnumeratedType(InputType<Integer> enumeratedType) {
        this.enumeratedType = enumeratedType;
    }

    public Class<Integer> getTypeClass() {
        return enumeratedType.getTypeClass();
    }

    public PropertyEditor getPropertyEditor() {
        return enumeratedType.getPropertyEditor();
    }

    @Required
    public void setDeviceDefinitionService(DeviceDefinitionService deviceDefinitionService) {
        this.deviceDefinitionService = deviceDefinitionService;
    }

}
