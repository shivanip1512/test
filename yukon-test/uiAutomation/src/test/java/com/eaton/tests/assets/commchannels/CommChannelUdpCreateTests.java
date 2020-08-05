package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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

public class CommChannelUdpCreateTests extends SeleniumTestSetup {
    private CommChannelsListPage listPage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;
    private Random randomNum;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        WebDriver driver = getDriver();
        driverExt = getDriverExt();
        softly = new SoftAssertions();
        
        driver.get(getBaseUrl() + Urls.Assets.COMM_CHANNELS_LIST);
        
        listPage = new CommChannelsListPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannel_UdpAllFieldsSuccess() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();
                
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT UDP " + timeStamp;

        randomNum = getRandomNum();
        final String EXPECTED_MSG = name + " saved successfully.";

        createModal.getName().setInputValue(name); 
        createModal.getType().selectItemByValue("UDPPORT");
        waitForLoadingSpinner();
        createModal.getPortNumber().setInputValue(Integer.toString(randomNum.nextInt(65536)));
        createModal.getBaudRate().selectItemByValue("BAUD_4800");

        createModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Assets.COMM_CHANNEL_DETAIL, Optional.of(10));

        CommChannelDetailPage detailPage = new CommChannelDetailPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelUdp_LabelsCorrect() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();

        createModal.getType().selectItemByValue("UDPPORT");        
        waitForLoadingSpinner();

        List<String> labels = createModal.getFieldLabels();

        softly.assertThat(labels.size()).isEqualTo(5);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).contains("Type:");
        softly.assertThat(labels.get(2)).contains("Port Number:");
        softly.assertThat(labels.get(3)).contains("Baud Rate:");
        softly.assertThat(labels.get(4)).contains("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelUdp_PortNumberMinValidation() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();

        createModal.getType().selectItemByValue("UDPPORT");
        waitForLoadingSpinner();
        
        String portNumber = "0";

        final String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";
        
        createModal.getPortNumber().setInputValue(portNumber);

        createModal.clickOkAndWaitForModalToClose();

        String errorMsg = createModal.getPortNumber().getValidationError();
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelUdp_PortNumberMaxValidation() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();

        createModal.getType().selectItemByValue("UDPPORT");
        waitForLoadingSpinner();
        
        String portNumber = "65536";

        final String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";
        
        createModal.getPortNumber().setInputValue(portNumber);

        createModal.clickOkAndWaitForModalToClose();

        String errorMsg = createModal.getPortNumber().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelUdp_PortNumberEmptyValidation() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();

        final String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";

        createModal.getType().selectItemByValue("UDPPORT");
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
