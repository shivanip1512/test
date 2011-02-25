package com.cannontech.core.roleproperties;

import java.beans.PropertyEditor;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.input.type.BaseEnumeratedType;
import com.cannontech.web.input.type.BooleanType;
import com.cannontech.web.input.type.InputOption;
import com.cannontech.web.input.type.InputType;
import com.cannontech.web.input.type.IntegerType;
import com.cannontech.web.input.type.LongType;
import com.cannontech.web.input.type.StringType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * This class should never have any bean dependencies.
 * 
 * For Role Properties, we need to keep in simple.
 *
 */
public class InputTypeFactory {
    private static final Logger log = YukonLogManager.getLogger(InputTypeFactory.class);
    
    private static final InputType<String> stringType = new StringType();
    private static final InputType<Boolean> booleanType = new BooleanType(true);
    private static final InputType<Integer> integerType = new IntegerType();
    private static final InputType<Long> longType = new LongType();


    public static <T extends Enum<T>> InputType<T> enumType(final Class<T> enumClass) {
        BaseEnumeratedType<T> type = new BaseEnumeratedType<T>() {
            
            private ImmutableList<InputOption> optionList;
            {
                Builder<InputOption> builder = ImmutableList.builder();
                T[] enumConstants = enumClass.getEnumConstants();
                for (T entry : enumConstants) {
                    InputOption inputOption = new InputOption();
                    inputOption.setText(entry.name());
                    inputOption.setValue(entry.name());
                    builder.add(inputOption);
                }
                optionList = builder.build();
            }

            @Override
            public List<InputOption> getOptionList() {
                return optionList;
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
    
    public static InputType<Integer> integerType() {
        return integerType;
    }
    
    public static InputType<Long> longType() {
        return longType;
    }
    
    public static Object convertPropertyValue(InputType<?> type, String value) {
        if (StringUtils.isBlank(value)) {
            if (log.isDebugEnabled()) {
                log.debug("converted '" + value + "' with " + type + " to null (as null)");
            }
            return null;
        }
        if (value.trim().equals(CtiUtilities.STRING_NONE)) {
            if (log.isDebugEnabled()) {
                log.debug("converted '" + value + "' with " + type + " to null (as STRING_NONE)");
            }
            return null;
        }
        
        PropertyEditor propertyEditor = type.getPropertyEditor();
        propertyEditor.setAsText(value);
        Object result = propertyEditor.getValue();
        if (log.isDebugEnabled()) {
            log.debug("converted '" + value + "' with " + type + " to " + result + " (as " + result.getClass().getSimpleName() + ")");
        }
        return result;
    }
    
    public static String convertPropertyValue(Object value, InputType<?> type) {
        if (value == null) {
            log.debug("converted null value to empty string");
            return "";
        }
        
        PropertyEditor propertyEditor = type.getPropertyEditor();
        propertyEditor.setValue(value);
        String result = propertyEditor.getAsText();
        if (log.isDebugEnabled()) {
            log.debug("converted '" + value + "' (of " + value.getClass().getSimpleName() + ") with " + type + " to " + result);
        }
        
        return result;
    }

}
