package com.cannontech.web.deviceConfiguration;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.cannontech.amr.rfn.dao.RfnDeviceAttributeDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.jaxb.Category;
import com.cannontech.common.device.config.model.jaxb.EnumOption;
import com.cannontech.common.device.config.model.jaxb.InputBase;
import com.cannontech.common.device.config.model.jaxb.InputBoolean;
import com.cannontech.common.device.config.model.jaxb.InputEnum;
import com.cannontech.common.device.config.model.jaxb.InputFloat;
import com.cannontech.common.device.config.model.jaxb.InputIndexed;
import com.cannontech.common.device.config.model.jaxb.InputInteger;
import com.cannontech.common.device.config.model.jaxb.InputMap;
import com.cannontech.common.device.config.model.jaxb.InputString;
import com.cannontech.common.device.config.model.jaxb.MapType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.deviceConfiguration.enumeration.CapControlAttribute;
import com.cannontech.web.deviceConfiguration.enumeration.DeviceConfigurationInputEnumeration;
import com.cannontech.web.deviceConfiguration.model.AttributeInput;
import com.cannontech.web.deviceConfiguration.model.AttributeMappingField;
import com.cannontech.web.deviceConfiguration.model.AttributeMappingInput;
import com.cannontech.web.deviceConfiguration.model.CategoryTemplate;
import com.cannontech.web.deviceConfiguration.model.RfnChannelField;
import com.cannontech.web.deviceConfiguration.model.RfnChannelInput;
import com.cannontech.web.deviceConfiguration.model.EnumField;
import com.cannontech.web.deviceConfiguration.model.Field;
import com.cannontech.web.deviceConfiguration.model.FloatField;
import com.cannontech.web.deviceConfiguration.model.IntegerField;
import com.cannontech.web.deviceConfiguration.model.RateInput;
import com.cannontech.web.deviceConfiguration.model.RateMapField;
import com.cannontech.web.deviceConfiguration.model.RateMapField.DisplayableRate;
import com.cannontech.web.input.type.BaseEnumeratedType;
import com.cannontech.web.input.type.BooleanType;
import com.cannontech.web.input.type.DelegatingEnumeratedType;
import com.cannontech.web.input.type.FloatType;
import com.cannontech.web.input.type.InputOption;
import com.cannontech.web.input.type.InputType;
import com.cannontech.web.input.type.IntegerType;
import com.cannontech.web.input.type.StringType;
import com.cannontech.web.input.validate.InputValidator;
import com.google.common.collect.ImmutableMap.Builder;

@Component
public class DeviceConfigurationHelper {
    private static final Logger log = YukonLogManager.getLogger(DeviceConfigurationHelper.class);

    private static final String moduleBaseKey = "yukon.web.modules.tools.configs";
    private static final String categoryBaseKey = moduleBaseKey + ".category";
    private static final String channelErrorBaseKey = categoryBaseKey + ".rfnChannelConfiguration.error";

    private final Map<EnumOption, DeviceConfigurationInputEnumeration> fieldToEnumerationMap;
    private final Map<MapType, DeviceConfigurationInputEnumeration> fieldToMapTypeMap;

    @Autowired private ObjectFormattingService formattingService;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private RfnDeviceAttributeDao rfnDeviceAttributeDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

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
     * Create a default category input, without providing input map
     */
    public CategoryTemplate createDefaultTemplate(Category category, Integer configId, YukonUserContext userContext) {
        return createTemplate(category, null, configId, userContext);
    }

    /**
     * Create a category template object from a category to use for rendering in the view.
     * @param category the category for which fields will be loaded.
     * @param userContext the user context for resolving strings.
     * @return the populated category template.
     */
    public CategoryTemplate createTemplate(Category category, Map<String, String> categoryInputs, Integer configId, YukonUserContext userContext) {
        String categoryKey = categoryBaseKey + "." + category.getType().value();
        String name = getResolvedString(categoryKey + ".title", userContext);
        List<Field<?>> fields = new ArrayList<>();

        for (InputBase input : category.getIntegerOrFloatOrBoolean()) {
            addInput(fields, input, userContext, categoryKey, categoryInputs, configId);
        }

        CategoryTemplate retVal = new CategoryTemplate(name, category.getType(), fields);

        return retVal;
    }

