package com.cannontech.messaging.serialization.thrift.test.validator;

import java.util.Collection;
import java.util.Map;

public class ValidationUtil {

    public static boolean isPrimitive(Class<?> clazz) {
        return (clazz.isPrimitive() || clazz.isEnum() || clazz == Boolean.class || clazz == Byte.class ||
                clazz == Character.class || clazz == Short.class || clazz == Integer.class || clazz == Long.class ||
                clazz == Float.class || clazz == Double.class || clazz == String.class);
    }

    public static boolean isClassValidatorCandidate(Class<?> clazz) {
        if (isPrimitive(clazz) || clazz.isEnum() || clazz.isArray() || Collection.class.isAssignableFrom(clazz) ||
            Map.class.isAssignableFrom(clazz)) {
            return false;
        }
        return true;
    }
}
