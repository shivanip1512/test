package com.cannontech.web.dev;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
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
import com.cannontech.web.dev.PointMappingIcd.ElsterA3PointDefinition;
import com.cannontech.web.dev.PointMappingIcd.ModelPointDefinition;
import com.cannontech.web.dev.PointMappingIcd.Modifiers;
import com.cannontech.web.dev.PointMappingIcd.Named;
import com.cannontech.web.dev.PointMappingIcd.PointDefinition;
import com.cannontech.web.dev.PointMappingIcd.SentinelPointDefinition;
import com.cannontech.web.dev.PointMappingIcd.Units;
import com.cannontech.web.dev.PointMappingIcd.WaterNodePointDefinition;
import com.cannontech.web.security.annotation.CheckCparm;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableList;

@Controller
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class PointMappingIcdController {

    @Value("classpath:yukonPointMappingIcd.yaml") private Resource inputFile;
    @Autowired private RfnPointMappingDao rfnPointMappingDao;
    
    class NameScale {
        String name;
        double multiplier;
        PointDefinition basePoint;
        @Override
        public String toString() {
            return name + ":" + multiplier;
        }
        public PointDefinition getBasePoint() {
            if (basePoint == null) {
                basePoint = new PointDefinition();
            }
            return basePoint;
        }
        public void setBaseUom(Units unit) {
            getBasePoint().unit = unit;
        }
        public void addBaseModifier(Modifiers modifier) {
            getBasePoint().modifiers.add(modifier);
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
                    .map(e -> e.name + ":" + pointDescriber.apply(e.value))
                    .collect(Collectors.joining(",\r\n"));
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
                
                model.addAttribute("manufacturers", parsedIcd.meterModels.keySet().toString());
                
                String models = 
                        parsedIcd.meterModels.values().stream()
                            .flatMap(List::stream)
                            .map(m -> 
                                m.original + ":" + 
                                    Optional.ofNullable(m.mm)
                                        .map(RfnManufacturerModel::toString)
                                        .orElse("null"))
                            .collect(Collectors.joining(",\r\n"));
                
                model.addAttribute("models", models); 

                String units = 
                        parsedIcd.unitsOfMeasure.entrySet().stream()
                            .map(e -> e.getKey().toString() + ":" + e.getValue()) 
                            .collect(Collectors.joining(",\r\n"));
                
                model.addAttribute("units", units); 

                String metrics = 
                        parsedIcd.metricIds.entrySet().stream()
                            .map(e -> e.getKey() + ":" + e.getValue().name + 
                                "\r\n\t" + e.getValue().unit + " - " + e.getValue().modifiers) 
                            .collect(Collectors.joining(",\r\n"));
                
                model.addAttribute("metrics", metrics); 

                Function<PointDefinition, String> pointDescriber = e -> 
                        "\r\n\t" + e.unit.toString() + 
                        "\r\n\t" + e.modifiers.toString();

                Function<ModelPointDefinition, String> modelPointDescriber = e -> 
                        pointDescriber.apply(e) + 
                        "\r\n\t" + e.models.stream().map(m -> m.mm.toString()).collect(Collectors.joining(","));
                
                Function<ElsterA3PointDefinition, String> elsterA3PointDescriber = e -> 
                        modelPointDescriber.apply(e) + 
                        "\r\n\t" +
                            Optional.ofNullable(e.otherNames)
                                .map(names -> String.join(",", names))
                                .orElse("(no other names)");
                
                Function<SentinelPointDefinition, String> sentinelPointDescriber = e -> 
                        modelPointDescriber.apply(e) + 
                        "\r\n\t" + e.itronName;

                Function<WaterNodePointDefinition, String> waterNodePointDescriber = e -> 
                        pointDescriber.apply(e) + 
                        "\r\n\t" + e.metricId;
                
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
                
                try {
                    String rfnPointMapping =
                            parseRfnPointMappingXml().entrySet().stream()
                                .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                                .map(e -> e.getKey() + ":\n" + 
                                        e.getValue().entrySet().stream()
                                            .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                                            .map(ee -> ee.getKey() + ":" + ee.getValue())
                                            .collect(Collectors.joining("\n"))) 
                                .map(s -> s.replace("\n", "\n    "))
                                .collect(Collectors.joining("\n"));
                    
                    model.addAttribute("rfnPointMapping", rfnPointMapping);
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
        for (Element rfnDeviceElement : rootElement.getChildren()) {
            if (rfnDeviceElement.getName().equals("pointGroup")) {
                Set<PaoType> paoTypes = EnumSet.noneOf(PaoType.class);
                Map<PointDefinition, NameScale> points = new HashMap<>();
                
                Iterable<Element> pointGroupChildren = rfnDeviceElement.getChildren();
                
                for (Element pointGroupChild : pointGroupChildren) {
                    if (pointGroupChild.getName().equals("paoType")) {
                        String paoTypeStr = pointGroupChild.getAttributeValue("value");
                        PaoType paoType = PaoType.valueOf(paoTypeStr);
                        paoTypes.add(paoType);
                    } else if (pointGroupChild.getName().equals("point")) {
                        PointDefinition pd = new PointDefinition();
                        NameScale ns = new NameScale(); 
                        
                        ns.name = pointGroupChild.getAttribute("name").getValue();
                        Iterable<Element> pointChildren = pointGroupChild.getChildren();
                        for (Element pointChild : pointChildren) {
                            if (pointChild.getName().equals("uom")) {
                                pd.unit = PointMappingIcd.Units.getByCommonName(pointChild.getValue());
                            } else if (pointChild.getName().equals("multiplier")) {
                                ns.multiplier = Double.parseDouble(pointChild.getAttribute("value").getValue());
                            } else if (pointChild.getName().equals("modifiers")) {
                                Iterable<Element> modifiers = pointChild.getChildren();
                                for (Element modifier : modifiers) {
                                    if (modifier.getName().equals("modifier")) {
                                        pd.modifiers.add(PointMappingIcd.Modifiers.getByCommonName(modifier.getValue()));
                                    }
                                }
                            } else if (pointChild.getName().equals("baseUom")) {
                                ns.setBaseUom(PointMappingIcd.Units.getByCommonName(pointChild.getValue()));
                            } else if (pointChild.getName().equals("baseModifiers")) {
                                Iterable<Element> baseModifiers = pointChild.getChildren();
                                for (Element modifier : baseModifiers) {
                                    if (modifier.getName().equals("modifier")) {
                                        ns.addBaseModifier(PointMappingIcd.Modifiers.getByCommonName(modifier.getValue()));
                                    }
                                }
                            }
                        }
                        points.put(pd, ns);
                    }
                }
                paoTypes.forEach(paoType -> 
                    paoTypePoints
                        .computeIfAbsent(paoType, k -> new HashMap<>())
                        .putAll(points));
            } else {
                throw new RuntimeException("Unknown element type " + rfnDeviceElement); 
            }
        }
        
        return paoTypePoints;
    }
}
