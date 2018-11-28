package com.cannontech.amr.rfn.service.pointmapping;

import java.io.IOException;
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

import com.cannontech.common.pao.PaoType;

public class RfnPointMappingTest {
    
    private final static String mapping = "com/cannontech/amr/rfn/service/pointmapping/rfnPointMapping.xml";

    @Test
    public void detectDuplicateMappings() throws IOException, JDOMException {
        ClassPathResource rfnPointMappingXml = new ClassPathResource(mapping);
        SAXBuilder saxBuilder = new SAXBuilder();
        Document configDoc = saxBuilder.build(rfnPointMappingXml.getInputStream());

        configDoc.getRootElement().getChildren("pointGroup").stream()
            .flatMap(pointGroup -> {
                Set<PaoType> paoTypes = 
                    pointGroup.getChildren("paoType").stream()
                        .map(e -> e.getAttributeValue("value"))
                        .map(PaoType::valueOf)
                        .collect(Collectors.toSet());

                return pointGroup.getChildren("point").stream()
                        .map(UnitOfMeasureToPointMappingParser::createPointMapper)
                        .flatMap(pointMapper -> paoTypes.stream().map(paoType -> Pair.of(paoType, pointMapper)));
            })
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
}