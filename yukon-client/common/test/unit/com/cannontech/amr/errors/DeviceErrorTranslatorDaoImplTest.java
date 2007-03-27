package com.cannontech.amr.errors;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.amr.errors.dao.impl.DeviceErrorTranslatorDaoImpl;
import com.cannontech.amr.errors.model.DeviceErrorDescription;

public class DeviceErrorTranslatorDaoImplTest {
    private DeviceErrorTranslatorDaoImpl translator;

    @Before
    public void setUp() throws Exception {
        translator = new DeviceErrorTranslatorDaoImpl();
        ClassLoader classLoader = getClass().getClassLoader();
        String path = "com/cannontech/amr/errors/dao/impl/error-code.xml";
        InputStream resourceAsStream = classLoader.getResourceAsStream(path);
        translator.setErrorDefinitions(resourceAsStream);
        translator.initialize();
    }

    @Test
    public void testTraslateErrorCode() {
        DeviceErrorDescription description = translator.traslateErrorCode(15);
        assertNotNull("returned null data", description);
        assertNotNull("returned null description", description.getDescription());
        assertNotNull("returned null trouble", description.getTroubleshooting());
        
        
        description = translator.traslateErrorCode(23475845); // no way this is a real error!
        assertNotNull("returned null data", description);
        assertNotNull("returned null description", description.getDescription());
        assertNotNull("returned null trouble", description.getTroubleshooting());
        
    }


}
