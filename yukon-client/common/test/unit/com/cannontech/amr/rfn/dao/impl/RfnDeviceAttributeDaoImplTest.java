package com.cannontech.amr.rfn.dao.impl;

import static java.util.function.Predicate.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;

import com.cannontech.amr.rfn.dao.impl.RfnDeviceAttributeDaoImpl.MetricIdAttributeMapping;
import com.cannontech.amr.rfn.service.pointmapping.icd.PointMappingIcd;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.YamlParserUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class RfnDeviceAttributeDaoImplTest {
    
    RfnDeviceAttributeDaoImpl rfnDeviceAttributeDao = new RfnDeviceAttributeDaoImpl();

    private static final String EMPTY = "";
    private static final int TOU_OFFSET = 1000;
    
    @BeforeEach
    public void initialize() throws IOException {
        InputStream mapping = this.getClass().getClassLoader().getResourceAsStream("metricIdToAttributeMapping.json");
        
        rfnDeviceAttributeDao.setInputFile(new InputStreamResource(mapping));
        
        rfnDeviceAttributeDao.initialize();
    }
    
    @Test
    public void test_allMetricsMapped() throws IOException {
        ClassPathResource yukonPointMappingIcdYaml = new ClassPathResource("yukonPointMappingIcd.yaml");

        PointMappingIcd icd = YamlParserUtils.parseToObject(yukonPointMappingIcdYaml.getInputStream(), PointMappingIcd.class);

        Set<Integer> baseMetricIds = rfnDeviceAttributeDao.getAttributeLookup().keySet().stream()
                .map(metricId -> metricId % TOU_OFFSET) // Trim down to just the base metric ID
                .collect(Collectors.toSet());

        Set<Integer> icdMetricIds = icd.metricIds.keySet();

        Set<Integer> unmappedMetricIds = Sets.difference(icdMetricIds, baseMetricIds);
        Set<Integer> unexpectedUnmappedMetricIds = Sets.difference(unmappedMetricIds, getKnownUnmappedMetricIds());

        assertTrue(unexpectedUnmappedMetricIds.isEmpty(),
                "Found metric IDs not mapped in metricIdToAttributeMapping.json: " + unexpectedUnmappedMetricIds);

        Set<Integer> missingMetricIds = Sets.difference(baseMetricIds, icdMetricIds);
        Set<Integer> unexpectedMissingMetricIds = Sets.difference(missingMetricIds, getKnownMissingMetricIds());

        assertTrue(unexpectedMissingMetricIds.isEmpty(),
                "Found metric IDs in metricIdToAttributeMapping.json not in yukonPointMappingIcd.yaml: " + unexpectedMissingMetricIds);
    }
    
    @Test
    public void test_allMetricDefinitionsUnique() throws IOException {
        ClassPathResource yukonPointMappingIcdYaml = new ClassPathResource("yukonPointMappingIcd.yaml");

        PointMappingIcd icd = YamlParserUtils.parseToObject(yukonPointMappingIcdYaml.getInputStream(), PointMappingIcd.class);

        icd.metricIds.entrySet().stream().collect(
                Collectors.groupingBy(e -> e.getValue().getUnit(),
                Collectors.groupingBy(e -> Set.copyOf(e.getValue().getModifiers()),
                Collectors.reducing((t1, t2) -> { 
                    fail("Multiple metric IDs for identical UOM/modifiers:"
                            + "\n" + t1 
                            + "\n" + t2);
                    return t1;
                }))));
    }
    
    @Test
    public void test_allAttributesUnique() throws IOException {
        var mapping = this.getClass().getClassLoader().getResourceAsStream("metricIdToAttributeMapping.json");
        var inputStream = new InputStreamResource(mapping);
        var jsonString = IOUtils.toString(inputStream.getInputStream(), StandardCharsets.UTF_8);
        var metricList = JsonUtils.fromJson(jsonString, MetricIdAttributeMapping.class);
        
        var duplicateAttributes =
            metricList.metricMapping.stream().collect(
                    Collectors.groupingBy(mia -> mia.attribute,
                    Collectors.mapping(mia -> mia.metricId,
                    Collectors.toSet())))
                .entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .collect(Collectors.toList());
        
        assertEquals(Collections.emptyList(), duplicateAttributes, "Attribute mapped to multiple metrics");
    }
    
    @Test
    public void test_getMetricIdForAttribute() {

        assertEquals((Integer)  5, rfnDeviceAttributeDao.getMetricIdForAttribute(BuiltInAttribute.DELIVERED_DEMAND, PaoType.RFN420CL));
        
        assertEquals((Integer)200, rfnDeviceAttributeDao.getMetricIdForAttribute(BuiltInAttribute.INSTANTANEOUS_KW, PaoType.RFN430SL1));
    }
    
    @Test
    public void test_nonIntervalAttributes() {
        //  Any attribute that contains minimum/maximum/peak/frozen is not an RFN interval attribute
        var nonIntervalQualifiers = Set.of(
                "minimum", 
                "maximum", 
                "peak",
                "frozen");

        var allowedIntervalAttributes = Set.of(
                BuiltInAttribute.RECEIVED_KWH_FROZEN,
                BuiltInAttribute.USAGE_FROZEN);

        //  Make sure that all allowedIntervalAttributes are actually isIntervalApplicable
        var unexpectedNonInterval = 
                allowedIntervalAttributes.stream()
                    .filter(not(BuiltInAttribute::isIntervalApplicable))
                    .collect(Collectors.toList());
        
        assertTrue(unexpectedNonInterval.isEmpty(),
                "Found allowedIntervalAttributes that are not isIntervalApplicable:" + unexpectedNonInterval);
        
        var qualifierPattern = 
                Pattern.compile(String.join("|", nonIntervalQualifiers), 
                                Pattern.CASE_INSENSITIVE)
                       .asPredicate();
        
        var unexpectedIntervalApplicable =
                rfnDeviceAttributeDao.getAttributesForAllTypes().stream()
                    .filter(BuiltInAttribute::isIntervalApplicable)
                    .filter(not(allowedIntervalAttributes::contains))
                    .filter(attr -> qualifierPattern.test(attr.name()))
                    .collect(Collectors.toList());
        
        assertTrue(unexpectedIntervalApplicable.isEmpty(), 
                "Found non-interval attributes claiming isIntervalApplicable: " + unexpectedIntervalApplicable);
    }
    
    @Test
    public void test_rateAttributes() {

        Map<Integer, Map<Integer, BuiltInAttribute>> touGroupings = 
                rfnDeviceAttributeDao.getAttributeLookup().entrySet().stream()
                    .filter(e -> e.getKey() >= TOU_OFFSET)  //  only get TOU rates
                    .collect(Collectors.groupingBy(e -> e.getKey() / TOU_OFFSET, 
                             Collectors.toMap(Entry::getKey, Entry::getValue)));

        assertEquals(EMPTY, findTouMismatches(touGroupings.remove(1), "_RATE_A"), "No mismatched TOU rate A attributes");
        assertEquals(EMPTY, findTouMismatches(touGroupings.remove(2), "_RATE_B"), "No mismatched TOU rate B attributes");
        assertEquals(EMPTY, findTouMismatches(touGroupings.remove(3), "_RATE_C"), "No mismatched TOU rate C attributes");
        assertEquals(EMPTY, findTouMismatches(touGroupings.remove(4), "_RATE_D"), "No mismatched TOU rate D attributes");
        
        assertEquals("{}", touGroupings.toString(), "No additional TOU rate attributes");
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
                54, 55, 56, 57,
                59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 78, 79,
                84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 
                106, 107, 108, 
                113,
                125, 126, 127, 
                150, 151, 152, 153, 154, 155, 156, 157, 158, 
                182, 183, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196,
                197, 198,
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