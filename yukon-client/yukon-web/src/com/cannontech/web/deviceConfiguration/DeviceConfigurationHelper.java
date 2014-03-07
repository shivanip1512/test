package com.cannontech.web.deviceConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.config.model.jaxb.Category;
import com.cannontech.common.device.config.model.jaxb.EnumOption;
import com.cannontech.common.device.config.model.jaxb.InputBase;
import com.cannontech.common.device.config.model.jaxb.InputBoolean;
import com.cannontech.common.device.config.model.jaxb.InputEnum;
import com.cannontech.common.device.config.model.jaxb.InputFloat;
import com.cannontech.common.device.config.model.jaxb.InputInteger;
import com.cannontech.common.device.config.model.jaxb.InputMap;
import com.cannontech.common.device.config.model.jaxb.MapType;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.deviceConfiguration.enumeration.DeviceConfigurationInputEnumeration;
import com.cannontech.web.deviceConfiguration.enumeration.DeviceConfigurationInputEnumeration.DisplayableValue;
import com.cannontech.web.deviceConfiguration.model.CategoryTemplate;
import com.cannontech.web.deviceConfiguration.model.Field;
import com.cannontech.web.deviceConfiguration.model.FloatField;
import com.cannontech.web.deviceConfiguration.model.IntegerField;
import com.cannontech.web.deviceConfiguration.model.RateMapField;
import com.cannontech.web.deviceConfiguration.model.RateMapField.DisplayableRate;
import com.cannontech.web.input.type.BooleanType;
import com.cannontech.web.input.type.DelegatingEnumeratedType;
import com.cannontech.web.input.type.FloatType;
import com.cannontech.web.input.type.InputOption;
import com.cannontech.web.input.type.InputOptionProvider;
import com.cannontech.web.input.type.InputType;
import com.cannontech.web.input.type.IntegerType;
import com.cannontech.web.input.type.StringType;
import com.google.common.collect.ImmutableMap.Builder;

@Component
public class DeviceConfigurationHelper {
    private static final Logger log = YukonLogManager.getLogger(DeviceConfigurationHelper.class);
   
    private static final String moduleBaseKey = "yukon.web.modules.tools.configs";
    private static final String categoryBaseKey = moduleBaseKey + ".category";
    
    private final Map<EnumOption, DeviceConfigurationInputEnumeration> fieldToEnumerationMap;
    private final Map<MapType, DeviceConfigurationInputEnumeration> fieldToMapTypeMap;
    
    @Autowired private ObjectFormattingService formattingService;
    @Autowired private List<DeviceConfigurationInputEnumeration> configurationInputEnumerations;
    
    @Autowired
    public DeviceConfigurationHelper(List<DeviceConfigurationInputEnumeration> deviceConfigurationInputEnumerations) {
        Builder<EnumOption, DeviceConfigurationInputEnumeration> enumBuilder = new Builder<>();
        Builder<MapType, DeviceConfigurationInputEnumeration> mapTypeBuilder = new Builder<>();
        
        for (EnumOption option : EnumOption.values()) {
            enumBuilder.put(option, getEnumerationForOptionName(option, deviceConfigurationInputEnumerations));
        }
        
        for (MapType option : MapType.values()) {
            mapTypeBuilder.put(option, getMapTypeForName(option, deviceConfigurationInputEnumerations));
        }

        fieldToEnumerationMap = enumBuilder.build();
        fieldToMapTypeMap = mapTypeBuilder.build();
    }
    
    /**
     * Create a category template object from a category to use for rendering in the view.
     * @param category the category for which fields will be loaded.
     * @param context the user context for resolving strings.
     * @return the populated category template.
     */
    public CategoryTemplate createTemplate(Category category, YukonUserContext context) {
        String categoryKey = categoryBaseKey + "." + category.getType().value();
        String name = getResolvedString(categoryKey + ".title", context);
        List<Field<?>> fields = new ArrayList<>();

        for (InputBase input : category.getIntegerOrFloatOrBoolean()) {
            String inputKey = categoryKey + "." + input.getField();
            String displayName = getResolvedString(inputKey, context);

            String description = null;
            try {
                description = getResolvedString(inputKey + ".description", context);
            } catch (NoSuchMessageException nsme) {
                // Nobody cares.
            }
            
            if (input.getClass() == InputEnum.class) {
                InputType<?> inputType = convertEnumField((InputEnum) input, context);
                fields.add(new Field<>(displayName, input.getField(), description, true, inputType));
            } else if (input.getClass() == InputInteger.class) {
                fields.add(createIntegerField(displayName, description, (InputInteger)input));
            } else if (input.getClass() == InputFloat.class) {
                fields.add(createFloatField(displayName, description, (InputFloat)input));
            } else if (input.getClass() == InputMap.class) { 
                fields.add(createMapField(displayName, description, (InputMap)input, categoryKey, context));
            } else if (input.getClass() == InputBoolean.class) {
                fields.add(new Field<>(displayName, input.getField(), description, new BooleanType()));
            } else {
                log.error("Received unsupported input type: " + input.getClass());
            }
        }
        
        CategoryTemplate retVal = new CategoryTemplate(name, category.getType(), fields);

        return retVal;
    }
    
