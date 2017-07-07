package com.cannontech.common.rfn.dataStreaming;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.dataStreaming.DataStreamingAttributeHelper.DataStreamingPaoAttributes;
import com.google.common.collect.Maps;

public class DataStreamingAttributeHelperTest {
    private static Map<PaoType, Map<BuiltInAttribute, String>> attrToPointMap;

    @Before
    public void setUp() throws Exception {
        attrToPointMap = new HashMap<>();
    }
    
    @Test
    public void testInOrder() throws Exception {
        testDeviceTypeXMLs();
        testSupportedPointsRfnXML();
        testSupportedPointsRfnMappingXML();
    }

    public void testConsistentPaoTypes() {
        EnumMap<PaoType, Set<BuiltInAttribute>> typeToSupportedAttributes = Maps.newEnumMap(PaoType.class);
        for (DataStreamingPaoAttributes dspa : DataStreamingPaoAttributes.values()) {
            Collection<BuiltInAttribute> existingAttributes = typeToSupportedAttributes.get(dspa.getPaoType());
            if (existingAttributes != null) {
                Assert.assertEquals("Pao type " + dspa.getPaoType() + " not consistent when adding " + dspa, existingAttributes, dspa.getSupportedAttributes());
            } else {
                typeToSupportedAttributes.put(dspa.getPaoType(), dspa.getSupportedAttributes());
            }
        }
    }