    /**
     * Create and add a field input
     */
    private void addInput(List<Field<?>> fields, InputBase input, YukonUserContext userContext, String categoryKey,
            Map<String, String> categoryInputs, Integer configId) {

        final String fieldName;
        final String inputKey;
        final String displayName;

        fieldName = input.getField();
        inputKey = categoryKey + "." + fieldName;
        displayName = getResolvedString(inputKey, userContext);

        String description = null;
        try {
            description = getResolvedString(inputKey + ".description", userContext);
        } catch (NoSuchMessageException nsme) {
            // Nobody cares.
        }

        if (input.getClass() == InputEnum.class) {
            BaseEnumeratedType<String> enumField = convertEnumField((InputEnum) input, userContext);
            fields.add(new EnumField(displayName, fieldName, description, enumField, input.getDefault()));
        } else if (input.getClass() == InputInteger.class) {
            fields.add(createIntegerField(displayName, fieldName, description, (InputInteger) input));
        } else if (input.getClass() == InputFloat.class) {
            fields.add(createFloatField(displayName, fieldName, description, (InputFloat) input));
        } else if (input.getClass() == InputMap.class) { 
            fields.add(createMapField(displayName, description, (InputMap)input, categoryKey, userContext));
        } else if (input.getClass() == InputBoolean.class) {
            fields.add(Field.createField(displayName, fieldName, description, new BooleanType(), input.getDefault()));
        } else if (input.getClass() == InputIndexed.class) {
            fields.add(createIndexedField(displayName, fieldName, description, (InputIndexed)input, configId));
        } else {
            log.error("Received unsupported input type: " + input.getClass());
        }
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
                                    String categoryKey, YukonUserContext userContext) {
        // TODO: Make this work for non-TOU maps. Making a lot of assumptions here.
        List<InputMap.Entry> entries = inputMap.getEntry();

        List<DisplayableRate> inputTypes = new ArrayList<>();

        DeviceConfigurationInputEnumeration enumeration = fieldToMapTypeMap.get(inputMap.getType());
        List<InputOption> timeOptions = enumeration.getDisplayableValues(userContext);
        DelegatingEnumeratedType<String> timeEnumeratedType = new DelegatingEnumeratedType<>();
        timeEnumeratedType.setOptionList(timeOptions);
        timeEnumeratedType.setEnumeratedType(new StringType());
        timeEnumeratedType.setRenderer(enumeration.getSelectionType().getRenderer());

        DeviceConfigurationInputEnumeration rateEnum = fieldToEnumerationMap.get(EnumOption.RATE);
        List<InputOption> rateOptions = rateEnum.getDisplayableValues(userContext);
        DelegatingEnumeratedType<String> rateEnumeratedType = new DelegatingEnumeratedType<>();
        rateEnumeratedType.setOptionList(rateOptions);
        rateEnumeratedType.setEnumeratedType(new StringType());
        rateEnumeratedType.setRenderer(rateEnum.getSelectionType().getRenderer());

        for (InputMap.Entry entry : entries) {
            inputTypes.add(new DisplayableRate(timeEnumeratedType, rateEnumeratedType, entry.getField()));
        }

        InputType<RateInput> rateInput = new InputType<RateInput>(){
            @Override
            public String getRenderer() {
                return "rateMapType.jsp";
            }
            @Override
            public InputValidator<RateInput> getValidator() {
                return null;
            }
            @Override
            public PropertyEditor getPropertyEditor() {
                return null;
            }
            @Override
            public Class<RateInput> getTypeClass() {
                return RateInput.class;
            }
        };

        return new RateMapField(displayName, inputMap.getField(), description, rateInput, inputTypes);
    }

    private IntegerField createIntegerField(String displayName, String fieldName, String description, InputInteger input) {
        IntegerType intType = new IntegerType();

        if (input.getMinValue() != null) {
            intType.setMinValue(input.getMinValue());
        }

        if (input.getMaxValue() != null) {
            intType.setMaxValue(input.getMaxValue());
        }

        return new IntegerField(displayName, fieldName, description, intType, input.getDefault());
    }

