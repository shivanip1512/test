package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.commchannel.CreateTcpCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelsListPage;
import com.eaton.pages.assets.commchannels.CommChannelDetailPage;

public class CommChannelTcpCreateTests extends SeleniumTestSetup {
    private CommChannelsListPage listPage;
    private DriverExtensions driverExt;
    String modalTitle = "Create Comm Channel";
    String type = "TCP";

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
    public void createCommChannelTcp_AllFields_Success() {
        CreateTcpCommChannelModal createModal = listPage.showAndWaitCreateTcpCommChannelModal();

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT Comm Channel TCP " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createModal.getName().setInputValue(name);
        createModal.getType().selectItemByValue("TCPPORT");
        waitForLoadingSpinner();
        createModal.getBaudRate().selectItemByValue("BAUD_2400");

        createModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Assets.COMM_CHANNEL_DETAIL, Optional.of(10));

        CommChannelDetailPage detailPage = new CommChannelDetailPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void createCommChannelTcp_Labels_Correct() {
        SoftAssertions softly = new SoftAssertions();
        CreateTcpCommChannelModal createModal = listPage.showAndWaitCreateTcpCommChannelModal();

        createModal.getType().selectItemByValue("TCPPORT");
        waitForLoadingSpinner();

        List<String> labels = createModal.getFieldLabels();

        softly.assertThat(labels.size()).isEqualTo(4);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).isEqualTo("Type:");
        softly.assertThat(labels.get(2)).isEqualTo("Baud Rate:");
        softly.assertThat(labels.get(3)).isEqualTo("Status:");
        softly.assertAll();
    }    
}