    /**
     * Used on startup for map creation help.
     * @param option the map type being searched for.
     * @param deviceConfigurationInputEnumerations the enumerations being searched.
     * @return the matching enumeration object for the map type provided.
     */
    private DeviceConfigurationInputEnumeration getMapTypeForName(MapType option,
        List<DeviceConfigurationInputEnumeration> deviceConfigurationInputEnumerations) {
        
        for (DeviceConfigurationInputEnumeration enumeration : deviceConfigurationInputEnumerations) {
            if (enumeration.getEnumOptionName() == option.value()) {
                return enumeration;
            }
        }
        
        throw new NotFoundException("No input enumeration exists for the option " + option.value());
    }  
    
    /**
     * Used on startup for map creation help.
     * @param option the enum option being searched for.
     * @param deviceConfigurationInputEnumerations the enumerations being searched.
     * @return the matching enumeration object for the enum option provided.
     */
    private DeviceConfigurationInputEnumeration getEnumerationForOptionName(EnumOption option, 
        List<DeviceConfigurationInputEnumeration> deviceConfigurationInputEnumerations) {
        
        for (DeviceConfigurationInputEnumeration enumeration : deviceConfigurationInputEnumerations) {
            if (enumeration.getEnumOptionName() == option.value()) {
                return enumeration;
            }
        }
        
        throw new NotFoundException("No input enumeration exists for the option " + option.value());
    }
    
    private Field<?> createMapField(String displayName, String description, InputMap inputMap, 
                                    String categoryKey, YukonUserContext context) {
        // TODO: Make this work for non-TOU maps. Making a lot of assumptions here.
        List<InputMap.Entry> entries = inputMap.getEntry();
        
        List<DisplayableRate> inputTypes = new ArrayList<>();
        
        DeviceConfigurationInputEnumeration enumeration = fieldToMapTypeMap.get(inputMap.getType());
        
        for (InputMap.Entry entry : entries) {
            // Handle the time.
            DelegatingEnumeratedType<String> timeEnumeratedType = new DelegatingEnumeratedType<>();
            
            List<InputOptionProvider> options = new ArrayList<>();
            for (DisplayableValue displayableValue : enumeration.getDisplayableValues()) {
                InputOption option = new InputOption();
                
                option.setValue(displayableValue.getValue());
                option.setText(displayableValue.getValue());
                options.add(option);
            }
            
            timeEnumeratedType.setOptionList(options);
            timeEnumeratedType.setEnumeratedType(new StringType());
            
            // Handle the rate.
            DelegatingEnumeratedType<String> rateEnumeratedType = new DelegatingEnumeratedType<>();
            
            options = new ArrayList<>();
            
            DeviceConfigurationInputEnumeration rateEnum = fieldToEnumerationMap.get(EnumOption.RATE);
            for (DisplayableValue displayableValue : rateEnum.getDisplayableValues()) {
                InputOption option = new InputOption();
                
                option.setValue(displayableValue.getValue());
                option.setText(formattingService.formatObjectAsString(displayableValue.getMessage(), context));
                options.add(option);
            }
            
            rateEnumeratedType.setOptionList(options);
            rateEnumeratedType.setEnumeratedType(new StringType());
            
            inputTypes.add(new DisplayableRate(timeEnumeratedType, rateEnumeratedType, entry.getField()));
        }
        
        return new RateMapField(displayName, inputMap.getField(), description, inputTypes);
    }
    
    private IntegerField createIntegerField(String displayName, String description, InputInteger input) {
        IntegerType intType = new IntegerType();
        
        if (input.getMinValue() != null) {
            intType.setMinValue(input.getMinValue());
        }
        
        if (input.getMaxValue() != null) {
            intType.setMaxValue(input.getMaxValue());
        }
        
        return new IntegerField(displayName, input.getField(), description, intType);
    }

    private FloatField createFloatField(String displayName, String description, InputFloat input) {
        FloatType floatType = new FloatType();
        
        if (input.getMinValue() != null) {
            floatType.setMinValue(input.getMinValue());
        }
        
        if (input.getMaxValue() != null) {
            floatType.setMaxValue(input.getMaxValue());
        }
        
        return input.getDecimalDigits() == null ?
            new FloatField(displayName, input.getField(), description, floatType) :
            new FloatField(displayName, input.getField(), description, input.getDecimalDigits(), floatType);
    }
    
    private InputType<?> convertEnumField(InputEnum input, YukonUserContext context) {
        DeviceConfigurationInputEnumeration enumeration = fieldToEnumerationMap.get(input.getType());
        
        DelegatingEnumeratedType<String> inputType = new DelegatingEnumeratedType<>();
        
        List<InputOptionProvider> options = new ArrayList<>();
        for (DisplayableValue displayableValue : enumeration.getDisplayableValues()) {
            InputOption option = new InputOption();
            option.setValue(displayableValue.getValue());
            option.setText(formattingService.formatObjectAsString(displayableValue.getMessage(), context));
            options.add(option);
        }
        
        inputType.setOptionList(options);
        inputType.setEnumeratedType(new StringType());
        
        return inputType;
    }

    private String getResolvedString(String key, YukonUserContext context) {
        YukonMessageSourceResolvable ymsr = new YukonMessageSourceResolvable(key);
        return formattingService.formatObjectAsString(ymsr, context);
    }
}
