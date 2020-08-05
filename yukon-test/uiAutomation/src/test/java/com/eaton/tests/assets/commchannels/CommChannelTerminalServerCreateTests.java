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
import com.eaton.pages.assets.commchannels.CommChannelDetailPage;
import com.eaton.pages.assets.commchannels.CommChannelsListPage;

public class CommChannelTerminalServerCreateTests extends SeleniumTestSetup {
    private CommChannelsListPage listPage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        WebDriver driver = getDriver();
        driverExt = getDriverExt();
        softly = new SoftAssertions();
        
        driver.get(getBaseUrl() + Urls.Assets.COMM_CHANNELS_LIST);
        
        listPage = new CommChannelsListPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelTerminalServer_RequiredFieldsOnlySuccess() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Comm Channel Terminal Server " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createModal.getName().setInputValue(name);
        createModal.getType().selectItemByValue("TSERVER_SHARED");
        waitForLoadingSpinner();
        createModal.getIpAddress().setInputValue("127.0.0.1");
        createModal.getPortNumber().setInputValue(Integer.toString(getRandomNum().nextInt(65536)));
        createModal.getBaudRate().selectItemByValue("BAUD_14400");

        createModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Assets.COMM_CHANNEL_DETAIL, Optional.of(10));

        CommChannelDetailPage detailPage = new CommChannelDetailPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelTerminalServer_FieldLabelsCorrect() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();

        createModal.getType().selectItemByValue("TSERVER_SHARED");
        waitForLoadingSpinner();

        List<String> labels = createModal.getFieldLabels();

        softly.assertThat(labels.size()).isEqualTo(6);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).contains("Type:");
        softly.assertThat(labels.get(2)).contains("IP Address:");
        softly.assertThat(labels.get(3)).contains("Port Number:");
        softly.assertThat(labels.get(4)).contains("Baud Rate:");
        softly.assertThat(labels.get(5)).contains("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelTerminalServer_IpAddress_RequiredValidation() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();

        final String EXPECTED_MSG = "IP Address is required.";

        createModal.getType().selectItemByValue("TSERVER_SHARED");
        waitForLoadingSpinner();

        createModal.clickOkAndWaitForModalToClose();

        String errorMsg = createModal.getIpAddress().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelTerminalServer_IpAddress_InvalidValidation() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();

        String ipAddress = "#123";

        final String EXPECTED_MSG = "Invalid IP/Host Name.";

        createModal.getType().selectItemByValue("TSERVER_SHARED");
        waitForLoadingSpinner();
        createModal.getIpAddress().setInputValue(ipAddress);

        createModal.clickOkAndWaitForModalToClose();

        String errorMsg = createModal.getIpAddress().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelTerminalServer_PortNumber_MinValidation() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();

        final String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";

        createModal.getType().selectItemByValue("TSERVER_SHARED");
        waitForLoadingSpinner();
        createModal.getPortNumber().setInputValue("0");

        createModal.clickOkAndWaitForModalToClose();

        String errorMsg = createModal.getPortNumber().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelTerminalServer_PortNumber_MaxValidation() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();                

        final String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";

        createModal.getType().selectItemByValue("TSERVER_SHARED");
        waitForLoadingSpinner();
        createModal.getPortNumber().setInputValue("65536");

        createModal.clickOkAndWaitForModalToClose();

        String errorMsg = createModal.getPortNumber().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelTerminalServer_PortNumber_RequiredValidation() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();

        final String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";

        createModal.getType().selectItemByValue("TSERVER_SHARED");
        waitForLoadingSpinner();

        createModal.clickOkAndWaitForModalToClose();

        String errorMsg = createModal.getPortNumber().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(listPage);
    }
}
