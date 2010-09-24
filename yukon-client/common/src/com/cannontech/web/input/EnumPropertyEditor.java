/**
 * 
 */
package com.cannontech.web.input;

import java.beans.PropertyEditorSupport;

import org.springframework.validation.DataBinder;

import com.cannontech.core.roleproperties.YukonRoleProperty;

public class EnumPropertyEditor<T extends Enum<T>> extends PropertyEditorSupport {
    
    private final Class<T> enumClass;

    public EnumPropertyEditor(Class<T> enumClass) {
        this.enumClass = enumClass;
    }
    
    public void setAsText(String text) throws IllegalArgumentException {
        if (org.apache.commons.lang.StringUtils.isNotBlank(text)) {
            T enumValue = Enum.valueOf(enumClass, text);
            setValue(enumValue);
        }
        else {
            setValue(null);
        }
    }

    public String getAsText() {
        Object value = getValue();
        T enumValue = enumClass.cast(value);
        if (enumValue != null) {
            return enumValue.name();
        }
        else {
            return "";
        }
    }
    
    public static <T extends Enum<T>> void register(DataBinder binder, Class<T> clazz) {
        binder.registerCustomEditor(YukonRoleProperty.class, new EnumPropertyEditor<T>(clazz));
    }
}