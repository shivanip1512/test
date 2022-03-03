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

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TOOLS })
    public void toolsMenu_CollectionActions_UrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 0);
        
        assertThat(url).contains(Urls.Tools.COLLECTION_ACTIONS);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TOOLS })
    public void toolsMenu_Commander_UrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 1);

        assertThat(url).contains(Urls.Tools.COMMANDER);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TOOLS })
    public void toolsMenu_DataExportActions_UrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 2);

        assertThat(url).contains(Urls.Tools.DATA_EXPORTER);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TOOLS })
    public void toolsMenu_DataStreaming_UrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 3);

        assertThat(url).contains(Urls.Tools.DATA_STREAMING);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TOOLS })
    public void toolsMenu_DataViewer_UrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 4);

        assertThat(url).contains(Urls.Tools.DATA_VIEWER);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TOOLS })
    public void toolsMenu_DeviceConfiguration_UrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 5);

        assertThat(url).contains(Urls.Tools.DEVICE_CONFIGURATION);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TOOLS })
    public void toolsMenu_DeviceGroups_UrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 6);

        assertThat(url).contains(Urls.Tools.DEVICE_GROUP);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TOOLS })
    public void toolsMenu_Schedules_UrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 7);

        assertThat(url).contains(Urls.Tools.SCHEDULES);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TOOLS })
    public void toolsMenu_Scripts_UrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 8);

        assertThat(url).contains(Urls.Tools.SCRIPTS);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TOOLS })
    public void toolsMenu_Trends_UrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 9);

        assertThat(url).contains(Urls.Tools.TRENDS_LIST);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TOOLS })
    public void toolsMenu_Reports_UrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 10);

        assertThat(url).contains(Urls.Tools.REPORTS);
    }
}
