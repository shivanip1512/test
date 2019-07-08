package com.cannontech.amr.rfn.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.InputStreamResource;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public class RfnDeviceAttributeDaoImplTest {
    
    RfnDeviceAttributeDaoImpl rfnDeviceAttributeDao = new RfnDeviceAttributeDaoImpl();

    private static final String EMPTY = "";
    
    @Before
    public void initialize() throws IOException {
        InputStream mapping = this.getClass().getClassLoader().getResourceAsStream("metricIdToAttributeMapping.json");
        
        rfnDeviceAttributeDao.setInputFile(new InputStreamResource(mapping));
        
        rfnDeviceAttributeDao.initialize();
    }
    
    @Test
    public void test_getMetricIdForAttribute() {

        Assert.assertEquals((Integer)  5, rfnDeviceAttributeDao.getMetricIdForAttribute(BuiltInAttribute.DELIVERED_DEMAND, PaoType.RFN420CL));
        
        Assert.assertEquals((Integer)200, rfnDeviceAttributeDao.getMetricIdForAttribute(BuiltInAttribute.INSTANTANEOUS_KW, PaoType.RFN430SL1));
    }
    
    @Test
    public void test_rateAttributes() {

        Map<Integer, Map<Integer, BuiltInAttribute>> touGroupings = 
                rfnDeviceAttributeDao.getAttributeLookup().entrySet().stream()
                    .filter(e -> e.getKey() > 999)  //  only get TOU rates
                    .collect(Collectors.groupingBy(e -> e.getKey() / 1000, 
                             Collectors.toMap(Entry::getKey, Entry::getValue)));

        Assert.assertEquals("No mismatched TOU rate A attributes", EMPTY, findTouMismatches(touGroupings.remove(1), "_RATE_A"));
        Assert.assertEquals("No mismatched TOU rate B attributes", EMPTY, findTouMismatches(touGroupings.remove(2), "_RATE_B"));
        Assert.assertEquals("No mismatched TOU rate C attributes", EMPTY, findTouMismatches(touGroupings.remove(3), "_RATE_C"));
        Assert.assertEquals("No mismatched TOU rate D attributes", EMPTY, findTouMismatches(touGroupings.remove(4), "_RATE_D"));
        
        Assert.assertEquals("No additional TOU rate attributes", "{}", touGroupings.toString());
    }

    private String findTouMismatches(Map<Integer, BuiltInAttribute> attributes, String rateSuffix) {
        return attributes.entrySet().stream()
            .map(a -> a.getKey() + ":" + a.getValue())
            .filter(n -> ! n.regionMatches(n.length() - rateSuffix.length(), rateSuffix, 0, rateSuffix.length()))
            .collect(Collectors.joining(","));
    }
}