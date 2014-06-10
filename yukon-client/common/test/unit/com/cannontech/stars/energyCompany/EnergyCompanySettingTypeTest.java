package com.cannontech.stars.energyCompany;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.stars.energyCompany.EnergyCompanySettingType;

public class EnergyCompanySettingTypeTest {

    @Test
    public void test_defaultValueType() {
        for (EnergyCompanySettingType settingType :EnergyCompanySettingType.values()) {
            Object defaultValue = settingType.getDefaultValue();
            Class<?> typeClass = settingType.getType().getTypeClass();
            try {
                typeClass.cast(defaultValue);
            } catch (ClassCastException e) {
                Assert.fail(settingType + "'s default value:'" + defaultValue + "' type:" 
                        + defaultValue.getClass().getSimpleName() + "  cannot be cast to setting type: " 
                        + typeClass.getSimpleName());
            }
        }
    }
}
