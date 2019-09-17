package com.cannontech.core.roleproperties;

import java.beans.PropertyEditor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.input.type.BaseEnumType;
import com.cannontech.web.input.type.BooleanType;
import com.cannontech.web.input.type.DoubleType;
import com.cannontech.web.input.type.EnumInputType;
import com.cannontech.web.input.type.InputOption;
import com.cannontech.web.input.type.InputOptionProvider;
import com.cannontech.web.input.type.InputType;
import com.cannontech.web.input.type.IntegerType;
import com.cannontech.web.input.type.LongType;
import com.cannontech.web.input.type.SliderType;
import com.cannontech.web.input.type.StringType;
import com.cannontech.web.input.type.UserType;
import com.cannontech.web.input.type.WeekDaysType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Sets;

/**
 * This class should never have any bean dependencies.
 * 
 * For Role Properties, we need to keep in simple.
 */
public class InputTypeFactory {
    private static final Logger log = YukonLogManager.getLogger(InputTypeFactory.class);
    
    private static final InputType<String> stringType = new StringType();
    private static final InputType<Boolean> booleanType = new BooleanType(true);
    private static final InputType<Integer> integerType = new IntegerType();
    private static final InputType<Long> longType = new LongType();
    private static final InputType<Integer> userType = new UserType();
    private static final InputType<Double> doubleType = new DoubleType();

    public static <T extends Enum<T>> EnumInputType<T> enumType(final Class<T> enumClass) {
    	BaseEnumType<T> type = new BaseEnumType<T>() {
            
            private ImmutableList<InputOptionProvider> optionList;
            {
                Builder<InputOptionProvider> builder = ImmutableList.builder();
                T[] enumConstants = enumClass.getEnumConstants();
                for (T entry : enumConstants) {
                    InputOption inputOption = new InputOption(entry);
                    builder.add(inputOption);
                }
                optionList = builder.build();
            }

            @Override
            public List<InputOptionProvider> getOptionList() {
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
    
    public static <T extends Enum<T>> EnumInputType<T> permissionType(HierarchyPermissionLevel... levels) {
        BaseEnumType<T> type = new BaseEnumType<T>() {
            private ImmutableList<InputOptionProvider> optionList;
            {
                List<HierarchyPermissionLevel> selectedLevels = Arrays.asList(levels).stream()
                        .distinct()
                        .collect(Collectors.toList());
                selectedLevels.sort(Comparator.comparing(HierarchyPermissionLevel::getLevel));
                
                Builder<InputOptionProvider> builder = ImmutableList.builder();
                for (HierarchyPermissionLevel entry :  selectedLevels) {
                    InputOption inputOption = new InputOption(entry);
                    builder.add(inputOption);
                }
                optionList = builder.build();
            }

            @Override
            public List<InputOptionProvider> getOptionList() {
                return optionList;
            }

            @Override
            public PropertyEditor getPropertyEditor() {
                PropertyEditor enumPropertyEditor = new EnumPropertyEditor<HierarchyPermissionLevel>(HierarchyPermissionLevel.class);
                return enumPropertyEditor;
            }

            @Override
            public Class<T> getTypeClass() {
                return (Class<T>) HierarchyPermissionLevel.class;
            }
            
            @Override
            public String toString() {
                return "EnumType[" + levels + "]";
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
    
    public static InputType<Double> doubleType() {
        return doubleType;
    }
    
    public static InputType<Integer> userType() {
        return userType;
    }
    
    public static InputType<String> weekDaysType(String setting) {
        return new WeekDaysType(setting);
    }

    public static InputType<String> sliderType(String setting, int hours) {
        return new SliderType(setting, hours);
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
