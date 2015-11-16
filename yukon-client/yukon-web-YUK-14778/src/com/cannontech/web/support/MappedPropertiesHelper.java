/**
 * 
 */
package com.cannontech.web.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.DataBinder;

import com.cannontech.web.input.type.InputType;

public class MappedPropertiesHelper<T> {
    
    private final String mapField;
    private final List<MappedPropertiesHelper.MappableProperty<T, ?>> mappableProperties = new ArrayList<>();
    
    // This map is just to provide a convenient way to lookup a specific property for those
    // times when simply looping over them is not desired.
    private final Map<String, MappedPropertiesHelper.MappableProperty<T, ?>> map = new HashMap<>(); 

    public MappedPropertiesHelper(String mapField) {
        this.mapField = mapField;
    }

    public List<MappedPropertiesHelper.MappableProperty<T, ?>> getMappableProperties() {
        return mappableProperties;
    }
    
    public Map<String, MappedPropertiesHelper.MappableProperty<T, ?>> getMap() {
        return map;
    }

    public String getMapField() {
        return mapField;
    }

    public <V> void add(String keyText, String fieldText, T extra, InputType<V> valueType) {
        String path = mapField + "[" + keyText + "]." + fieldText;
        MappedPropertiesHelper.MappableProperty<T, V> result =
            new MappedPropertiesHelper.MappableProperty<T, V>(path, extra, valueType);
        mappableProperties.add(result);
        map.put(keyText, result);
    }

    public <V> void add(String keyText, T extra, InputType<V> valueType) {
        String path = mapField + "[" + keyText + "]";
        MappedPropertiesHelper.MappableProperty<T, V> result =
            new MappedPropertiesHelper.MappableProperty<T, V>(path, extra, valueType);
        mappableProperties.add(result);
        map.put(keyText, result);
    }

    public void register(DataBinder binder) {
        for (MappedPropertiesHelper.MappableProperty<T, ?> property : getMappableProperties()) {
            binder.registerCustomEditor(property.getValueType().getClass(), property.getPath(),
                    property.getValueType().getPropertyEditor());
        }
    }

    public static class MappableProperty<E, V> {
        
        private final String path;
        private final InputType<V> valueType;
        private final E extra;

        public MappableProperty(String path, E extra, InputType<V> valueType) {
            this.path = path;
            this.extra = extra;
            this.valueType = valueType;
        }

        public String getPath() {
            return path;
        }

        public E getExtra() {
            return extra;
        }

        public InputType<V> getValueType() {
            return valueType;
        }
    }
    
}