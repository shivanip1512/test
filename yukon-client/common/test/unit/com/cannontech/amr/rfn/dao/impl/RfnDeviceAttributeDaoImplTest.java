package com.cannontech.amr.rfn.dao.impl;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.InputStreamResource;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public class RfnDeviceAttributeDaoImplTest {
    
    RfnDeviceAttributeDaoImpl rfnDeviceAttributeDao = new RfnDeviceAttributeDaoImpl();

    @Before
    public void initialize() throws IOException {
        InputStream mapping = this.getClass().getClassLoader().getResourceAsStream("metricIdToAttributeMapping.json");
        
        rfnDeviceAttributeDao.setInputFile(new InputStreamResource(mapping));
        
        rfnDeviceAttributeDao.initialize();
    }
    
    @Test
    public void test_getMetricIdForAttribute() {

        Assert.assertEquals((Integer)  5, rfnDeviceAttributeDao.getMetricIdForAttribute(BuiltInAttribute.DELIVERED_DEMAND, PaoType.RFN420CL));
        
        Assert.assertEquals((Integer)200, rfnDeviceAttributeDao.getMetricIdForAttribute(BuiltInAttribute.DEMAND, PaoType.RFN430SL1));
    }
}