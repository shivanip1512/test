package com.cannontech.amr.rfn.service.pointmapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
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
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class RfnPointMappingTest {
    
    private final static String mapping = "com/cannontech/amr/rfn/service/pointmapping/rfnPointMapping.xml";

    private Map<PaoType, Map<PointMapping, NameScale>> getRfnPointMapping() throws JDOMException, IOException {
        return RfnPointMappingParser.getPaoTypePoints(getRfnXmlStream());
    }

    private PointMappingIcd getPointMappingIcd() throws IOException {
        ClassPathResource yukonPointMappingIcdYaml = new ClassPathResource("yukonPointMappingIcd.yaml");
        
        PointMappingIcd icd = YukonPointMappingIcdParser.parse(yukonPointMappingIcdYaml.getInputStream());
        return icd;
    }

    private InputStream getRfnXmlStream() throws IOException
    {
        ClassPathResource rfnPointMappingXml = new ClassPathResource(mapping);
        return rfnPointMappingXml.getInputStream();
    }
    
    @Test
    public void testOrdered() throws IOException, JDOMException
    {
        //  Duplicate mappings will cause a parser error in RfnPointMappingParser's stream code, so detect those first
        detectDuplicateMappings();
        
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
        
        final int MaxPointCount = 10;

        Set<PointMapping> rpmMappedPoints = rpmMapped.get(true);
        Set<PointMapping> icdMappedPoints = icdMapped.get(true);
        Set<PointMapping> rpmUnmappedPoints = rpmMapped.get(false);
        Set<PointMapping> icdUnmappedPoints = icdMapped.get(false);
        
        verifyEmpty(type, rpmTypePoints, MaxPointCount, Sets.intersection(rpmUnmappedPoints, icdMappedPoints), "marked as unmapped in rfnPointMapping.xml, but exist in yukonPointMapping.yaml");
        verifyEmpty(type, icdTypePoints, MaxPointCount, Sets.intersection(rpmMappedPoints, icdUnmappedPoints), "marked as unmapped in yukonPointMapping.yaml, but exist in rfnPointMapping.xml");

        verifyEmpty(type, rpmTypePoints, MaxPointCount, Sets.intersection(rpmUnmappedPoints, icdUnmappedPoints), "marked as unmapped in both rfnPointMapping.xml and yukonPointMapping.yaml");

        verifyEmpty(type, rpmTypePoints, MaxPointCount, Sets.difference(rpmMappedPoints, icdMappedPoints), "not in yukonPointMappingIcd.yaml");
        verifyEmpty(type, icdTypePoints, MaxPointCount, Sets.difference(icdMappedPoints, rpmMappedPoints), "not in rfnPointMapping.xml");
        
        rfnPointMapping.remove(type);
    }

    private void verifyEmpty(PaoType type, Map<PointMapping, NameScale> typePoints, final int MaxPointCount,
            SetView<PointMapping> extraneous, String violation) {
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
    
}