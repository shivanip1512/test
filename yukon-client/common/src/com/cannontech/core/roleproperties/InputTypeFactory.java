package com.cannontech.core.roleproperties;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.cannontech.web.input.type.BaseEnumeratedType;
import com.cannontech.web.input.type.BooleanType;
import com.cannontech.web.input.type.InputOption;
import com.cannontech.web.input.type.InputType;
import com.cannontech.web.input.type.StringType;

/**
 * This class should never have any bean dependencies.
 * 
 * For Role Properties, we need to keep in simple.
 *
 */
public class InputTypeFactory {
    private static final InputType<String> stringType = new StringType();
    private static final InputType<Boolean> booleanType = new BooleanType();

    public static <T extends Enum<T>> InputType<T> enumType(final Class<T> enumClass) {
        BaseEnumeratedType<T> type = new BaseEnumeratedType<T>() {

            @Override
            public List<InputOption> getOptionList() {
                return null;
            }

            @Override
            public PropertyEditor getPropertyEditor() {
                PropertyEditor enumPropertyEditor = new EnumPropertyEditor<T>(enumClass);
                return enumPropertyEditor;
            }

            @Override
            public Class<T> getTypeClass() {
                return enumClass;
            }
            
            @Override
            public String toString() {
                return "EnumType[" + enumClass.getSimpleName() + "]";
            }
            
        };
        return type;
    }
    
    public static InputType<String> stringType() {
        return stringType;
    }
    
    public static InputType<Boolean> booleanType() {
        return booleanType;
    }
    
    private static class EnumPropertyEditor<T extends Enum<T>> extends PropertyEditorSupport {
        
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
    }

}
