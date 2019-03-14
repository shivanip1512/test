package com.cannontech.amr.rfn.service.pointmapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.cannontech.amr.rfn.service.pointmapping.icd.ManufacturerModel;
import com.cannontech.amr.rfn.service.pointmapping.icd.MeterClass;
import com.cannontech.amr.rfn.service.pointmapping.icd.ModelPointDefinition;
import com.cannontech.amr.rfn.service.pointmapping.icd.NameScale;
import com.cannontech.amr.rfn.service.pointmapping.icd.Named;
import com.cannontech.amr.rfn.service.pointmapping.icd.PointDefinition;
import com.cannontech.amr.rfn.service.pointmapping.icd.CoincidentGroupingCollector;
import com.cannontech.amr.rfn.service.pointmapping.icd.PointMapping;
import com.cannontech.amr.rfn.service.pointmapping.icd.PointMappingIcd;
import com.cannontech.amr.rfn.service.pointmapping.icd.RfnPointMappingParser;
import com.cannontech.amr.rfn.service.pointmapping.icd.YukonPointMappingIcdParser;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDaoImplTest;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.stream.StreamUtils;
import com.cannontech.common.stream.Try;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class RfnPointMappingTest {
    
    PaoDefinitionDao paoDefinitionDao = PaoDefinitionDaoImplTest.getTestPaoDefinitionDao();
    private static final String MAPPING = "com/cannontech/amr/rfn/service/pointmapping/rfnPointMapping.xml";
    private static final Multimap<PaoType, String> knownMissingRfnPointMappingPoints = getMissingRfnPointMappingPoints(); 
    
    private Map<PaoType, Map<PointMapping, NameScale>> getRfnPointMapping() throws JDOMException, IOException {
        return RfnPointMappingParser.getPaoTypePoints(getRfnXmlStream());
    }

    private PointMappingIcd getPointMappingIcd() throws IOException {
        ClassPathResource yukonPointMappingIcdYaml = new ClassPathResource("yukonPointMappingIcd.yaml");
        
        return YukonPointMappingIcdParser.parse(yukonPointMappingIcdYaml.getInputStream());
    }

    private InputStream getRfnXmlStream() throws IOException {
        ClassPathResource rfnPointMappingXml = new ClassPathResource(MAPPING);
        return rfnPointMappingXml.getInputStream();
    }
    
    @Test
    public void testOrdered() throws IOException, JDOMException {
        //  Duplicate mappings will cause a parser error in RfnPointMappingParser's stream code, so detect those first
        detectDuplicateMappings();
        
        confirmPointsExistOnDevices();
        
        compareToYukonPointMappingIcd();
    }
    
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
                        Assert.fail(String.join("\n", 
                            "PaoType " + l.getKey() + " has conflicting entries:",
                            l.getValue().toString(),
                            r.getValue().toString()));
                        return null;
                  }));
    }
    
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
                    .filter(e -> knownMissingRfnPointMappingPoints.containsEntry(e.getKey(), e.getValue()))
                    .map(e -> e.getKey() + "/" + e.getValue())
                    .collect(Collectors.joining("\n"));
        
        String unexpectedFailure =
                failures.entries().stream()
                    .filter(e -> !knownMissingRfnPointMappingPoints.containsEntry(e.getKey(), e.getValue()))
                    .map(e -> e.getKey() + "/" + e.getValue())
                    .collect(Collectors.joining("\n"));

        assertTrue("Points listed in knownMissingRfnPointMappingPoints but were successfully found:\n" + unexpectedSuccess, unexpectedSuccess.isEmpty());
        assertTrue("Points not found:\n" + unexpectedFailure, unexpectedFailure.isEmpty());
    }
    
    public void compareToYukonPointMappingIcd() throws IOException, JDOMException {

        PointMappingIcd icd = getPointMappingIcd();

        Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping = getRfnPointMapping();

        assertEquals(rfnPointMapping.get(PaoType.RFN420CL), 
                     rfnPointMapping.get(PaoType.RFN420CD));
        
        compareMeterClassModelPoints(icd, rfnPointMapping, icd.itronCentronWithAdvancedMetrology, MeterClass.ITRON_CENTRON);

        rfnPointMapping.remove(PaoType.RFN420CD);  //  already tested via the 420CL

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
        compareMeterClassModelPoints(icd, rfnPointMapping, icd.lgyrFocusAxRx_rfn500, MeterClass.LGYR_FOCUS_AX_RX_500);
        compareMeterClassModelPoints(icd, rfnPointMapping, icd.lgyrS4_rfn500, MeterClass.LGYR_S4);

        compareMeterClassPoints(icd, rfnPointMapping, icd.nextGenWaterNode, MeterClass.NEXT_GEN_WATER_NODE);
        
        //  1st gen water meter not mapped in yukonPointMapping
        rfnPointMapping.remove(PaoType.RFWMETER);

        //  Gas node not mapped in yukonPointMapping
        rfnPointMapping.remove(PaoType.RFG201);
        rfnPointMapping.remove(PaoType.RFG301);

        //  RFN-430kV unmapped in yukonPointMapping
        rfnPointMapping.remove(PaoType.RFN430KV);

        
        rfnPointMapping.entrySet().stream()
            .forEach(e -> Assert.fail(e.getKey() + " has " + e.getValue().size() + " unmapped points." 
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
        
        assertNotNull("No points found for type " + type, rpmTypePoints);
        
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
            Assert.fail(complaint);
        }
    }
    
    private static Multimap<PaoType, String> getMissingRfnPointMappingPoints() {
        return ImmutableMultimap.<PaoType, String>builder()
                .put(PaoType.RFN420FD, "Amps Phase B")
                .put(PaoType.RFN420FD, "Amps Phase C")
                .put(PaoType.RFN420FD, "Avg Volts Phase B")
                .put(PaoType.RFN420FD, "Avg Volts Phase C")
                .put(PaoType.RFN420FD, "Max Volts Phase B")
                .put(PaoType.RFN420FD, "Max Volts Phase C")
                .put(PaoType.RFN420FD, "Min Volts Phase B")
                .put(PaoType.RFN420FD, "Min Volts Phase C")
                .put(PaoType.RFN420FD, "Power Factor Degrees Phase B")
                .put(PaoType.RFN420FD, "Power Factor Degrees Phase C")
                .put(PaoType.RFN420FD, "Volt Degrees Phase B")
                .put(PaoType.RFN420FD, "Volt Degrees Phase C")
                .put(PaoType.RFN420FD, "Volts Phase B")
                .put(PaoType.RFN420FD, "Volts Phase C")
                
                .put(PaoType.RFN420FRD, "Amps Phase B")
                .put(PaoType.RFN420FRD, "Amps Phase C")
                .put(PaoType.RFN420FRD, "Avg Volts Phase B")
                .put(PaoType.RFN420FRD, "Avg Volts Phase C")
                .put(PaoType.RFN420FRD, "Max Volts Phase B")
                .put(PaoType.RFN420FRD, "Max Volts Phase C")
                .put(PaoType.RFN420FRD, "Min Volts Phase B")
                .put(PaoType.RFN420FRD, "Min Volts Phase C")
                .put(PaoType.RFN420FRD, "Power Factor Degrees Phase B")
                .put(PaoType.RFN420FRD, "Power Factor Degrees Phase C")
                .put(PaoType.RFN420FRD, "Volt Degrees Phase B")
                .put(PaoType.RFN420FRD, "Volt Degrees Phase C")
                .put(PaoType.RFN420FRD, "Volts Phase B")
                .put(PaoType.RFN420FRD, "Volts Phase C")
                
                .put(PaoType.RFN420FRX, "Amps Phase B")
                .put(PaoType.RFN420FRX, "Amps Phase C")
                .put(PaoType.RFN420FRX, "Avg Volts Phase B")
                .put(PaoType.RFN420FRX, "Avg Volts Phase C")
                .put(PaoType.RFN420FRX, "Max Volts Phase B")
                .put(PaoType.RFN420FRX, "Max Volts Phase C")
                .put(PaoType.RFN420FRX, "Min Volts Phase B")
                .put(PaoType.RFN420FRX, "Min Volts Phase C")
                .put(PaoType.RFN420FRX, "Power Factor Degrees Phase B")
                .put(PaoType.RFN420FRX, "Power Factor Degrees Phase C")
                .put(PaoType.RFN420FRX, "Volt Degrees Phase B")
                .put(PaoType.RFN420FRX, "Volt Degrees Phase C")
                .put(PaoType.RFN420FRX, "Volts Phase B")
                .put(PaoType.RFN420FRX, "Volts Phase C")
                
                .put(PaoType.RFN420FX, "Amps Phase B")
                .put(PaoType.RFN420FX, "Amps Phase C")
                .put(PaoType.RFN420FX, "Avg Volts Phase B")
                .put(PaoType.RFN420FX, "Avg Volts Phase C")
                .put(PaoType.RFN420FX, "Max Volts Phase B")
                .put(PaoType.RFN420FX, "Max Volts Phase C")
                .put(PaoType.RFN420FX, "Min Volts Phase B")
                .put(PaoType.RFN420FX, "Min Volts Phase C")
                .put(PaoType.RFN420FX, "Power Factor Degrees Phase B")
                .put(PaoType.RFN420FX, "Power Factor Degrees Phase C")
                .put(PaoType.RFN420FX, "Volt Degrees Phase B")
                .put(PaoType.RFN420FX, "Volt Degrees Phase C")
                .put(PaoType.RFN420FX, "Volts Phase B")
                .put(PaoType.RFN420FX, "Volts Phase C")
                
                .put(PaoType.RFN430A3D, "Peak Demand Daily")
                .put(PaoType.RFN430A3K, "Peak Demand Daily")
                .put(PaoType.RFN430A3R, "Peak Demand Daily")
                .put(PaoType.RFN430A3T, "Peak Demand Daily")
                
                .put(PaoType.RFN430KV, "Average Delivered Power Factor")
                .put(PaoType.RFN430KV, "Average Delivered Power Factor")
                .put(PaoType.RFN430KV, "Average Received Power Factor")
                .put(PaoType.RFN430KV, "Average Received Power Factor")
                .put(PaoType.RFN430KV, "Delivered kVA")
                .put(PaoType.RFN430KV, "Delivered kVA")
                .put(PaoType.RFN430KV, "Delivered kVAr")
                .put(PaoType.RFN430KV, "Peak Demand Daily")
                .put(PaoType.RFN430KV, "Received kVA")
                .put(PaoType.RFN430KV, "Received kVA")
                .put(PaoType.RFN430KV, "Received kVAr")
                .put(PaoType.RFN430KV, "Received kW")
                
                .put(PaoType.RFN430SL4, "Net Delivered kVARh")
                .put(PaoType.RFN430SL4, "Net Received kVARh")
                .put(PaoType.RFN430SL4, "Rate A Net Received kVARh")
                .put(PaoType.RFN430SL4, "Rate B Net Received kVARh")
                .put(PaoType.RFN430SL4, "Rate C Net Received kVARh")
                .put(PaoType.RFN430SL4, "Rate D Net Received kVARh")
                
                .put(PaoType.RFN440_2131TD, "Forward Inductive kVARh")
                .put(PaoType.RFN440_2131TD, "Reverse Inductive kVARh")
                .put(PaoType.RFN440_2132TD, "Forward Inductive kVARh")
                .put(PaoType.RFN440_2132TD, "Reverse Inductive kVARh")
                .put(PaoType.RFN440_2133TD, "Forward Inductive kVARh")
                .put(PaoType.RFN440_2133TD, "Reverse Inductive kVARh")
                
                .put(PaoType.RFN520FAX, "Delivered Peak kVA Frozen")
                .put(PaoType.RFN520FAX, "Delivered Peak kVA")
                .put(PaoType.RFN520FAX, "Delivered Peak kVAr Frozen")
                .put(PaoType.RFN520FAX, "Delivered Peak kVAr")
                .put(PaoType.RFN520FAX, "Delivered kVAh")
                .put(PaoType.RFN520FAX, "Delivered kVArh")
                .put(PaoType.RFN520FAX, "Delivered kVArh")
                .put(PaoType.RFN520FAX, "Net kVAh")
                .put(PaoType.RFN520FAX, "Net kVArh")
                .put(PaoType.RFN520FAX, "Power Factor Degrees")
                .put(PaoType.RFN520FAX, "Received kVAh")
                .put(PaoType.RFN520FAX, "Received kVArh")
                .put(PaoType.RFN520FAX, "Sum Peak kVA Frozen")
                .put(PaoType.RFN520FAX, "Sum Peak kVA")
                .put(PaoType.RFN520FAX, "Sum Peak kVAr Frozen")
                .put(PaoType.RFN520FAX, "Sum Peak kVAr")
                .put(PaoType.RFN520FAX, "Sum kVAh")
                .put(PaoType.RFN520FAX, "Sum kVArh")
                
                .put(PaoType.RFN520FAXD, "Delivered Peak kVA Frozen")
                .put(PaoType.RFN520FAXD, "Delivered Peak kVA")
                .put(PaoType.RFN520FAXD, "Delivered Peak kVAr Frozen")
                .put(PaoType.RFN520FAXD, "Delivered Peak kVAr")
                .put(PaoType.RFN520FAXD, "Delivered kVAh")
                .put(PaoType.RFN520FAXD, "Delivered kVArh")
                .put(PaoType.RFN520FAXD, "Delivered kVArh")
                .put(PaoType.RFN520FAXD, "Net kVAh")
                .put(PaoType.RFN520FAXD, "Net kVArh")
                .put(PaoType.RFN520FAXD, "Power Factor Degrees")
                .put(PaoType.RFN520FAXD, "Received kVAh")
                .put(PaoType.RFN520FAXD, "Received kVArh")
                .put(PaoType.RFN520FAXD, "Sum Peak kVA Frozen")
                .put(PaoType.RFN520FAXD, "Sum Peak kVA")
                .put(PaoType.RFN520FAXD, "Sum Peak kVAr Frozen")
                .put(PaoType.RFN520FAXD, "Sum Peak kVAr")
                .put(PaoType.RFN520FAXD, "Sum kVAh")
                .put(PaoType.RFN520FAXD, "Sum kVArh")
                
                .put(PaoType.RFN520FRX, "Delivered Peak kVA Frozen")
                .put(PaoType.RFN520FRX, "Delivered Peak kVAr Frozen")
                .put(PaoType.RFN520FRX, "Power Factor Degrees")
                .put(PaoType.RFN520FRX, "Sum Peak kVA Frozen")
                .put(PaoType.RFN520FRX, "Sum Peak kVAr Frozen")
                
                .put(PaoType.RFN520FRXD, "Delivered Peak kVA Frozen")
                .put(PaoType.RFN520FRXD, "Delivered Peak kVAr Frozen")
                .put(PaoType.RFN520FRXD, "Power Factor Degrees")
                .put(PaoType.RFN520FRXD, "Sum Peak kVA Frozen")
                .put(PaoType.RFN520FRXD, "Sum Peak kVAr Frozen")
                
                .put(PaoType.RFN530FAX, "Delivered Peak kVA Frozen")
                .put(PaoType.RFN530FAX, "Delivered Peak kVA")
                .put(PaoType.RFN530FAX, "Delivered Peak kVAr Frozen")
                .put(PaoType.RFN530FAX, "Delivered Peak kVAr")
                .put(PaoType.RFN530FAX, "Delivered kVAh")
                .put(PaoType.RFN530FAX, "Delivered kVArh")
                .put(PaoType.RFN530FAX, "Delivered kVArh")
                .put(PaoType.RFN530FAX, "Net kVAh")
                .put(PaoType.RFN530FAX, "Net kVArh")
                .put(PaoType.RFN530FAX, "Peak Demand Daily")
                .put(PaoType.RFN530FAX, "Power Factor Degrees Phase A")
                .put(PaoType.RFN530FAX, "Power Factor Degrees Phase B")
                .put(PaoType.RFN530FAX, "Power Factor Degrees Phase C")
                .put(PaoType.RFN530FAX, "Received kVAh")
                .put(PaoType.RFN530FAX, "Received kVArh")
                .put(PaoType.RFN530FAX, "Sum Peak kVA Frozen")
                .put(PaoType.RFN530FAX, "Sum Peak kVA")
                .put(PaoType.RFN530FAX, "Sum Peak kVAr Frozen")
                .put(PaoType.RFN530FAX, "Sum Peak kVAr")
                .put(PaoType.RFN530FAX, "Sum kVAh")
                .put(PaoType.RFN530FAX, "Sum kVArh")
                
                .put(PaoType.RFN530FRX, "Delivered Peak kVA Frozen")
                .put(PaoType.RFN530FRX, "Delivered Peak kVAr Frozen")
                .put(PaoType.RFN530FRX, "Peak Demand Daily")
                .put(PaoType.RFN530FRX, "Power Factor Degrees Phase A")
                .put(PaoType.RFN530FRX, "Power Factor Degrees Phase B")
                .put(PaoType.RFN530FRX, "Power Factor Degrees Phase C")
                .put(PaoType.RFN530FRX, "Sum Peak kVA Frozen")
                .put(PaoType.RFN530FRX, "Sum Peak kVAr Frozen")
                
                .put(PaoType.RFN530S4EAX, "Avg Voltage Phase A")
                .put(PaoType.RFN530S4EAX, "Avg Voltage Phase B")
                .put(PaoType.RFN530S4EAX, "Avg Voltage Phase C")
                .put(PaoType.RFN530S4EAX, "Delivered Peak kVA (Rate A kVA)")
                .put(PaoType.RFN530S4EAX, "Delivered Peak kVA (Rate B kVA)")
                .put(PaoType.RFN530S4EAX, "Delivered Peak kVA (Rate C kVA)")
                .put(PaoType.RFN530S4EAX, "Delivered Peak kVA (Rate D kVA)")
                .put(PaoType.RFN530S4EAX, "Delivered Peak kVAr (Rate A kVAr)")
                .put(PaoType.RFN530S4EAX, "Delivered Peak kVAr (Rate B kVAr)")
                .put(PaoType.RFN530S4EAX, "Delivered Peak kVAr (Rate C kVAr)")
                .put(PaoType.RFN530S4EAX, "Delivered Peak kVAr (Rate D kVAr)")
                .put(PaoType.RFN530S4EAX, "Delivered Peak kVAr")
                .put(PaoType.RFN530S4EAX, "Delivered kVA")
                .put(PaoType.RFN530S4EAX, "Delivered kVAh (Rate A kVAh)")
                .put(PaoType.RFN530S4EAX, "Delivered kVAh (Rate B kVAh)")
                .put(PaoType.RFN530S4EAX, "Delivered kVAh (Rate C kVAh)")
                .put(PaoType.RFN530S4EAX, "Delivered kVAh (Rate D kVAh)")
                .put(PaoType.RFN530S4EAX, "Delivered kVAh")
                .put(PaoType.RFN530S4EAX, "Delivered kVAr")
                .put(PaoType.RFN530S4EAX, "Delivered kVArh (Rate A kVArh)")
                .put(PaoType.RFN530S4EAX, "Delivered kVArh (Rate B kVArh)")
                .put(PaoType.RFN530S4EAX, "Delivered kVArh (Rate C kVArh)")
                .put(PaoType.RFN530S4EAX, "Delivered kVArh (Rate D kVArh)")
                .put(PaoType.RFN530S4EAX, "Delivered kVArh")
                .put(PaoType.RFN530S4EAX, "Delivered kW")
                .put(PaoType.RFN530S4EAX, "Delivered kWh")
                .put(PaoType.RFN530S4EAX, "Max Voltage Phase A")
                .put(PaoType.RFN530S4EAX, "Max Voltage Phase B")
                .put(PaoType.RFN530S4EAX, "Max Voltage Phase C")
                .put(PaoType.RFN530S4EAX, "Min Voltage Phase A")
                .put(PaoType.RFN530S4EAX, "Min Voltage Phase B")
                .put(PaoType.RFN530S4EAX, "Min Voltage Phase C")
                .put(PaoType.RFN530S4EAX, "Net Peak kW (Rate A kW)")
                .put(PaoType.RFN530S4EAX, "Net Peak kW (Rate B kW)")
                .put(PaoType.RFN530S4EAX, "Net Peak kW (Rate C kW)")
                .put(PaoType.RFN530S4EAX, "Net Peak kW (Rate D kW)")
                .put(PaoType.RFN530S4EAX, "Net Peak kW")
                .put(PaoType.RFN530S4EAX, "Net kVArh (Rate A kVArh)")
                .put(PaoType.RFN530S4EAX, "Net kVArh (Rate B kVArh)")
                .put(PaoType.RFN530S4EAX, "Net kVArh (Rate C kVArh)")
                .put(PaoType.RFN530S4EAX, "Net kVArh (Rate D kVArh)")
                .put(PaoType.RFN530S4EAX, "Net kVArh")
                .put(PaoType.RFN530S4EAX, "Net kWh (Rate A kWh)")
                .put(PaoType.RFN530S4EAX, "Net kWh (Rate B kWh)")
                .put(PaoType.RFN530S4EAX, "Net kWh (Rate C kWh)")
                .put(PaoType.RFN530S4EAX, "Net kWh (Rate D kWh)")
                .put(PaoType.RFN530S4EAX, "Peak Demand Daily")
                .put(PaoType.RFN530S4EAX, "Peak kVA")
                .put(PaoType.RFN530S4EAX, "Peak kW (Rate A kW)")
                .put(PaoType.RFN530S4EAX, "Peak kW (Rate B kW)")
                .put(PaoType.RFN530S4EAX, "Peak kW (Rate C kW)")
                .put(PaoType.RFN530S4EAX, "Peak kW (Rate D kW)")
                .put(PaoType.RFN530S4EAX, "Peak kW Frozen (Rate A kW)")
                .put(PaoType.RFN530S4EAX, "Peak kW Frozen (Rate B kW)")
                .put(PaoType.RFN530S4EAX, "Peak kW Frozen (Rate C kW)")
                .put(PaoType.RFN530S4EAX, "Peak kW Frozen (Rate D kW)")
                .put(PaoType.RFN530S4EAX, "Peak kW Frozen")
                .put(PaoType.RFN530S4EAX, "Peak kW")
                .put(PaoType.RFN530S4EAX, "Power Factor Degrees Phase A")
                .put(PaoType.RFN530S4EAX, "Power Factor Degrees Phase B")
                .put(PaoType.RFN530S4EAX, "Power Factor Degrees Phase C")
                .put(PaoType.RFN530S4EAX, "Rate A kWh")
                .put(PaoType.RFN530S4EAX, "Rate B kWh")
                .put(PaoType.RFN530S4EAX, "Rate C kWh")
                .put(PaoType.RFN530S4EAX, "Rate D kWh")
                .put(PaoType.RFN530S4EAX, "Received Peak kVA (Rate A kVA)")
                .put(PaoType.RFN530S4EAX, "Received Peak kVA (Rate B kVA)")
                .put(PaoType.RFN530S4EAX, "Received Peak kVA (Rate C kVA)")
                .put(PaoType.RFN530S4EAX, "Received Peak kVA (Rate D kVA)")
                .put(PaoType.RFN530S4EAX, "Received Peak kVA")
                .put(PaoType.RFN530S4EAX, "Received Peak kVAr (Rate A kVAr)")
                .put(PaoType.RFN530S4EAX, "Received Peak kVAr (Rate B kVAr)")
                .put(PaoType.RFN530S4EAX, "Received Peak kVAr (Rate C kVAr)")
                .put(PaoType.RFN530S4EAX, "Received Peak kVAr (Rate D kVAr)")
                .put(PaoType.RFN530S4EAX, "Received Peak kVAr")
                .put(PaoType.RFN530S4EAX, "Received Peak kW (Rate A kW)")
                .put(PaoType.RFN530S4EAX, "Received Peak kW (Rate B kW)")
                .put(PaoType.RFN530S4EAX, "Received Peak kW (Rate C kW)")
                .put(PaoType.RFN530S4EAX, "Received Peak kW (Rate D kW)")
                .put(PaoType.RFN530S4EAX, "Received Peak kW")
                .put(PaoType.RFN530S4EAX, "Received kVA")
                .put(PaoType.RFN530S4EAX, "Received kVAh (Rate A kVAh)")
                .put(PaoType.RFN530S4EAX, "Received kVAh (Rate B kVAh)")
                .put(PaoType.RFN530S4EAX, "Received kVAh (Rate C kVAh)")
                .put(PaoType.RFN530S4EAX, "Received kVAh (Rate D kVAh)")
                .put(PaoType.RFN530S4EAX, "Received kVAh")
                .put(PaoType.RFN530S4EAX, "Received kVAr")
                .put(PaoType.RFN530S4EAX, "Received kVArh (Rate A kVArh)")
                .put(PaoType.RFN530S4EAX, "Received kVArh (Rate B kVArh)")
                .put(PaoType.RFN530S4EAX, "Received kVArh (Rate C kVArh)")
                .put(PaoType.RFN530S4EAX, "Received kVArh (Rate D kVArh)")
                .put(PaoType.RFN530S4EAX, "Received kVArh")
                .put(PaoType.RFN530S4EAX, "Received kW")
                .put(PaoType.RFN530S4EAX, "Received kWh (Rate A kWh)")
                .put(PaoType.RFN530S4EAX, "Received kWh (Rate B kWh)")
                .put(PaoType.RFN530S4EAX, "Received kWh (Rate C kWh)")
                .put(PaoType.RFN530S4EAX, "Received kWh (Rate D kWh)")
                .put(PaoType.RFN530S4EAX, "Sum Peak kVA (Rate A kVA)")
                .put(PaoType.RFN530S4EAX, "Sum Peak kVA (Rate B kVA)")
                .put(PaoType.RFN530S4EAX, "Sum Peak kVA (Rate C kVA)")
                .put(PaoType.RFN530S4EAX, "Sum Peak kVA (Rate D kVA)")
                .put(PaoType.RFN530S4EAX, "Sum Peak kVA")
                .put(PaoType.RFN530S4EAX, "Sum Peak kVAr (Rate A kVAr)")
                .put(PaoType.RFN530S4EAX, "Sum Peak kVAr (Rate B kVAr)")
                .put(PaoType.RFN530S4EAX, "Sum Peak kVAr (Rate C kVAr)")
                .put(PaoType.RFN530S4EAX, "Sum Peak kVAr (Rate D kVAr)")
                .put(PaoType.RFN530S4EAX, "Sum Peak kVAr")
                .put(PaoType.RFN530S4EAX, "Sum Peak kW (Rate A kW)")
                .put(PaoType.RFN530S4EAX, "Sum Peak kW (Rate B kW)")
                .put(PaoType.RFN530S4EAX, "Sum Peak kW (Rate C kW)")
                .put(PaoType.RFN530S4EAX, "Sum Peak kW (Rate D kW)")
                .put(PaoType.RFN530S4EAX, "Sum kVA")
                .put(PaoType.RFN530S4EAX, "Sum kVAh (Rate A kVAh)")
                .put(PaoType.RFN530S4EAX, "Sum kVAh (Rate B kVAh)")
                .put(PaoType.RFN530S4EAX, "Sum kVAh (Rate C kVAh)")
                .put(PaoType.RFN530S4EAX, "Sum kVAh (Rate D kVAh)")
                .put(PaoType.RFN530S4EAX, "Sum kVAh")
                .put(PaoType.RFN530S4EAX, "Sum kVAr")
                .put(PaoType.RFN530S4EAX, "Sum kVArh (Rate A kVArh)")
                .put(PaoType.RFN530S4EAX, "Sum kVArh (Rate B kVArh)")
                .put(PaoType.RFN530S4EAX, "Sum kVArh (Rate C kVArh)")
                .put(PaoType.RFN530S4EAX, "Sum kVArh (Rate D kVArh)")
                .put(PaoType.RFN530S4EAX, "Sum kVArh")
                .put(PaoType.RFN530S4EAX, "Sum kW")
                .put(PaoType.RFN530S4EAX, "Sum kWh (Rate A kWh)")
                .put(PaoType.RFN530S4EAX, "Sum kWh (Rate B kWh)")
                .put(PaoType.RFN530S4EAX, "Sum kWh (Rate C kWh)")
                .put(PaoType.RFN530S4EAX, "Sum kWh (Rate D kWh)")
                .put(PaoType.RFN530S4EAX, "kVAh Leading (Q1 + Q3)")
                .put(PaoType.RFN530S4EAX, "kVArh Leading (Q1 + Q3)")
                
                .put(PaoType.RFN530S4EAXR, "Avg Voltage Phase A")
                .put(PaoType.RFN530S4EAXR, "Avg Voltage Phase B")
                .put(PaoType.RFN530S4EAXR, "Avg Voltage Phase C")
                .put(PaoType.RFN530S4EAXR, "Delivered Peak kVA (Rate A kVA)")
                .put(PaoType.RFN530S4EAXR, "Delivered Peak kVA (Rate B kVA)")
                .put(PaoType.RFN530S4EAXR, "Delivered Peak kVA (Rate C kVA)")
                .put(PaoType.RFN530S4EAXR, "Delivered Peak kVA (Rate D kVA)")
                .put(PaoType.RFN530S4EAXR, "Delivered Peak kVAr (Rate A kVAr)")
                .put(PaoType.RFN530S4EAXR, "Delivered Peak kVAr (Rate B kVAr)")
                .put(PaoType.RFN530S4EAXR, "Delivered Peak kVAr (Rate C kVAr)")
                .put(PaoType.RFN530S4EAXR, "Delivered Peak kVAr (Rate D kVAr)")
                .put(PaoType.RFN530S4EAXR, "Delivered Peak kVAr")
                .put(PaoType.RFN530S4EAXR, "Delivered kVA")
                .put(PaoType.RFN530S4EAXR, "Delivered kVAh (Rate A kVAh)")
                .put(PaoType.RFN530S4EAXR, "Delivered kVAh (Rate B kVAh)")
                .put(PaoType.RFN530S4EAXR, "Delivered kVAh (Rate C kVAh)")
                .put(PaoType.RFN530S4EAXR, "Delivered kVAh (Rate D kVAh)")
                .put(PaoType.RFN530S4EAXR, "Delivered kVAh")
                .put(PaoType.RFN530S4EAXR, "Delivered kVAr")
                .put(PaoType.RFN530S4EAXR, "Delivered kVArh (Rate A kVArh)")
                .put(PaoType.RFN530S4EAXR, "Delivered kVArh (Rate B kVArh)")
                .put(PaoType.RFN530S4EAXR, "Delivered kVArh (Rate C kVArh)")
                .put(PaoType.RFN530S4EAXR, "Delivered kVArh (Rate D kVArh)")
                .put(PaoType.RFN530S4EAXR, "Delivered kVArh")
                .put(PaoType.RFN530S4EAXR, "Delivered kW")
                .put(PaoType.RFN530S4EAXR, "Delivered kWh")
                .put(PaoType.RFN530S4EAXR, "Max Voltage Phase A")
                .put(PaoType.RFN530S4EAXR, "Max Voltage Phase B")
                .put(PaoType.RFN530S4EAXR, "Max Voltage Phase C")
                .put(PaoType.RFN530S4EAXR, "Min Voltage Phase A")
                .put(PaoType.RFN530S4EAXR, "Min Voltage Phase B")
                .put(PaoType.RFN530S4EAXR, "Min Voltage Phase C")
                .put(PaoType.RFN530S4EAXR, "Net Peak kW (Rate A kW)")
                .put(PaoType.RFN530S4EAXR, "Net Peak kW (Rate B kW)")
                .put(PaoType.RFN530S4EAXR, "Net Peak kW (Rate C kW)")
                .put(PaoType.RFN530S4EAXR, "Net Peak kW (Rate D kW)")
                .put(PaoType.RFN530S4EAXR, "Net Peak kW")
                .put(PaoType.RFN530S4EAXR, "Net kVArh (Rate A kVArh)")
                .put(PaoType.RFN530S4EAXR, "Net kVArh (Rate B kVArh)")
                .put(PaoType.RFN530S4EAXR, "Net kVArh (Rate C kVArh)")
                .put(PaoType.RFN530S4EAXR, "Net kVArh (Rate D kVArh)")
                .put(PaoType.RFN530S4EAXR, "Net kVArh")
                .put(PaoType.RFN530S4EAXR, "Peak Demand Daily")
                .put(PaoType.RFN530S4EAXR, "Peak kVA")
                .put(PaoType.RFN530S4EAXR, "Peak kW (Rate A kW)")
                .put(PaoType.RFN530S4EAXR, "Peak kW (Rate B kW)")
                .put(PaoType.RFN530S4EAXR, "Peak kW (Rate C kW)")
                .put(PaoType.RFN530S4EAXR, "Peak kW (Rate D kW)")
                .put(PaoType.RFN530S4EAXR, "Peak kW Frozen (Rate A kW)")
                .put(PaoType.RFN530S4EAXR, "Peak kW Frozen (Rate B kW)")
                .put(PaoType.RFN530S4EAXR, "Peak kW Frozen (Rate C kW)")
                .put(PaoType.RFN530S4EAXR, "Peak kW Frozen (Rate D kW)")
                .put(PaoType.RFN530S4EAXR, "Peak kW Frozen")
                .put(PaoType.RFN530S4EAXR, "Peak kW")
                .put(PaoType.RFN530S4EAXR, "Power Factor Degrees Phase A")
                .put(PaoType.RFN530S4EAXR, "Power Factor Degrees Phase B")
                .put(PaoType.RFN530S4EAXR, "Power Factor Degrees Phase C")
                .put(PaoType.RFN530S4EAXR, "Rate A kWh")
                .put(PaoType.RFN530S4EAXR, "Rate B kWh")
                .put(PaoType.RFN530S4EAXR, "Rate C kWh")
                .put(PaoType.RFN530S4EAXR, "Rate D kWh")
                .put(PaoType.RFN530S4EAXR, "Received Peak kVA (Rate A kVA)")
                .put(PaoType.RFN530S4EAXR, "Received Peak kVA (Rate B kVA)")
                .put(PaoType.RFN530S4EAXR, "Received Peak kVA (Rate C kVA)")
                .put(PaoType.RFN530S4EAXR, "Received Peak kVA (Rate D kVA)")
                .put(PaoType.RFN530S4EAXR, "Received Peak kVA")
                .put(PaoType.RFN530S4EAXR, "Received Peak kVAr (Rate A kVAr)")
                .put(PaoType.RFN530S4EAXR, "Received Peak kVAr (Rate B kVAr)")
                .put(PaoType.RFN530S4EAXR, "Received Peak kVAr (Rate C kVAr)")
                .put(PaoType.RFN530S4EAXR, "Received Peak kVAr (Rate D kVAr)")
                .put(PaoType.RFN530S4EAXR, "Received Peak kVAr")
                .put(PaoType.RFN530S4EAXR, "Received Peak kW (Rate A kW)")
                .put(PaoType.RFN530S4EAXR, "Received Peak kW (Rate B kW)")
                .put(PaoType.RFN530S4EAXR, "Received Peak kW (Rate C kW)")
                .put(PaoType.RFN530S4EAXR, "Received Peak kW (Rate D kW)")
                .put(PaoType.RFN530S4EAXR, "Received Peak kW")
                .put(PaoType.RFN530S4EAXR, "Received kVA")
                .put(PaoType.RFN530S4EAXR, "Received kVAh (Rate A kVAh)")
                .put(PaoType.RFN530S4EAXR, "Received kVAh (Rate B kVAh)")
                .put(PaoType.RFN530S4EAXR, "Received kVAh (Rate C kVAh)")
                .put(PaoType.RFN530S4EAXR, "Received kVAh (Rate D kVAh)")
                .put(PaoType.RFN530S4EAXR, "Received kVAh")
                .put(PaoType.RFN530S4EAXR, "Received kVAr")
                .put(PaoType.RFN530S4EAXR, "Received kVArh (Rate A kVArh)")
                .put(PaoType.RFN530S4EAXR, "Received kVArh (Rate B kVArh)")
                .put(PaoType.RFN530S4EAXR, "Received kVArh (Rate C kVArh)")
                .put(PaoType.RFN530S4EAXR, "Received kVArh (Rate D kVArh)")
                .put(PaoType.RFN530S4EAXR, "Received kVArh")
                .put(PaoType.RFN530S4EAXR, "Received kW")
                .put(PaoType.RFN530S4EAXR, "Received kWh (Rate A kWh)")
                .put(PaoType.RFN530S4EAXR, "Received kWh (Rate B kWh)")
                .put(PaoType.RFN530S4EAXR, "Received kWh (Rate C kWh)")
                .put(PaoType.RFN530S4EAXR, "Received kWh (Rate D kWh)")
                .put(PaoType.RFN530S4EAXR, "Sum Peak kVA (Rate A kVA)")
                .put(PaoType.RFN530S4EAXR, "Sum Peak kVA (Rate B kVA)")
                .put(PaoType.RFN530S4EAXR, "Sum Peak kVA (Rate C kVA)")
                .put(PaoType.RFN530S4EAXR, "Sum Peak kVA (Rate D kVA)")
                .put(PaoType.RFN530S4EAXR, "Sum Peak kVA")
                .put(PaoType.RFN530S4EAXR, "Sum Peak kVAr (Rate A kVAr)")
                .put(PaoType.RFN530S4EAXR, "Sum Peak kVAr (Rate B kVAr)")
                .put(PaoType.RFN530S4EAXR, "Sum Peak kVAr (Rate C kVAr)")
                .put(PaoType.RFN530S4EAXR, "Sum Peak kVAr (Rate D kVAr)")
                .put(PaoType.RFN530S4EAXR, "Sum Peak kVAr")
                .put(PaoType.RFN530S4EAXR, "Sum kVA")
                .put(PaoType.RFN530S4EAXR, "Sum kVAh (Rate A kVAh)")
                .put(PaoType.RFN530S4EAXR, "Sum kVAh (Rate B kVAh)")
                .put(PaoType.RFN530S4EAXR, "Sum kVAh (Rate C kVAh)")
                .put(PaoType.RFN530S4EAXR, "Sum kVAh (Rate D kVAh)")
                .put(PaoType.RFN530S4EAXR, "Sum kVAh")
                .put(PaoType.RFN530S4EAXR, "Sum kVAr")
                .put(PaoType.RFN530S4EAXR, "Sum kVArh (Rate A kVArh)")
                .put(PaoType.RFN530S4EAXR, "Sum kVArh (Rate B kVArh)")
                .put(PaoType.RFN530S4EAXR, "Sum kVArh (Rate C kVArh)")
                .put(PaoType.RFN530S4EAXR, "Sum kVArh (Rate D kVArh)")
                .put(PaoType.RFN530S4EAXR, "Sum kVArh")
                .put(PaoType.RFN530S4EAXR, "Sum kW")
                .put(PaoType.RFN530S4EAXR, "kVAh Leading (Q1 + Q3)")
                .put(PaoType.RFN530S4EAXR, "kVArh Leading (Q1 + Q3)")
                
                .put(PaoType.RFN530S4ERX, "Avg Voltage Phase A")
                .put(PaoType.RFN530S4ERX, "Avg Voltage Phase B")
                .put(PaoType.RFN530S4ERX, "Avg Voltage Phase C")
                .put(PaoType.RFN530S4ERX, "Delivered Peak kVA (Rate A kVA)")
                .put(PaoType.RFN530S4ERX, "Delivered Peak kVA (Rate B kVA)")
                .put(PaoType.RFN530S4ERX, "Delivered Peak kVA (Rate C kVA)")
                .put(PaoType.RFN530S4ERX, "Delivered Peak kVA (Rate D kVA)")
                .put(PaoType.RFN530S4ERX, "Delivered Peak kVAr (Rate A kVAr)")
                .put(PaoType.RFN530S4ERX, "Delivered Peak kVAr (Rate B kVAr)")
                .put(PaoType.RFN530S4ERX, "Delivered Peak kVAr (Rate C kVAr)")
                .put(PaoType.RFN530S4ERX, "Delivered Peak kVAr (Rate D kVAr)")
                .put(PaoType.RFN530S4ERX, "Delivered Peak kVAr")
                .put(PaoType.RFN530S4ERX, "Delivered kVA")
                .put(PaoType.RFN530S4ERX, "Delivered kVAh (Rate A kVAh)")
                .put(PaoType.RFN530S4ERX, "Delivered kVAh (Rate B kVAh)")
                .put(PaoType.RFN530S4ERX, "Delivered kVAh (Rate C kVAh)")
                .put(PaoType.RFN530S4ERX, "Delivered kVAh (Rate D kVAh)")
                .put(PaoType.RFN530S4ERX, "Delivered kVAh")
                .put(PaoType.RFN530S4ERX, "Delivered kVAr")
                .put(PaoType.RFN530S4ERX, "Delivered kVArh (Rate A kVArh)")
                .put(PaoType.RFN530S4ERX, "Delivered kVArh (Rate B kVArh)")
                .put(PaoType.RFN530S4ERX, "Delivered kVArh (Rate C kVArh)")
                .put(PaoType.RFN530S4ERX, "Delivered kVArh (Rate D kVArh)")
                .put(PaoType.RFN530S4ERX, "Delivered kVArh")
                .put(PaoType.RFN530S4ERX, "Delivered kW")
                .put(PaoType.RFN530S4ERX, "Delivered kWh")
                .put(PaoType.RFN530S4ERX, "Max Voltage Phase A")
                .put(PaoType.RFN530S4ERX, "Max Voltage Phase B")
                .put(PaoType.RFN530S4ERX, "Max Voltage Phase C")
                .put(PaoType.RFN530S4ERX, "Min Voltage Phase A")
                .put(PaoType.RFN530S4ERX, "Min Voltage Phase B")
                .put(PaoType.RFN530S4ERX, "Min Voltage Phase C")
                .put(PaoType.RFN530S4ERX, "Net Peak kW (Rate A kW)")
                .put(PaoType.RFN530S4ERX, "Net Peak kW (Rate B kW)")
                .put(PaoType.RFN530S4ERX, "Net Peak kW (Rate C kW)")
                .put(PaoType.RFN530S4ERX, "Net Peak kW (Rate D kW)")
                .put(PaoType.RFN530S4ERX, "Net Peak kW")
                .put(PaoType.RFN530S4ERX, "Net kVArh (Rate A kVArh)")
                .put(PaoType.RFN530S4ERX, "Net kVArh (Rate B kVArh)")
                .put(PaoType.RFN530S4ERX, "Net kVArh (Rate C kVArh)")
                .put(PaoType.RFN530S4ERX, "Net kVArh (Rate D kVArh)")
                .put(PaoType.RFN530S4ERX, "Net kWh (Rate A kWh)")
                .put(PaoType.RFN530S4ERX, "Net kWh (Rate B kWh)")
                .put(PaoType.RFN530S4ERX, "Net kWh (Rate C kWh)")
                .put(PaoType.RFN530S4ERX, "Net kWh (Rate D kWh)")
                .put(PaoType.RFN530S4ERX, "Peak Demand Daily")
                .put(PaoType.RFN530S4ERX, "Peak kVA")
                .put(PaoType.RFN530S4ERX, "Peak kW (Rate A kW)")
                .put(PaoType.RFN530S4ERX, "Peak kW (Rate B kW)")
                .put(PaoType.RFN530S4ERX, "Peak kW (Rate C kW)")
                .put(PaoType.RFN530S4ERX, "Peak kW (Rate D kW)")
                .put(PaoType.RFN530S4ERX, "Peak kW Frozen (Rate A kW)")
                .put(PaoType.RFN530S4ERX, "Peak kW Frozen (Rate B kW)")
                .put(PaoType.RFN530S4ERX, "Peak kW Frozen (Rate C kW)")
                .put(PaoType.RFN530S4ERX, "Peak kW Frozen (Rate D kW)")
                .put(PaoType.RFN530S4ERX, "Peak kW Frozen")
                .put(PaoType.RFN530S4ERX, "Peak kW")
                .put(PaoType.RFN530S4ERX, "Power Factor Degrees Phase A")
                .put(PaoType.RFN530S4ERX, "Power Factor Degrees Phase B")
                .put(PaoType.RFN530S4ERX, "Power Factor Degrees Phase C")
                .put(PaoType.RFN530S4ERX, "Rate A kWh")
                .put(PaoType.RFN530S4ERX, "Rate B kWh")
                .put(PaoType.RFN530S4ERX, "Rate C kWh")
                .put(PaoType.RFN530S4ERX, "Rate D kWh")
                .put(PaoType.RFN530S4ERX, "Received Peak kVA (Rate A kVA)")
                .put(PaoType.RFN530S4ERX, "Received Peak kVA (Rate B kVA)")
                .put(PaoType.RFN530S4ERX, "Received Peak kVA (Rate C kVA)")
                .put(PaoType.RFN530S4ERX, "Received Peak kVA (Rate D kVA)")
                .put(PaoType.RFN530S4ERX, "Received Peak kVA")
                .put(PaoType.RFN530S4ERX, "Received Peak kVAr (Rate A kVAr)")
                .put(PaoType.RFN530S4ERX, "Received Peak kVAr (Rate B kVAr)")
                .put(PaoType.RFN530S4ERX, "Received Peak kVAr (Rate C kVAr)")
                .put(PaoType.RFN530S4ERX, "Received Peak kVAr (Rate D kVAr)")
                .put(PaoType.RFN530S4ERX, "Received Peak kVAr")
                .put(PaoType.RFN530S4ERX, "Received Peak kW (Rate A kW)")
                .put(PaoType.RFN530S4ERX, "Received Peak kW (Rate B kW)")
                .put(PaoType.RFN530S4ERX, "Received Peak kW (Rate C kW)")
                .put(PaoType.RFN530S4ERX, "Received Peak kW (Rate D kW)")
                .put(PaoType.RFN530S4ERX, "Received Peak kW")
                .put(PaoType.RFN530S4ERX, "Received kVA")
                .put(PaoType.RFN530S4ERX, "Received kVAh (Rate A kVAh)")
                .put(PaoType.RFN530S4ERX, "Received kVAh (Rate B kVAh)")
                .put(PaoType.RFN530S4ERX, "Received kVAh (Rate C kVAh)")
                .put(PaoType.RFN530S4ERX, "Received kVAh (Rate D kVAh)")
                .put(PaoType.RFN530S4ERX, "Received kVAh")
                .put(PaoType.RFN530S4ERX, "Received kVAr")
                .put(PaoType.RFN530S4ERX, "Received kVArh (Rate A kVArh)")
                .put(PaoType.RFN530S4ERX, "Received kVArh (Rate B kVArh)")
                .put(PaoType.RFN530S4ERX, "Received kVArh (Rate C kVArh)")
                .put(PaoType.RFN530S4ERX, "Received kVArh (Rate D kVArh)")
                .put(PaoType.RFN530S4ERX, "Received kVArh")
                .put(PaoType.RFN530S4ERX, "Received kW")
                .put(PaoType.RFN530S4ERX, "Received kWh (Rate A kWh)")
                .put(PaoType.RFN530S4ERX, "Received kWh (Rate B kWh)")
                .put(PaoType.RFN530S4ERX, "Received kWh (Rate C kWh)")
                .put(PaoType.RFN530S4ERX, "Received kWh (Rate D kWh)")
                .put(PaoType.RFN530S4ERX, "Sum Peak kVA (Rate A kVA)")
                .put(PaoType.RFN530S4ERX, "Sum Peak kVA (Rate B kVA)")
                .put(PaoType.RFN530S4ERX, "Sum Peak kVA (Rate C kVA)")
                .put(PaoType.RFN530S4ERX, "Sum Peak kVA (Rate D kVA)")
                .put(PaoType.RFN530S4ERX, "Sum Peak kVAr (Rate A kVAr)")
                .put(PaoType.RFN530S4ERX, "Sum Peak kVAr (Rate B kVAr)")
                .put(PaoType.RFN530S4ERX, "Sum Peak kVAr (Rate C kVAr)")
                .put(PaoType.RFN530S4ERX, "Sum Peak kVAr (Rate D kVAr)")
                .put(PaoType.RFN530S4ERX, "Sum Peak kW (Rate A kW)")
                .put(PaoType.RFN530S4ERX, "Sum Peak kW (Rate B kW)")
                .put(PaoType.RFN530S4ERX, "Sum Peak kW (Rate C kW)")
                .put(PaoType.RFN530S4ERX, "Sum Peak kW (Rate D kW)")
                .put(PaoType.RFN530S4ERX, "Sum kVA")
                .put(PaoType.RFN530S4ERX, "Sum kVAh (Rate A kVAh)")
                .put(PaoType.RFN530S4ERX, "Sum kVAh (Rate B kVAh)")
                .put(PaoType.RFN530S4ERX, "Sum kVAh (Rate C kVAh)")
                .put(PaoType.RFN530S4ERX, "Sum kVAh (Rate D kVAh)")
                .put(PaoType.RFN530S4ERX, "Sum kVAr")
                .put(PaoType.RFN530S4ERX, "Sum kVArh (Rate A kVArh)")
                .put(PaoType.RFN530S4ERX, "Sum kVArh (Rate B kVArh)")
                .put(PaoType.RFN530S4ERX, "Sum kVArh (Rate C kVArh)")
                .put(PaoType.RFN530S4ERX, "Sum kVArh (Rate D kVArh)")
                .put(PaoType.RFN530S4ERX, "Sum kW")
                .put(PaoType.RFN530S4ERX, "Sum kWh (Rate A kWh)")
                .put(PaoType.RFN530S4ERX, "Sum kWh (Rate B kWh)")
                .put(PaoType.RFN530S4ERX, "Sum kWh (Rate C kWh)")
                .put(PaoType.RFN530S4ERX, "Sum kWh (Rate D kWh)")
                .put(PaoType.RFN530S4ERX, "kVAh Leading (Q1 + Q3)")
                .put(PaoType.RFN530S4ERX, "kVArh Leading (Q1 + Q3)")
                
                .put(PaoType.RFN530S4ERXR, "Avg Voltage Phase A")
                .put(PaoType.RFN530S4ERXR, "Avg Voltage Phase B")
                .put(PaoType.RFN530S4ERXR, "Avg Voltage Phase C")
                .put(PaoType.RFN530S4ERXR, "Delivered Peak kVA (Rate A kVA)")
                .put(PaoType.RFN530S4ERXR, "Delivered Peak kVA (Rate B kVA)")
                .put(PaoType.RFN530S4ERXR, "Delivered Peak kVA (Rate C kVA)")
                .put(PaoType.RFN530S4ERXR, "Delivered Peak kVA (Rate D kVA)")
                .put(PaoType.RFN530S4ERXR, "Delivered Peak kVAr (Rate A kVAr)")
                .put(PaoType.RFN530S4ERXR, "Delivered Peak kVAr (Rate B kVAr)")
                .put(PaoType.RFN530S4ERXR, "Delivered Peak kVAr (Rate C kVAr)")
                .put(PaoType.RFN530S4ERXR, "Delivered Peak kVAr (Rate D kVAr)")
                .put(PaoType.RFN530S4ERXR, "Delivered Peak kVAr")
                .put(PaoType.RFN530S4ERXR, "Delivered kVA")
                .put(PaoType.RFN530S4ERXR, "Delivered kVAh (Rate A kVAh)")
                .put(PaoType.RFN530S4ERXR, "Delivered kVAh (Rate B kVAh)")
                .put(PaoType.RFN530S4ERXR, "Delivered kVAh (Rate C kVAh)")
                .put(PaoType.RFN530S4ERXR, "Delivered kVAh (Rate D kVAh)")
                .put(PaoType.RFN530S4ERXR, "Delivered kVAh")
                .put(PaoType.RFN530S4ERXR, "Delivered kVAr")
                .put(PaoType.RFN530S4ERXR, "Delivered kVArh (Rate A kVArh)")
                .put(PaoType.RFN530S4ERXR, "Delivered kVArh (Rate B kVArh)")
                .put(PaoType.RFN530S4ERXR, "Delivered kVArh (Rate C kVArh)")
                .put(PaoType.RFN530S4ERXR, "Delivered kVArh (Rate D kVArh)")
                .put(PaoType.RFN530S4ERXR, "Delivered kVArh")
                .put(PaoType.RFN530S4ERXR, "Delivered kW")
                .put(PaoType.RFN530S4ERXR, "Delivered kWh")
                .put(PaoType.RFN530S4ERXR, "Max Voltage Phase A")
                .put(PaoType.RFN530S4ERXR, "Max Voltage Phase B")
                .put(PaoType.RFN530S4ERXR, "Max Voltage Phase C")
                .put(PaoType.RFN530S4ERXR, "Min Voltage Phase A")
                .put(PaoType.RFN530S4ERXR, "Min Voltage Phase B")
                .put(PaoType.RFN530S4ERXR, "Min Voltage Phase C")
                .put(PaoType.RFN530S4ERXR, "Net Peak kW (Rate A kW)")
                .put(PaoType.RFN530S4ERXR, "Net Peak kW (Rate B kW)")
                .put(PaoType.RFN530S4ERXR, "Net Peak kW (Rate C kW)")
                .put(PaoType.RFN530S4ERXR, "Net Peak kW (Rate D kW)")
                .put(PaoType.RFN530S4ERXR, "Net Peak kW")
                .put(PaoType.RFN530S4ERXR, "Peak Demand Daily")
                .put(PaoType.RFN530S4ERXR, "Peak kVA")
                .put(PaoType.RFN530S4ERXR, "Peak kW (Rate A kW)")
                .put(PaoType.RFN530S4ERXR, "Peak kW (Rate B kW)")
                .put(PaoType.RFN530S4ERXR, "Peak kW (Rate C kW)")
                .put(PaoType.RFN530S4ERXR, "Peak kW (Rate D kW)")
                .put(PaoType.RFN530S4ERXR, "Peak kW Frozen (Rate A kW)")
                .put(PaoType.RFN530S4ERXR, "Peak kW Frozen (Rate B kW)")
                .put(PaoType.RFN530S4ERXR, "Peak kW Frozen (Rate C kW)")
                .put(PaoType.RFN530S4ERXR, "Peak kW Frozen (Rate D kW)")
                .put(PaoType.RFN530S4ERXR, "Peak kW Frozen")
                .put(PaoType.RFN530S4ERXR, "Peak kW")
                .put(PaoType.RFN530S4ERXR, "Power Factor Degrees Phase A")
                .put(PaoType.RFN530S4ERXR, "Power Factor Degrees Phase B")
                .put(PaoType.RFN530S4ERXR, "Power Factor Degrees Phase C")
                .put(PaoType.RFN530S4ERXR, "Rate A kWh")
                .put(PaoType.RFN530S4ERXR, "Rate B kWh")
                .put(PaoType.RFN530S4ERXR, "Rate C kWh")
                .put(PaoType.RFN530S4ERXR, "Rate D kWh")
                .put(PaoType.RFN530S4ERXR, "Received Peak kVA (Rate A kVA)")
                .put(PaoType.RFN530S4ERXR, "Received Peak kVA (Rate B kVA)")
                .put(PaoType.RFN530S4ERXR, "Received Peak kVA (Rate C kVA)")
                .put(PaoType.RFN530S4ERXR, "Received Peak kVA (Rate D kVA)")
                .put(PaoType.RFN530S4ERXR, "Received Peak kVA")
                .put(PaoType.RFN530S4ERXR, "Received Peak kVAr (Rate A kVAr)")
                .put(PaoType.RFN530S4ERXR, "Received Peak kVAr (Rate B kVAr)")
                .put(PaoType.RFN530S4ERXR, "Received Peak kVAr (Rate C kVAr)")
                .put(PaoType.RFN530S4ERXR, "Received Peak kVAr (Rate D kVAr)")
                .put(PaoType.RFN530S4ERXR, "Received Peak kVAr")
                .put(PaoType.RFN530S4ERXR, "Received Peak kW (Rate A kW)")
                .put(PaoType.RFN530S4ERXR, "Received Peak kW (Rate B kW)")
                .put(PaoType.RFN530S4ERXR, "Received Peak kW (Rate C kW)")
                .put(PaoType.RFN530S4ERXR, "Received Peak kW (Rate D kW)")
                .put(PaoType.RFN530S4ERXR, "Received Peak kW")
                .put(PaoType.RFN530S4ERXR, "Received kVA")
                .put(PaoType.RFN530S4ERXR, "Received kVAh (Rate A kVAh)")
                .put(PaoType.RFN530S4ERXR, "Received kVAh (Rate B kVAh)")
                .put(PaoType.RFN530S4ERXR, "Received kVAh (Rate C kVAh)")
                .put(PaoType.RFN530S4ERXR, "Received kVAh (Rate D kVAh)")
                .put(PaoType.RFN530S4ERXR, "Received kVAh")
                .put(PaoType.RFN530S4ERXR, "Received kVAr")
                .put(PaoType.RFN530S4ERXR, "Received kVArh (Rate A kVArh)")
                .put(PaoType.RFN530S4ERXR, "Received kVArh (Rate B kVArh)")
                .put(PaoType.RFN530S4ERXR, "Received kVArh (Rate C kVArh)")
                .put(PaoType.RFN530S4ERXR, "Received kVArh (Rate D kVArh)")
                .put(PaoType.RFN530S4ERXR, "Received kVArh")
                .put(PaoType.RFN530S4ERXR, "Received kW")
                .put(PaoType.RFN530S4ERXR, "Received kWh (Rate A kWh)")
                .put(PaoType.RFN530S4ERXR, "Received kWh (Rate B kWh)")
                .put(PaoType.RFN530S4ERXR, "Received kWh (Rate C kWh)")
                .put(PaoType.RFN530S4ERXR, "Received kWh (Rate D kWh)")
                .put(PaoType.RFN530S4ERXR, "Sum kVA")
                .put(PaoType.RFN530S4ERXR, "Sum kVAr")
                .put(PaoType.RFN530S4ERXR, "Sum kW")
                .put(PaoType.RFN530S4ERXR, "kVAh Leading (Q1 + Q3)")
                .put(PaoType.RFN530S4ERXR, "kVArh Leading (Q1 + Q3)")
                
                .put(PaoType.RFN530S4X, "Average Power Factor")
                .put(PaoType.RFN530S4X, "Net kVA")
                .put(PaoType.RFN530S4X, "Net kVAr")
                .put(PaoType.RFN530S4X, "Net kVArh (Rate A kVArh)")
                .put(PaoType.RFN530S4X, "Net kVArh (Rate B kVArh)")
                .put(PaoType.RFN530S4X, "Net kVArh (Rate C kVArh)")
                .put(PaoType.RFN530S4X, "Net kVArh (Rate D kVArh)")
                .put(PaoType.RFN530S4X, "Net kVArh")
                .put(PaoType.RFN530S4X, "Net kW")
                .put(PaoType.RFN530S4X, "Net kWh (Rate A kWh)")
                .put(PaoType.RFN530S4X, "Net kWh (Rate B kWh)")
                .put(PaoType.RFN530S4X, "Net kWh (Rate C kWh)")
                .put(PaoType.RFN530S4X, "Net kWh (Rate D kWh)")
                .put(PaoType.RFN530S4X, "Peak Demand Daily")
                .put(PaoType.RFN530S4X, "Power Factor Degrees Phase A")
                .put(PaoType.RFN530S4X, "Power Factor Degrees Phase B")
                .put(PaoType.RFN530S4X, "Power Factor Degrees Phase C")
                .put(PaoType.RFN530S4X, "kVA (Quadrants 1 2)")
                .put(PaoType.RFN530S4X, "kVA (Quadrants 1 3)")
                .put(PaoType.RFN530S4X, "kVA (Quadrants 2 4)")
                .put(PaoType.RFN530S4X, "kVA (Quadrants 3 4)")
                .put(PaoType.RFN530S4X, "kVAh Leading (Q1 + Q3)")
                .put(PaoType.RFN530S4X, "kVAr (Quadrants 1 3)")
                .put(PaoType.RFN530S4X, "kVAr (Quadrants 1 4)")
                .put(PaoType.RFN530S4X, "kVAr (Quadrants 2 3)")
                .put(PaoType.RFN530S4X, "kVAr (Quadrants 2 4)")
                .put(PaoType.RFN530S4X, "kVArh Leading (Q1 + Q3)")
                .build();
    }
}