    private FloatField createFloatField(String displayName, String fieldName, String description, InputFloat input) {
        FloatType floatType = new FloatType();

        if (input.getMinValue() != null) {
            floatType.setMinValue(input.getMinValue());
        }

        if (input.getMaxValue() != null) {
            floatType.setMaxValue(input.getMaxValue());
        }

        return input.getDecimalDigits() == null ? 
                new FloatField(displayName, input.getField(), description, floatType, input.getDefault()) :
                new FloatField(displayName, input.getField(), description, input.getDecimalDigits(), floatType,
                        input.getDefault());
    }

    /**
     * Create an indexed field.  Each indexed field type currently has its own type and renderer. 
     */
    private Field<? extends List<?>> createIndexedField(String displayName, String fieldName, String description,
            InputIndexed inputIndexed, Integer configId) {
        List<InputBase> inputs = inputIndexed.getIntegerOrFloatOrBoolean();
        
        if (isRfnChannelInput(inputs)) {
            return createChannelField(displayName, fieldName, description, inputIndexed, configId);
        }
        if (isAttributeMappingInput(inputs)) {
            return createAttributeMappingField(displayName, fieldName, description, inputIndexed, configId);
        }
        
        Map<String, Class<?>> elements = 
                inputIndexed.getIntegerOrFloatOrBoolean().stream()
                    .collect(Collectors.toMap(
                            InputBase::getField,
                            InputBase::getClass));
        
        throw new NotFoundException("No indexed field type exists for indexed field \"" + fieldName + "\" with elements " + elements);
    }
    
