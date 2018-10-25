package com.cannontech.web.dev.icd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.cannontech.common.pao.PaoType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class RfnPointMappingParser {
    public static Map<PaoType, Map<PointDefinition, PointInfo>> parseRfnPointMappingXml(InputStream rfnPointMappingFile) throws JDOMException, IOException {
        
        Map<PaoType, Map<PointDefinition, Map<PointDefinition, NameScale>>> paoTypePoints = Maps.newHashMap(); 
        
        SAXBuilder saxBuilder = new SAXBuilder();
        Document configDoc = saxBuilder.build(rfnPointMappingFile);

        Element rootElement = configDoc.getRootElement();
        //  iterate through pointGroups
        rootElement.getChildren("pointGroup").stream()
            .forEach(rfnPointGroup -> {
                Map<PointDefinition, Map<PointDefinition, NameScale>> coincidentPointMapping = 
                        rfnPointGroup.getChildren("point").stream()
                        .collect(Collectors.toMap(
                            //  Use the base point as the key, or null for non-coincident points.
                            pointElement -> {
                                Units baseUnit = getUnit(pointElement.getChildText("baseUom"));
                                if (baseUnit == null) {
                                    return null;
                                }
                                return new PointDefinition(baseUnit, extractModifiers(pointElement.getChild("baseModifiers")));
                            },
                            //  The value is a map of PointDefinition to NameScale
                            pointElement -> {
                                Units unit = getUnit(pointElement.getChildText("uom"));
                                Set<Modifiers> modifiers = extractModifiers(pointElement.getChild("modifiers"));
                                PointDefinition pd = new PointDefinition(unit, modifiers);
                                
                                String name = pointElement.getAttributeValue("name");
                                double multiplier = extractMultiplier(pointElement);
                                NameScale ns = new NameScale(name, multiplier);
                                
                                return ImmutableMap.of(pd, ns);
                            },
                            //  Combine multiple maps together - they cannot have repeated PointDefinition keys
                            (map1, map2) -> {
                                return ImmutableMap.<PointDefinition, NameScale>builder()
                                    .putAll(map1)
                                    .putAll(map2)
                                    .build();
                            }));

                rfnPointGroup.getChildren("paoType").stream()
                    .map(paoTypeElement -> paoTypeElement.getAttributeValue("value"))
                    .map(PaoType::valueOf)
                    .forEach(paoType -> {
                        Map<PointDefinition, Map<PointDefinition, NameScale>> paoTypeCoincidents = 
                                paoTypePoints.computeIfAbsent(paoType, k -> Maps.newHashMap());
                        
                        coincidentPointMapping.forEach((pointDefinition, coincidentPoints) ->
                            paoTypeCoincidents.computeIfAbsent(pointDefinition, k -> Maps.newHashMap())
                                              .putAll(coincidentPoints));
                    });
            });
        
        return Maps.transformValues(paoTypePoints, coincidentPointMapping -> 
            //  Get the non-coincident points (those with a null baseUom)
            coincidentPointMapping.get(null).entrySet().stream()
                    .collect(Collectors.toMap(
                        Entry::getKey,
                        e -> new PointInfo(
                                e.getValue(), 
                                //  Get the coincident points for the parent point
                                coincidentPointMapping.getOrDefault(e.getKey(), Collections.emptyMap())))));
    }

    private static double extractMultiplier(Element pointGroupChild) {
        return Optional.ofNullable(pointGroupChild.getChild("multipler"))
            .map(m -> m.getAttributeValue("value"))
            .map(Double::parseDouble)
            .orElse(1.0);
    }

    private static Units getUnit(String unitName) {
        return Optional.ofNullable(unitName)
            .map(Units::getByCommonName)
            .orElse(null);
    }

    private static Set<Modifiers> extractModifiers(Element modifier) {
        return Optional.ofNullable(modifier)
            .map(m -> m.getChildren("modifier").stream()
                        .map(Element::getValue)
                        .map(Modifiers::getByCommonName)
                        .collect(Collectors.toSet()))
            .orElse(null);
    }
}
