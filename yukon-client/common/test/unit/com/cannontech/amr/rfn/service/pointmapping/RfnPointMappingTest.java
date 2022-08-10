package com.cannontech.amr.rfn.service.pointmapping;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.core.io.ClassPathResource;

import com.cannontech.amr.rfn.service.pointmapping.icd.CoincidentGroupingCollector;
import com.cannontech.amr.rfn.service.pointmapping.icd.ManufacturerModel;
import com.cannontech.amr.rfn.service.pointmapping.icd.MeterClass;
import com.cannontech.amr.rfn.service.pointmapping.icd.ModelPointDefinition;
import com.cannontech.amr.rfn.service.pointmapping.icd.NameScale;
import com.cannontech.amr.rfn.service.pointmapping.icd.Named;
import com.cannontech.amr.rfn.service.pointmapping.icd.PointDefinition;
import com.cannontech.amr.rfn.service.pointmapping.icd.PointMapping;
import com.cannontech.amr.rfn.service.pointmapping.icd.PointMappingIcd;
import com.cannontech.amr.rfn.service.pointmapping.icd.RfnPointMappingParser;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDaoImplTest;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.stream.StreamUtils;
import com.cannontech.common.stream.Try;
import com.cannontech.common.util.YamlParserUtils;
import com.cannontech.database.data.point.PointType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

@TestMethodOrder(OrderAnnotation.class)
public class RfnPointMappingTest {
    
    private static final PaoDefinitionDao paoDefinitionDao = PaoDefinitionDaoImplTest.getTestPaoDefinitionDao();
    private static final String MAPPING = "com/cannontech/amr/rfn/service/pointmapping/rfnPointMapping.xml";
    private static final SetMultimap<PaoType, String> knownMissingPoints = getPointsKnownMissingFromPaoDefinition();
    private static final SetMultimap<PaoType, String> knownUnmappedPoints = getPointsKnownMissingFromRfnPointMapping();

    private Map<PaoType, Map<PointMapping, NameScale>> getRfnPointMapping() throws JDOMException, IOException {
        return RfnPointMappingParser.getPaoTypePoints(getRfnXmlStream());
    }

    private PointMappingIcd getPointMappingIcd() throws IOException {
        ClassPathResource yukonPointMappingIcdYaml = new ClassPathResource("yukonPointMappingIcd.yaml");
        
        return YamlParserUtils.parseToObject(yukonPointMappingIcdYaml.getInputStream(), PointMappingIcd.class, yukonPointMappingIcdYaml.getFilename());
    }

    private InputStream getRfnXmlStream() throws IOException {
        ClassPathResource rfnPointMappingXml = new ClassPathResource(MAPPING);
        return rfnPointMappingXml.getInputStream();
    }
    
    @Test
    @Order(1)
    public void detectMissingPaoTypes() throws IOException, JDOMException {
        SAXBuilder saxBuilder = new SAXBuilder();

        Document configDoc = saxBuilder.build(getRfnXmlStream());

        var rfnMappingPaoTypes =
                configDoc.getRootElement().getChildren("pointGroup").stream()
                    //  Collect the paoType entries from the point group structure:
                    //    <pointMappings>
                    //        <pointGroup>
                    //            <paoType(s)>
                    .flatMap(pointGroup -> pointGroup.getChildren("paoType").stream())
                    .map(e -> e.getAttributeValue("value"))
                    .map(PaoType::valueOf)
                    .collect(Collectors.toSet());

        var differences = Sets.symmetricDifference(PaoType.getRfMeterTypesThatSupportPoints(), rfnMappingPaoTypes);

        assertTrue(differences.isEmpty(), "Found mismatch between PaoType.getRfMeterTypes() and rfnPointMapping.xml paoTypes:" + differences);
    }
    
    @Test
    @Order(2)
    public void detectDuplicateMappings() throws IOException, JDOMException {

        SAXBuilder saxBuilder = new SAXBuilder();
        
        Document configDoc = saxBuilder.build(getRfnXmlStream());

        configDoc.getRootElement().getChildren("pointGroup").stream()
            //  Flatten the point group structure from this:
            //    <pointMappings>
            //        <pointGroup>
            //            <paoType(s)>
            //            <point(s)>
            //  to this:
            //    Pair(paoType, pointMapper)
            .flatMap(pointGroup -> {
                //  First, get all of the paoTypes in the point group...
                Set<PaoType> paoTypes = 
                    pointGroup.getChildren("paoType").stream()
                        .map(e -> e.getAttributeValue("value"))
                        .map(PaoType::valueOf)
                        .collect(Collectors.toSet());

                //  ... then take each point in the pointGroup..
                return pointGroup.getChildren("point").stream()
                        //  ... create a pointMapper out of it...
                        .map(UnitOfMeasureToPointMappingParser::createPointMapper)
                        //  ... and create a stream of each paoType paired with each pointMapper.
                        .flatMap(pointMapper -> paoTypes.stream().map(paoType -> Pair.of(paoType, pointMapper)));
            })
            //  Collect all pairs, checking for duplicates.
            .collect(Collectors.toMap(
                  Function.identity(), 
                  Function.identity(),
                  (l, r) -> {
                        fail(String.join("\n", 
                            "PaoType " + l.getKey() + " has conflicting entries:",
                            l.getValue().toString(),
                            r.getValue().toString()));
                        return null;
                  }));
    }
    
    @Test
    @Order(3)
    public void confirmPointsExistOnDevices() throws JDOMException, IOException {
    
        Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping = getRfnPointMapping();

        Map<Boolean, List<Pair<PaoType, String>>> pointLookupAttempts =
            rfnPointMapping.entrySet().stream()
                .flatMap(e -> 
                    e.getValue().values().stream()
                        .map(nameScale -> Pair.of(e.getKey(), nameScale.getName())))
                .collect(Collectors.partitioningBy(e -> 
                    Try.of(() -> paoDefinitionDao.getPointIdentifierByDefaultName(e.getKey(), e.getValue()))
                        .isSuccess()));
        
        Multimap<PaoType, String> successes = pointLookupAttempts.get(true).stream()
                .collect(StreamUtils.toMultimap(Entry::getKey, Entry::getValue));
        Multimap<PaoType, String> failures = pointLookupAttempts.get(false).stream()
                .collect(StreamUtils.toMultimap(Entry::getKey, Entry::getValue));
        
        String unexpectedSuccess =
                successes.entries().stream()
                    .filter(e -> knownMissingPoints.containsEntry(e.getKey(), e.getValue()))
                    .map(e -> e.getKey() + "/" + e.getValue())
                    .collect(Collectors.joining("\n"));
        
        String unexpectedFailure =
                failures.entries().stream()
                    .filter(e -> !knownMissingPoints.containsEntry(e.getKey(), e.getValue()))
                    .map(e -> e.getKey() + "/" + e.getValue())
                    .collect(Collectors.joining("\n"));

        
        assertTrue(unexpectedSuccess.isEmpty(), "Points listed in knownMissingRfnPointMappingPoints but were successfully found:\n" + unexpectedSuccess);
        assertTrue(unexpectedFailure.isEmpty(), "Points not found:\n" + unexpectedFailure);
    }

    @Test
    @Order(4)
    public void confirmDevicePointsHaveMappings() throws JDOMException, IOException {
        Multimap<PaoType, String> rfnPointMappingNames = HashMultimap.create();

        getRfnPointMapping().forEach((paoType, pointMappings) -> rfnPointMappingNames.putAll(paoType,
                pointMappings.entrySet().stream()
                        .filter(e -> e.getKey().isMappedFor(paoType))
                        .map(e -> e.getValue().getName())
                        .collect(Collectors.toList())));

        // Confirm that all knownUnmappedPoints exist in paoDefinition
        var missingUnmappedPoints =
            knownUnmappedPoints.asMap().entrySet().stream()
                .flatMap(e -> {
                    var pointNames = paoDefinitionDao.getAllPointTemplates(e.getKey())
                                                     .stream()
                                                     .map(PointTemplate::getName)
                                                     .collect(Collectors.toSet());
                    var unmapped = Sets.newHashSet(e.getValue());
                    var nonexistent = Sets.difference(unmapped, pointNames);
                    return nonexistent.stream().map(pointName -> e.getKey() + "/" + pointName);
                })
                .collect(Collectors.toList());
        
        assertTrue(missingUnmappedPoints.isEmpty(), 
                "Points declared as \"unmapped\" not found on device, remove from knownUnmappedPoints:" + missingUnmappedPoints);
        
        // Confirm that all knownUnmappedPoints really are missing from rfnPointMapping
        Multimap<PaoType, String> unexpectedlyMapped = Multimaps.filterEntries(knownUnmappedPoints,
                e -> rfnPointMappingNames.containsEntry(e.getKey(), e.getValue()));

        assertTrue(unexpectedlyMapped.isEmpty(), 
                "Points declared as \"unmapped\" actually found in rfnPointMapping:" + unexpectedlyMapped);

        // These are points created by the system
        Predicate<String> ignoredPointNames = pointName ->
                // Populated by Calc
                pointName.equals("Outages")
                // Populated by the Outage and Restore event processors
                || pointName.equals("Outage Count")
                || pointName.equals("Outage Restore Count")
                || pointName.equals("Blink Count")
                || pointName.equals("Blink Restore Count")
                // Populated by PerIntervalAndLoadProfileCalculator
                || pointName.endsWith("per Interval")
                || pointName.endsWith("Profile")
                // not in rfnPointMapping.xml
                || pointName.equals("RSRP")
                || pointName.equals("RSRQ")
                || pointName.equals("RSSI")
                || pointName.equals("SINR");

        Multimap<PaoType, String> paoDefinitionPointNames = HashMultimap.create();

        // Look at the points on the PaoTypes with at least one entry in rfnPointMapping
        rfnPointMappingNames.keySet().forEach(paoType -> paoDefinitionPointNames.putAll(paoType,
                paoDefinitionDao.getAllPointTemplates(paoType).stream()
                        .filter(pt -> pt.getPointType() == PointType.Analog)
                        .map(PointTemplate::getName)
                        .filter(ignoredPointNames.negate())
                        .filter(pointName -> !knownUnmappedPoints.containsEntry(paoType, pointName))
                        .collect(Collectors.toList())));

        Multimap<PaoType, String> unmappedAnalogPoints = Multimaps.filterEntries(
                paoDefinitionPointNames,
                e -> !rfnPointMappingNames.containsEntry(e.getKey(), e.getValue()));

        assertTrue(unmappedAnalogPoints.isEmpty(),
                "Analog points in PaoDefinition missing mappings in rfnPointMapping.xml:" + unmappedAnalogPoints);
    }

    @Test
    @Order(5)
    public void compareToYukonPointMappingIcd() throws IOException, JDOMException {

        PointMappingIcd icd = getPointMappingIcd();

        Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping = getRfnPointMapping();

        compareMeterClassModelPoints(icd, rfnPointMapping, icd.itronCentronWithAdvancedMetrology, MeterClass.ITRON_CENTRON);

        compareMeterClassPoints(icd, rfnPointMapping, icd.lgyrFocusAxRfn420WithAdvancedMetrology, MeterClass.LGYR_FOCUS_AX);

        //  RFN-410 Focus AX models not mapped in yukonPointMapping
        rfnPointMapping.remove(PaoType.RFN410FX);
        rfnPointMapping.remove(PaoType.RFN410FD);
        rfnPointMapping.remove(PaoType.RFN410FD);

        //  RFN-420 Focus RX models not mapped in yukonPointMapping
        rfnPointMapping.remove(PaoType.RFN420FRX);
        rfnPointMapping.remove(PaoType.RFN420FRD);

        compareMeterClassModelPoints(icd, rfnPointMapping, icd.elsterA3, MeterClass.ELSTER_A3);
        compareMeterClassPoints(icd, rfnPointMapping, icd.lgyrFocusKwhWithAdvancedMetrology, MeterClass.LGYR_FOCUS_KWH);

        //  RFN-410FL (Focus kWh in RFN-410) not mapped in yukonPointMapping
        rfnPointMapping.remove(PaoType.RFN410FL);
        
        compareMeterClassModelPoints(icd, rfnPointMapping, icd.elo, MeterClass.ELO);
        compareMeterClassModelPoints(icd, rfnPointMapping, icd.itronSentinel, MeterClass.ITRON_SENTINEL);
        compareMeterClassModelPoints(icd, rfnPointMapping, icd.lgyrFocusAx_rfn500, MeterClass.LGYR_FOCUS_AX_500);
        compareMeterClassModelPoints(icd, rfnPointMapping, icd.lgyrFocusAxe_rfn500, MeterClass.LGYR_FOCUS_AXE_500);
        compareMeterClassModelPoints(icd, rfnPointMapping, icd.lgyrS4_rfn500, MeterClass.LGYR_S4);

        compareMeterClassPoints(icd, rfnPointMapping, icd.nextGenWaterNode, MeterClass.NEXT_GEN_WATER_NODE);
        
        //  1st gen water meter not mapped in yukonPointMapping
        rfnPointMapping.remove(PaoType.RFWMETER);

        //  RFG-201 not mapped in yukonPointMapping
        rfnPointMapping.remove(PaoType.RFG201);

        compareMeterClassPoints(icd, rfnPointMapping, icd.gasNode, MeterClass.GAS_NODE);

        //  RFN-430kV unmapped in yukonPointMapping
        rfnPointMapping.remove(PaoType.RFN430KV);

        // DER Edge Coordinator is unmapped in yukonPointMapping
        rfnPointMapping.remove(PaoType.getNoPointSupportMeterTypes());

        
        rfnPointMapping.entrySet().stream()
            .forEach(e -> fail(e.getKey() + " has " + e.getValue().size() + " points in rfnPointMapping that are not in yukonPointMappingIcd." 
                                      + "\nFirst entry: " + Iterables.getFirst(e.getValue().entrySet(), null)));
    }

