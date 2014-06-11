package com.cannontech.system;

import static com.cannontech.core.roleproperties.InputTypeFactory.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class GlobalSettingTypeTest {

    @Test
    public void test_isSensitiveInformation() {
        for (GlobalSettingType settingType : GlobalSettingType.values()) {
            if (settingType.isSensitiveInformation()) {
                // If this test fails it is because an encrypted global setting type was changed (e.g. from string to int)
                // or a new global setting was added to isSensitiveInformation to be encrypted and isn't a string type
                // If we need to encrypt non string types we need to modify GlobalSettingDao, GlobalSettingEditorDao
                // and GlobalSettingUpdateDao to handle non string types.
                boolean isStringType = settingType.getType().equals(stringType());
                assertTrue(settingType + " is not a stringType and doesn't support encryption", isStringType);
            }
        }
    }

    @Test
    public void test_defaultValueType() {
        for (GlobalSettingType settingType : GlobalSettingType.values()) {
            Object defaultValue = settingType.getDefaultValue();
            Class<?> typeClass = settingType.getType().getTypeClass();
            try {
                typeClass.cast(defaultValue);
            } catch (ClassCastException e) {
                fail(settingType + "'s default value:'" + defaultValue + "' type:" 
                        + defaultValue.getClass().getSimpleName() + "  cannot be cast to setting type: " 
                        + typeClass.getSimpleName());
            }
        }
    }
}
