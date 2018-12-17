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
import com.cannontech.amr.rfn.service.pointmapping.icd.ElsterA3PointDefinition;
import com.cannontech.amr.rfn.service.pointmapping.icd.PointMappingIcd;
import com.cannontech.amr.rfn.service.pointmapping.icd.RfnPointMappingParser;
import com.cannontech.amr.rfn.service.pointmapping.icd.SentinelPointDefinition;
import com.cannontech.amr.rfn.service.pointmapping.icd.WaterNodePointDefinition;
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

        compareItronCentron(icd, rfnPointMapping);

        compareLgyrFocus400(icd, rfnPointMapping);

        compareElsterA3(icd, rfnPointMapping);
        
        compareLgyrFocusKwh(icd, rfnPointMapping);
        
        compareElo(icd, rfnPointMapping);

        compareItronSentinel(icd, rfnPointMapping);
        
        compareLgyrFocusAxRx500(icd, rfnPointMapping);

        compareLgyrS4(icd, rfnPointMapping);

        compareWaterNode(icd, rfnPointMapping);

        //  Gas node not mapped in yukonPointMapping
        rfnPointMapping.remove(PaoType.RFG201);

        //  RFN-430kV unmapped in yukonPointMapping
        rfnPointMapping.remove(PaoType.RFN430KV);

        
        rfnPointMapping.entrySet().stream()
            .forEach(e -> Assert.fail(e.getKey() + " has " + e.getValue().size() + " unmapped points." 
                                      + "\nFirst entry: " + Iterables.getFirst(e.getValue().entrySet(), null)));
    }

    private void compareWaterNode(PointMappingIcd icd, Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping) {
        List<Named<WaterNodePointDefinition>> points = icd.nextGenWaterNode;
        
        comparePoints(points, rfnPointMapping, PaoType.RFW201);
        
        //  1st gen water meter not mapped in yukonPointMapping
        rfnPointMapping.remove(PaoType.RFWMETER);
    }

    private void compareLgyrS4(PointMappingIcd icd, Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping) {
        List<Named<ModelPointDefinition>> points = icd.lgyrS4_rfn500;
        
        for (PaoType type : getPaoTypesForMeterClass(icd, MeterClass.LGYR_S4)) {
            compareModelPoints(points, rfnPointMapping, type);
        }
    }

    private void compareLgyrFocusAxRx500(PointMappingIcd icd, Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping) {
        List<Named<ModelPointDefinition>> points = icd.lgyrFocusAxRx_rfn500;
        
        for (PaoType type : getPaoTypesForMeterClass(icd, MeterClass.LGYR_FOCUS_AX_RX_500)) {
            compareModelPoints(points, rfnPointMapping, type);
        }
    }

    private void compareItronSentinel(PointMappingIcd icd, Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping) {
        List<Named<SentinelPointDefinition>> points = icd.itronSentinel;
        
        for (PaoType type : getPaoTypesForMeterClass(icd, MeterClass.ITRON_SENTINEL)) {
            compareModelPoints(points, rfnPointMapping, type);
        }
    }

    private void compareElo(PointMappingIcd icd, Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping) {
        List<Named<ModelPointDefinition>> points = icd.elo;
        
        for (PaoType type : getPaoTypesForMeterClass(icd, MeterClass.ELO)) {
            compareModelPoints(points, rfnPointMapping, type);
        }
    }

    private void compareLgyrFocusKwh(PointMappingIcd icd, Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping) {
        List<Named<PointDefinition>> points = icd.lgyrFocusKwhWithAdvancedMetrology;
        
        for (PaoType type : getPaoTypesForMeterClass(icd, MeterClass.LGYR_FOCUS_KWH)) {
            comparePoints(points, rfnPointMapping, type);
        }

        //  RFN-410FL (Focus kWh in RFN-410) not mapped in yukonPointMapping
        rfnPointMapping.remove(PaoType.RFN410FL);
    }

    private void compareElsterA3(PointMappingIcd icd, Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping) {
        List<Named<ElsterA3PointDefinition>> points = icd.elsterA3;
        
        for (PaoType type : getPaoTypesForMeterClass(icd, MeterClass.ELSTER_A3)) {
            compareModelPoints(points, rfnPointMapping, type);
        }
    }

    private void compareLgyrFocus400(PointMappingIcd icd, Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping) {
        List<Named<PointDefinition>> points = icd.lgyrFocusAxRfn420WithAdvancedMetrology;
        
        for (PaoType type : getPaoTypesForMeterClass(icd, MeterClass.LGYR_FOCUS_AX)) {
            comparePoints(points, rfnPointMapping, type);
        }
        
        //  RFN-410 Focus AX models not mapped in yukonPointMapping
        rfnPointMapping.remove(PaoType.RFN410FX);
        rfnPointMapping.remove(PaoType.RFN410FD);
        rfnPointMapping.remove(PaoType.RFN410FD);

        //  RFN-420 Focus RX models not mapped in yukonPointMapping
        rfnPointMapping.remove(PaoType.RFN420FRX);
        rfnPointMapping.remove(PaoType.RFN420FRD);
    }

    private void compareItronCentron(PointMappingIcd icd, Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping) {
        List<Named<ModelPointDefinition>> points = icd.itronCentronWithAdvancedMetrology;
        
        Map<PointMapping, NameScale> rfn420cl = rfnPointMapping.get(PaoType.RFN420CL);
        Map<PointMapping, NameScale> rfn420cd = rfnPointMapping.get(PaoType.RFN420CD);
        
        assertEquals(rfn420cl, rfn420cd);
        
        compareModelPoints(points, rfnPointMapping, PaoType.RFN410CL);
        compareModelPoints(points, rfnPointMapping, PaoType.RFN420CL);

        rfnPointMapping.remove(PaoType.RFN420CD);  //  already tested via the 420CL
    }

    private Set<PaoType> getPaoTypesForMeterClass(PointMappingIcd icd, MeterClass meterClass) {
        return icd.meterModels.get(meterClass).stream().map(ManufacturerModel::getManufacturerModel).map(RfnManufacturerModel::getType).collect(Collectors.toSet());
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
        
        Set<PointMapping> rpmMappedPoints = Sets.filter(rpmTypePoints.keySet(), p -> p.isMappedFor(type));

        Set<PointMapping> icdMappedPoints = Sets.filter(icdTypePoints.keySet(), p -> p.isMappedFor(type));
        
        final int MaxPointCount = 10;
        
        SetView<PointMapping> rpmExtra = Sets.difference(rpmMappedPoints, icdMappedPoints);

        if (!rpmExtra.isEmpty()) {
            Assert.fail(type + " has " + rpmExtra.size() + " point(s) not in yukonPointMappingIcd.yaml, first " + MaxPointCount + " points:\n" + 
                    rpmExtra.stream()
                        .limit(MaxPointCount)
                        .map(cp -> rpmTypePoints.get(cp).getName() + "\n        " + cp)
                        .collect(Collectors.joining("\n")));
        }
        
        SetView<PointMapping> icdExtra = Sets.difference(icdMappedPoints, rpmMappedPoints);

        if (!icdExtra.isEmpty()) {
            Assert.fail(type + " has " + icdExtra.size() + " point(s) not in rfnPointMapping.xml, first " + MaxPointCount + " points:\n" + 
                    icdExtra.stream()
                        .limit(MaxPointCount)
                        .map(cp -> icdTypePoints.get(cp).getName() + "\n        " + cp)
                        .collect(Collectors.joining("\n")));
        }
        
        rfnPointMapping.remove(type);
    }
    
}