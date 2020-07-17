package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.modals.CreateCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelDetailPage;
import com.eaton.pages.assets.commchannels.CommChannelsListPage;

public class CommChannelLocalSerialPortCreateTests extends SeleniumTestSetup {
    private CommChannelsListPage channelCreatePage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;
    String type = "Local Serial Port";

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        softly = new SoftAssertions();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        navigate(Urls.Assets.COMM_CHANNELS_LIST);
        channelCreatePage = new CommChannelsListPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelLocalSerialPort_AllFieldsSuccess() {
        CreateCommChannelModal createModal = channelCreatePage.showAndWaitCreateCommChannelModal();

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT Comm Channel Local Serial Port " + timeStamp;
        String physicalPort = "com3";
        String baudRate = "9600";

        final String EXPECTED_MSG = name + " saved successfully.";

        createModal.getName().setInputValue(name);
        createModal.getType().selectItemByText(type);
        createModal.getPhysicalPort().selectItemByText(physicalPort);
        createModal.getBaudRate().selectItemByText(baudRate);

        createModal.clickOkAndWait();

        waitForUrlToLoad(Urls.Assets.COMM_CHANNEL_DETAIL, Optional.of(10));

        CommChannelDetailPage detailPage = new CommChannelDetailPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelLocalSerialPort_LabelsCorrect() {
        CreateCommChannelModal createModal = channelCreatePage.showAndWaitCreateCommChannelModal();

        createModal.getType().selectItemByText(type);

        List<String> labels = createModal.getFieldLabels();

        softly.assertThat(labels.size()).isEqualTo(5);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).contains("Type:");
        softly.assertThat(labels.get(2)).contains("Physical Port:");
        softly.assertThat(labels.get(3)).contains("Baud Rate:");
        softly.assertThat(labels.get(4)).contains("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void createCommChannelLocalSerialPort_PhysicalPortOtherRequiredValidation() {
        CreateCommChannelModal createModal = channelCreatePage.showAndWaitCreateCommChannelModal();

        String physicalPort = "Other";

        final String EXPECTED_MSG = "Physical Port is required.";

        createModal.getType().selectItemByText(type);
        createModal.getPhysicalPort().selectItemByText(physicalPort);

        createModal.clickOkAndWait();

        String errorMsg = createModal.getPhysicalPortOther().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }
}
