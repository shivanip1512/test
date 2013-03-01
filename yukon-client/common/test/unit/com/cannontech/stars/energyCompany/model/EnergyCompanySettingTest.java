package com.cannontech.stars.energyCompany.model;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.stars.energyCompany.EnergyCompanySettingType;

public class EnergyCompanySettingTest {

    @Test
    public void test_isChanged_Comments() {
        EnergyCompanySetting setting1 = new EnergyCompanySetting();
        EnergyCompanySetting setting2 = new EnergyCompanySetting();
        setting1.setType(EnergyCompanySettingType.ENERGY_COMPANY_DEFAULT_TIME_ZONE);
        setting2.setType(EnergyCompanySettingType.ENERGY_COMPANY_DEFAULT_TIME_ZONE);
        
        Assert.assertEquals(false, setting1.isChanged(setting2));
        Assert.assertEquals(false, setting2.isChanged(setting1));
        
        setting1.setComments("abc");
        Assert.assertEquals(true, setting1.isChanged(setting2));
        Assert.assertEquals(true, setting2.isChanged(setting1));
        
        setting2.setComments("abc");
        Assert.assertEquals(false, setting1.isChanged(setting2));
        Assert.assertEquals(false, setting2.isChanged(setting1));
        
        setting2.setComments("ABC");
        Assert.assertEquals(true, setting1.isChanged(setting2));
        Assert.assertEquals(true, setting2.isChanged(setting1));
        
        setting2.setComments(null);
        setting1.setComments("");
        Assert.assertEquals(false, setting1.isChanged(setting2));
        Assert.assertEquals(false, setting2.isChanged(setting1));
    }
    
    @Test
    public void test_isChanged_Enabled() {
        EnergyCompanySetting setting1 = new EnergyCompanySetting();
        EnergyCompanySetting setting2 = new EnergyCompanySetting();
        setting1.setType(EnergyCompanySettingType.ENERGY_COMPANY_DEFAULT_TIME_ZONE);
        setting2.setType(EnergyCompanySettingType.ENERGY_COMPANY_DEFAULT_TIME_ZONE);
        
        Assert.assertEquals(false, setting1.isChanged(setting2));
        Assert.assertEquals(false, setting2.isChanged(setting1));
        
        
        setting1.setStatus(SettingStatus.ALWAYS_SET);
        Assert.assertEquals(true, setting1.isChanged(setting2));
        Assert.assertEquals(true, setting2.isChanged(setting1));
        
        setting2.setStatus(SettingStatus.ALWAYS_SET);
        Assert.assertEquals(false, setting1.isChanged(setting2));
        Assert.assertEquals(false, setting2.isChanged(setting1));
        
        setting2.setStatus(SettingStatus.SET);
        Assert.assertEquals(true, setting1.isChanged(setting2));
        Assert.assertEquals(true, setting2.isChanged(setting1));
    }
    
    @Test
    public void test_isChanged_Value() {
        EnergyCompanySetting setting1 = new EnergyCompanySetting();
        EnergyCompanySetting setting2 = new EnergyCompanySetting();
        setting1.setType(EnergyCompanySettingType.ENERGY_COMPANY_DEFAULT_TIME_ZONE);
        setting2.setType(EnergyCompanySettingType.ENERGY_COMPANY_DEFAULT_TIME_ZONE);
        
        Assert.assertEquals(false, setting1.isChanged(setting2));
        Assert.assertEquals(false, setting2.isChanged(setting1));
        
        setting1.setValue("abc");
        Assert.assertEquals(true, setting1.isChanged(setting2));
        Assert.assertEquals(true, setting2.isChanged(setting1));
        
        setting2.setValue("abc");
        Assert.assertEquals(false, setting1.isChanged(setting2));
        Assert.assertEquals(false, setting2.isChanged(setting1));
        
        setting2.setValue("abAc");
        Assert.assertEquals(true, setting1.isChanged(setting2));
        Assert.assertEquals(true, setting2.isChanged(setting1));
        
        setting2.setValue(null);
        setting1.setValue("");
        Assert.assertEquals(false, setting1.isChanged(setting2));
        Assert.assertEquals(false, setting2.isChanged(setting1));
    }
}