    //Validates points against specific device type xmls
    private void testDeviceTypeXMLs() {
        for (DataStreamingPaoAttributes dspa : DataStreamingPaoAttributes.values()) {
            String deviceTypeName = dspa.getPaoType().name();
            Set<BuiltInAttribute> builtInAttributesSet = dspa.getSupportedAttributes();
            Set<String> pointSet = new HashSet<>();
            Map<BuiltInAttribute, String> mapDevice = new HashMap<BuiltInAttribute, String>();
            try {
                File fXmlFile =
                    new File("../yukon-shared/src/main/resources/pao/definition/meter/" + deviceTypeName + ".xml");
                System.out.println("Compare DataStreamingAttributeHelper with " + deviceTypeName + ".xml Starts");
                System.out.println("==========================================================================================================");
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);
                // optional, but recommended
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("pao");

                for (int idx = 0; idx < nList.getLength(); idx++) {

                    Node nNode = nList.item(idx);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        // Points
                        NodeList pointInfosList = eElement.getElementsByTagName("pointInfos");
                        for (int pointIdx = 0; pointIdx < pointInfosList.getLength(); pointIdx++) {
                            Node pointNode = pointInfosList.item(pointIdx);
                            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element pointInfosElement = (Element) pointNode;
                                // Points
                                NodeList pointInfoList = pointInfosElement.getElementsByTagName("pointInfo");
                                for (int pointInfoIdx = 0; pointInfoIdx < pointInfoList.getLength(); pointInfoIdx++) {
                                    Node pointInfoNode = pointInfoList.item(pointInfoIdx);
                                    if (pointInfoNode.getNodeType() == Node.ELEMENT_NODE) {
                                        Element pointInfoElement = (Element) pointInfoNode;
                                        List<String> attrs =
                                            Arrays.asList(pointInfoElement.getAttribute("attributes").split(","));
                                        pointSet.addAll(attrs);
                                        for (String attribute : attrs) {
                                            if (StringUtils.isNotEmpty(attribute)) {
                                                try {
                                                    if (mapDevice.get(BuiltInAttribute.valueOf(attribute)) == null) {
                                                        mapDevice.put(BuiltInAttribute.valueOf(attribute),
                                                            pointInfoElement.getAttribute("name"));
                                                    }
                                                } catch (IllegalArgumentException e) {
                                                    System.out.println("***** BuiltInAttribute not found *****:"
                                                        + attribute);
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }

                    }

                }

                builtInAttributesSet.forEach(entry -> {
                    BuiltInAttribute builtInAttribute = (BuiltInAttribute) entry;
                    if (!pointSet.contains(builtInAttribute.name())) {
                        System.out.println("***** Point not found: *****" + builtInAttribute.getDescription());
                    }
                });
                attrToPointMap.put(dspa.getPaoType(), mapDevice);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        System.out.println("----------------------------------------------------------------------------------");
    }

    //Validates points against rfnPointMapping.xml
    private void testSupportedPointsRfnMappingXML() {
        Map<String, Set<String>> map = new HashMap<>();
        Set<String> pointSet = null;
        try {

            File fXmlFile = new File("../common/src/com/cannontech/amr/rfn/service/pointmapping/rfnPointMapping.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            // optional, but recommended
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("pointGroup");

            for (int idx = 0; idx < nList.getLength(); idx++) {
                Node nNode = nList.item(idx);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    // Points
                    pointSet = new HashSet<>();
                    NodeList pointList = eElement.getElementsByTagName("point");
                    for (int pointIdx = 0; pointIdx < pointList.getLength(); pointIdx++) {
                        Node pointNode = pointList.item(pointIdx);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                            Element pointElement = (Element) pointNode;

                            pointSet.add(pointElement.getAttribute("name"));
                        }
                    }
                    NodeList paoTypeList = eElement.getElementsByTagName("paoType");
                    for (int paoIdx = 0; paoIdx < paoTypeList.getLength(); paoIdx++) {
                        Node paoNode = paoTypeList.item(paoIdx);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                            Element paoElement = (Element) paoNode;

                            Set<String> existingPoints =
                                map.get(paoElement.getAttribute("value")) != null
                                    ? map.get(paoElement.getAttribute("value")) : new HashSet<>();
                            existingPoints.addAll(pointSet);
                            map.put(paoElement.getAttribute("value"), existingPoints);
                        }
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Compare DataStreamingAttributeHelper with rfnMapping.xml Starts");
        System.out.println("==========================================================================================================");
        for (DataStreamingPaoAttributes dspa : DataStreamingPaoAttributes.values()) {
            Map<BuiltInAttribute, String> testMap = attrToPointMap.get(dspa.getPaoType());
            Set<BuiltInAttribute> builtInAttributesSet = dspa.getSupportedAttributes();
            Set<String> rfnMappingSet = map.get(dspa.getPaoType().name());
            builtInAttributesSet.forEach(entry -> {
                BuiltInAttribute builtInAttribute = (BuiltInAttribute) entry;
                if (rfnMappingSet == null || testMap == null) {
                    System.out.println("***** Pao Type ***** ::" + dspa.getPaoType() + "Point not found:"
                        + builtInAttribute.getDescription());
                } else if (!rfnMappingSet.contains(testMap.get(builtInAttribute)))
                    System.out.println("***** Pao Type ***** ::" + dspa.getPaoType() + "Point not found:"
                        + builtInAttribute.getDescription());
            });
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
    }

  //Validates points against RFN.xml
    private void testSupportedPointsRfnXML() {
        Set<String> pointSet = new HashSet<>();
        try {
            File fXmlFile = new File("../yukon-shared/src/main/resources/pao/definition/meter/points/RFN.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            // optional, but recommended
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("points");

            for (int idx = 0; idx < nList.getLength(); idx++) {
                Node nNode = nList.item(idx);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    // Points
                    NodeList pointList = eElement.getElementsByTagName("point");
                    for (int pointIdx = 0; pointIdx < pointList.getLength(); pointIdx++) {
                        Node pointNode = pointList.item(pointIdx);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element pointElement = (Element) pointNode;
                            pointSet.add(pointElement.getElementsByTagName("name").item(0).getTextContent());
                        }
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Compare DataStreamingAttributeHelper with RFN.xml Starts");
        System.out.println("==========================================================================================================");
        for (DataStreamingPaoAttributes dspa : DataStreamingPaoAttributes.values()) {
            Set<BuiltInAttribute> builtInAttributesSet = dspa.getSupportedAttributes();
            Map<BuiltInAttribute, String> testMap = attrToPointMap.get(dspa.getPaoType());
            builtInAttributesSet.forEach(entry -> {
                BuiltInAttribute builtInAttribute = (BuiltInAttribute) entry;
                if (pointSet == null || testMap == null) {
                    System.out.println("***** Pao Type *****::" + dspa.getPaoType() + "\nPoint not found:"
                        + builtInAttribute.getDescription());
                } else if (!pointSet.contains(testMap.get(builtInAttribute))) {
                    System.out.println("***** Pao Type *****::" + dspa.getPaoType() + "\nPoint not found:"
                        + builtInAttribute.getDescription());
                }
            });
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
    }

}
