package com.cannontech.stars.energyCompany;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class CacheKeyTest {

    @Test
    public void test_CacheKey() {
        // Test ID = 1
        ECSettingCacheKey accountNumber_id1 = ECSettingCacheKey.of(EnergyCompanySettingType.ACCOUNT_NUMBER_LENGTH, 1);
        assertSame(accountNumber_id1, ECSettingCacheKey.of(EnergyCompanySettingType.ACCOUNT_NUMBER_LENGTH, 1));
        
        ECSettingCacheKey singleEnergyCompany_id1 = ECSettingCacheKey.of(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY, 1);
        assertNotSame(singleEnergyCompany_id1, ECSettingCacheKey.of(EnergyCompanySettingType.ACCOUNT_NUMBER_LENGTH, 1));
        assertSame(singleEnergyCompany_id1, ECSettingCacheKey.of(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY, 1));

        // TEST ID = 2
        ECSettingCacheKey accountNumber_id2 = ECSettingCacheKey.of(EnergyCompanySettingType.ACCOUNT_NUMBER_LENGTH, 2);
        assertSame(accountNumber_id2, ECSettingCacheKey.of(EnergyCompanySettingType.ACCOUNT_NUMBER_LENGTH, 2));

        ECSettingCacheKey singleEnergyCompany_id2 = ECSettingCacheKey.of(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY, 2);
        assertNotSame(singleEnergyCompany_id2, ECSettingCacheKey.of(EnergyCompanySettingType.ACCOUNT_NUMBER_LENGTH, 2));
        assertSame(singleEnergyCompany_id2, ECSettingCacheKey.of(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY, 2));

        // Once more for good measure
        assertSame(accountNumber_id1, ECSettingCacheKey.of(EnergyCompanySettingType.ACCOUNT_NUMBER_LENGTH, 1));
        assertSame(accountNumber_id2, ECSettingCacheKey.of(EnergyCompanySettingType.ACCOUNT_NUMBER_LENGTH, 2));
        assertSame(singleEnergyCompany_id1, ECSettingCacheKey.of(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY, 1));
        assertSame(singleEnergyCompany_id2, ECSettingCacheKey.of(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY, 2));
    }
}
