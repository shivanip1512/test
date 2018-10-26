package com.cannontech.web.dev;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jdom2.JDOMException;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import com.cannontech.common.pao.PaoType;
import com.cannontech.web.dev.icd.PointDefinition;
import com.cannontech.web.dev.icd.PointInfo;
import com.cannontech.web.dev.icd.PointMappingIcd;
import com.cannontech.web.dev.icd.RfnPointMappingParser;
import com.cannontech.web.dev.icd.YukonPointMappingIcdParser;

public class PointMappingIcdTest {

    @Test
    public void asJson() throws IOException {

        ClassPathResource yukonPointMappingIcdYaml = new ClassPathResource("yukonPointMappingIcd.yaml");
        
        String icd = YukonPointMappingIcdParser.parseToJson(yukonPointMappingIcdYaml.getInputStream());

        InputStream icdStream = this.getClass().getResource("icd.json").openStream();

        String icdExpected = IOUtils.toString(icdStream);

        assertEquals(icdExpected, icd);
    }

    @Test
    public void compareToRfnPointMappingXml() throws IOException, JDOMException {

        ClassPathResource yukonPointMappingIcdYaml = new ClassPathResource("yukonPointMappingIcd.yaml");
        //ClassPathResource rfnPointMappingXml = new ClassPathResource("com/cannontech/amr/rfn/service/pointmapping/rfnPointMapping.xml");
        
        PointMappingIcd icd = YukonPointMappingIcdParser.parse(yukonPointMappingIcdYaml.getInputStream());

        //Map<PaoType, Map<PointDefinition, PointInfo>> rfnPointMapping = RfnPointMappingParser.parseRfnPointMappingXml(rfnPointMappingXml.getInputStream());
    }
}
