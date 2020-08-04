package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.CreateCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelsListPage;
import com.eaton.pages.assets.commchannels.CommChannelDetailPage;

public class CommChannelTcpCreateTests extends SeleniumTestSetup {
    private CommChannelsListPage listPage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;
    String modalTitle = "Create Comm Channel";
    String type = "TCP";

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        WebDriver driver = getDriver();
        driverExt = getDriverExt();
        softly = new SoftAssertions();
        
        driver.get(getBaseUrl() + Urls.Assets.COMM_CHANNELS_LIST);
        
        listPage = new CommChannelsListPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelTcp_AllFieldsSuccess() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT Comm Channel TCP " + timeStamp;
        String baudRate = "2400";

        final String EXPECTED_MSG = name + " saved successfully.";

        createModal.getName().setInputValue(name);
        createModal.getType().selectItemByText(type);
        waitForLoadingSpinner();
        createModal.getBaudRate().selectItemByText(baudRate);

        createModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Assets.COMM_CHANNEL_DETAIL, Optional.of(10));

        CommChannelDetailPage detailPage = new CommChannelDetailPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelTcp_LabelsCorrect() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();

        createModal.getType().selectItemByText(type);
        waitForLoadingSpinner();

        List<String> labels = createModal.getFieldLabels();

        softly.assertThat(labels.size()).isEqualTo(4);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).contains("Type:");
        softly.assertThat(labels.get(2)).contains("Baud Rate:");
        softly.assertThat(labels.get(3)).contains("Status:");
        softly.assertAll();
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(listPage);
        listPage = new CommChannelsListPage(driverExt);
    }
}
