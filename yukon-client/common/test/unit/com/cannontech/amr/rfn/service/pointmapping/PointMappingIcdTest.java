package com.cannontech.amr.rfn.service.pointmapping;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.cannontech.amr.rfn.service.pointmapping.icd.YukonPointMappingIcdParser;

public class PointMappingIcdTest {

    @Test
    @Ignore
    public void asJson() throws IOException {

        ClassPathResource yukonPointMappingIcdYaml = new ClassPathResource("yukonPointMappingIcd.yaml");
        
        String icd = YukonPointMappingIcdParser.parseToJson(yukonPointMappingIcdYaml.getInputStream());

        InputStream icdStream = this.getClass().getResource("icd.json").openStream();

        String icdExpected = IOUtils.toString(icdStream);

        assertEquals(icdExpected, icd);
    }
}
