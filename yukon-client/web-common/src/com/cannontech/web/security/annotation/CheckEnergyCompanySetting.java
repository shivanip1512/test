package com.cannontech.web.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cannontech.stars.energyCompany.EnergyCompanySettingType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckEnergyCompanySetting {
    EnergyCompanySettingType value();
}