    private <T extends ModelPointDefinition> void compareMeterClassModelPoints(PointMappingIcd icd, Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping,
            List<Named<T>> points, MeterClass meterClass) {
        for (PaoType type : getPaoTypesForMeterClass(icd, meterClass)) {
            compareModelPoints(points, rfnPointMapping, type);
        }
    }

    private <T extends PointDefinition> void compareMeterClassPoints(PointMappingIcd icd, Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping,
            List<Named<T>> points, MeterClass meterClass) {
        for (PaoType type : getPaoTypesForMeterClass(icd, meterClass)) {
            comparePoints(points, rfnPointMapping, type);
        }
    }

    private Set<PaoType> getPaoTypesForMeterClass(PointMappingIcd icd, MeterClass meterClass) {
        return icd.meterModels.get(meterClass).stream()
                .map(ManufacturerModel::getManufacturerModel)
                .map(RfnManufacturerModel::getType)
                .collect(Collectors.toSet());
    }

    private <T extends ModelPointDefinition> void compareModelPoints(List<Named<T>> points, Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping, PaoType type) {
        Map<PointMapping, NameScale> icdTypePoints = 
                points.stream()
                    .filter(nmpd -> nmpd.value.getModels().stream()
                                .map(ManufacturerModel::getManufacturerModel)
                                .map(RfnManufacturerModel::getType)
                                .anyMatch(type::equals))
                    .collect(new CoincidentGroupingCollector());
        
        compare(rfnPointMapping, type, icdTypePoints);
    }

    private <T extends PointDefinition> void comparePoints(List<Named<T>> points, Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping, PaoType type) {
        
        Map<PointMapping, NameScale> icdTypePoints = 
                points.stream().collect(new CoincidentGroupingCollector());
        
        compare(rfnPointMapping, type, icdTypePoints);
    }

    private void compare(Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping, PaoType type,
            Map<PointMapping, NameScale> icdTypePoints) {
        Map<PointMapping, NameScale> rpmTypePoints = rfnPointMapping.get(type);
        
        assertNotNull(rpmTypePoints, "No points found for type " + type);
        
        Map<Boolean,Set<PointMapping>> rpmMapped = rpmTypePoints.keySet().stream().collect(Collectors.partitioningBy(p -> p.isMappedFor(type), Collectors.toSet()));
        Map<Boolean,Set<PointMapping>> icdMapped = icdTypePoints.keySet().stream().collect(Collectors.partitioningBy(p -> p.isMappedFor(type), Collectors.toSet()));
        
        Set<PointMapping> rpmMappedPoints = rpmMapped.get(true);
        Set<PointMapping> icdMappedPoints = icdMapped.get(true);
        Set<PointMapping> rpmUnmappedPoints = rpmMapped.get(false);
        Set<PointMapping> icdUnmappedPoints = icdMapped.get(false);
        
        verifyEmpty(type, rpmTypePoints, Sets.intersection(rpmUnmappedPoints, icdMappedPoints), "marked as unmapped in rfnPointMapping.xml, but exist in yukonPointMapping.yaml");
        verifyEmpty(type, icdTypePoints, Sets.intersection(rpmMappedPoints, icdUnmappedPoints), "marked as unmapped in yukonPointMapping.yaml, but exist in rfnPointMapping.xml");

        verifyEmpty(type, rpmTypePoints, Sets.intersection(rpmUnmappedPoints, icdUnmappedPoints), "marked as unmapped in both rfnPointMapping.xml and yukonPointMapping.yaml");

        verifyEmpty(type, rpmTypePoints, Sets.difference(rpmMappedPoints, icdMappedPoints), "not in yukonPointMappingIcd.yaml");
        verifyEmpty(type, icdTypePoints, Sets.difference(icdMappedPoints, rpmMappedPoints), "not in rfnPointMapping.xml");
        
        rfnPointMapping.remove(type);
    }

    private void verifyEmpty(PaoType type, Map<PointMapping, NameScale> typePoints, SetView<PointMapping> extraneous, String violation) {
        final int MaxPointCount = 10;

        if (!extraneous.isEmpty()) {
            String complaint = type + " has " + extraneous.size() + " point(s) " + violation;
            if (extraneous.size() > MaxPointCount) {
                complaint += ", first " + MaxPointCount + " listed:"; 
            }
            complaint += "\n" + extraneous.stream()
                .limit(MaxPointCount)
                .map(cp -> typePoints.get(cp).getName() + "\n        " + cp)
                .collect(Collectors.joining("\n"));
            fail(complaint);
        }
    }
    
