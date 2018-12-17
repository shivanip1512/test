package com.cannontech.amr.rfn.service.pointmapping.icd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.stream.Try;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class RfnPointMappingParser {
    public static Map<PaoType, Map<PointDefinition, NameScaleCoincidents>> getPaoTypePointsMappedToCoincidents(InputStream rfnPointMappingFile) throws JDOMException, IOException {
        
        Element rootElement = getRootElement(rfnPointMappingFile);
        
        //  iterate through pointGroups
        Map<PaoType, Map<BasePointDefinition, Map<PointDefinition, NameScale>>> paoTypePoints = 
                rootElement.getChildren("pointGroup").stream()
                    .flatMap(rfnPointGroup -> {
                        List<PaoType> paoTypes = getPaoTypes(rfnPointGroup);
                        
                        //  Get the map of points in the pointGroup
                        Map<BasePointDefinition, Map<PointDefinition, NameScale>> points =
                                rfnPointGroup.getChildren("point").stream()
                                    .collect(Collectors.toMap(
                                            //  Use the base point as the key, or null for non-coincident points.
                                            RfnPointMappingParser::getBasePoint,
                                            //  The value is a map of PointDefinition to NameScale
                                            RfnPointMappingParser::getPointDefinitionToNameScale,
                                            RfnPointMappingParser::combineMaps));
                        
                        //  Associate each paoType with its own copy of the points
                        return paoTypes.stream()
                                .map(paoType -> Pair.of(paoType, copyOf(points)));
                    })
                    .collect(Collectors.toMap(
                              Pair::getLeft, 
                              Pair::getRight, 
                              RfnPointMappingParser::combineMapOfMaps));
        
        return Maps.transformValues(paoTypePoints, coincidentPointMapping -> 
            //  Get the non-coincident points (those with a null baseUom)
            coincidentPointMapping.get(null).entrySet().stream()
                    .collect(Collectors.toMap(
                        Entry::getKey,
                        e -> new NameScaleCoincidents(
                                e.getValue(), 
                                //  Get the coincident points for the parent point
                                coincidentPointMapping.getOrDefault(e.getKey(), Collections.emptyMap())))));
    }

    public static Map<PaoType, Map<PointMapping, NameScale>> getPaoTypePoints(InputStream rfnPointMappingFile) throws JDOMException, IOException {
        
        Element rootElement = getRootElement(rfnPointMappingFile);
        
        //  iterate through pointGroups
        return rootElement.getChildren("pointGroup").stream()
                    .flatMap(rfnPointGroup -> {
                        List<PaoType> paoTypes = getPaoTypes(rfnPointGroup);
                        
                        //  Get the map of points in the pointGroup
                        Map<PointMapping, NameScale> points =
                                rfnPointGroup.getChildren("point").stream()
                                    .collect(Collectors.toMap(
                                            RfnPointMappingParser::getNormalizedPoint,
                                            RfnPointMappingParser::getNameScale));
                        
                        //  Associate each paoType with its own copy of the points
                        return paoTypes.stream()
                                .map(paoType -> Pair.of(paoType, new HashMap<>(points)));
                    })
                    .collect(Collectors.toMap(
                              Pair::getLeft, 
                              Pair::getRight, 
                              RfnPointMappingParser::combineMaps));
    }

    private static <T, U, V> Map<T, Map<U, V>> copyOf(Map<T, Map<U, V>> original) {
        return original.entrySet().stream()
                    .collect(Collectors.toMap(
                            Entry::getKey, 
                            e -> new HashMap<U, V>(e.getValue())));
    }
    
    private static <T, U> Map<T, U> combineMaps(Map<T, U> map1, Map<T, U> map2) {
        HashMap<T, U> combined = new HashMap<T, U>(map1);
        combined.putAll(map2);
        return combined;
    }
    
    private static <T, U, V> Map<T, Map<U, V>> combineMapOfMaps(Map<T, Map<U, V>> map1, Map<T, Map<U, V>> map2) {
        Map<U, V> nullMap2 = map2.remove(null);
        
        map1.putAll(map2);
        
        if (nullMap2 != null) {
            Map<U, V> nullMap1 = map1.get(null);
            
            if (nullMap1 != null) {
                nullMap1.putAll(nullMap2);
            } else {
                map1.put(null, nullMap2);
            }
        }
        
        return map1;
    }

    private static Element getRootElement(InputStream rfnPointMappingFile) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document configDoc = saxBuilder.build(rfnPointMappingFile);
        return configDoc.getRootElement();
    }

    private static List<PaoType> getPaoTypes(Element rfnPointGroup) {
        return rfnPointGroup.getChildren("paoType").stream()
            .map(paoTypeElement -> paoTypeElement.getAttributeValue("value"))
            .map(PaoType::valueOf)
            .collect(Collectors.toList());
    }

    private static Map<PointDefinition, NameScale> getPointDefinitionToNameScale(Element pointElement) {
        return ImmutableMap.of(getPointDefinition(pointElement), 
                               getNameScale(pointElement));
    }

    private static NameScale getNameScale(Element pointElement) {
        return new NameScale(getName(pointElement), 
                             getMultiplier(pointElement));
    }

    private static PointDefinition getPointDefinition(Element pointElement) {
        Units unit = getUnit(pointElement.getChildText("uom"));
        Set<Modifiers> modifiers = getModifiers(pointElement.getChild("modifiers"));
        boolean mapped = isMapped(pointElement);
        boolean coincident = isCoincident(pointElement);
        return new PointDefinition(unit, modifiers, mapped, coincident);
    }
    
    private static boolean isCoincident(Element pointElement) {
        return pointElement.getChild("baseUom") != null;
    }

    private static PointMapping getNormalizedPoint(Element pointElement) {
        return new PointMapping(getPointDefinition(pointElement), 
                                   getBasePoint(pointElement),
                                   getExcludedTypes(pointElement));
    }
    
    private static Set<PaoType> getExcludedTypes(Element pointElement) {
        return Optional.ofNullable(pointElement.getAttribute("icdExcludedTypes"))
                .map(Attribute::getValue)
                .map(s -> s.split(","))
                .map(Arrays::asList)
                .orElse(Collections.emptyList())
                .stream()
                .map(PaoType::valueOf)
                .collect(Collectors.toSet());
    }

    private static boolean isMapped(Element pointElement) {
        return Optional.ofNullable(pointElement.getAttribute("icd"))
                .flatMap(a -> Try.of(a::getBooleanValue).getOptional())
                .orElse(true);
    }

    private static BasePointDefinition getBasePoint(Element pointElement) {
        return Optional.ofNullable(getUnit(pointElement.getChildText("baseUom")))
                .map(baseUnit -> new BasePointDefinition(baseUnit, getModifiers(pointElement.getChild("baseModifiers"))))
                .orElse(null);
    }

    private static String getName(Element pointElement) {
        return pointElement.getAttributeValue("name");
    }

    private static double getMultiplier(Element pointElement) {
        return Optional.ofNullable(pointElement.getChild("multiplier"))
            .map(m -> m.getAttributeValue("value"))
            .map(Double::parseDouble)
            .orElse(1.0);
    }

    private static Units getUnit(String unitName) {
        return Optional.ofNullable(unitName)
            .map(Units::getByCommonName)
            .orElse(null);
    }

    private static Set<Modifiers> getModifiers(Element modifier) {
        return Optional.ofNullable(modifier)
            .map(m -> m.getChildren("modifier").stream()
                        .map(Element::getValue)
                        .map(Modifiers::getByCommonName)
                        .collect(Collectors.toSet()))
            .orElse(null);
    }
}
