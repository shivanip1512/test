package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.commchannel.CreateTerminalServerCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelDetailPage;
import com.eaton.pages.assets.commchannels.CommChannelsListPage;
import com.github.javafaker.Faker;

public class CommChannelTerminalServerCreateTests extends SeleniumTestSetup {
    private CommChannelsListPage listPage;
    private DriverExtensions driverExt;
    private Faker faker;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        navigate(Urls.Assets.COMM_CHANNELS_LIST);
        listPage = new CommChannelsListPage(driverExt);
        faker = SeleniumTestSetup.getFaker();
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(listPage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.COMM_CHANNELS })
    public void createCommChannelTerminalServer_AllFields_Success() {
        CreateTerminalServerCommChannelModal createModal = listPage.showAndWaitCreateTerminalServerCommChannelModal();
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Terminal Server " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createModal.getName().setInputValue(name);
        createModal.getType().selectItemByValue("TSERVER_SHARED");
        waitForLoadingSpinner();
        createModal.getIpAddress().setInputValue("127.0.0.1");
        createModal.getPortNumber().setInputValue(Integer.toString(faker.number().numberBetween(1, 65536)));
        createModal.getBaudRate().selectItemByValue("BAUD_14400");

        createModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Assets.COMM_CHANNEL_DETAIL, Optional.of(10));

        CommChannelDetailPage detailPage = new CommChannelDetailPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void createCommChannelTerminalServer_Labels_Correct() {
        SoftAssertions softly = new SoftAssertions();
        CreateTerminalServerCommChannelModal createModal = listPage.showAndWaitCreateTerminalServerCommChannelModal();

        createModal.getType().selectItemByValue("TSERVER_SHARED");
        waitForLoadingSpinner();

        List<String> labels = createModal.getFieldLabels();

        softly.assertThat(labels.size()).isEqualTo(6);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).isEqualTo("Type:");
        softly.assertThat(labels.get(2)).isEqualTo("IP Address:");
        softly.assertThat(labels.get(3)).isEqualTo("Port Number:");
        softly.assertThat(labels.get(4)).isEqualTo("Baud Rate:");
        softly.assertThat(labels.get(5)).isEqualTo("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void createCommChannelTerminalServer_IpAddress_RequiredValidation() {
        CreateTerminalServerCommChannelModal createModal = listPage.showAndWaitCreateTerminalServerCommChannelModal();

        final String EXPECTED_MSG = "IP Address is required.";

        createModal.getType().selectItemByValue("TSERVER_SHARED");
        waitForLoadingSpinner();

        createModal.clickOk();

        String errorMsg = createModal.getIpAddress().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void createCommChannelTerminalServer_IpAddress_InvalidValidation() {
        CreateTerminalServerCommChannelModal createModal = listPage.showAndWaitCreateTerminalServerCommChannelModal();

        String ipAddress = "#123";

        final String EXPECTED_MSG = "Invalid IP/Host Name.";

        createModal.getType().selectItemByValue("TSERVER_SHARED");
        waitForLoadingSpinner();
        createModal.getIpAddress().setInputValue(ipAddress);

        createModal.clickOk();

        String errorMsg = createModal.getIpAddress().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void createCommChannelTerminalServer_PortNumber_MinValueValidation() {
        CreateTerminalServerCommChannelModal createModal = listPage.showAndWaitCreateTerminalServerCommChannelModal();

        final String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";

        createModal.getType().selectItemByValue("TSERVER_SHARED");
        waitForLoadingSpinner();
        createModal.getPortNumber().setInputValue("0");

        createModal.clickOk();

        String errorMsg = createModal.getPortNumber().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void createCommChannelTerminalServer_PortNumber_MaxValueValidation() {
        CreateTerminalServerCommChannelModal createModal = listPage.showAndWaitCreateTerminalServerCommChannelModal();                

        final String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";

        createModal.getType().selectItemByValue("TSERVER_SHARED");
        waitForLoadingSpinner();
        createModal.getPortNumber().setInputValue("65536");

        createModal.clickOk();;

        String errorMsg = createModal.getPortNumber().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void createCommChannelTerminalServer_PortNumber_RequiredValidation() {
        CreateTerminalServerCommChannelModal createModal = listPage.showAndWaitCreateTerminalServerCommChannelModal();

        final String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";

        createModal.getType().selectItemByValue("TSERVER_SHARED");
        waitForLoadingSpinner();

        createModal.clickOk();

        String errorMsg = createModal.getPortNumber().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }    
}
