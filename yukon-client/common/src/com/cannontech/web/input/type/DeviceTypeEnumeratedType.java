package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Implementation of input type which represents a list input type. This class
 * gets it's property editor and type class from the input type member.
 */
public class DeviceTypeEnumeratedType extends BaseEnumeratedType<Integer> {

    
    private PaoDefinitionService paoDefinitionService = null;
    private List<InputOptionProvider> optionList = Lists.newArrayList();
    private InputType<Integer> enumeratedType;
    
    public List<InputOptionProvider> getOptionList() {

        // re-get available routes
        optionList = Lists.newArrayList();

        Multimap<String, PaoDefinition> deviceGroupMap = paoDefinitionService.getPaoDisplayGroupMap();
        for (String key : deviceGroupMap.keySet()) {
            
            for (PaoDefinition def :  deviceGroupMap.get(key)) {
                
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
    public void setPaoDefinitionService(PaoDefinitionService paoDefinitionService) {
        this.paoDefinitionService = paoDefinitionService;
    }

}
