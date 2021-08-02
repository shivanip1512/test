package com.cannontech.web.dev;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ModelMap;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;

public class DeveloperControllerTest {
    /**
     * DeveloperController uses reflection to populate two maps which it uses for the DbChange message
     * feature. Since we cannot predict when or if DBChangeMsg.java gets refactored we can use this test to
     * detect when it does.
     * 
     * This test will fail for one of two reasons. 1. DeveloperController.dbChangePage() method was changed or
     * the attributes 'databaseFields', 'categoryFields' were changed. 2. DBChangeMsg was finally refactored
     * and we need to fix the initialization of the two maps.
     */
    @Test
    public void test_dbChangeMaps() {

        ConfigurationSource configSource = createMock(ConfigurationSource.class);
        expect(configSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE)).andReturn(true);
        replay(configSource);

        DeveloperController controller = new DeveloperController(configSource);

        ModelMap model = new ModelMap();
        controller.dbChangePage(model);
        Object databaseFields = model.get("databaseFields");
        Object categoryFields = model.get("categoryFields");

        assertTrue(databaseFields != null, "Controller did not properly initialize databseFields");
        assertTrue(databaseFields instanceof Map,
                "'databaseFields' is no longer a map. This test probably needs to be updated");
        assertTrue(((Map<?, ?>) databaseFields).size() > 10, "'databaseFields' is empty. Was DBChangeMsg refactored?");
        assertTrue(categoryFields != null, "Controller did not properly initialize categoryFields");
        assertTrue(categoryFields instanceof Map, "'categoryFields' is no longer a map. This test probably needs to be updated");
        assertTrue(((Map<?, ?>) categoryFields).size() > 10, "'categoryFields' is empty. Was DBChangeMsg refactored?");
    }
}
