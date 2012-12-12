package com.cannontech.amr.device.search.model;

import com.cannontech.database.data.lite.LiteYukonPAObject;


public class DeviceSearchFilterBy {
    private String name;
    private String value;
    private DeviceSearchField field;
    private Validator validator;

    public DeviceSearchFilterBy(DeviceSearchField field, String name, Validator validator) {
        this.field = field;
        this.name = name;
        this.validator = validator;
    }

    public DeviceSearchField getField() {
        return field;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getFormatKey() {
        return "yukon.web.modules.commanderSelect.filterBy." + name;
    }
    
    public boolean isValid(LiteYukonPAObject lPao) {
        return (validator == null) ? true : validator.isValid(lPao, value);
    }
    
    public interface Validator {
        /**
         * Method used to decide if a pao should be displayed or not based on the filter value
         * @param lPao The pao
         * @param value The filter value
         * @return if the pao should be displayed or not
         */
        boolean isValid(LiteYukonPAObject lPao, String value);
    }
}
