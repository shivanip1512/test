package com.cannontech.web.dev;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.ui.ModelMap;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;

public class DeveloperControllerTest {
    /**
     * DeveloperController uses reflection to populate two maps which it uses for the DbChange message feature. Since
     * we cannot predict when or if DBChangeMsg.java gets refactored we can use this test to detect when it does.
     * 
     * This test will fail for one of two reasons. 1. DeveloperController.dbChangePage() method was changed or the
     * attributes 'databaseFields', 'categoryFields' were changed. 2. DBChangeMsg was finally refactored and we need
     * to fix the initialization of the two maps.
     */
    @Test
    public void test_dbChangeMaps() {
        
        ConfigurationSource configSource = createMock(ConfigurationSource.class);
        expect(configSource.getBoolean(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)).andReturn(true);
        replay(configSource);

        DeveloperController controller = new DeveloperController(configSource);
        
        ModelMap model = new ModelMap();
        controller.dbChangePage(model);
        @SuppressWarnings("unchecked")
        Map<String, Integer> databaseFields = (Map<String, Integer>) model.get("databaseFields");
        @SuppressWarnings("unchecked")
        Map<String, String>  categoryFields = (Map<String, String> ) model.get("categoryFields");
        
        Assert.assertTrue("Controller did not properly initialize databseFields" , databaseFields != null);
        Assert.assertTrue("'databaseFields' is empty. Was DBChangeMsg refactored?" , databaseFields.size() > 10);
        Assert.assertTrue("Controller did not properly initialize categoryFields" , categoryFields != null);
        Assert.assertTrue("'categoryFields' is empty. Was DBChangeMsg refactored?" , categoryFields.size() > 10);
    }
}
