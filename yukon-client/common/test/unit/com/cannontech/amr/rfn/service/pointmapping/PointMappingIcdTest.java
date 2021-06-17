package com.cannontech.amr.rfn.service.pointmapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import com.cannontech.common.util.YamlParserUtils;

public class PointMappingIcdTest {

    @Test
    @Disabled
    public void asJson() throws IOException {

        ClassPathResource yukonPointMappingIcdYaml = new ClassPathResource("yukonPointMappingIcd.yaml");
        
        String icd = YamlParserUtils.parseToJson(yukonPointMappingIcdYaml.getInputStream());

        InputStream icdStream = this.getClass().getResource("icd.json").openStream();

        String icdExpected = IOUtils.toString(icdStream);

        assertEquals(icdExpected, icd);
    }
}