    private boolean isRfnChannelInput(List<InputBase> inputs) {
        if (inputs.size() == 2) {
            InputBase input1 = inputs.get(0);
            InputBase input2 = inputs.get(1);
            if (input1.getClass() == InputEnum.class &&
                input2.getClass() == InputEnum.class) {
                InputEnum inputEnum1 = (InputEnum) input1;
                InputEnum inputEnum2 = (InputEnum) input2;
                if (inputEnum1.getType() == EnumOption.CHANNEL_TYPE &&
                    inputEnum2.getType() == EnumOption.READ_TYPE) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAttributeMappingInput(List<InputBase> inputs) {
        if (inputs.size() == 2) {
            InputBase input1 = inputs.get(0);
            InputBase input2 = inputs.get(1);
            if (input1.getClass() == InputEnum.class &&
                input2.getClass() == InputString.class) {
                InputEnum inputEnum = (InputEnum) input1;
                if (inputEnum.getType() == EnumOption.CAP_CONTROL_ATTRIBUTE) {
                    return true;
                }
            }
        }
        return false;
    }

    private RfnChannelField createChannelField(String displayName, String fieldName, String description,
                                            InputIndexed inputIndexed, Integer configId) {

        InputType<List<RfnChannelInput>> channelType = new InputType<List<RfnChannelInput>>(){
            @Override
            public String getRenderer() {
                return "rfnChannelType.jsp";
            }
            @Override
            public InputValidator<List<RfnChannelInput>> getValidator() {
                return rfnChannelValidator;
            }
            @Override
            public PropertyEditor getPropertyEditor() {
                return null;
            }
            @SuppressWarnings("unchecked")
            @Override
            public Class<List<RfnChannelInput>> getTypeClass() {
                return (Class<List<RfnChannelInput>>)(Class<?>)List.class;
            }
        };

        return new RfnChannelField(displayName, fieldName, description, channelType, getAttributeListForConfigId(configId));
    } 

    private static final InputValidator<List<RfnChannelInput>> rfnChannelValidator = new InputValidator<List<RfnChannelInput>>() {

        @Override
        public void validate(String path, String displayName, List<RfnChannelInput> values, Errors errors) {
            int interval = 0;
            int midnight = 0;
            for (RfnChannelInput value : values) {
                switch (value.getRead()) {
                case MIDNIGHT :
                    midnight++;
                    break;
                case INTERVAL:
                    interval++;
                    break;
                case DISABLED:
                    break;
                }
            }

            if (interval > 15) {
                errors.rejectValue(path, channelErrorBaseKey + ".reportingExceeded");
            }
            if (midnight + interval > 80) {
                errors.rejectValue(path, channelErrorBaseKey + ".midnightExceeded");
            }
        }

        @Override
        public String getDescription() {
            return "Max 15 reporting channels, 80 enabled channels";
        }

    };

    private AttributeMappingField createAttributeMappingField(String displayName, String fieldName, String description,
                                                        InputIndexed inputIndexed, Integer configId) {
    
        InputType<List<AttributeMappingInput>> attributeMappingType = new InputType<List<AttributeMappingInput>>(){
            @Override
            public String getRenderer() {
                return "attributeMappingType.jsp";
            }
            @Override
            public InputValidator<List<AttributeMappingInput>> getValidator() {
                return new InputValidator<List<AttributeMappingInput>>() {
                    @Override
                    public void validate(String path, String displayName, List<AttributeMappingInput> value, Errors errors) {
                        //  TODO - validate point name length, currently only limited by input tag
                    }
                    @Override
                    public String getDescription() {
                        return "Attribute mapping points";
                    }
                };
            }
            @Override
            public PropertyEditor getPropertyEditor() {
                return null;
            }
            @SuppressWarnings("unchecked")
            @Override
            public Class<List<AttributeMappingInput>> getTypeClass() {
                return (Class<List<AttributeMappingInput>>)(Class<?>)List.class;
            }
        };
        
        for (InputBase input : inputIndexed.getIntegerOrFloatOrBoolean()) {
            if (input.getClass() == InputEnum.class) {
                InputEnum inputEnum = (InputEnum)input;
                if (inputEnum.getType() == EnumOption.CAP_CONTROL_ATTRIBUTE) {
                    return new AttributeMappingField(displayName, fieldName, description, attributeMappingType, CapControlAttribute.getAttributes());
                }
            }
        }

        Map<String, Class<?>> elements = 
                inputIndexed.getIntegerOrFloatOrBoolean().stream()
                    .collect(Collectors.toMap(
                            InputBase::getField,
                            InputBase::getClass));
        
        throw new NotFoundException("No attribute-based field exists inside attribute mapping field \"" + fieldName + "\" with elements " + elements);
    } 

    private BaseEnumeratedType<String> convertEnumField(InputEnum input, YukonUserContext userContext) {

        DelegatingEnumeratedType<String> inputType = new DelegatingEnumeratedType<>();
        DeviceConfigurationInputEnumeration enumeration = fieldToEnumerationMap.get(input.getType());

        List<InputOption> options = enumeration.getDisplayableValues(userContext);

        inputType.setOptionList(options);
        inputType.setEnumeratedType(new StringType());

        inputType.setRenderer(enumeration.getSelectionType().getRenderer());

        return inputType;
    }

    private String getResolvedString(String key, YukonUserContext userContext) {
        YukonMessageSourceResolvable ymsr = new YukonMessageSourceResolvable(key);
        return formattingService.formatObjectAsString(ymsr, userContext);
    }

    public List<BuiltInAttribute> getAttributeListForConfigId(Integer configId) {
        List<BuiltInAttribute> attributes = new ArrayList<>();
        if (configId != null) {

            Set<PaoType> supportedTypes = deviceConfigurationDao.getDeviceConfiguration(configId).getSupportedDeviceTypes();
            attributes.addAll(rfnDeviceAttributeDao.getAttributesForPaoTypes(supportedTypes));

        } else {
            attributes.addAll(rfnDeviceAttributeDao.getAttributesForAllTypes());
        }
        return attributes;
    }

    private class ToAttribute<T extends AttributeInput> implements com.google.common.base.Function<T, String> {
        final MessageSourceAccessor messageSourceAccessor;
        ToAttribute(YukonUserContext userContext) {
            messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);
        }
        @Override
        public String apply(T input) {
            return messageSourceAccessor.getMessage(input.getAttribute().getMessage());
        }
    }

    public <T extends AttributeInput> com.google.common.base.Function<T, String> toAttribute(YukonUserContext userContext) {
        return new ToAttribute<T>(userContext);
    }
}