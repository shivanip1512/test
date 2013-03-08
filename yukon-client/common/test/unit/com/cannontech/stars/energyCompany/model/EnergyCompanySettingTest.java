package com.cannontech.stars.energyCompany.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.cannontech.stars.energyCompany.EnergyCompanySettingType;

public class EnergyCompanySettingTest {

    @Test
    public void test_isChanged_Comments() {
        EnergyCompanySetting setting1 = new EnergyCompanySetting();
        EnergyCompanySetting setting2 = new EnergyCompanySetting();
        setting1.setType(EnergyCompanySettingType.ENERGY_COMPANY_DEFAULT_TIME_ZONE);
        setting2.setType(EnergyCompanySettingType.ENERGY_COMPANY_DEFAULT_TIME_ZONE);
        
        assertFalse(setting1.isChanged(setting2));
        assertFalse(setting2.isChanged(setting1));
        
        setting1.setComments("abc");
        assertTrue(setting1.isChanged(setting2));
        assertTrue(setting2.isChanged(setting1));
        
        setting2.setComments("abc");
        assertFalse(setting1.isChanged(setting2));
        assertFalse(setting2.isChanged(setting1));
        
        setting2.setComments("ABC");
        assertTrue(setting1.isChanged(setting2));
        assertTrue(setting2.isChanged(setting1));
        
        setting2.setComments(null);
        setting1.setComments("");
        assertFalse(setting1.isChanged(setting2));
        assertFalse(setting2.isChanged(setting1));
    }
    
    @Test
    public void test_isChanged_Enabled() {
        EnergyCompanySetting setting1 = new EnergyCompanySetting();
        EnergyCompanySetting setting2 = new EnergyCompanySetting();
        setting1.setType(EnergyCompanySettingType.ENERGY_COMPANY_DEFAULT_TIME_ZONE);
        setting2.setType(EnergyCompanySettingType.ENERGY_COMPANY_DEFAULT_TIME_ZONE);
        
        assertFalse(setting1.isChanged(setting2));
        assertFalse(setting2.isChanged(setting1));
        
        
        setting1.setStatus(SettingStatus.ALWAYS_SET);
        assertTrue(setting1.isChanged(setting2));
        assertTrue(setting2.isChanged(setting1));
        
        setting2.setStatus(SettingStatus.ALWAYS_SET);
        assertFalse(setting1.isChanged(setting2));
        assertFalse(setting2.isChanged(setting1));
        
        setting2.setStatus(SettingStatus.SET);
        assertTrue(setting1.isChanged(setting2));
        assertTrue(setting2.isChanged(setting1));
    }
    
    @Test
    public void test_isChanged_Value() {
        EnergyCompanySetting setting1 = new EnergyCompanySetting();
        EnergyCompanySetting setting2 = new EnergyCompanySetting();
        setting1.setType(EnergyCompanySettingType.ENERGY_COMPANY_DEFAULT_TIME_ZONE);
        setting2.setType(EnergyCompanySettingType.ENERGY_COMPANY_DEFAULT_TIME_ZONE);
        
        assertFalse(setting1.isChanged(setting2));
        assertFalse(setting2.isChanged(setting1));
        
        setting1.setValue("abc");
        assertTrue(setting1.isChanged(setting2));
        assertTrue(setting2.isChanged(setting1));
        
        setting2.setValue("abc");
        assertFalse(setting1.isChanged(setting2));
        assertFalse(setting2.isChanged(setting1));
        
        setting2.setValue("abAc");
        assertTrue(setting1.isChanged(setting2));
        assertTrue(setting2.isChanged(setting1));
        
        setting2.setValue(null);
        setting1.setValue("");
        assertFalse(setting1.isChanged(setting2));
        assertFalse(setting2.isChanged(setting1));
    }
    
    @Test
    public void test_isValueChanged() {
        EnergyCompanySetting setting1 = new EnergyCompanySetting();
        EnergyCompanySetting setting2 = new EnergyCompanySetting();
        setting1.setType(EnergyCompanySettingType.ENERGY_COMPANY_DEFAULT_TIME_ZONE);
        setting2.setType(EnergyCompanySettingType.ENERGY_COMPANY_DEFAULT_TIME_ZONE);
        
        assertFalse(setting1.isValueChanged(setting2));
        assertFalse(setting2.isValueChanged(setting1));
        
        setting1.setComments("comments are changed");
        assertFalse(setting1.isValueChanged(setting2));
        assertFalse(setting2.isValueChanged(setting1));
        
        setting1.setStatus(SettingStatus.UNSET);
        assertFalse(setting1.isValueChanged(setting2));
        assertFalse(setting2.isValueChanged(setting1));
        
        setting1.setValue("Value is cahnged");
        assertTrue(setting1.isValueChanged(setting2));
        assertTrue(setting2.isValueChanged(setting1));
        
        // Setting2 is changed to same as setting1
        setting2.setValue("Value is cahnged");
        assertFalse(setting1.isValueChanged(setting2));
        assertFalse(setting2.isValueChanged(setting1));
    }
}
