package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.commchannel.CreateLocalSerialPortCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelDetailPage;
import com.eaton.pages.assets.commchannels.CommChannelsListPage;

public class CommChannelLocalSerialPortCreateTests extends SeleniumTestSetup {
    private CommChannelsListPage listPage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();        
        navigate(Urls.Assets.COMM_CHANNELS_LIST);
        listPage = new CommChannelsListPage(driverExt);
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(listPage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.COMM_CHANNELS })
    public void createCommChannelLocalSerialPort_AllFields_Success() {
        CreateLocalSerialPortCommChannelModal createModal = listPage.showAndWaitCreateLocalSerialPortCommChannelModal();

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT Local Serial Port " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createModal.getName().setInputValue(name);
        createModal.getType().selectItemByValue("LOCAL_SHARED");
        waitForLoadingSpinner();
        createModal.getPhysicalPort().selectItemByValue("com2");
        createModal.getBaudRate().selectItemByValue("BAUD_9600");

        createModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Assets.COMM_CHANNEL_DETAIL, Optional.empty());

        CommChannelDetailPage detailPage = new CommChannelDetailPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void createCommChannelLocalSerialPort_Labels_Correct() {
        SoftAssertions softly = new SoftAssertions();
        CreateLocalSerialPortCommChannelModal createModal = listPage.showAndWaitCreateLocalSerialPortCommChannelModal();

        createModal.getType().selectItemByValue("LOCAL_SHARED");
        waitForLoadingSpinner();

        List<String> labels = createModal.getFieldLabels();

        softly.assertThat(labels.size()).isEqualTo(5);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).isEqualTo("Type:");
        softly.assertThat(labels.get(2)).isEqualTo("Physical Port:");
        softly.assertThat(labels.get(3)).isEqualTo("Baud Rate:");
        softly.assertThat(labels.get(4)).isEqualTo("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void createCommChannelLocalSerialPort_PhysicalPortOther_RequiredValidation() {
        CreateLocalSerialPortCommChannelModal createModal = listPage.showAndWaitCreateLocalSerialPortCommChannelModal();

        final String EXPECTED_MSG = "Physical Port is required.";

        createModal.getType().selectItemByValue("LOCAL_SHARED");
        waitForLoadingSpinner();
        createModal.getPhysicalPort().selectItemByValue("Other");

        createModal.clickOk();

        String errorMsg = createModal.getPhysicalPortOther().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }    
}
