package com.cannontech.amr.rfn.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;

import com.cannontech.amr.rfn.service.pointmapping.icd.PointMappingIcd;
import com.cannontech.amr.rfn.service.pointmapping.icd.YukonPointMappingIcdParser;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.Sets;

public class RfnDeviceAttributeDaoImplTest {
    
    RfnDeviceAttributeDaoImpl rfnDeviceAttributeDao = new RfnDeviceAttributeDaoImpl();

    private static final String EMPTY = "";
    private static final int TOU_OFFSET = 1000;
    
    @Before
    public void initialize() throws IOException {
        InputStream mapping = this.getClass().getClassLoader().getResourceAsStream("metricIdToAttributeMapping.json");
        
        rfnDeviceAttributeDao.setInputFile(new InputStreamResource(mapping));
        
        rfnDeviceAttributeDao.initialize();
    }
    
    @Test
    public void test_allMetricsMapped() throws IOException {
        ClassPathResource yukonPointMappingIcdYaml = new ClassPathResource("yukonPointMappingIcd.yaml");

        PointMappingIcd icd = YukonPointMappingIcdParser.parse(yukonPointMappingIcdYaml.getInputStream());

        Set<Integer> baseMetricIds = rfnDeviceAttributeDao.getAttributeLookup().keySet().stream()
                .map(metricId -> metricId % TOU_OFFSET) // Trim down to just the base metric ID
                .collect(Collectors.toSet());

        Set<Integer> icdMetricIds = icd.metricIds.keySet();

        Set<Integer> unmappedMetricIds = Sets.difference(icdMetricIds, baseMetricIds);
        Set<Integer> unexpectedUnmappedMetricIds = Sets.difference(unmappedMetricIds, getKnownUnmappedMetricIds());

        Assert.assertTrue(
                "Found metric IDs not mapped in metricIdToAttributeMapping.json: " + unexpectedUnmappedMetricIds,
                unexpectedUnmappedMetricIds.isEmpty());

        Set<Integer> missingMetricIds = Sets.difference(baseMetricIds, icdMetricIds);
        Set<Integer> unexpectedMissingMetricIds = Sets.difference(missingMetricIds, getKnownMissingMetricIds());

        Assert.assertTrue(
                "Found metric IDs in metricIdToAttributeMapping.json not in yukonPointMappingIcd.yaml: " + unexpectedMissingMetricIds,
                unexpectedMissingMetricIds.isEmpty());
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
                    .filter(e -> e.getKey() >= TOU_OFFSET)  //  only get TOU rates
                    .collect(Collectors.groupingBy(e -> e.getKey() / TOU_OFFSET, 
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

    //  TODO - add these to metricIdToAttributeMapping.json
    private Set<Integer> getKnownUnmappedMetricIds() {
        return Set.of(
                10, 14, 15, 16, 17, 18, 19, 20, 
                27, 28, 
                31, 
                35, 
                37, 38, 39, 40, 
                45, 46, 47, 48, 
                54, 55, 56, 
                59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 
                84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 
                106, 107, 108, 
                125, 126, 127, 
                150, 151, 152, 153, 154, 155, 156, 157, 158, 
                182, 183, 185, 186, 187, 188, 189, 190, 191, 192, 193, 
                203, 
                206, 207, 208, 209, 
                211, 212, 213, 214, 215, 216, 217, 
                220, 221, 222, 
                230, 
                255, 256, 
                300, 
                310, 
                320);
    }

    //  TODO - add these to yukonPointMappingIcd.yaml
    private Set<Integer> getKnownMissingMetricIds() {
        return Set.of(
                78, 79, 
                330, 331, 
                340, 341, 342, 343, 344, 345, 
                350, 351, 352, 353, 354, 355);
    }
}