    // Tell SonarLint to ignore the duplicated strings
    @SuppressWarnings("squid:S1192")
    private static SetMultimap<PaoType, String> getPointsKnownMissingFromPaoDefinition() {
        // These are all points that are defined in rfnPointMapping for a given PaoType that have no point in paoDefinition.
        // This can be due to a pointGroup that contains definitions that should not be applied to one or more of their PaoTypes.
        // For example, the Amps Phase B point on the single-phase RFN420FD below.
        var missing = HashMultimap.<PaoType, String>create();

        missing.put(PaoType.RFN420FD, "Amps Phase B");
        missing.put(PaoType.RFN420FD, "Amps Phase C");
        missing.put(PaoType.RFN420FD, "Avg Volts Phase B");
        missing.put(PaoType.RFN420FD, "Avg Volts Phase C");
        missing.put(PaoType.RFN420FD, "Max Volts Phase B");
        missing.put(PaoType.RFN420FD, "Max Volts Phase C");
        missing.put(PaoType.RFN420FD, "Min Volts Phase B");
        missing.put(PaoType.RFN420FD, "Min Volts Phase C");
        missing.put(PaoType.RFN420FD, "Power Factor Degrees Phase B");
        missing.put(PaoType.RFN420FD, "Power Factor Degrees Phase C");
        missing.put(PaoType.RFN420FD, "Volt Degrees Phase B");
        missing.put(PaoType.RFN420FD, "Volt Degrees Phase C");
        missing.put(PaoType.RFN420FD, "Volts Phase B");
        missing.put(PaoType.RFN420FD, "Volts Phase C");
        
        missing.put(PaoType.RFN420FRD, "Amps Phase B");
        missing.put(PaoType.RFN420FRD, "Amps Phase C");
        missing.put(PaoType.RFN420FRD, "Avg Volts Phase B");
        missing.put(PaoType.RFN420FRD, "Avg Volts Phase C");
        missing.put(PaoType.RFN420FRD, "Max Volts Phase B");
        missing.put(PaoType.RFN420FRD, "Max Volts Phase C");
        missing.put(PaoType.RFN420FRD, "Min Volts Phase B");
        missing.put(PaoType.RFN420FRD, "Min Volts Phase C");
        missing.put(PaoType.RFN420FRD, "Power Factor Degrees Phase B");
        missing.put(PaoType.RFN420FRD, "Power Factor Degrees Phase C");
        missing.put(PaoType.RFN420FRD, "Volt Degrees Phase B");
        missing.put(PaoType.RFN420FRD, "Volt Degrees Phase C");
        missing.put(PaoType.RFN420FRD, "Volts Phase B");
        missing.put(PaoType.RFN420FRD, "Volts Phase C");
        
        missing.put(PaoType.RFN420FRX, "Amps Phase B");
        missing.put(PaoType.RFN420FRX, "Amps Phase C");
        missing.put(PaoType.RFN420FRX, "Avg Volts Phase B");
        missing.put(PaoType.RFN420FRX, "Avg Volts Phase C");
        missing.put(PaoType.RFN420FRX, "Max Volts Phase B");
        missing.put(PaoType.RFN420FRX, "Max Volts Phase C");
        missing.put(PaoType.RFN420FRX, "Min Volts Phase B");
        missing.put(PaoType.RFN420FRX, "Min Volts Phase C");
        missing.put(PaoType.RFN420FRX, "Power Factor Degrees Phase B");
        missing.put(PaoType.RFN420FRX, "Power Factor Degrees Phase C");
        missing.put(PaoType.RFN420FRX, "Volt Degrees Phase B");
        missing.put(PaoType.RFN420FRX, "Volt Degrees Phase C");
        missing.put(PaoType.RFN420FRX, "Volts Phase B");
        missing.put(PaoType.RFN420FRX, "Volts Phase C");
        
        missing.put(PaoType.RFN420FX, "Amps Phase B");
        missing.put(PaoType.RFN420FX, "Amps Phase C");
        missing.put(PaoType.RFN420FX, "Avg Volts Phase B");
        missing.put(PaoType.RFN420FX, "Avg Volts Phase C");
        missing.put(PaoType.RFN420FX, "Max Volts Phase B");
        missing.put(PaoType.RFN420FX, "Max Volts Phase C");
        missing.put(PaoType.RFN420FX, "Min Volts Phase B");
        missing.put(PaoType.RFN420FX, "Min Volts Phase C");
        missing.put(PaoType.RFN420FX, "Power Factor Degrees Phase B");
        missing.put(PaoType.RFN420FX, "Power Factor Degrees Phase C");
        missing.put(PaoType.RFN420FX, "Volt Degrees Phase B");
        missing.put(PaoType.RFN420FX, "Volt Degrees Phase C");
        missing.put(PaoType.RFN420FX, "Volts Phase B");
        missing.put(PaoType.RFN420FX, "Volts Phase C");
        
        missing.put(PaoType.RFN430A3D, "Peak Demand Daily");
        missing.put(PaoType.RFN430A3K, "Peak Demand Daily");
        missing.put(PaoType.RFN430A3R, "Peak Demand Daily");
        missing.put(PaoType.RFN430A3T, "Peak Demand Daily");
        
        missing.put(PaoType.RFN430KV, "Average Delivered Power Factor");
        missing.put(PaoType.RFN430KV, "Average Received Power Factor");
        missing.put(PaoType.RFN430KV, "Delivered kVA");
        missing.put(PaoType.RFN430KV, "Delivered kVAr");
        missing.put(PaoType.RFN430KV, "Peak Demand Daily");
        missing.put(PaoType.RFN430KV, "Received kVA");
        missing.put(PaoType.RFN430KV, "Received kVAr");
        missing.put(PaoType.RFN430KV, "Received kW");
        
        missing.put(PaoType.RFN430SL4, "Net Delivered kVARh");
        missing.put(PaoType.RFN430SL4, "Net Received kVARh");
        missing.put(PaoType.RFN430SL4, "Rate A Net Received kVARh");
        missing.put(PaoType.RFN430SL4, "Rate B Net Received kVARh");
        missing.put(PaoType.RFN430SL4, "Rate C Net Received kVARh");
        missing.put(PaoType.RFN430SL4, "Rate D Net Received kVARh");
        
        missing.put(PaoType.RFN440_2131TD, "Forward Inductive kVARh");
        missing.put(PaoType.RFN440_2131TD, "Reverse Inductive kVARh");
        missing.put(PaoType.RFN440_2132TD, "Forward Inductive kVARh");
        missing.put(PaoType.RFN440_2132TD, "Reverse Inductive kVARh");
        missing.put(PaoType.RFN440_2133TD, "Forward Inductive kVARh");
        missing.put(PaoType.RFN440_2133TD, "Reverse Inductive kVARh");
        
        //  Focus AX (Gen 1)
        missing.put(PaoType.RFN520FAX, "Peak kVA Lagging Frozen");
        missing.put(PaoType.RFN520FAX, "Peak kVA (Quadrants 1 2 4)");
        missing.put(PaoType.RFN520FAX, "Delivered Peak kVAr");
        missing.put(PaoType.RFN520FAX, "kVAh Lagging");
        missing.put(PaoType.RFN520FAX, "Delivered kVArh");
        missing.put(PaoType.RFN520FAX, "Net kVAh");
        missing.put(PaoType.RFN520FAX, "Net kVArh");
        missing.put(PaoType.RFN520FAX, "Power Factor Degrees");
        missing.put(PaoType.RFN520FAX, "Received kVAh");
        missing.put(PaoType.RFN520FAX, "Received kVArh");
        missing.put(PaoType.RFN520FAX, "Sum Peak kVA Frozen");
        missing.put(PaoType.RFN520FAX, "Sum Peak kVA");
        missing.put(PaoType.RFN520FAX, "Sum Peak kVAr Frozen");
        missing.put(PaoType.RFN520FAX, "Sum Peak kVAr");
        missing.put(PaoType.RFN520FAX, "Sum kVAh");
        missing.put(PaoType.RFN520FAX, "Sum kVArh");
        missing.put(PaoType.RFN520FAX, "Delivered kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN520FAX, "Delivered kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN520FAX, "Delivered kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN520FAX, "Delivered kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN520FAX, "Delivered kVArh (Quadrant 1)");
        missing.put(PaoType.RFN520FAX, "Delivered kVArh (Quadrant 1) (Rate A kVArh)");
        missing.put(PaoType.RFN520FAX, "Delivered kVArh (Quadrant 1) (Rate B kVArh)");
        missing.put(PaoType.RFN520FAX, "Delivered kVArh (Quadrant 1) (Rate C kVArh)");
        missing.put(PaoType.RFN520FAX, "Delivered kVArh (Quadrant 1) (Rate D kVArh)");
        missing.put(PaoType.RFN520FAX, "Received kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN520FAX, "Received kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN520FAX, "Received kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN520FAX, "Received kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN520FAX, "Received kVArh (Quadrant 4)");
        missing.put(PaoType.RFN520FAX, "Received kVArh (Quadrant 4) (Rate A kVArh)");
        missing.put(PaoType.RFN520FAX, "Received kVArh (Quadrant 4) (Rate B kVArh)");
        missing.put(PaoType.RFN520FAX, "Received kVArh (Quadrant 4) (Rate C kVArh)");
        missing.put(PaoType.RFN520FAX, "Received kVArh (Quadrant 4) (Rate D kVArh)");
        missing.put(PaoType.RFN520FAX, "Net kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN520FAX, "Net kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN520FAX, "Net kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN520FAX, "Net kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN520FAX, "Sum kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN520FAX, "Sum kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN520FAX, "Sum kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN520FAX, "Sum kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN520FAX, "Delivered kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN520FAX, "Delivered kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN520FAX, "Delivered kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN520FAX, "Delivered kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN520FAX, "kVAh (Quadrants 2 3 4)");
        missing.put(PaoType.RFN520FAX, "kVAh (Quadrants 2 3 4) (Rate A kVAh)");
        missing.put(PaoType.RFN520FAX, "kVAh (Quadrants 2 3 4) (Rate B kVAh)");
        missing.put(PaoType.RFN520FAX, "kVAh (Quadrants 2 3 4) (Rate C kVAh)");
        missing.put(PaoType.RFN520FAX, "kVAh (Quadrants 2 3 4) (Rate D kVAh)");
        missing.put(PaoType.RFN520FAX, "Net kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN520FAX, "Net kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN520FAX, "Net kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN520FAX, "Net kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN520FAX, "Sum kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN520FAX, "Sum kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN520FAX, "Sum kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN520FAX, "Sum kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN520FAX, "Delivered Peak kVAr (Rate A kVAr)");
        missing.put(PaoType.RFN520FAX, "Delivered Peak kVAr (Rate B kVAr)");
        missing.put(PaoType.RFN520FAX, "Delivered Peak kVAr (Rate C kVAr)");
        missing.put(PaoType.RFN520FAX, "Delivered Peak kVAr (Rate D kVAr)");
        missing.put(PaoType.RFN520FAX, "Peak kVA (Quadrants 1 2 4) (Rate A kVA)");
        missing.put(PaoType.RFN520FAX, "Peak kVA (Quadrants 1 2 4) (Rate B kVA)");
        missing.put(PaoType.RFN520FAX, "Peak kVA (Quadrants 1 2 4) (Rate C kVA)");
        missing.put(PaoType.RFN520FAX, "Peak kVA (Quadrants 1 2 4) (Rate D kVA)");
        missing.put(PaoType.RFN520FAX, "Sum Peak kVAr Frozen");
        missing.put(PaoType.RFN520FAX, "Delivered Peak kVAr Frozen");
        missing.put(PaoType.RFN520FAX, "Delivered Peak kVAr Frozen (Rate A kVAr)");
        missing.put(PaoType.RFN520FAX, "Delivered Peak kVAr Frozen (Rate B kVAr)");
        missing.put(PaoType.RFN520FAX, "Delivered Peak kVAr Frozen (Rate C kVAr)");
        missing.put(PaoType.RFN520FAX, "Delivered Peak kVAr Frozen (Rate D kVAr)");
        missing.put(PaoType.RFN520FAX, "Peak kVA (Quadrants 1 2 4) Frozen");
        missing.put(PaoType.RFN520FAX, "Peak kVA (Quadrants 1 2 4) Frozen (Rate A kVA)");
        missing.put(PaoType.RFN520FAX, "Peak kVA (Quadrants 1 2 4) Frozen (Rate B kVA)");
        missing.put(PaoType.RFN520FAX, "Peak kVA (Quadrants 1 2 4) Frozen (Rate C kVA)");
        missing.put(PaoType.RFN520FAX, "Peak kVA (Quadrants 1 2 4) Frozen (Rate D kVA)");
        missing.put(PaoType.RFN520FAX, "kVAh (Quadrants 1 2 4)");
        missing.put(PaoType.RFN520FAX, "kVAh (Quadrants 1 2 4) (Rate A kVAh)");
        missing.put(PaoType.RFN520FAX, "kVAh (Quadrants 1 2 4) (Rate B kVAh)");
        missing.put(PaoType.RFN520FAX, "kVAh (Quadrants 1 2 4) (Rate C kVAh)");
        missing.put(PaoType.RFN520FAX, "kVAh (Quadrants 1 2 4) (Rate D kVAh)");
        missing.put(PaoType.RFN520FAX, "Delivered kVAr");
        missing.put(PaoType.RFN520FAX, "Sum kVAr");
        missing.put(PaoType.RFN520FAX, "kVA (Quadrants 1 2 4)");
        missing.put(PaoType.RFN520FAX, "Sum kVA");
        
        missing.put(PaoType.RFN520FAXD, "Peak kVA Lagging Frozen");
        missing.put(PaoType.RFN520FAXD, "Peak kVA (Quadrants 1 2 4)");
        missing.put(PaoType.RFN520FAXD, "Delivered Peak kVAr");
        missing.put(PaoType.RFN520FAXD, "kVAh Lagging");
        missing.put(PaoType.RFN520FAXD, "Delivered kVArh");
        missing.put(PaoType.RFN520FAXD, "Net kVAh");
        missing.put(PaoType.RFN520FAXD, "Net kVArh");
        missing.put(PaoType.RFN520FAXD, "Power Factor Degrees");
        missing.put(PaoType.RFN520FAXD, "Received kVAh");
        missing.put(PaoType.RFN520FAXD, "Received kVArh");
        missing.put(PaoType.RFN520FAXD, "Sum Peak kVA Frozen");
        missing.put(PaoType.RFN520FAXD, "Sum Peak kVA");
        missing.put(PaoType.RFN520FAXD, "Sum Peak kVAr Frozen");
        missing.put(PaoType.RFN520FAXD, "Sum Peak kVAr");
        missing.put(PaoType.RFN520FAXD, "Sum kVAh");
        missing.put(PaoType.RFN520FAXD, "Sum kVArh");
        missing.put(PaoType.RFN520FAXD, "Delivered kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN520FAXD, "Delivered kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN520FAXD, "Delivered kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN520FAXD, "Delivered kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN520FAXD, "Received kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN520FAXD, "Received kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN520FAXD, "Received kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN520FAXD, "Received kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN520FAXD, "Delivered kVArh (Quadrant 1)");
        missing.put(PaoType.RFN520FAXD, "Delivered kVArh (Quadrant 1) (Rate A kVArh)");
        missing.put(PaoType.RFN520FAXD, "Delivered kVArh (Quadrant 1) (Rate B kVArh)");
        missing.put(PaoType.RFN520FAXD, "Delivered kVArh (Quadrant 1) (Rate C kVArh)");
        missing.put(PaoType.RFN520FAXD, "Delivered kVArh (Quadrant 1) (Rate D kVArh)");
        missing.put(PaoType.RFN520FAXD, "Received kVArh (Quadrant 4)");
        missing.put(PaoType.RFN520FAXD, "Received kVArh (Quadrant 4) (Rate A kVArh)");
        missing.put(PaoType.RFN520FAXD, "Received kVArh (Quadrant 4) (Rate B kVArh)");
        missing.put(PaoType.RFN520FAXD, "Received kVArh (Quadrant 4) (Rate C kVArh)");
        missing.put(PaoType.RFN520FAXD, "Received kVArh (Quadrant 4) (Rate D kVArh)");
        missing.put(PaoType.RFN520FAXD, "Net kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN520FAXD, "Net kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN520FAXD, "Net kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN520FAXD, "Net kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN520FAXD, "Sum kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN520FAXD, "Sum kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN520FAXD, "Sum kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN520FAXD, "Sum kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN520FAXD, "Delivered kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN520FAXD, "Delivered kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN520FAXD, "Delivered kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN520FAXD, "Delivered kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN520FAXD, "kVAh (Quadrants 2 3 4)");
        missing.put(PaoType.RFN520FAXD, "kVAh (Quadrants 2 3 4) (Rate A kVAh)");
        missing.put(PaoType.RFN520FAXD, "kVAh (Quadrants 2 3 4) (Rate B kVAh)");
        missing.put(PaoType.RFN520FAXD, "kVAh (Quadrants 2 3 4) (Rate C kVAh)");
        missing.put(PaoType.RFN520FAXD, "kVAh (Quadrants 2 3 4) (Rate D kVAh)");
        missing.put(PaoType.RFN520FAXD, "Net kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN520FAXD, "Net kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN520FAXD, "Net kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN520FAXD, "Net kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN520FAXD, "Sum kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN520FAXD, "Sum kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN520FAXD, "Sum kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN520FAXD, "Sum kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN520FAXD, "Delivered Peak kVAr (Rate A kVAr)");
        missing.put(PaoType.RFN520FAXD, "Delivered Peak kVAr (Rate B kVAr)");
        missing.put(PaoType.RFN520FAXD, "Delivered Peak kVAr (Rate C kVAr)");
        missing.put(PaoType.RFN520FAXD, "Delivered Peak kVAr (Rate D kVAr)");
        missing.put(PaoType.RFN520FAXD, "Peak kVA (Quadrants 1 2 4) (Rate A kVA)");
        missing.put(PaoType.RFN520FAXD, "Peak kVA (Quadrants 1 2 4) (Rate B kVA)");
        missing.put(PaoType.RFN520FAXD, "Peak kVA (Quadrants 1 2 4) (Rate C kVA)");
        missing.put(PaoType.RFN520FAXD, "Peak kVA (Quadrants 1 2 4) (Rate D kVA)");
        missing.put(PaoType.RFN520FAXD, "Sum Peak kVAr Frozen");
        missing.put(PaoType.RFN520FAXD, "Delivered Peak kVAr Frozen");
        missing.put(PaoType.RFN520FAXD, "Delivered Peak kVAr Frozen (Rate A kVAr)");
        missing.put(PaoType.RFN520FAXD, "Delivered Peak kVAr Frozen (Rate B kVAr)");
        missing.put(PaoType.RFN520FAXD, "Delivered Peak kVAr Frozen (Rate C kVAr)");
        missing.put(PaoType.RFN520FAXD, "Delivered Peak kVAr Frozen (Rate D kVAr)");
        missing.put(PaoType.RFN520FAXD, "Peak kVA (Quadrants 1 2 4) Frozen");
        missing.put(PaoType.RFN520FAXD, "Peak kVA (Quadrants 1 2 4) Frozen (Rate A kVA)");
        missing.put(PaoType.RFN520FAXD, "Peak kVA (Quadrants 1 2 4) Frozen (Rate B kVA)");
        missing.put(PaoType.RFN520FAXD, "Peak kVA (Quadrants 1 2 4) Frozen (Rate C kVA)");
        missing.put(PaoType.RFN520FAXD, "Peak kVA (Quadrants 1 2 4) Frozen (Rate D kVA)");
        missing.put(PaoType.RFN520FAXD, "kVAh (Quadrants 1 2 4)");
        missing.put(PaoType.RFN520FAXD, "kVAh (Quadrants 1 2 4) (Rate A kVAh)");
        missing.put(PaoType.RFN520FAXD, "kVAh (Quadrants 1 2 4) (Rate B kVAh)");
        missing.put(PaoType.RFN520FAXD, "kVAh (Quadrants 1 2 4) (Rate C kVAh)");
        missing.put(PaoType.RFN520FAXD, "kVAh (Quadrants 1 2 4) (Rate D kVAh)");
        missing.put(PaoType.RFN520FAXD, "Delivered kVAr");
        missing.put(PaoType.RFN520FAXD, "Sum kVAr");
        missing.put(PaoType.RFN520FAXD, "kVA (Quadrants 1 2 4)");
        missing.put(PaoType.RFN520FAXD, "Sum kVA");
        
        missing.put(PaoType.RFN520FRX, "Peak kVA Lagging Frozen");
        missing.put(PaoType.RFN520FRX, "Power Factor Degrees");
        
        missing.put(PaoType.RFN520FRXD, "Peak kVA Lagging Frozen");
        missing.put(PaoType.RFN520FRXD, "Power Factor Degrees");
        
        missing.put(PaoType.RFN530FAX, "Peak kVA Lagging Frozen");
        missing.put(PaoType.RFN530FAX, "Peak kVA (Quadrants 1 2 4)");
        missing.put(PaoType.RFN530FAX, "Delivered Peak kVAr");
        missing.put(PaoType.RFN530FAX, "kVAh Lagging");
        missing.put(PaoType.RFN530FAX, "Delivered kVArh");
        missing.put(PaoType.RFN530FAX, "Net kVAh");
        missing.put(PaoType.RFN530FAX, "Net kVArh");
        missing.put(PaoType.RFN530FAX, "Power Factor Degrees Phase A");
        missing.put(PaoType.RFN530FAX, "Power Factor Degrees Phase B");
        missing.put(PaoType.RFN530FAX, "Power Factor Degrees Phase C");
        missing.put(PaoType.RFN530FAX, "Received kVAh");
        missing.put(PaoType.RFN530FAX, "Received kVArh");
        missing.put(PaoType.RFN530FAX, "Sum Peak kVA Frozen");
        missing.put(PaoType.RFN530FAX, "Sum Peak kVA");
        missing.put(PaoType.RFN530FAX, "Sum Peak kVAr");
        missing.put(PaoType.RFN530FAX, "Sum kVAh");
        missing.put(PaoType.RFN530FAX, "Sum kVArh");
        missing.put(PaoType.RFN530FAX, "Current Angle Phase A");
        missing.put(PaoType.RFN530FAX, "Current Angle Phase B");
        missing.put(PaoType.RFN530FAX, "Current Angle Phase C");
        missing.put(PaoType.RFN530FAX, "Delivered kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530FAX, "Delivered kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530FAX, "Delivered kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530FAX, "Delivered kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530FAX, "Received kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530FAX, "Received kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530FAX, "Received kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530FAX, "Received kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530FAX, "Delivered kVArh (Quadrant 1)");
        missing.put(PaoType.RFN530FAX, "Delivered kVArh (Quadrant 1) (Rate A kVArh)");
        missing.put(PaoType.RFN530FAX, "Delivered kVArh (Quadrant 1) (Rate B kVArh)");
        missing.put(PaoType.RFN530FAX, "Delivered kVArh (Quadrant 1) (Rate C kVArh)");
        missing.put(PaoType.RFN530FAX, "Delivered kVArh (Quadrant 1) (Rate D kVArh)");
        missing.put(PaoType.RFN530FAX, "Received kVArh (Quadrant 4)");
        missing.put(PaoType.RFN530FAX, "Received kVArh (Quadrant 4) (Rate A kVArh)");
        missing.put(PaoType.RFN530FAX, "Received kVArh (Quadrant 4) (Rate B kVArh)");
        missing.put(PaoType.RFN530FAX, "Received kVArh (Quadrant 4) (Rate C kVArh)");
        missing.put(PaoType.RFN530FAX, "Received kVArh (Quadrant 4) (Rate D kVArh)");
        missing.put(PaoType.RFN530FAX, "Net kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530FAX, "Net kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530FAX, "Net kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530FAX, "Net kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530FAX, "Sum kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530FAX, "Sum kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530FAX, "Sum kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530FAX, "Sum kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530FAX, "Delivered kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN530FAX, "Delivered kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN530FAX, "Delivered kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN530FAX, "Delivered kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN530FAX, "kVAh (Quadrants 2 3 4)");
        missing.put(PaoType.RFN530FAX, "kVAh (Quadrants 2 3 4) (Rate A kVAh)");
        missing.put(PaoType.RFN530FAX, "kVAh (Quadrants 2 3 4) (Rate B kVAh)");
        missing.put(PaoType.RFN530FAX, "kVAh (Quadrants 2 3 4) (Rate C kVAh)");
        missing.put(PaoType.RFN530FAX, "kVAh (Quadrants 2 3 4) (Rate D kVAh)");
        missing.put(PaoType.RFN530FAX, "Net kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN530FAX, "Net kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN530FAX, "Net kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN530FAX, "Net kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN530FAX, "Sum kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN530FAX, "Sum kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN530FAX, "Sum kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN530FAX, "Sum kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN530FAX, "Delivered Peak kVAr (Rate A kVAr)");
        missing.put(PaoType.RFN530FAX, "Delivered Peak kVAr (Rate B kVAr)");
        missing.put(PaoType.RFN530FAX, "Delivered Peak kVAr (Rate C kVAr)");
        missing.put(PaoType.RFN530FAX, "Delivered Peak kVAr (Rate D kVAr)");
        missing.put(PaoType.RFN530FAX, "Peak kVA (Quadrants 1 2 4) (Rate A kVA)");
        missing.put(PaoType.RFN530FAX, "Peak kVA (Quadrants 1 2 4) (Rate B kVA)");
        missing.put(PaoType.RFN530FAX, "Peak kVA (Quadrants 1 2 4) (Rate C kVA)");
        missing.put(PaoType.RFN530FAX, "Peak kVA (Quadrants 1 2 4) (Rate D kVA)");
        missing.put(PaoType.RFN530FAX, "Sum Peak kVAr Frozen");
        missing.put(PaoType.RFN530FAX, "Delivered Peak kVAr Frozen");
        missing.put(PaoType.RFN530FAX, "Delivered Peak kVAr Frozen (Rate A kVAr)");
        missing.put(PaoType.RFN530FAX, "Delivered Peak kVAr Frozen (Rate B kVAr)");
        missing.put(PaoType.RFN530FAX, "Delivered Peak kVAr Frozen (Rate C kVAr)");
        missing.put(PaoType.RFN530FAX, "Delivered Peak kVAr Frozen (Rate D kVAr)");
        missing.put(PaoType.RFN530FAX, "Peak kVA (Quadrants 1 2 4) Frozen");
        missing.put(PaoType.RFN530FAX, "Peak kVA (Quadrants 1 2 4) Frozen (Rate A kVA)");
        missing.put(PaoType.RFN530FAX, "Peak kVA (Quadrants 1 2 4) Frozen (Rate B kVA)");
        missing.put(PaoType.RFN530FAX, "Peak kVA (Quadrants 1 2 4) Frozen (Rate C kVA)");
        missing.put(PaoType.RFN530FAX, "Peak kVA (Quadrants 1 2 4) Frozen (Rate D kVA)");
        missing.put(PaoType.RFN530FAX, "kVAh (Quadrants 1 2 4)");
        missing.put(PaoType.RFN530FAX, "kVAh (Quadrants 1 2 4) (Rate A kVAh)");
        missing.put(PaoType.RFN530FAX, "kVAh (Quadrants 1 2 4) (Rate B kVAh)");
        missing.put(PaoType.RFN530FAX, "kVAh (Quadrants 1 2 4) (Rate C kVAh)");
        missing.put(PaoType.RFN530FAX, "kVAh (Quadrants 1 2 4) (Rate D kVAh)");
        missing.put(PaoType.RFN530FAX, "Delivered kVAr");
        missing.put(PaoType.RFN530FAX, "Sum kVAr");
        missing.put(PaoType.RFN530FAX, "kVA (Quadrants 1 2 4)");
        missing.put(PaoType.RFN530FAX, "Sum kVA");

        missing.put(PaoType.RFN530FRX, "Peak kVA Lagging Frozen");
        missing.put(PaoType.RFN530FRX, "Power Factor Degrees Phase A");
        missing.put(PaoType.RFN530FRX, "Power Factor Degrees Phase B");
        missing.put(PaoType.RFN530FRX, "Power Factor Degrees Phase C");
        missing.put(PaoType.RFN530FRX, "Current Angle Phase A");
        missing.put(PaoType.RFN530FRX, "Current Angle Phase B");
        missing.put(PaoType.RFN530FRX, "Current Angle Phase C");
        
        //  Focus AXe (Gen 2)
        List.of(PaoType.RFN520FAXE, PaoType.CRL520FAXE, PaoType.RFN520FAXED, PaoType.CRL520FAXED).forEach(type -> {
            missing.put(type, "Peak kVA Lagging Frozen");
            missing.put(type, "Peak kVA (Quadrants 1 2 4)");
            missing.put(type, "Delivered Peak kVAr");
            missing.put(type, "kVAh Lagging");
            missing.put(type, "Delivered kVArh");
            missing.put(type, "Net kVAh");
            missing.put(type, "Net kVArh");
            missing.put(type, "Power Factor Degrees");
            missing.put(type, "Received kVAh");
            missing.put(type, "Received kVArh");
            missing.put(type, "Sum Peak kVA Frozen");
            missing.put(type, "Sum Peak kVA");
            missing.put(type, "Sum Peak kVAr Frozen");
            missing.put(type, "Sum Peak kVAr");
            missing.put(type, "Sum kVAh");
            missing.put(type, "Sum kVArh");
            missing.put(type, "Delivered kVArh (Rate A kVArh)");
            missing.put(type, "Delivered kVArh (Rate B kVArh)");
            missing.put(type, "Delivered kVArh (Rate C kVArh)");
            missing.put(type, "Delivered kVArh (Rate D kVArh)");
            missing.put(type, "Received kVArh (Rate A kVArh)");
            missing.put(type, "Received kVArh (Rate B kVArh)");
            missing.put(type, "Received kVArh (Rate C kVArh)");
            missing.put(type, "Received kVArh (Rate D kVArh)");
            missing.put(type, "Delivered kVArh (Quadrant 1)");
            missing.put(type, "Delivered kVArh (Quadrant 1) (Rate A kVArh)");
            missing.put(type, "Delivered kVArh (Quadrant 1) (Rate B kVArh)");
            missing.put(type, "Delivered kVArh (Quadrant 1) (Rate C kVArh)");
            missing.put(type, "Delivered kVArh (Quadrant 1) (Rate D kVArh)");
            missing.put(type, "Received kVArh (Quadrant 4)");
            missing.put(type, "Received kVArh (Quadrant 4) (Rate A kVArh)");
            missing.put(type, "Received kVArh (Quadrant 4) (Rate B kVArh)");
            missing.put(type, "Received kVArh (Quadrant 4) (Rate C kVArh)");
            missing.put(type, "Received kVArh (Quadrant 4) (Rate D kVArh)");
            missing.put(type, "Net kVArh (Rate A kVArh)");
            missing.put(type, "Net kVArh (Rate B kVArh)");
            missing.put(type, "Net kVArh (Rate C kVArh)");
            missing.put(type, "Net kVArh (Rate D kVArh)");
            missing.put(type, "Sum kVArh (Rate A kVArh)");
            missing.put(type, "Sum kVArh (Rate B kVArh)");
            missing.put(type, "Sum kVArh (Rate C kVArh)");
            missing.put(type, "Sum kVArh (Rate D kVArh)");
            missing.put(type, "Delivered kVAh (Rate A kVAh)");
            missing.put(type, "Delivered kVAh (Rate B kVAh)");
            missing.put(type, "Delivered kVAh (Rate C kVAh)");
            missing.put(type, "Delivered kVAh (Rate D kVAh)");
            missing.put(type, "kVAh (Quadrants 2 3 4)");
            missing.put(type, "kVAh (Quadrants 2 3 4) (Rate A kVAh)");
            missing.put(type, "kVAh (Quadrants 2 3 4) (Rate B kVAh)");
            missing.put(type, "kVAh (Quadrants 2 3 4) (Rate C kVAh)");
            missing.put(type, "kVAh (Quadrants 2 3 4) (Rate D kVAh)");
            missing.put(type, "Net kVAh (Rate A kVAh)");
            missing.put(type, "Net kVAh (Rate B kVAh)");
            missing.put(type, "Net kVAh (Rate C kVAh)");
            missing.put(type, "Net kVAh (Rate D kVAh)");
            missing.put(type, "Sum kVAh (Rate A kVAh)");
            missing.put(type, "Sum kVAh (Rate B kVAh)");
            missing.put(type, "Sum kVAh (Rate C kVAh)");
            missing.put(type, "Sum kVAh (Rate D kVAh)");
            missing.put(type, "Delivered Peak kVAr (Rate A kVAr)");
            missing.put(type, "Delivered Peak kVAr (Rate B kVAr)");
            missing.put(type, "Delivered Peak kVAr (Rate C kVAr)");
            missing.put(type, "Delivered Peak kVAr (Rate D kVAr)");
            missing.put(type, "Peak kVA (Quadrants 1 2 4) (Rate A kVA)");
            missing.put(type, "Peak kVA (Quadrants 1 2 4) (Rate B kVA)");
            missing.put(type, "Peak kVA (Quadrants 1 2 4) (Rate C kVA)");
            missing.put(type, "Peak kVA (Quadrants 1 2 4) (Rate D kVA)");
            missing.put(type, "Sum Peak kVAr Frozen");
            missing.put(type, "Delivered Peak kVAr Frozen");
            missing.put(type, "Delivered Peak kVAr Frozen (Rate A kVAr)");
            missing.put(type, "Delivered Peak kVAr Frozen (Rate B kVAr)");
            missing.put(type, "Delivered Peak kVAr Frozen (Rate C kVAr)");
            missing.put(type, "Delivered Peak kVAr Frozen (Rate D kVAr)");
            missing.put(type, "Peak kVA (Quadrants 1 2 4) Frozen");
            missing.put(type, "Peak kVA (Quadrants 1 2 4) Frozen (Rate A kVA)");
            missing.put(type, "Peak kVA (Quadrants 1 2 4) Frozen (Rate B kVA)");
            missing.put(type, "Peak kVA (Quadrants 1 2 4) Frozen (Rate C kVA)");
            missing.put(type, "Peak kVA (Quadrants 1 2 4) Frozen (Rate D kVA)");
            missing.put(type, "kVAh (Quadrants 1 2 4)");
            missing.put(type, "kVAh (Quadrants 1 2 4) (Rate A kVAh)");
            missing.put(type, "kVAh (Quadrants 1 2 4) (Rate B kVAh)");
            missing.put(type, "kVAh (Quadrants 1 2 4) (Rate C kVAh)");
            missing.put(type, "kVAh (Quadrants 1 2 4) (Rate D kVAh)");
            missing.put(type, "Delivered kVAr");
            missing.put(type, "Sum kVAr");
            missing.put(type, "kVA (Quadrants 1 2 4)");
            missing.put(type, "Sum kVA");
        });
        List.of(PaoType.RFN520FRXE, PaoType.RFN520FRXED, PaoType.CRL520FRXE, PaoType.CRL520FRXED).forEach(type -> {
            missing.put(type, "Peak kVA Lagging Frozen");
            missing.put(type, "Power Factor Degrees");
        });
        
        missing.put(PaoType.RFN530FAXE, "Peak kVA Lagging Frozen");
        missing.put(PaoType.RFN530FAXE, "Peak kVA (Quadrants 1 2 4)");
        missing.put(PaoType.RFN530FAXE, "Delivered Peak kVAr");
        missing.put(PaoType.RFN530FAXE, "kVAh Lagging");
        missing.put(PaoType.RFN530FAXE, "Delivered kVArh");
        missing.put(PaoType.RFN530FAXE, "Net kVAh");
        missing.put(PaoType.RFN530FAXE, "Net kVArh");
        missing.put(PaoType.RFN530FAXE, "Power Factor Degrees Phase A");
        missing.put(PaoType.RFN530FAXE, "Power Factor Degrees Phase B");
        missing.put(PaoType.RFN530FAXE, "Power Factor Degrees Phase C");
        missing.put(PaoType.RFN530FAXE, "Received kVAh");
        missing.put(PaoType.RFN530FAXE, "Received kVArh");
        missing.put(PaoType.RFN530FAXE, "Sum Peak kVA Frozen");
        missing.put(PaoType.RFN530FAXE, "Sum Peak kVA");
        missing.put(PaoType.RFN530FAXE, "Sum Peak kVAr");
        missing.put(PaoType.RFN530FAXE, "Sum kVAh");
        missing.put(PaoType.RFN530FAXE, "Sum kVArh");
        missing.put(PaoType.RFN530FAXE, "Current Angle Phase A");
        missing.put(PaoType.RFN530FAXE, "Current Angle Phase B");
        missing.put(PaoType.RFN530FAXE, "Current Angle Phase C");
        missing.put(PaoType.RFN530FAXE, "Delivered kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530FAXE, "Delivered kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530FAXE, "Delivered kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530FAXE, "Delivered kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530FAXE, "Received kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530FAXE, "Received kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530FAXE, "Received kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530FAXE, "Received kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530FAXE, "Delivered kVArh (Quadrant 1)");
        missing.put(PaoType.RFN530FAXE, "Delivered kVArh (Quadrant 1) (Rate A kVArh)");
        missing.put(PaoType.RFN530FAXE, "Delivered kVArh (Quadrant 1) (Rate B kVArh)");
        missing.put(PaoType.RFN530FAXE, "Delivered kVArh (Quadrant 1) (Rate C kVArh)");
        missing.put(PaoType.RFN530FAXE, "Delivered kVArh (Quadrant 1) (Rate D kVArh)");
        missing.put(PaoType.RFN530FAXE, "Received kVArh (Quadrant 4)");
        missing.put(PaoType.RFN530FAXE, "Received kVArh (Quadrant 4) (Rate A kVArh)");
        missing.put(PaoType.RFN530FAXE, "Received kVArh (Quadrant 4) (Rate B kVArh)");
        missing.put(PaoType.RFN530FAXE, "Received kVArh (Quadrant 4) (Rate C kVArh)");
        missing.put(PaoType.RFN530FAXE, "Received kVArh (Quadrant 4) (Rate D kVArh)");
        missing.put(PaoType.RFN530FAXE, "Net kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530FAXE, "Net kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530FAXE, "Net kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530FAXE, "Net kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530FAXE, "Sum kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530FAXE, "Sum kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530FAXE, "Sum kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530FAXE, "Sum kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530FAXE, "Delivered kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN530FAXE, "Delivered kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN530FAXE, "Delivered kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN530FAXE, "Delivered kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN530FAXE, "kVAh (Quadrants 2 3 4)");
        missing.put(PaoType.RFN530FAXE, "kVAh (Quadrants 2 3 4) (Rate A kVAh)");
        missing.put(PaoType.RFN530FAXE, "kVAh (Quadrants 2 3 4) (Rate B kVAh)");
        missing.put(PaoType.RFN530FAXE, "kVAh (Quadrants 2 3 4) (Rate C kVAh)");
        missing.put(PaoType.RFN530FAXE, "kVAh (Quadrants 2 3 4) (Rate D kVAh)");
        missing.put(PaoType.RFN530FAXE, "Net kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN530FAXE, "Net kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN530FAXE, "Net kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN530FAXE, "Net kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN530FAXE, "Sum kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN530FAXE, "Sum kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN530FAXE, "Sum kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN530FAXE, "Sum kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN530FAXE, "Delivered Peak kVAr (Rate A kVAr)");
        missing.put(PaoType.RFN530FAXE, "Delivered Peak kVAr (Rate B kVAr)");
        missing.put(PaoType.RFN530FAXE, "Delivered Peak kVAr (Rate C kVAr)");
        missing.put(PaoType.RFN530FAXE, "Delivered Peak kVAr (Rate D kVAr)");
        missing.put(PaoType.RFN530FAXE, "Peak kVA (Quadrants 1 2 4) (Rate A kVA)");
        missing.put(PaoType.RFN530FAXE, "Peak kVA (Quadrants 1 2 4) (Rate B kVA)");
        missing.put(PaoType.RFN530FAXE, "Peak kVA (Quadrants 1 2 4) (Rate C kVA)");
        missing.put(PaoType.RFN530FAXE, "Peak kVA (Quadrants 1 2 4) (Rate D kVA)");
        missing.put(PaoType.RFN530FAXE, "Sum Peak kVAr Frozen");
        missing.put(PaoType.RFN530FAXE, "Delivered Peak kVAr Frozen");
        missing.put(PaoType.RFN530FAXE, "Delivered Peak kVAr Frozen (Rate A kVAr)");
        missing.put(PaoType.RFN530FAXE, "Delivered Peak kVAr Frozen (Rate B kVAr)");
        missing.put(PaoType.RFN530FAXE, "Delivered Peak kVAr Frozen (Rate C kVAr)");
        missing.put(PaoType.RFN530FAXE, "Delivered Peak kVAr Frozen (Rate D kVAr)");
        missing.put(PaoType.RFN530FAXE, "Peak kVA (Quadrants 1 2 4) Frozen");
        missing.put(PaoType.RFN530FAXE, "Peak kVA (Quadrants 1 2 4) Frozen (Rate A kVA)");
        missing.put(PaoType.RFN530FAXE, "Peak kVA (Quadrants 1 2 4) Frozen (Rate B kVA)");
        missing.put(PaoType.RFN530FAXE, "Peak kVA (Quadrants 1 2 4) Frozen (Rate C kVA)");
        missing.put(PaoType.RFN530FAXE, "Peak kVA (Quadrants 1 2 4) Frozen (Rate D kVA)");
        missing.put(PaoType.RFN530FAXE, "kVAh (Quadrants 1 2 4)");
        missing.put(PaoType.RFN530FAXE, "kVAh (Quadrants 1 2 4) (Rate A kVAh)");
        missing.put(PaoType.RFN530FAXE, "kVAh (Quadrants 1 2 4) (Rate B kVAh)");
        missing.put(PaoType.RFN530FAXE, "kVAh (Quadrants 1 2 4) (Rate C kVAh)");
        missing.put(PaoType.RFN530FAXE, "kVAh (Quadrants 1 2 4) (Rate D kVAh)");
        missing.put(PaoType.RFN530FAXE, "Delivered kVAr");
        missing.put(PaoType.RFN530FAXE, "Sum kVAr");
        missing.put(PaoType.RFN530FAXE, "kVA (Quadrants 1 2 4)");
        missing.put(PaoType.RFN530FAXE, "Sum kVA");

        missing.put(PaoType.RFN530FRXE, "Peak kVA Lagging Frozen");
        missing.put(PaoType.RFN530FRXE, "Power Factor Degrees Phase A");
        missing.put(PaoType.RFN530FRXE, "Power Factor Degrees Phase B");
        missing.put(PaoType.RFN530FRXE, "Power Factor Degrees Phase C");
        missing.put(PaoType.RFN530FRXE, "Current Angle Phase A");
        missing.put(PaoType.RFN530FRXE, "Current Angle Phase B");
        missing.put(PaoType.RFN530FRXE, "Current Angle Phase C");

        missing.put(PaoType.RFN530S4EAX, "Avg Voltage Phase A");
        missing.put(PaoType.RFN530S4EAX, "Avg Voltage Phase B");
        missing.put(PaoType.RFN530S4EAX, "Avg Voltage Phase C");
        missing.put(PaoType.RFN530S4EAX, "Delivered Peak kVA (Rate A kVA)");
        missing.put(PaoType.RFN530S4EAX, "Delivered Peak kVA (Rate B kVA)");
        missing.put(PaoType.RFN530S4EAX, "Delivered Peak kVA (Rate C kVA)");
        missing.put(PaoType.RFN530S4EAX, "Delivered Peak kVA (Rate D kVA)");
        missing.put(PaoType.RFN530S4EAX, "Delivered Peak kVAr (Rate A kVAr)");
        missing.put(PaoType.RFN530S4EAX, "Delivered Peak kVAr (Rate B kVAr)");
        missing.put(PaoType.RFN530S4EAX, "Delivered Peak kVAr (Rate C kVAr)");
        missing.put(PaoType.RFN530S4EAX, "Delivered Peak kVAr (Rate D kVAr)");
        missing.put(PaoType.RFN530S4EAX, "Delivered Peak kVAr");
        missing.put(PaoType.RFN530S4EAX, "Delivered kVA");
        missing.put(PaoType.RFN530S4EAX, "Delivered kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN530S4EAX, "Delivered kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN530S4EAX, "Delivered kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN530S4EAX, "Delivered kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN530S4EAX, "Delivered kVAh");
        missing.put(PaoType.RFN530S4EAX, "Delivered kVAr");
        missing.put(PaoType.RFN530S4EAX, "Delivered kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530S4EAX, "Delivered kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530S4EAX, "Delivered kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530S4EAX, "Delivered kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530S4EAX, "Delivered kVArh");
        missing.put(PaoType.RFN530S4EAX, "Delivered kW");
        missing.put(PaoType.RFN530S4EAX, "Max Voltage Phase A");
        missing.put(PaoType.RFN530S4EAX, "Max Voltage Phase B");
        missing.put(PaoType.RFN530S4EAX, "Max Voltage Phase C");
        missing.put(PaoType.RFN530S4EAX, "Min Voltage Phase A");
        missing.put(PaoType.RFN530S4EAX, "Min Voltage Phase B");
        missing.put(PaoType.RFN530S4EAX, "Min Voltage Phase C");
        missing.put(PaoType.RFN530S4EAX, "Net Peak kW (Rate A kW)");
        missing.put(PaoType.RFN530S4EAX, "Net Peak kW (Rate B kW)");
        missing.put(PaoType.RFN530S4EAX, "Net Peak kW (Rate C kW)");
        missing.put(PaoType.RFN530S4EAX, "Net Peak kW (Rate D kW)");
        missing.put(PaoType.RFN530S4EAX, "Net Peak kW");
        missing.put(PaoType.RFN530S4EAX, "Net kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530S4EAX, "Net kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530S4EAX, "Net kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530S4EAX, "Net kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530S4EAX, "Net kVArh");
        missing.put(PaoType.RFN530S4EAX, "Peak Demand Daily");
        missing.put(PaoType.RFN530S4EAX, "Peak kVA");
        missing.put(PaoType.RFN530S4EAX, "Peak kW Frozen (Rate A kW)");
        missing.put(PaoType.RFN530S4EAX, "Peak kW Frozen (Rate B kW)");
        missing.put(PaoType.RFN530S4EAX, "Peak kW Frozen (Rate C kW)");
        missing.put(PaoType.RFN530S4EAX, "Peak kW Frozen (Rate D kW)");
        missing.put(PaoType.RFN530S4EAX, "Peak kW Frozen");
        missing.put(PaoType.RFN530S4EAX, "Power Factor Degrees Phase A");
        missing.put(PaoType.RFN530S4EAX, "Power Factor Degrees Phase B");
        missing.put(PaoType.RFN530S4EAX, "Power Factor Degrees Phase C");
        missing.put(PaoType.RFN530S4EAX, "Received Peak kVA (Rate A kVA)");
        missing.put(PaoType.RFN530S4EAX, "Received Peak kVA (Rate B kVA)");
        missing.put(PaoType.RFN530S4EAX, "Received Peak kVA (Rate C kVA)");
        missing.put(PaoType.RFN530S4EAX, "Received Peak kVA (Rate D kVA)");
        missing.put(PaoType.RFN530S4EAX, "Received Peak kVA");
        missing.put(PaoType.RFN530S4EAX, "Received Peak kVAr (Rate A kVAr)");
        missing.put(PaoType.RFN530S4EAX, "Received Peak kVAr (Rate B kVAr)");
        missing.put(PaoType.RFN530S4EAX, "Received Peak kVAr (Rate C kVAr)");
        missing.put(PaoType.RFN530S4EAX, "Received Peak kVAr (Rate D kVAr)");
        missing.put(PaoType.RFN530S4EAX, "Received Peak kVAr");
        missing.put(PaoType.RFN530S4EAX, "Received Peak kW (Rate A kW)");
        missing.put(PaoType.RFN530S4EAX, "Received Peak kW (Rate B kW)");
        missing.put(PaoType.RFN530S4EAX, "Received Peak kW (Rate C kW)");
        missing.put(PaoType.RFN530S4EAX, "Received Peak kW (Rate D kW)");
        missing.put(PaoType.RFN530S4EAX, "Received Peak kW");
        missing.put(PaoType.RFN530S4EAX, "Received kVA");
        missing.put(PaoType.RFN530S4EAX, "Received kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN530S4EAX, "Received kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN530S4EAX, "Received kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN530S4EAX, "Received kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN530S4EAX, "Received kVAh");
        missing.put(PaoType.RFN530S4EAX, "Received kVAr");
        missing.put(PaoType.RFN530S4EAX, "Received kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530S4EAX, "Received kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530S4EAX, "Received kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530S4EAX, "Received kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530S4EAX, "Received kVArh");
        missing.put(PaoType.RFN530S4EAX, "Received kW");
        missing.put(PaoType.RFN530S4EAX, "Received kWh (Rate A kWh)");
        missing.put(PaoType.RFN530S4EAX, "Received kWh (Rate B kWh)");
        missing.put(PaoType.RFN530S4EAX, "Received kWh (Rate C kWh)");
        missing.put(PaoType.RFN530S4EAX, "Received kWh (Rate D kWh)");
        missing.put(PaoType.RFN530S4EAX, "Sum Peak kVA (Rate A kVA)");
        missing.put(PaoType.RFN530S4EAX, "Sum Peak kVA (Rate B kVA)");
        missing.put(PaoType.RFN530S4EAX, "Sum Peak kVA (Rate C kVA)");
        missing.put(PaoType.RFN530S4EAX, "Sum Peak kVA (Rate D kVA)");
        missing.put(PaoType.RFN530S4EAX, "Sum Peak kVA");
        missing.put(PaoType.RFN530S4EAX, "Sum Peak kVAr (Rate A kVAr)");
        missing.put(PaoType.RFN530S4EAX, "Sum Peak kVAr (Rate B kVAr)");
        missing.put(PaoType.RFN530S4EAX, "Sum Peak kVAr (Rate C kVAr)");
        missing.put(PaoType.RFN530S4EAX, "Sum Peak kVAr (Rate D kVAr)");
        missing.put(PaoType.RFN530S4EAX, "Sum Peak kVAr");
        missing.put(PaoType.RFN530S4EAX, "Sum kVA");
        missing.put(PaoType.RFN530S4EAX, "Sum kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN530S4EAX, "Sum kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN530S4EAX, "Sum kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN530S4EAX, "Sum kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN530S4EAX, "Sum kVAh");
        missing.put(PaoType.RFN530S4EAX, "Sum kVAr");
        missing.put(PaoType.RFN530S4EAX, "Sum kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530S4EAX, "Sum kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530S4EAX, "Sum kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530S4EAX, "Sum kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530S4EAX, "Sum kVArh");
        missing.put(PaoType.RFN530S4EAX, "Sum kW");
        missing.put(PaoType.RFN530S4EAX, "kVAh Leading (Q1 + Q3)");
        missing.put(PaoType.RFN530S4EAX, "kVArh Leading (Q1 + Q3)");
        missing.put(PaoType.RFN530S4EAX, "Average Power Factor");
        missing.put(PaoType.RFN530S4EAX, "Average Power Factor Frozen");
        missing.put(PaoType.RFN530S4EAX, "Minimum Power Factor");
        missing.put(PaoType.RFN530S4EAX, "Minimum Power Factor Frozen");
        
        missing.put(PaoType.RFN530S4EAXR, "Avg Voltage Phase A");
        missing.put(PaoType.RFN530S4EAXR, "Avg Voltage Phase B");
        missing.put(PaoType.RFN530S4EAXR, "Avg Voltage Phase C");
        missing.put(PaoType.RFN530S4EAXR, "Delivered Peak kVA (Rate A kVA)");
        missing.put(PaoType.RFN530S4EAXR, "Delivered Peak kVA (Rate B kVA)");
        missing.put(PaoType.RFN530S4EAXR, "Delivered Peak kVA (Rate C kVA)");
        missing.put(PaoType.RFN530S4EAXR, "Delivered Peak kVA (Rate D kVA)");
        missing.put(PaoType.RFN530S4EAXR, "Delivered Peak kVAr (Rate A kVAr)");
        missing.put(PaoType.RFN530S4EAXR, "Delivered Peak kVAr (Rate B kVAr)");
        missing.put(PaoType.RFN530S4EAXR, "Delivered Peak kVAr (Rate C kVAr)");
        missing.put(PaoType.RFN530S4EAXR, "Delivered Peak kVAr (Rate D kVAr)");
        missing.put(PaoType.RFN530S4EAXR, "Delivered Peak kVAr");
        missing.put(PaoType.RFN530S4EAXR, "Delivered kVA");
        missing.put(PaoType.RFN530S4EAXR, "Delivered kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN530S4EAXR, "Delivered kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN530S4EAXR, "Delivered kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN530S4EAXR, "Delivered kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN530S4EAXR, "Delivered kVAh");
        missing.put(PaoType.RFN530S4EAXR, "Delivered kVAr");
        missing.put(PaoType.RFN530S4EAXR, "Delivered kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530S4EAXR, "Delivered kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530S4EAXR, "Delivered kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530S4EAXR, "Delivered kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530S4EAXR, "Delivered kVArh");
        missing.put(PaoType.RFN530S4EAXR, "Delivered kW");
        missing.put(PaoType.RFN530S4EAXR, "Max Voltage Phase A");
        missing.put(PaoType.RFN530S4EAXR, "Max Voltage Phase B");
        missing.put(PaoType.RFN530S4EAXR, "Max Voltage Phase C");
        missing.put(PaoType.RFN530S4EAXR, "Min Voltage Phase A");
        missing.put(PaoType.RFN530S4EAXR, "Min Voltage Phase B");
        missing.put(PaoType.RFN530S4EAXR, "Min Voltage Phase C");
        missing.put(PaoType.RFN530S4EAXR, "Net Peak kW (Rate A kW)");
        missing.put(PaoType.RFN530S4EAXR, "Net Peak kW (Rate B kW)");
        missing.put(PaoType.RFN530S4EAXR, "Net Peak kW (Rate C kW)");
        missing.put(PaoType.RFN530S4EAXR, "Net Peak kW (Rate D kW)");
        missing.put(PaoType.RFN530S4EAXR, "Net Peak kW");
        missing.put(PaoType.RFN530S4EAXR, "Net kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530S4EAXR, "Net kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530S4EAXR, "Net kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530S4EAXR, "Net kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530S4EAXR, "Net kVArh");
        missing.put(PaoType.RFN530S4EAXR, "Peak Demand Daily");
        missing.put(PaoType.RFN530S4EAXR, "Peak kVA");
        missing.put(PaoType.RFN530S4EAXR, "Peak kW Frozen (Rate A kW)");
        missing.put(PaoType.RFN530S4EAXR, "Peak kW Frozen (Rate B kW)");
        missing.put(PaoType.RFN530S4EAXR, "Peak kW Frozen (Rate C kW)");
        missing.put(PaoType.RFN530S4EAXR, "Peak kW Frozen (Rate D kW)");
        missing.put(PaoType.RFN530S4EAXR, "Peak kW Frozen");
        missing.put(PaoType.RFN530S4EAXR, "Power Factor Degrees Phase A");
        missing.put(PaoType.RFN530S4EAXR, "Power Factor Degrees Phase B");
        missing.put(PaoType.RFN530S4EAXR, "Power Factor Degrees Phase C");
        missing.put(PaoType.RFN530S4EAXR, "Received Peak kVA (Rate A kVA)");
        missing.put(PaoType.RFN530S4EAXR, "Received Peak kVA (Rate B kVA)");
        missing.put(PaoType.RFN530S4EAXR, "Received Peak kVA (Rate C kVA)");
        missing.put(PaoType.RFN530S4EAXR, "Received Peak kVA (Rate D kVA)");
        missing.put(PaoType.RFN530S4EAXR, "Received Peak kVA");
        missing.put(PaoType.RFN530S4EAXR, "Received Peak kVAr (Rate A kVAr)");
        missing.put(PaoType.RFN530S4EAXR, "Received Peak kVAr (Rate B kVAr)");
        missing.put(PaoType.RFN530S4EAXR, "Received Peak kVAr (Rate C kVAr)");
        missing.put(PaoType.RFN530S4EAXR, "Received Peak kVAr (Rate D kVAr)");
        missing.put(PaoType.RFN530S4EAXR, "Received Peak kVAr");
        missing.put(PaoType.RFN530S4EAXR, "Received Peak kW (Rate A kW)");
        missing.put(PaoType.RFN530S4EAXR, "Received Peak kW (Rate B kW)");
        missing.put(PaoType.RFN530S4EAXR, "Received Peak kW (Rate C kW)");
        missing.put(PaoType.RFN530S4EAXR, "Received Peak kW (Rate D kW)");
        missing.put(PaoType.RFN530S4EAXR, "Received Peak kW");
        missing.put(PaoType.RFN530S4EAXR, "Received kVA");
        missing.put(PaoType.RFN530S4EAXR, "Received kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN530S4EAXR, "Received kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN530S4EAXR, "Received kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN530S4EAXR, "Received kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN530S4EAXR, "Received kVAh");
        missing.put(PaoType.RFN530S4EAXR, "Received kVAr");
        missing.put(PaoType.RFN530S4EAXR, "Received kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530S4EAXR, "Received kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530S4EAXR, "Received kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530S4EAXR, "Received kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530S4EAXR, "Received kVArh");
        missing.put(PaoType.RFN530S4EAXR, "Received kW");
        missing.put(PaoType.RFN530S4EAXR, "Sum Peak kVA (Rate A kVA)");
        missing.put(PaoType.RFN530S4EAXR, "Sum Peak kVA (Rate B kVA)");
        missing.put(PaoType.RFN530S4EAXR, "Sum Peak kVA (Rate C kVA)");
        missing.put(PaoType.RFN530S4EAXR, "Sum Peak kVA (Rate D kVA)");
        missing.put(PaoType.RFN530S4EAXR, "Sum Peak kVA");
        missing.put(PaoType.RFN530S4EAXR, "Sum Peak kVAr (Rate A kVAr)");
        missing.put(PaoType.RFN530S4EAXR, "Sum Peak kVAr (Rate B kVAr)");
        missing.put(PaoType.RFN530S4EAXR, "Sum Peak kVAr (Rate C kVAr)");
        missing.put(PaoType.RFN530S4EAXR, "Sum Peak kVAr (Rate D kVAr)");
        missing.put(PaoType.RFN530S4EAXR, "Sum Peak kVAr");
        missing.put(PaoType.RFN530S4EAXR, "Sum kVA");
        missing.put(PaoType.RFN530S4EAXR, "Sum kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN530S4EAXR, "Sum kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN530S4EAXR, "Sum kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN530S4EAXR, "Sum kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN530S4EAXR, "Sum kVAh");
        missing.put(PaoType.RFN530S4EAXR, "Sum kVAr");
        missing.put(PaoType.RFN530S4EAXR, "Sum kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530S4EAXR, "Sum kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530S4EAXR, "Sum kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530S4EAXR, "Sum kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530S4EAXR, "Sum kVArh");
        missing.put(PaoType.RFN530S4EAXR, "Sum kW");
        missing.put(PaoType.RFN530S4EAXR, "kVAh Leading (Q1 + Q3)");
        missing.put(PaoType.RFN530S4EAXR, "kVArh Leading (Q1 + Q3)");
        missing.put(PaoType.RFN530S4EAXR, "Average Power Factor");
        missing.put(PaoType.RFN530S4EAXR, "Average Power Factor Frozen");
        missing.put(PaoType.RFN530S4EAXR, "Minimum Power Factor");
        missing.put(PaoType.RFN530S4EAXR, "Minimum Power Factor Frozen");
        missing.put(PaoType.RFN530S4EAXR, "Received kWh (Rate A kWh)");
        missing.put(PaoType.RFN530S4EAXR, "Received kWh (Rate B kWh)");
        missing.put(PaoType.RFN530S4EAXR, "Received kWh (Rate C kWh)");
        missing.put(PaoType.RFN530S4EAXR, "Received kWh (Rate D kWh)");

        missing.put(PaoType.RFN530S4ERX, "Avg Voltage Phase A");
        missing.put(PaoType.RFN530S4ERX, "Avg Voltage Phase B");
        missing.put(PaoType.RFN530S4ERX, "Avg Voltage Phase C");
        missing.put(PaoType.RFN530S4ERX, "Coincident Power Factor");
        missing.put(PaoType.RFN530S4ERX, "Delivered Peak kVA (Rate A kVA)");
        missing.put(PaoType.RFN530S4ERX, "Delivered Peak kVA (Rate B kVA)");
        missing.put(PaoType.RFN530S4ERX, "Delivered Peak kVA (Rate C kVA)");
        missing.put(PaoType.RFN530S4ERX, "Delivered Peak kVA (Rate D kVA)");
        missing.put(PaoType.RFN530S4ERX, "Delivered Peak kVAr (Rate A kVAr)");
        missing.put(PaoType.RFN530S4ERX, "Delivered Peak kVAr (Rate B kVAr)");
        missing.put(PaoType.RFN530S4ERX, "Delivered Peak kVAr (Rate C kVAr)");
        missing.put(PaoType.RFN530S4ERX, "Delivered Peak kVAr (Rate D kVAr)");
        missing.put(PaoType.RFN530S4ERX, "Delivered Peak kVAr");
        missing.put(PaoType.RFN530S4ERX, "Delivered kVA");
        missing.put(PaoType.RFN530S4ERX, "Delivered kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN530S4ERX, "Delivered kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN530S4ERX, "Delivered kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN530S4ERX, "Delivered kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN530S4ERX, "Delivered kVAh");
        missing.put(PaoType.RFN530S4ERX, "Delivered kVAr");
        missing.put(PaoType.RFN530S4ERX, "Delivered kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530S4ERX, "Delivered kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530S4ERX, "Delivered kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530S4ERX, "Delivered kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530S4ERX, "Delivered kVArh");
        missing.put(PaoType.RFN530S4ERX, "Delivered kW");
        missing.put(PaoType.RFN530S4ERX, "Max Voltage Phase A");
        missing.put(PaoType.RFN530S4ERX, "Max Voltage Phase B");
        missing.put(PaoType.RFN530S4ERX, "Max Voltage Phase C");
        missing.put(PaoType.RFN530S4ERX, "Min Voltage Phase A");
        missing.put(PaoType.RFN530S4ERX, "Min Voltage Phase B");
        missing.put(PaoType.RFN530S4ERX, "Min Voltage Phase C");
        missing.put(PaoType.RFN530S4ERX, "Net Peak kW (Rate A kW)");
        missing.put(PaoType.RFN530S4ERX, "Net Peak kW (Rate B kW)");
        missing.put(PaoType.RFN530S4ERX, "Net Peak kW (Rate C kW)");
        missing.put(PaoType.RFN530S4ERX, "Net Peak kW (Rate D kW)");
        missing.put(PaoType.RFN530S4ERX, "Net Peak kW");
        missing.put(PaoType.RFN530S4ERX, "Net kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530S4ERX, "Net kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530S4ERX, "Net kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530S4ERX, "Net kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530S4ERX, "Peak Demand Daily");
        missing.put(PaoType.RFN530S4ERX, "Peak kVA");
        missing.put(PaoType.RFN530S4ERX, "Peak kW Frozen (Rate A kW)");
        missing.put(PaoType.RFN530S4ERX, "Peak kW Frozen (Rate B kW)");
        missing.put(PaoType.RFN530S4ERX, "Peak kW Frozen (Rate C kW)");
        missing.put(PaoType.RFN530S4ERX, "Peak kW Frozen (Rate D kW)");
        missing.put(PaoType.RFN530S4ERX, "Peak kW Frozen");
        missing.put(PaoType.RFN530S4ERX, "Power Factor Degrees Phase A");
        missing.put(PaoType.RFN530S4ERX, "Power Factor Degrees Phase B");
        missing.put(PaoType.RFN530S4ERX, "Power Factor Degrees Phase C");
        missing.put(PaoType.RFN530S4ERX, "Received Peak kVA (Rate A kVA)");
        missing.put(PaoType.RFN530S4ERX, "Received Peak kVA (Rate B kVA)");
        missing.put(PaoType.RFN530S4ERX, "Received Peak kVA (Rate C kVA)");
        missing.put(PaoType.RFN530S4ERX, "Received Peak kVA (Rate D kVA)");
        missing.put(PaoType.RFN530S4ERX, "Received Peak kVA");
        missing.put(PaoType.RFN530S4ERX, "Received Peak kVAr (Rate A kVAr)");
        missing.put(PaoType.RFN530S4ERX, "Received Peak kVAr (Rate B kVAr)");
        missing.put(PaoType.RFN530S4ERX, "Received Peak kVAr (Rate C kVAr)");
        missing.put(PaoType.RFN530S4ERX, "Received Peak kVAr (Rate D kVAr)");
        missing.put(PaoType.RFN530S4ERX, "Received Peak kVAr");
        missing.put(PaoType.RFN530S4ERX, "Received Peak kW (Rate A kW)");
        missing.put(PaoType.RFN530S4ERX, "Received Peak kW (Rate B kW)");
        missing.put(PaoType.RFN530S4ERX, "Received Peak kW (Rate C kW)");
        missing.put(PaoType.RFN530S4ERX, "Received Peak kW (Rate D kW)");
        missing.put(PaoType.RFN530S4ERX, "Received Peak kW");
        missing.put(PaoType.RFN530S4ERX, "Received kVA");
        missing.put(PaoType.RFN530S4ERX, "Received kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN530S4ERX, "Received kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN530S4ERX, "Received kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN530S4ERX, "Received kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN530S4ERX, "Received kVAh");
        missing.put(PaoType.RFN530S4ERX, "Received kVAr");
        missing.put(PaoType.RFN530S4ERX, "Received kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530S4ERX, "Received kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530S4ERX, "Received kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530S4ERX, "Received kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530S4ERX, "Received kVArh");
        missing.put(PaoType.RFN530S4ERX, "Received kW");
        missing.put(PaoType.RFN530S4ERX, "Received kWh (Rate A kWh)");
        missing.put(PaoType.RFN530S4ERX, "Received kWh (Rate B kWh)");
        missing.put(PaoType.RFN530S4ERX, "Received kWh (Rate C kWh)");
        missing.put(PaoType.RFN530S4ERX, "Received kWh (Rate D kWh)");
        missing.put(PaoType.RFN530S4ERX, "Sum Peak kVA (Rate A kVA)");
        missing.put(PaoType.RFN530S4ERX, "Sum Peak kVA (Rate B kVA)");
        missing.put(PaoType.RFN530S4ERX, "Sum Peak kVA (Rate C kVA)");
        missing.put(PaoType.RFN530S4ERX, "Sum Peak kVA (Rate D kVA)");
        missing.put(PaoType.RFN530S4ERX, "Sum Peak kVAr (Rate A kVAr)");
        missing.put(PaoType.RFN530S4ERX, "Sum Peak kVAr (Rate B kVAr)");
        missing.put(PaoType.RFN530S4ERX, "Sum Peak kVAr (Rate C kVAr)");
        missing.put(PaoType.RFN530S4ERX, "Sum Peak kVAr (Rate D kVAr)");
        missing.put(PaoType.RFN530S4ERX, "Sum kVA");
        missing.put(PaoType.RFN530S4ERX, "Sum kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN530S4ERX, "Sum kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN530S4ERX, "Sum kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN530S4ERX, "Sum kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN530S4ERX, "Sum kVAr");
        missing.put(PaoType.RFN530S4ERX, "Sum kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530S4ERX, "Sum kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530S4ERX, "Sum kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530S4ERX, "Sum kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530S4ERX, "Sum kW");
        missing.put(PaoType.RFN530S4ERX, "kVAh Leading (Q1 + Q3)");
        missing.put(PaoType.RFN530S4ERX, "kVArh Leading (Q1 + Q3)");
        
        missing.put(PaoType.RFN530S4ERXR, "Avg Voltage Phase A");
        missing.put(PaoType.RFN530S4ERXR, "Avg Voltage Phase B");
        missing.put(PaoType.RFN530S4ERXR, "Avg Voltage Phase C");
        missing.put(PaoType.RFN530S4ERXR, "Delivered Peak kVA (Rate A kVA)");
        missing.put(PaoType.RFN530S4ERXR, "Delivered Peak kVA (Rate B kVA)");
        missing.put(PaoType.RFN530S4ERXR, "Delivered Peak kVA (Rate C kVA)");
        missing.put(PaoType.RFN530S4ERXR, "Delivered Peak kVA (Rate D kVA)");
        missing.put(PaoType.RFN530S4ERXR, "Delivered Peak kVAr (Rate A kVAr)");
        missing.put(PaoType.RFN530S4ERXR, "Delivered Peak kVAr (Rate B kVAr)");
        missing.put(PaoType.RFN530S4ERXR, "Delivered Peak kVAr (Rate C kVAr)");
        missing.put(PaoType.RFN530S4ERXR, "Delivered Peak kVAr (Rate D kVAr)");
        missing.put(PaoType.RFN530S4ERXR, "Delivered Peak kVAr");
        missing.put(PaoType.RFN530S4ERXR, "Delivered kVA");
        missing.put(PaoType.RFN530S4ERXR, "Delivered kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN530S4ERXR, "Delivered kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN530S4ERXR, "Delivered kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN530S4ERXR, "Delivered kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN530S4ERXR, "Delivered kVAh");
        missing.put(PaoType.RFN530S4ERXR, "Delivered kVAr");
        missing.put(PaoType.RFN530S4ERXR, "Delivered kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530S4ERXR, "Delivered kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530S4ERXR, "Delivered kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530S4ERXR, "Delivered kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530S4ERXR, "Delivered kVArh");
        missing.put(PaoType.RFN530S4ERXR, "Delivered kW");
        missing.put(PaoType.RFN530S4ERXR, "Max Voltage Phase A");
        missing.put(PaoType.RFN530S4ERXR, "Max Voltage Phase B");
        missing.put(PaoType.RFN530S4ERXR, "Max Voltage Phase C");
        missing.put(PaoType.RFN530S4ERXR, "Min Voltage Phase A");
        missing.put(PaoType.RFN530S4ERXR, "Min Voltage Phase B");
        missing.put(PaoType.RFN530S4ERXR, "Min Voltage Phase C");
        missing.put(PaoType.RFN530S4ERXR, "Net Peak kW (Rate A kW)");
        missing.put(PaoType.RFN530S4ERXR, "Net Peak kW (Rate B kW)");
        missing.put(PaoType.RFN530S4ERXR, "Net Peak kW (Rate C kW)");
        missing.put(PaoType.RFN530S4ERXR, "Net Peak kW (Rate D kW)");
        missing.put(PaoType.RFN530S4ERXR, "Net Peak kW");
        missing.put(PaoType.RFN530S4ERXR, "Peak Demand Daily");
        missing.put(PaoType.RFN530S4ERXR, "Peak kVA");
        missing.put(PaoType.RFN530S4ERXR, "Peak kW Frozen (Rate A kW)");
        missing.put(PaoType.RFN530S4ERXR, "Peak kW Frozen (Rate B kW)");
        missing.put(PaoType.RFN530S4ERXR, "Peak kW Frozen (Rate C kW)");
        missing.put(PaoType.RFN530S4ERXR, "Peak kW Frozen (Rate D kW)");
        missing.put(PaoType.RFN530S4ERXR, "Peak kW Frozen");
        missing.put(PaoType.RFN530S4ERXR, "Power Factor Degrees Phase A");
        missing.put(PaoType.RFN530S4ERXR, "Power Factor Degrees Phase B");
        missing.put(PaoType.RFN530S4ERXR, "Power Factor Degrees Phase C");
        missing.put(PaoType.RFN530S4ERXR, "Received Peak kVA (Rate A kVA)");
        missing.put(PaoType.RFN530S4ERXR, "Received Peak kVA (Rate B kVA)");
        missing.put(PaoType.RFN530S4ERXR, "Received Peak kVA (Rate C kVA)");
        missing.put(PaoType.RFN530S4ERXR, "Received Peak kVA (Rate D kVA)");
        missing.put(PaoType.RFN530S4ERXR, "Received Peak kVA");
        missing.put(PaoType.RFN530S4ERXR, "Received Peak kVAr (Rate A kVAr)");
        missing.put(PaoType.RFN530S4ERXR, "Received Peak kVAr (Rate B kVAr)");
        missing.put(PaoType.RFN530S4ERXR, "Received Peak kVAr (Rate C kVAr)");
        missing.put(PaoType.RFN530S4ERXR, "Received Peak kVAr (Rate D kVAr)");
        missing.put(PaoType.RFN530S4ERXR, "Received Peak kVAr");
        missing.put(PaoType.RFN530S4ERXR, "Received Peak kW (Rate A kW)");
        missing.put(PaoType.RFN530S4ERXR, "Received Peak kW (Rate B kW)");
        missing.put(PaoType.RFN530S4ERXR, "Received Peak kW (Rate C kW)");
        missing.put(PaoType.RFN530S4ERXR, "Received Peak kW (Rate D kW)");
        missing.put(PaoType.RFN530S4ERXR, "Received Peak kW");
        missing.put(PaoType.RFN530S4ERXR, "Received kVA");
        missing.put(PaoType.RFN530S4ERXR, "Received kVAh (Rate A kVAh)");
        missing.put(PaoType.RFN530S4ERXR, "Received kVAh (Rate B kVAh)");
        missing.put(PaoType.RFN530S4ERXR, "Received kVAh (Rate C kVAh)");
        missing.put(PaoType.RFN530S4ERXR, "Received kVAh (Rate D kVAh)");
        missing.put(PaoType.RFN530S4ERXR, "Received kVAh");
        missing.put(PaoType.RFN530S4ERXR, "Received kVAr");
        missing.put(PaoType.RFN530S4ERXR, "Received kVArh (Rate A kVArh)");
        missing.put(PaoType.RFN530S4ERXR, "Received kVArh (Rate B kVArh)");
        missing.put(PaoType.RFN530S4ERXR, "Received kVArh (Rate C kVArh)");
        missing.put(PaoType.RFN530S4ERXR, "Received kVArh (Rate D kVArh)");
        missing.put(PaoType.RFN530S4ERXR, "Received kVArh");
        missing.put(PaoType.RFN530S4ERXR, "Received kW");
        missing.put(PaoType.RFN530S4ERXR, "Sum kVA");
        missing.put(PaoType.RFN530S4ERXR, "Sum kVAr");
        missing.put(PaoType.RFN530S4ERXR, "Sum kW");
        missing.put(PaoType.RFN530S4ERXR, "kVAh Leading (Q1 + Q3)");
        missing.put(PaoType.RFN530S4ERXR, "kVArh Leading (Q1 + Q3)");
        missing.put(PaoType.RFN530S4ERXR, "Received kWh (Rate A kWh)");
        missing.put(PaoType.RFN530S4ERXR, "Received kWh (Rate B kWh)");
        missing.put(PaoType.RFN530S4ERXR, "Received kWh (Rate C kWh)");
        missing.put(PaoType.RFN530S4ERXR, "Received kWh (Rate D kWh)");
        
        List.of(PaoType.RFN530S4X, PaoType.CRL530S4X).forEach(type -> {
            missing.put(type, "Average Power Factor");
            missing.put(type, "Net kVA");
            missing.put(type, "Net kVAr");
            missing.put(type, "Net kVArh (Rate A kVArh)");
            missing.put(type, "Net kVArh (Rate B kVArh)");
            missing.put(type, "Net kVArh (Rate C kVArh)");
            missing.put(type, "Net kVArh (Rate D kVArh)");
            missing.put(type, "Net kVArh");
            missing.put(type, "Net kW");
            missing.put(type, "Net kWh (Rate A kWh)");
            missing.put(type, "Net kWh (Rate B kWh)");
            missing.put(type, "Net kWh (Rate C kWh)");
            missing.put(type, "Net kWh (Rate D kWh)");
            missing.put(type, "Peak Demand Daily");
            missing.put(type, "Power Factor Degrees Phase A");
            missing.put(type, "Power Factor Degrees Phase B");
            missing.put(type, "Power Factor Degrees Phase C");
            missing.put(type, "kVA (Quadrants 1 2)");
            missing.put(type, "kVA (Quadrants 1 3)");
            missing.put(type, "kVA (Quadrants 2 4)");
            missing.put(type, "kVA (Quadrants 3 4)");
            missing.put(type, "kVAh Leading (Q1 + Q3)");
            missing.put(type, "kVAr (Quadrants 1 3)");
            missing.put(type, "kVAr (Quadrants 1 4)");
            missing.put(type, "kVAr (Quadrants 2 3)");
            missing.put(type, "kVAr (Quadrants 2 4)");
            missing.put(type, "kVArh Leading (Q1 + Q3)");
            missing.put(type, "Average Power Factor Frozen");
        });
        
        return missing;
    }

