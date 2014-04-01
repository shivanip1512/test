package com.cannontech.stars.core.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.core.dynamic.impl.MockAsyncDynamicDataSourceImpl;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.stars.core.service.impl.YukonEnergyCompanyServiceImpl;
import com.google.common.collect.Maps;

public class YukonEnergyCompanyServiceTest {
    /**
     *              2  -  8                             
     *           /  
     *  0 - 1  -   3                              
     *           \                       
     *              4  -  5                                    
     *                  \                                           
     *                    6  -  7
     */
    private Map<Integer, Integer> childToParentHierarchy;
    
    public MockAsyncDynamicDataSourceImpl asyncDynamicDataSource = new MockAsyncDynamicDataSourceImpl() {
        @Override
        public void addDatabaseChangeEventListener(DbChangeCategory changeCategory,
                                                   com.cannontech.core.dynamic.DatabaseChangeEventListener listener) {
            // No need to do anything for this test
        }
    };
    
    public YukonEnergyCompanyService yukonEnergyCompanyService  = new YukonEnergyCompanyServiceImpl(asyncDynamicDataSource) {
        @Override
        protected Map<Integer, Integer> getChildToParentEnergyCompanyHierarchy() {
            return childToParentHierarchy;
        }
    };
    
    @Before
    public void setup() {
        childToParentHierarchy = Maps.newHashMap();
        childToParentHierarchy.put(1, 0);
        childToParentHierarchy.put(2, 1);
        childToParentHierarchy.put(3, 1);
        childToParentHierarchy.put(4, 1);
        childToParentHierarchy.put(5, 4);
        childToParentHierarchy.put(6, 4);
        childToParentHierarchy.put(7, 6);
        childToParentHierarchy.put(8, 2);
    }
    
    @Test
    public void childEnergyCompaniesTest_NoChildren() {
        List<Integer> childEnergyCompanies = yukonEnergyCompanyService.getChildEnergyCompanies(7);
        Assert.assertTrue(childEnergyCompanies.isEmpty());
    }

    @Test
    public void childEnergyCompaniesTest_OneChild() {
        List<Integer> childEnergyCompanies = yukonEnergyCompanyService.getChildEnergyCompanies(6);

        Assert.assertEquals(1, childEnergyCompanies.size());
        testExpectedEnergyCompanyIdList(Arrays.asList(7), childEnergyCompanies);
    }


    @Test
    public void childEnergyCompaniesTest_MultipleChildren() {
        List<Integer> childEnergyCompanies = yukonEnergyCompanyService.getChildEnergyCompanies(4);

        Assert.assertEquals(3, childEnergyCompanies.size());
        testExpectedEnergyCompanyIdList(Arrays.asList(5, 6, 7), childEnergyCompanies);
    }

    @Test
    public void childEnergyCompaniesTest_TopEnergyCompany() {
        List<Integer> childEnergyCompanies = yukonEnergyCompanyService.getChildEnergyCompanies(0);

        Assert.assertEquals(8, childEnergyCompanies.size());
        testExpectedEnergyCompanyIdList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8), childEnergyCompanies);
    }

    /**
     * This method checks the supplied energyCompanyId list and makes sure it only items contained in the list.
     */
    private void testExpectedEnergyCompanyIdList(List<Integer> expectedEnergyCompanyIds, List<Integer> actualEnergyCompanyIds) {
        Assert.assertTrue(actualEnergyCompanyIds.containsAll(expectedEnergyCompanyIds));
        Assert.assertTrue(expectedEnergyCompanyIds.containsAll(actualEnergyCompanyIds));
    }
}