package com.cannontech.stars.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cannontech.core.dynamic.impl.MockAsyncDynamicDataSourceImpl;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.impl.EnergyCompanyDaoImpl;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.EnergyCompany.Builder;
import com.google.common.collect.Lists;

public class EnergyCompanyDaoTest {
    /**
     *              2  -  8                             
     *           /  
     *  0 - 1  -   3                              
     *           \                       
     *              4  -  5                                    
     *                  \                                           
     *                    6  -  7
     */
    private Map<Integer, EnergyCompany> energyCompanies;

    public MockAsyncDynamicDataSourceImpl asyncDynamicDataSource = new MockAsyncDynamicDataSourceImpl() {
        @Override
        public void addDatabaseChangeEventListener(DbChangeCategory changeCategory,
                                                   com.cannontech.core.dynamic.DatabaseChangeEventListener listener) {
            
            // No need to do anything for this test
        }
        @Override
        public void addDBChangeListener(com.cannontech.database.cache.DBChangeListener l) {
            // No need to do anything for this test
        };
    };

    public EnergyCompanyDao ecDao  = new EnergyCompanyDaoImpl(asyncDynamicDataSource) {
        @Override
        public EnergyCompany getEnergyCompany(int ecId) {
            return energyCompanies.get(ecId);
        }
    };

    @BeforeEach
    public void setup() {
        Builder ecBuilder = new EnergyCompany.Builder();
        ecBuilder.addEnergyCompany(0, "", null, 0, null);
        ecBuilder.addEnergyCompany(1, "", null, 0, 0);
        ecBuilder.addEnergyCompany(2, "", null, 0, 1);
        ecBuilder.addEnergyCompany(3, "", null, 0, 1);
        ecBuilder.addEnergyCompany(4, "", null, 0, 1);
        ecBuilder.addEnergyCompany(5, "", null, 0, 4);
        ecBuilder.addEnergyCompany(6, "", null, 0, 4);
        ecBuilder.addEnergyCompany(7, "", null, 0, 6);
        ecBuilder.addEnergyCompany(8, "", null, 0, 2);
        energyCompanies = ecBuilder.build();
    }

    @Test
    public void childEnergyCompaniesTest_NoChildren() {
        List<EnergyCompany> childEnergyCompanies = ecDao.getEnergyCompany(7).getDescendants(false);
        assertTrue(childEnergyCompanies.isEmpty());
    }

    @Test
    public void childEnergyCompaniesTest_OneChild() {
        List<EnergyCompany> childEnergyCompanies = ecDao.getEnergyCompany(6).getDescendants(false);

        assertEquals(1, childEnergyCompanies.size());
        testExpectedEnergyCompanyIdList(Arrays.asList(7), childEnergyCompanies);
    }

    @Test
    public void childEnergyCompaniesTest_MultipleChildren() {
        List<EnergyCompany> childEnergyCompanies = ecDao.getEnergyCompany(4).getDescendants(false);

        assertEquals(3, childEnergyCompanies.size());
        testExpectedEnergyCompanyIdList(Arrays.asList(5, 6, 7), childEnergyCompanies);
    }

    @Test
    public void childEnergyCompaniesTest_TopEnergyCompany() {
        List<EnergyCompany> childEnergyCompanies = ecDao.getEnergyCompany(0).getDescendants(false);

        assertEquals(8, childEnergyCompanies.size());
        testExpectedEnergyCompanyIdList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8), childEnergyCompanies);
    }

    /**
     * This method checks the supplied energyCompanyId list and makes sure it only items contained in the list.
     */
    private void testExpectedEnergyCompanyIdList(List<Integer> expectedEnergyCompanyIds,
                                                 List<EnergyCompany> actualEnergyCompanies) {
        List<Integer> actualEnergyCompanyIds = Lists.transform(actualEnergyCompanies, EnergyCompanyDao.TO_ID_FUNCTION);
        assertTrue(actualEnergyCompanyIds.containsAll(expectedEnergyCompanyIds));
        assertTrue(expectedEnergyCompanyIds.containsAll(actualEnergyCompanyIds));
    }
}