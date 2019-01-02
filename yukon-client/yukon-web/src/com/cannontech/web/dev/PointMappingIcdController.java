package com.cannontech.web.dev;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.rfn.service.pointmapping.icd.ManufacturerModel;
import com.cannontech.amr.rfn.service.pointmapping.icd.ModelPointDefinition;
import com.cannontech.amr.rfn.service.pointmapping.icd.NameScale;
import com.cannontech.amr.rfn.service.pointmapping.icd.Named;
import com.cannontech.amr.rfn.service.pointmapping.icd.PointDefinition;
import com.cannontech.amr.rfn.service.pointmapping.icd.PointMapping;
import com.cannontech.amr.rfn.service.pointmapping.icd.CoincidentGroupingCollector;
import com.cannontech.amr.rfn.service.pointmapping.icd.ElsterA3PointDefinition;
import com.cannontech.amr.rfn.service.pointmapping.icd.PointMappingIcd;
import com.cannontech.amr.rfn.service.pointmapping.icd.RfnPointMappingParser;
import com.cannontech.amr.rfn.service.pointmapping.icd.SentinelPointDefinition;
import com.cannontech.amr.rfn.service.pointmapping.icd.WaterNodePointDefinition;
import com.cannontech.amr.rfn.service.pointmapping.icd.YukonPointMappingIcdParser;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.dao.RfnPointMappingDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.web.security.annotation.CheckCparm;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Controller
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class PointMappingIcdController {

    @Value("classpath:yukonPointMappingIcd.yaml") private Resource inputFile;
    @Autowired private RfnPointMappingDao rfnPointMappingDao;
    
    static final Collector<CharSequence, ?, String> COMMA_NEWLINE = Collectors.joining(",\n"); 
    static final String COLON = ":";
    static final String COLON_NEWLINE = ":\n";
    static final String NEWLINE = "\n";
    static final String NEWLINE_TAB = "\n\t";
    
    public static class ModelPointSupport {
        private String name;
        private Integer metric;
        private List<RfnManufacturerModel> models;
        public ModelPointSupport(String name, Integer metric, List<RfnManufacturerModel> models) {
            this.name = name;
            this.metric = metric;
            this.models = models;
        }
        public String getName() {
            return name;
        }
        public Integer getMetric() {
            return metric;
        }
        public List<RfnManufacturerModel> getModels() {
            return models;
        }
    };
    
    public static class PointSection {
        private String title;
        private String id;
        private String content;
        public static <T extends PointDefinition> PointSection of(String title, List<Named<T>> points, Function<T, String> describer) {
            PointSection ps = new PointSection();
            
            ps.title = title;
            ps.id = StringUtils.uncapitalize(
                        Arrays.stream(title.split("[ -]"))
                            .map(StringUtils::lowerCase)
                            .map(StringUtils::capitalize)
                            .collect(Collectors.joining())) + "Points";
            ps.content = describePoints(points, describer);
            
            return ps;
        }
        public String getTitle() {
            return title;
        }
        public String getId() {
            return id;
        }
        public String getContent() {
            return content;
        }
    }

    private static <T extends PointDefinition> String describePoints(List<Named<T>> points, Function<T, String> pointDescriber) {
        return points.stream()
                    .map(e -> e.name + COLON + pointDescriber.apply(e.value))
                    .collect(COMMA_NEWLINE);
    }
    
    public static class PointRow {
        private PointMapping pointMapping;
        private String rfnPointMapping;
        private String yukonPointMappingIcd;
        public PointMapping getPointMapping() {
            return pointMapping;
        }
        public void setPoint(PointMapping pd) {
            this.pointMapping = pd;
        }
        public String getRfnPointMapping() {
            return rfnPointMapping;
        }
        public void setRfnPointMapping(String rfnPointMapping) {
            this.rfnPointMapping = rfnPointMapping;
        }
        public String getYukonPointMappingIcd() {
            return yukonPointMappingIcd;
        }
        public void setYukonPointMappingIcd(String yukonPointMappingIcd) {
            this.yukonPointMappingIcd = yukonPointMappingIcd;
        }
    }
    
    @RequestMapping("/rfn/icd/view")
    public String view(ModelMap model) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {

        PointMappingIcd parsedIcd = YukonPointMappingIcdParser.parse(inputFile.getInputStream());

        model.addAttribute("icd", YukonPointMappingIcdParser.parseToJson(inputFile.getInputStream()));

        model.addAttribute("meterModels", parsedIcd.meterModels); 

        model.addAttribute("units", parsedIcd.unitsOfMeasure); 

        model.addAttribute("metrics", parsedIcd.metricIds); 

        Function<PointDefinition, String> pointDescriber = e -> 
                NEWLINE_TAB + e.getUnit().toString() + 
                NEWLINE_TAB + e.getModifiers().toString();

        Function<ModelPointDefinition, String> modelPointDescriber = e -> 
                pointDescriber.apply(e) + 
                NEWLINE_TAB + e.models.stream().map(m -> m.getManufacturerModel().toString()).collect(Collectors.joining(","));
        
        Function<ElsterA3PointDefinition, String> elsterA3PointDescriber = e -> 
                modelPointDescriber.apply(e) + 
                NEWLINE_TAB +
                    Optional.ofNullable(e.otherNames)
                        .map(names -> String.join(",", names))
                        .orElse("(no other names)");
        
        Function<SentinelPointDefinition, String> sentinelPointDescriber = e -> 
                modelPointDescriber.apply(e) + 
                NEWLINE_TAB + e.itronName;

        Function<WaterNodePointDefinition, String> waterNodePointDescriber = e -> 
                pointDescriber.apply(e) + 
                NEWLINE_TAB + e.metricId;
        
        List<PointSection> points = ImmutableList.of(
            PointSection.of("Centron",                   parsedIcd.itronCentronWithAdvancedMetrology,      modelPointDescriber),
            PointSection.of("Focus AX",                  parsedIcd.lgyrFocusAxRfn420WithAdvancedMetrology, pointDescriber),
            PointSection.of("Focus kWh",                 parsedIcd.lgyrFocusKwhWithAdvancedMetrology,      pointDescriber),
            PointSection.of("Elster A3",                 parsedIcd.elsterA3,                               elsterA3PointDescriber),
            PointSection.of("ELO",                       parsedIcd.elo,                                    modelPointDescriber),
            PointSection.of("Itron Sentinel",            parsedIcd.itronSentinel,                          sentinelPointDescriber),
            PointSection.of("RFN-500 Landis and Gyr S4", parsedIcd.lgyrS4_rfn500,                          modelPointDescriber),
            PointSection.of("RFN-500 Focus AX+RX",       parsedIcd.lgyrFocusAxRx_rfn500,                   modelPointDescriber),
            PointSection.of("Next Gen Water Node",       parsedIcd.nextGenWaterNode,                       waterNodePointDescriber));
        
        model.addAttribute("points", points);
        
        Map<? extends PointDefinition, Integer> metricLookup = parsedIcd.metricIds.entrySet().stream()
                .collect(Collectors.toMap(Entry::getValue, Entry::getKey));
        
        List<ModelPointSupport> centronxPoints = parsedIcd.itronCentronWithAdvancedMetrology.stream()
            .map(nmpd -> new ModelPointSupport(nmpd.name, metricLookup.get(nmpd.value), Lists.transform(nmpd.value.models, ManufacturerModel::getManufacturerModel)))
            .collect(Collectors.toList());
        
        model.addAttribute("centronxPoints", centronxPoints);
        
        Map<PointMapping, NameScale> icdPoints = parsedIcd.elsterA3.stream()
                .filter(mpd -> mpd.value.models.stream().anyMatch(m -> m.getManufacturerModel() == RfnManufacturerModel.RFN_430A3K))
                .collect(new CoincidentGroupingCollector());

        try {
            Map<PaoType, Map<PointMapping, NameScale>> rfnPointMapping = RfnPointMappingParser.getPaoTypePoints(rfnPointMappingDao.getPointMappingFile());
            
            Map<PointMapping, NameScale> rpm_Rfn430A3k = rfnPointMapping.get(PaoType.RFN430A3K);
            
            Function<NameScale, String> getPointName = pointInfo ->
                Optional.ofNullable(pointInfo).map(NameScale::getName).orElse("(missing)");
            
            List<PointRow> pointTable = 
                    Sets.union(rpm_Rfn430A3k.keySet(), icdPoints.keySet()).stream()
                        .sorted((pm1, pm2) -> pm1.compareTo(pm2))
                        .map(pd -> {
                            PointRow pr = new PointRow();
                            
                            pr.setPoint(pd);
                            
                            pr.setRfnPointMapping(getPointName.apply(rpm_Rfn430A3k.get(pd)));
                            
                            pr.setYukonPointMappingIcd(getPointName.apply(icdPoints.get(pd)));
                            
                            return pr;
                        })
                        .collect(Collectors.toList());
            
            model.addAttribute("pointTable", pointTable);
            
            String formattedRfnPointMapping =
                    rfnPointMapping.entrySet().stream()
                        .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                        .map(e -> e.getKey() + COLON_NEWLINE + 
                                e.getValue().entrySet().stream()
                                    .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                                    .map(ee -> 
                                        ee.getKey().getPointDefinition() + COLON + ee.getValue() 
                                        + Optional.ofNullable(ee.getKey().getBasePoint())
                                                .map(bp -> NEWLINE_TAB + bp.getUnit() + COLON + bp.getModifiers())
                                                .orElse(""))
                                    .collect(Collectors.joining(NEWLINE))) 
                        .map(s -> s.replace(NEWLINE, NEWLINE_TAB))
                        .collect(Collectors.joining(NEWLINE));
            
            model.addAttribute("rfnPointMapping", formattedRfnPointMapping);
        } catch (IOException e1) {
            model.addAttribute("rfnPointMapping", e1);
        } catch (JDOMException e1) {
            model.addAttribute("rfnPointMapping", e1);
        }
    
        return "rfn/pointMappingIcd.jsp";
    }
}
