package com.cannontech.web.dev;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yaml.snakeyaml.Yaml;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.dao.RfnPointMappingDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.web.dev.icd.ElsterA3PointDefinition;
import com.cannontech.web.dev.icd.Model;
import com.cannontech.web.dev.icd.ModelPointDefinition;
import com.cannontech.web.dev.icd.Modifiers;
import com.cannontech.web.dev.icd.Named;
import com.cannontech.web.dev.icd.PointDefinition;
import com.cannontech.web.dev.icd.PointMappingIcd;
import com.cannontech.web.dev.icd.SentinelPointDefinition;
import com.cannontech.web.dev.icd.Units;
import com.cannontech.web.dev.icd.WaterNodePointDefinition;
import com.cannontech.web.security.annotation.CheckCparm;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
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
    
    static class CoincidentPointDefinition extends PointDefinition {
        
        public CoincidentPointDefinition(Units u, Set<Modifiers> modifiers, PointDefinition basePoint) {
            this.unit = u;
            this.modifiers = modifiers;
            this.basePoint = basePoint;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!super.equals(obj))
                return false;
            if (getClass() != obj.getClass())
                return false;
            CoincidentPointDefinition other = (CoincidentPointDefinition) obj;
            return Objects.equals(basePoint, other.basePoint);
        }

        PointDefinition basePoint;
    };
    
    class NameScale {
        public NameScale(String name, double multiplier) {
            this.name = name;
            this.multiplier = multiplier;
        }
        String name;
        double multiplier;
        @Override
        public String toString() {
            return name + COLON + multiplier;
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
        private String point;
        private String rfnPointMapping;
        private String yukonPointMappingIcd;
        public String getPoint() {
            return point;
        }
        public void setPoint(String point) {
            this.point = point;
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
    public String view(ModelMap model) {

        try {
            Yaml y = new Yaml();
            
            Object yamlObject = y.load(inputFile.getInputStream());
            
            ObjectMapper jsonFormatter = new ObjectMapper();
            
            jsonFormatter.enable(SerializationFeature.INDENT_OUTPUT);
            
            model.addAttribute("icd", jsonFormatter.writeValueAsString(yamlObject));
            
            byte[] jsonBytes = jsonFormatter.writeValueAsBytes(yamlObject);
            
            try {
                jsonFormatter.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                
                PointMappingIcd parsedIcd = jsonFormatter.readValue(jsonBytes, PointMappingIcd.class);
                
                model.addAttribute("meterModels", parsedIcd.meterModels); 

                model.addAttribute("units", parsedIcd.unitsOfMeasure); 

                model.addAttribute("metrics", parsedIcd.metricIds); 

                Function<PointDefinition, String> pointDescriber = e -> 
                        NEWLINE_TAB + e.unit.toString() + 
                        NEWLINE_TAB + e.modifiers.toString();

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
                    PointSection.of("Centron",                   parsedIcd.centronWithAdvancedMetrology,  modelPointDescriber),
                    PointSection.of("Focus AX",                  parsedIcd.focusAxWithAdvancedMetrology,  pointDescriber),
                    PointSection.of("Focus kWh",                 parsedIcd.focusKwhWithAdvancedMetrology, pointDescriber),
                    PointSection.of("Elster A3",                 parsedIcd.elsterA3,                      elsterA3PointDescriber),
                    PointSection.of("ELO",                       parsedIcd.elo,                           pointDescriber),
                    PointSection.of("Itron Sentinel",            parsedIcd.itronSentinel,                 sentinelPointDescriber),
                    PointSection.of("RFN-500 Landis and Gyr S4", parsedIcd.rfn500LgyrS4,                  modelPointDescriber),
                    PointSection.of("RFN-500 Focus AX",          parsedIcd.rfn500LgyrFocusAx,             modelPointDescriber),
                    PointSection.of("Next Gen Water Node",       parsedIcd.nextGenWaterNode,              waterNodePointDescriber));
                
                model.addAttribute("points", points);
                
                List<ModelPointSupport> centronxPoints = parsedIcd.centronWithAdvancedMetrology.stream()
                    .map(nmpd -> new ModelPointSupport(nmpd.name, 42, Lists.transform(nmpd.value.models, Model::getManufacturerModel)))
                    .collect(Collectors.toList());
                
                model.addAttribute("centronxPoints", centronxPoints);
                
                Map<PointDefinition, NameScale> pointsS4eax = new HashMap<>();
                
                PointDefinition basePoint = null;
                
                for (Named<ModelPointDefinition> pointDefinition : parsedIcd.centronWithAdvancedMetrology) {
                    if (pointDefinition.value.models.stream().anyMatch(m -> m.getManufacturerModel() == RfnManufacturerModel.RFN_420CL)) {
                        Units u = pointDefinition.value.unit;
                        Set<Modifiers> modifiers = new HashSet<>(pointDefinition.value.modifiers);
                        PointDefinition pd;
                        if (modifiers.removeIf(Modifiers::isCoincident)) {
                            pd = new CoincidentPointDefinition(u, modifiers, basePoint);
                        } else {
                            pd = new PointDefinition();
                            pd.unit = u;
                            pd.modifiers = modifiers;
                        }
                        
                        pointsS4eax.put(pd, new NameScale(pointDefinition.name, 1.0));
                    }
                }
                
                try {
                    Map<PaoType, Map<PointDefinition, NameScale>> rfnPointMapping = parseRfnPointMappingXml();
                    
                    Map<PointDefinition, NameScale> rpmS4eax = rfnPointMapping.get(PaoType.RFN420CL);
                    
                    List<PointRow> pointTable = 
                            Sets.union(rpmS4eax.keySet(), pointsS4eax.keySet()).stream()
                                .sorted((pd1, pd2) -> pd1.compareTo(pd2))
                                .map(pd -> {
                                    PointRow pr = new PointRow();
                                    
                                    pr.setPoint(pd.toString());
                                    
                                    pr.setRfnPointMapping(Optional.ofNullable(rpmS4eax.get(pd)).map(NameScale::toString).orElse("(missing)"));
                                    
                                    pr.setYukonPointMappingIcd(Optional.ofNullable(pointsS4eax.get(pd)).map(NameScale::toString).orElse("(missing)"));
                                    
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
                                            .map(ee -> ee.getKey() + COLON + ee.getValue())
                                            .collect(Collectors.joining(NEWLINE))) 
                                .map(s -> s.replace(NEWLINE, NEWLINE_TAB))
                                .collect(Collectors.joining(NEWLINE));
                    
                    model.addAttribute("rfnPointMapping", formattedRfnPointMapping);
                } catch (IOException e1) {
                    model.addAttribute("rfnPointMapping", e1);
                } catch (JDOMException e1) {
                    model.addAttribute("rfnPointMapping", e1);
                }
                
            } catch (IOException e) {
                model.addAttribute("models", e);
            }
            
        } catch (IOException e) {
            model.addAttribute("icd", e);
        }

        return "rfn/pointMappingIcd.jsp";
    }
    
    Map<PaoType, Map<PointDefinition, NameScale>> parseRfnPointMappingXml() throws JDOMException, IOException {
        
        Map<PaoType, Map<PointDefinition, NameScale>> paoTypePoints = new HashMap<>(); 
        
        SAXBuilder saxBuilder = new SAXBuilder();
        Document configDoc = saxBuilder.build(rfnPointMappingDao.getPointMappingFile());

        Element rootElement = configDoc.getRootElement();
        //  iterate through pointGroups
        rootElement.getChildren("pointGroup").stream()
            .forEach(rfnDeviceElement -> {
                Map<PointDefinition, NameScale> points = 
                        rfnDeviceElement.getChildren("point").stream()
                            .map(pointGroupChild -> {

                                Units unit = getUnit(pointGroupChild.getChildText("uom"));
                                Units baseUnit = 
                                        Optional.ofNullable(pointGroupChild.getChildText("baseUom"))
                                            .map(this::getUnit)
                                            .orElse(null);
                                
                                Set<Modifiers> modifiers = extractModifiers(pointGroupChild.getChild("modifiers"));
                                Set<Modifiers> baseModifiers = 
                                        Optional.ofNullable(pointGroupChild.getChild("baseModifiers"))
                                            .map(this::extractModifiers)
                                            .orElse(null);

                                String name = pointGroupChild.getAttributeValue("name");
                                Double multiplier = 
                                        Optional.ofNullable(pointGroupChild.getChild("multipler"))
                                            .map(m -> m.getAttributeValue("value"))
                                            .map(Double::parseDouble)
                                            .orElse(1.0);

                                if (baseUnit != null) {
                                    PointDefinition basePoint = new PointDefinition();
                                    basePoint.unit = baseUnit;
                                    basePoint.modifiers = baseModifiers;
                                    return new SimpleEntry<>(new CoincidentPointDefinition(unit, modifiers, basePoint), new NameScale(name, multiplier));
                                } else {
                                    PointDefinition pd = new PointDefinition();
                                    pd.unit = unit;
                                    pd.modifiers = modifiers;
                                    return new SimpleEntry<>(pd, new NameScale(name, multiplier));
                                }
                            })
                            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
                
                rfnDeviceElement.getChildren("paoType").stream()
                    .map(e -> e.getAttributeValue("value"))
                    .map(PaoType::valueOf)
                    .forEach(paoType -> 
                        paoTypePoints
                            .computeIfAbsent(paoType, k -> new HashMap<>())
                            .putAll(points));
            });
        
        return paoTypePoints;
    }

    private Units getUnit(String unitName) {
        return Optional.ofNullable(unitName)
                .map(Units::getByCommonName)
                .orElse(null);
    }

    private Set<Modifiers> extractModifiers(Element modifier) {
        return modifier.getChildren("modifier").stream()
            .map(Element::getValue)
            .map(Modifiers::getByCommonName)
            .collect(Collectors.toSet());
    }
}
