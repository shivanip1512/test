package com.cannontech.amr.errors;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.amr.errors.dao.impl.DeviceErrorTranslatorDaoImpl;
import com.cannontech.amr.errors.model.DeviceErrorDescription;

public class DeviceErrorTranslatorDaoImplTest {
    private DeviceErrorTranslatorDaoImpl translator;

    @Before
    public void setUp() throws Exception {
        try {
            translator = new DeviceErrorTranslatorDaoImpl();
            ClassLoader classLoader = getClass().getClassLoader();
            String path = "com/cannontech/amr/errors/dao/impl/error-code.xml";
            InputStream resourceAsStream = classLoader.getResourceAsStream(path);
            translator.setErrorDefinitions(resourceAsStream);
            translator.initialize();
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        }
    }

    @Test
    public void testTraslateErrorCode() {
        try {
            DeviceErrorDescription description = translator.translateErrorCode(15);
            assertNotNull("returned null data", description);
            assertNotNull("returned null description", description.getDescription());
            assertNotNull("returned null trouble", description.getTroubleshooting());

            description = translator.translateErrorCode(23475845); // no way this is a real error!
            assertNotNull("returned null data", description);
            assertNotNull("returned null description", description.getDescription());
            assertNotNull("returned null trouble", description.getTroubleshooting());
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            throw exception;
        }
    }
}
