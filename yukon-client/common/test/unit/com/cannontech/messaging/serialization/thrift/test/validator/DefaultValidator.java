package com.cannontech.messaging.serialization.thrift.test.validator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class DefaultValidator<T extends Object> extends Validator<T> {

    private String name;

    public DefaultValidator(Class<T> clazz) {
        this(clazz, null);
    }

    public DefaultValidator(Class<T> clazz, ValidatorService validatorSvc) {
        super(clazz, validatorSvc);
        this.name = clazz.getSimpleName();
    }

    @Override
    public ValidationResult validate(Object value, Object expected) {
        ValidationResult result = new ValidationResult(getName(), getValidatedClass());

        if (isBasicEqualityCheckConclusive(value, expected, result)) {
            return result;
        }

        Class<T> clazz = getValidatedClass();

        if (clazz.isArray()) {
            return this.validateArray(value, expected);
        }
        else if (Collection.class.isAssignableFrom(clazz)) {
            return this.validateCollection((Collection) value, (Collection) expected);
        }
        else if (Map.class.isAssignableFrom(clazz)) {
            return this.validateMap((Map) value, (Map) expected);
        }
        else {
            result.addError("value mismatch: was expecting \"" + expected + "\" but received \"" + value + "\"");
        }

        return result;
    }

    private ValidationResult validateArray(Object valueArray, Object expectedArray) {
        Class<?> clazz = getValidatedClass().getComponentType();
        ValidationResult result = new ValidationResult(getName(), clazz);

        int len = Array.getLength(valueArray);
        if (len != Array.getLength(expectedArray)) {
            result.addError("Array Lengths are different: was expecting \"" + Array.getLength(expectedArray) +
                            "\" but received \"" + len + "\"");
            return result;
        }

        for (int i = 0; i < len; i++) {
            ValidationResult elementResult =
                getValidatorSvc().validate(Array.get(valueArray, i), Array.get(expectedArray, i));
            elementResult.setName("@_" + i);
            result.addNestedResults(elementResult);
        }

        return result;
    }

    private ValidationResult validateCollection(Collection valueCollection, Collection expectedCollection) {
        ValidationResult result = new ValidationResult(getName(), getValidatedClass());
        Object[] valueArray = valueCollection.toArray();
        Object[] expectedArray = expectedCollection.toArray();

        if (valueArray.length != expectedArray.length) {
            result.addError("Collection sizes are different: was expecting \"" + expectedArray.length +
                            "\" but received \"" + valueArray.length + "\"");
            return result;
        }

        if (!Set.class.isAssignableFrom(getValidatedClass())) {

            return validateArray(valueArray, expectedArray);
        }

        // we have to ignore order
        Collection collectionCopy = new ArrayList(valueCollection);
        for (Object element : expectedCollection) {
            boolean found = false;

            for (Object copyElement : collectionCopy) {
                if (!getValidatorSvc().validate(copyElement, element).hasError()) {
                    collectionCopy.remove(copyElement);
                    found = true;
                    break;
                }
            }

            if (!found) {
                result.addError("missing element '" + element + "'");
            }
        }

        for (Object copyElement : collectionCopy) {
            result.addError("Unexpected element '" + copyElement + "'");
        }

        return result;
    }

    private ValidationResult validateMap(Map valueMap, Map expectedMap) {
        Set valueSet = valueMap.entrySet();
        Set expectedSet = expectedMap.entrySet();
        return validateCollection(valueSet, expectedSet);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
