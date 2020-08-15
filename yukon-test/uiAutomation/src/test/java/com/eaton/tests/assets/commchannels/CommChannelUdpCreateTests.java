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

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(listPage);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelUdp_AllFieldsSuccess() {
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

        assertThat(EXPECTED_MSG).isEqualTo(userMsg);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelUdp_LabelsCorrect() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();

        createModal.getType().selectItemByValue("UDPPORT");        
        waitForLoadingSpinner();

        List<String> labels = createModal.getFieldLabels();

        softly.assertThat(5).isEqualTo(labels.size());
        softly.assertThat("Name:").isEqualTo(labels.get(0));
        softly.assertThat("Type:").isEqualTo(labels.get(1));
        softly.assertThat("Port Number:").isEqualTo(labels.get(2));
        softly.assertThat("Baud Rate:").isEqualTo(labels.get(3));
        softly.assertThat("Status:").isEqualTo(labels.get(4));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelUdp_PortNumber_MinValidation() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();

        createModal.getType().selectItemByValue("UDPPORT");
        waitForLoadingSpinner();
        
        String portNumber = "0";

        final String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";
        
        createModal.getPortNumber().setInputValue(portNumber);

        createModal.clickOk();

        String errorMsg = createModal.getPortNumber().getValidationError();
        
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelUdp_PortNumber_MaxValidation() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();

        createModal.getType().selectItemByValue("UDPPORT");
        waitForLoadingSpinner();
        
        String portNumber = "65536";

        final String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";
        
        createModal.getPortNumber().setInputValue(portNumber);

        createModal.clickOk();

        String errorMsg = createModal.getPortNumber().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelUdp_PortNumber_EmptyValidation() {
        CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();

        final String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";

        createModal.getType().selectItemByValue("UDPPORT");
        waitForLoadingSpinner();

        createModal.clickOk();

        String errorMsg = createModal.getPortNumber().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }    
}
