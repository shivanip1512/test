package com.cannontech.web.input;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.DataBinder;

import com.cannontech.core.roleproperties.YukonRoleProperty;

public class EnumPropertyEditor<T extends Enum<T>> extends PropertyEditorSupport {
    
    private final Class<T> enumClass;

    public EnumPropertyEditor(Class<T> enumClass) {
        this.enumClass = enumClass;
    }
    
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.isNotBlank(text)) {
            T enumValue = Enum.valueOf(enumClass, text);
            setValue(enumValue);
        }
        else {
            setValue(null);
        }
    }

    @Override
    public String getAsText() {
        Object value = getValue();
        T enumValue = enumClass.cast(value);
        if (enumValue != null) {
            return enumValue.name();
        }

        return "";
    }
    
    public static <T extends Enum<T>> void register(DataBinder binder, Class<T> clazz) {
        binder.registerCustomEditor(YukonRoleProperty.class, new EnumPropertyEditor<T>(clazz));
    }
}
