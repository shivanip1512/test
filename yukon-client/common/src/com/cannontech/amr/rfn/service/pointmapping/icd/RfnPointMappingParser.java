package com.cannontech.amr.rfn.service.pointmapping.icd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.stream.Try;
import com.google.common.collect.Sets;

public class RfnPointMappingParser {
    public static Map<PaoType, Map<PointMapping, NameScale>> getPaoTypePoints(InputStream rfnPointMappingFile) throws JDOMException, IOException {
        
        Element rootElement = getRootElement(rfnPointMappingFile);
        
        //  iterate through pointGroups
        return rootElement.getChildren("pointGroup").stream()
                    .flatMap(rfnPointGroup -> {
                        Set<PaoType> paoTypes = getPaoTypes(rfnPointGroup);
                        
                        //  Get the map of points in the pointGroup
                        Map<PointMapping, NameScale> points =
                                rfnPointGroup.getChildren("point").stream()
                                    .collect(Collectors.toMap(
                                            RfnPointMappingParser::getPointMapping,
                                            RfnPointMappingParser::getNameScale));
                        
                        validateExcludedTypes(points, paoTypes);
                        
                        //  Associate each paoType with its own copy of the points
                        return paoTypes.stream()
                                .map(paoType -> Pair.of(paoType, new HashMap<>(points)));
                    })
                    .collect(Collectors.toMap(
                              Pair::getLeft, 
                              Pair::getRight, 
                              RfnPointMappingParser::combineMaps));
    }

    private static void validateExcludedTypes(Map<PointMapping, NameScale> points, Set<PaoType> paoTypes) {
        points.entrySet().stream()
            .filter(e -> !paoTypes.containsAll(e.getKey().getExcludedTypes()))
            .findFirst()
            .ifPresent(e -> { 
                Object invalidTypes = Sets.difference(e.getKey().getExcludedTypes(), paoTypes);
                throw new RuntimeException(e.getValue().getName() + " excludes " + invalidTypes + ", which is not included in pointGroup types " + paoTypes);
            });
    }

    private static <T, U> Map<T, U> combineMaps(Map<T, U> map1, Map<T, U> map2) {
        HashMap<T, U> combined = new HashMap<>(map1);
        combined.putAll(map2);
        return combined;
    }
    
    private static Element getRootElement(InputStream rfnPointMappingFile) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document configDoc = saxBuilder.build(rfnPointMappingFile);
        return configDoc.getRootElement();
    }

    private static Set<PaoType> getPaoTypes(Element rfnPointGroup) {
        return rfnPointGroup.getChildren("paoType").stream()
            .map(paoTypeElement -> paoTypeElement.getAttributeValue("value"))
            .map(PaoType::valueOf)
            .collect(Collectors.toSet());
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

    private static PointMapping getPointMapping(Element pointElement) {
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
