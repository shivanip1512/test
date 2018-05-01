package com.cannontech.web.dev;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;

import com.cannontech.common.config.dao.RfnPointMappingDao;

public class PointMappingIcdControllerTest {

    @Test
    public void test_asJson() throws IOException {

        PointMappingIcdController controller = new PointMappingIcdController();

        ReflectionTestUtils.setField(controller, "inputFile", new ClassPathResource("yukonPointMappingIcd.yaml"));
        ReflectionTestUtils.setField(controller, "rfnPointMappingDao", new RfnPointMappingDao() {
            @Override
            public InputStream getPointMappingFile() {
                try {
                    return new ClassPathResource("rfnPointMapping.xml").getInputStream();
                } catch (IOException e) {
                    return null;
                }
            }
        });
        
        ModelMap model = new ModelMap();
        controller.view(model);
        Object icd = model.get("icd");

        assertTrue("ICD not String", icd instanceof String);
        
        InputStream icdStream = this.getClass().getResource("icd.json").openStream();

        String icdExpected = IOUtils.toString(icdStream);

        assertEquals(icdExpected, (String)icd);
    }
}
