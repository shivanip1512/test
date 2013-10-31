package com.cannontech.common.mock;

import java.util.Map;

import com.cannontech.system.GlobalSettingType;
import com.google.common.collect.Maps;

public class MockGlobalSettingDao extends GlobalSettingDaoAdapter {
    private Map<GlobalSettingType, Object> values = Maps.newHashMap();
    
    public Object addValue(GlobalSettingType type, Object value) {
        Class<?> typeClass = type.getType().getTypeClass();
        //only add the value to the map if it is an appropriate type for this setting
        if(typeClass.isAssignableFrom(value.getClass())) {
            return values.put(type, value);
        }
        throw new IllegalArgumentException("Value of type \"" + value.getClass() + "\" is invalid for Global Setting \"" + type);
    }
    
    @Override
    public String getString(GlobalSettingType setting) {
        String value = values.get(setting).toString();
        if(value == null) {
            value = (String) setting.getDefaultValue();
        }
        return value;
    }

    @Override
    public boolean getBoolean(GlobalSettingType setting) {
        Boolean value = (Boolean) values.get(setting);
        if(value == null) {
            value = (Boolean) setting.getDefaultValue();
        }
        return value;
    }

    @Override
    public int getInteger(GlobalSettingType setting) {
        Integer value = (Integer) values.get(setting);
        if(value == null) {
            value = (Integer) setting.getDefaultValue();
        }
        return value;
    }

    @Override
    public <E extends Enum<E>> E getEnum(GlobalSettingType setting, Class<E> enumClass) {
        String value = values.get(setting).toString();
        if(value == null) {
            value = setting.getDefaultValue().toString();
        }
        return Enum.valueOf(enumClass, value);
    }
}
