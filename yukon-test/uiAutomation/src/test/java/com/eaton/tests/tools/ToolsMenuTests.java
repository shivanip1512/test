package com.eaton.tests.tools;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;

public class ToolsMenuTests extends SeleniumTestSetup {

    private HomePage page;
    private static final int TOOLS_INDEX = 4;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.HOME);
        page = new HomePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TOOLS })
    public void toolsMenu_collectionActionsUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 0);
        
        assertThat(url).contains(Urls.Tools.COLLECTION_ACTIONS);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TOOLS })
    public void toolsMenu_commanderUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 1);

        assertThat(url).contains(Urls.Tools.COMMANDER);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TOOLS })
    public void toolsMenu_dataExportActionsUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 2);

        assertThat(url).contains(Urls.Tools.DATA_EXPORTER);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TOOLS })
    public void toolsMenu_dataStreamingUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 3);

        assertThat(url).contains(Urls.Tools.DATA_STREAMING);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TOOLS })
    public void toolsMenu_dataViewerUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 4);

        assertThat(url).contains(Urls.Tools.DATA_VIEWER);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TOOLS })
    public void toolsMenu_deviceConfigurationUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 5);

        assertThat(url).contains(Urls.Tools.DEVICE_CONFIGURATION);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TOOLS })
    public void toolsMenu_deviceGroupsUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 6);

        assertThat(url).contains(Urls.Tools.DEVICE_GROUP);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TOOLS })
    public void toolsMenu_schedulesUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 7);

        assertThat(url).contains(Urls.Tools.SCHEDULES);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TOOLS })
    public void toolsMenu_scriptsUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 8);

        assertThat(url).contains(Urls.Tools.SCRIPTS);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TOOLS })
    public void toolsMenu_trendsUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 9);

        assertThat(url).contains(Urls.Tools.TRENDS_LIST);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TOOLS })
    public void toolsMenu_reportsUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 10);

        assertThat(url).contains(Urls.Tools.REPORTS);
    }
}