    // Tell SonarLint to ignore the duplicated strings
    @SuppressWarnings("squid:S1192")
    private static SetMultimap<PaoType, String> getPointsKnownMissingFromRfnPointMapping() {
        return ImmutableSetMultimap.<PaoType, String>builder()
                .putAll(PaoType.RFN410CL,
                        "Peak Demand Daily")
                .putAll(PaoType.RFN410FD,
                        "Peak Demand Daily")
                .putAll(PaoType.RFN410FL,
                        "Peak Demand Daily")
                .putAll(PaoType.RFN410FX,
                        "Peak Demand Daily")
                .putAll(PaoType.RFN420CD,
                        "Peak Demand Daily")
                .putAll(PaoType.WRL420CD,
                        "Peak Demand Daily",
                        "RSSI"  // This is not a channel, but special handle by RfnNodeWiFiCommArchiveRequestListener  
                        )
                .putAll(PaoType.RFN420CL,
                        "Peak Demand Daily")
                .putAll(PaoType.WRL420CL,
                        "Peak Demand Daily",
                        "RSSI"  // This is not a channel, but special handle by RfnNodeWiFiCommArchiveRequestListener
                        )
                .putAll(PaoType.RFN420FD,
                        "Peak Demand Daily")
                .putAll(PaoType.RFN420FL,
                        "Peak Demand Daily")
                .putAll(PaoType.RFN420FRD,
                        "Peak Demand Daily")
                .putAll(PaoType.RFN420FRX,
                        "Peak Demand Daily")
                .putAll(PaoType.RFN420FX,
                        "Peak Demand Daily")
                .putAll(PaoType.RFN430A3K,
                        "Average Delivered Power Factor",
                        "Average Received Power Factor",
                        "Coincident Peak kW",
                        "Coincident Power Factor",
                        "Coincident kVA",
                        "Net kWh (Rate A kWh)",
                        "Net kWh (Rate B kWh)",
                        "Net kWh (Rate C kWh)",
                        "Net kWh (Rate D kWh)",
                        "Rate A Coincident Peak kW",
                        "Rate A Coincident kVA",
                        "Rate B Coincident Peak kW",
                        "Rate B Coincident kVA",
                        "Rate C Coincident Peak kW",
                        "Rate C Coincident kVA",
                        "Rate D Coincident Peak kW",
                        "Rate D Coincident kVA")
                .putAll(PaoType.RFN430A3R,
                        "Average Delivered Power Factor",
                        "Average Received Power Factor",
                        "Coincident Peak kW",
                        "Coincident Power Factor",
                        "Coincident kVA",
                        "Net kWh (Rate A kWh)",
                        "Net kWh (Rate B kWh)",
                        "Net kWh (Rate C kWh)",
                        "Net kWh (Rate D kWh)",
                        "Rate A Coincident Peak kW",
                        "Rate A Coincident kVA",
                        "Rate B Coincident Peak kW",
                        "Rate B Coincident kVA",
                        "Rate C Coincident Peak kW",
                        "Rate C Coincident kVA",
                        "Rate D Coincident Peak kW",
                        "Rate D Coincident kVA")
                .putAll(PaoType.RFN430A3T,
                        "Net kWh (Rate A kWh)",
                        "Net kWh (Rate B kWh)",
                        "Net kWh (Rate C kWh)",
                        "Net kWh (Rate D kWh)")
                .putAll(PaoType.RFN430KV,
                        "Coincident Peak kW",
                        "Coincident Power Factor",
                        "Coincident kVA",
                        "Rate A Coincident Peak kW",
                        "Rate A Coincident kVA",
                        "Rate B Coincident Peak kW",
                        "Rate B Coincident kVA",
                        "Rate C Coincident Peak kW",
                        "Rate C Coincident kVA",
                        "Rate D Coincident Peak kW",
                        "Rate D Coincident kVA")
                .putAll(PaoType.RFN430SL1,
                        "Demand at Peak kVa Coincidental",
                        "kVA at Peak kW Coincidental")
                .putAll(PaoType.RFN430SL2,
                        "Demand at Peak kVa Coincidental",
                        "kVA at Peak kW Coincidental")
                .putAll(PaoType.RFN430SL3,
                        "Demand at Peak kVa Coincidental",
                        "kVA at Peak kW Coincidental")
                .putAll(PaoType.RFN430SL4,
                        "Demand at Peak kVa Coincidental",
                        "kVA at Peak kW Coincidental")
                .putAll(PaoType.RFN440_2131TD,
                        "Device Temperature",
                        "Forward Inductive kVArh",
                        "Reverse Inductive kVArh")
                .putAll(PaoType.RFN440_2132TD,
                        "Device Temperature",
                        "Forward Inductive kVArh",
                        "Reverse Inductive kVArh")
                .putAll(PaoType.RFN440_2133TD,
                        "Device Temperature",
                        "Forward Inductive kVArh",
                        "Reverse Inductive kVArh")
                .putAll(PaoType.RFN510FL,
                        "Peak Demand Daily")
                .putAll(PaoType.CRL520FRXE,
                        "RSRP",
                        "RSRQ",
                        "RSSI",
                        "SINR")
                .putAll(PaoType.CRL520FAXE,
                        "RSRP",
                        "RSRQ",
                        "RSSI",
                        "SINR")
                .putAll(PaoType.CRL520FAXED,
                        "RSRP",
                        "RSRQ",
                        "RSSI",
                        "SINR")
                .putAll(PaoType.CRL520FRXED,
                        "RSRP",
                        "RSRQ",
                        "RSSI",
                        "SINR")
                .putAll(PaoType.CRL530S4X,
                        "RSRP",
                        "RSRQ",
                        "RSSI",
                        "SINR",
                        "Net kWh")
                .putAll(PaoType.RFN530S4X,
                        "Net kWh")
                .build();
    }
}