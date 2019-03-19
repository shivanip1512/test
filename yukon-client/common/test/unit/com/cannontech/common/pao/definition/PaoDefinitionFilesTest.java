package com.cannontech.common.pao.definition;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.cannontech.common.pao.definition.loader.PaoConfigurationException;
import com.cannontech.common.pao.definition.loader.jaxb.Offset;
import com.cannontech.common.pao.definition.loader.jaxb.Point;
import com.cannontech.common.pao.definition.loader.jaxb.Points;
import com.google.common.collect.Maps;

public class PaoDefinitionFilesTest {

    private Resource pointsXsd;
    private static final String XML_CLASSPATH = "classpath*:pao/definition/**/*.xml";

    @Before
    public void setUp() {
        try (var ctx = new ClassPathXmlApplicationContext()) {
            ctx.getResource("classpath:pao/definition/pao.xsd");
            pointsXsd = ctx.getResource("classpath:pao/definition/points.xsd");
        }
    }

    @Test
    public void testPoints() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(XML_CLASSPATH);
        for (Resource resource : resources) {
            if (resource.getURL().getPath().contains("/points/")) {
                validatePointOffsets(resource);
            }
        }
    }
    
    private void validatePointOffsets(Resource resource) {

        String fileUrl = "";
        try {
            fileUrl = resource.getURL().toString();
            JAXBContext jaxbContext = JAXBContext.newInstance(Points.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            try (InputStream inputStream = resource.getInputStream()) {
                validateXmlSchema(inputStream, pointsXsd.getURL());
                Points points = (Points) unmarshaller.unmarshal(resource.getInputStream());

                BinaryOperator<Point> failOnDuplicatePointTypeOffset = (x, y) -> { 
                        Assert.fail(MessageFormat.format("Duplicate point type+offset for {0}, {1} {2}", resource, x.getType(), x.getOffset())); 
                        return null; 
                    };
                    
                /**
                 * Special case points to exclude from highestOffsets
                 */
                Predicate<Point> excludeOutlierPoints = p -> { 
                    switch (p.getType()) {
                    case "Status":
                        switch (p.getOffset()) {
                        case 1000:  //  Exclude Outage Status
                        case 3000:  //  Exclude Phase Status
                        case 9999:  //  Exclude Device Reset Indicator
                            return false;
                        }
                    }
                    return true;
                };

                Function<Point, String> getEffectivePointType = p -> {
                        if (p.getOffset() >= 20000) {
                            return "System" + p.getType();
                        } else if (p.getOffset() > 10000) {
                            switch (p.getType()) {
                            case "Analog":
                                return "AnalogOutput";
                            case "Status":
                                return "StatusOutput";
                            default:
                                return "Unexpected";
                            }
                        } else if (p.getOffset() >= 2000) {
                            return "System" + p.getType();
                        }
                        return p.getType();
                    };

                Map<String, Integer> calculatedMaxOffsetByType =
                        points.getPoint().stream()
                            .filter(excludeOutlierPoints)
                            .collect(
                                Collectors.groupingBy(
                                    getEffectivePointType, 
                                    Collectors.collectingAndThen(
                                        Collectors.toMap(
                                            Point::getOffset, 
                                            Function.identity(),
                                            failOnDuplicatePointTypeOffset),
                                        m -> m.keySet().stream()
                                            .reduce(Integer::max)
                                            .get())));
                
                var highestOffsets = points.getHighestOffsets();
                
                var rawOffsetsByType = Maps.<String, Offset>newHashMap();
                rawOffsetsByType.put("Analog", highestOffsets.getAnalog());
                rawOffsetsByType.put("AnalogOutput", highestOffsets.getAnalogOutput());
                rawOffsetsByType.put("CalcAnalog", highestOffsets.getCalcAnalog());
                rawOffsetsByType.put("CalcStatus", highestOffsets.getCalcStatus());
                rawOffsetsByType.put("DemandAccumulator", highestOffsets.getDemandAccumulator());
                rawOffsetsByType.put("PulseAccumulator", highestOffsets.getPulseAccumulator());
                rawOffsetsByType.put("Status", highestOffsets.getStatus());
                rawOffsetsByType.put("StatusOutput", highestOffsets.getStatusOutput());
                rawOffsetsByType.put("SystemAnalog", highestOffsets.getSystemAnalog());
                rawOffsetsByType.put("SystemPulseAccumulator", highestOffsets.getSystemPulseAccumulator());
                rawOffsetsByType.put("SystemStatus", highestOffsets.getSystemStatus());

                Map<String, Integer> declaredMaxOffsetByType =
                        Maps.transformValues(
                            Maps.filterValues(
                                rawOffsetsByType,
                                Objects::nonNull),
                            Offset::getOffset);
                
                Assert.assertEquals(resource + "\nhighestOffsets", mapToString(calculatedMaxOffsetByType), mapToString(declaredMaxOffsetByType));
            }
        } catch (IOException | JAXBException | SAXException | ParserConfigurationException e) {
            throw new PaoConfigurationException(
                "Unable to load file:" + fileUrl + ". File is located in yukon-shared project.", e);
        }
    }
    
    private String mapToString(Map<String, Integer> namedIntMap) {
        return namedIntMap.entrySet().stream()
                .sorted((a, b) -> a.getKey().compareTo(b.getKey()))
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", ", "{", "}"));
    }

    private void validateXmlSchema(InputStream stream, URL schemaUrl)
            throws IOException, SAXException, ParserConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false); // this is for DTD, so we want it off
        factory.setNamespaceAware(true);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(schemaUrl);
        factory.setSchema(schema);

        SAXParser saxParser = factory.newSAXParser();

        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setErrorHandler(new ErrorHandler() {

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void warning(SAXParseException exception) throws SAXException {
                throw exception;
            }
        });

        xmlReader.parse(new InputSource(stream));
    }
}
