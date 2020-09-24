package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.commchannel.CreateCommChannelModal;
import com.eaton.elements.modals.commchannel.EditTcpCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelsListPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.pages.assets.commchannels.CommChannelTcpDetailPage;
import com.eaton.rest.api.drsetup.JsonFileHelper;

import io.restassured.response.ExtractableResponse;

public class CommChannelTcpDetailsTests extends SeleniumTestSetup {
    private CommChannelTcpDetailPage detailPage;
    private DriverExtensions driverExt;
    private Integer commChannelId;
    private String commChannelName;
    private JSONObject jo;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        commChannelName = "TCP Comm Channel " + timeStamp;

        // Creating one TCP port comm channel using hard coded json file.
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", commChannelName);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        commChannelId = createResponse.path("id");
        
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + commChannelId);
        detailPage = new CommChannelTcpDetailPage(driverExt, commChannelId);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(detailPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsTcp_Page_TitleCorrect() {
        String EXPECTED_TITLE = commChannelName;
        
        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsTcp_Tab_TitlesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> titles = detailPage.getTabElement().getTitles();

        softly.assertThat(titles.size()).isEqualTo(2);
        softly.assertThat(titles.get(0)).isEqualTo("Info");
        softly.assertThat(titles.get(1)).isEqualTo("Configuration");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsTcp_InfoTab_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        String infoTitle = "Info";
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        List<String> labels = detailPage.getTabElement().getTabLabels(infoTitle);

        softly.assertThat(labels.size()).isEqualTo(4);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).contains("Type:");
        softly.assertThat(labels.get(2)).contains("Baud Rate:");
        softly.assertThat(labels.get(3)).contains("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsTcp_InfoTab_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getTabElement().getTabValues("Info");

        softly.assertThat(4).isEqualTo(values.size());
        softly.assertThat(values.get(0)).isEqualTo(commChannelName);
        softly.assertThat(values.get(1)).isEqualTo("TCP");
        softly.assertThat(values.get(2)).isEqualTo("1200");
        softly.assertThat(values.get(3)).isEqualTo("Enabled");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsTcp_ConfigTabTimingSection_Displayed() {
        String infoTitle = "Configuration";

        detailPage.getTabElement().clickTabAndWait(infoTitle);

        Section timing = detailPage.getTimingSection();

        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsTcp_ConfigTab_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        String infoTitle = "Configuration";

        detailPage.getTabElement().clickTabAndWait(infoTitle);

        List<String> labels = detailPage.getTabElement().getTabLabels(infoTitle);

        softly.assertThat(labels.size()).isEqualTo(5);
        softly.assertThat(labels.get(0)).isEqualTo("Pre Tx Wait:");
        softly.assertThat(labels.get(1)).isEqualTo("RTS To Tx Wait:");
        softly.assertThat(labels.get(2)).isEqualTo("Post Tx Wait:");
        softly.assertThat(labels.get(3)).isEqualTo("Receive Data Wait:");
        softly.assertThat(labels.get(4)).isEqualTo("Additional Time Out:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsTcp_ConfigTab_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        detailPage.getTabElement().clickTabAndWait("Configuration");

        List<String> values = detailPage.getTabElement().getTabValues("Configuration");

        softly.assertThat(values.size()).isEqualTo(5);
        softly.assertThat(values.get(0)).isEqualTo("25  ms");
        softly.assertThat(values.get(1)).isEqualTo("0  ms");
        softly.assertThat(values.get(2)).isEqualTo("10  ms");
        softly.assertThat(values.get(3)).isEqualTo("0  ms");
        softly.assertThat(values.get(4)).isEqualTo("0  sec");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsTcp_Panel_TitleCorrect() {
        String expectedPanelText = "Comm Channel Information";

        String actualPanelText = detailPage.getCommChannelInfoPanel().getPanelName();

        assertThat(actualPanelText).isEqualTo(expectedPanelText);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsTcp_Edit_NavigatesToCorrectUrl() {
        setRefreshPage(true);
        String expectedModalTitle = "Edit " + commChannelName;

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        String actualModalTitle = editModal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsTCP_Create_NavigatesToCorrectUrl() {
        setRefreshPage(true);
        String expectedModalTitle = "Create Comm Channel";

        CreateCommChannelModal createModal = detailPage.showCreateCommChannelModal();
        String actualModalTitle = createModal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDeleteTcp_Delete_TitleCorrect() {
        setRefreshPage(true);
        String expectedModalTitle = "Confirm Delete";

        ConfirmModal deleteConfirmModal = detailPage.showDeleteCommChannelModal();

        String actualModalTitle = deleteConfirmModal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDeleteTcp_DeleteConfirmMessage_Correct() {
        setRefreshPage(true);
        String expectedModalMessage = "Are you sure you want to delete \"" + commChannelName + "\"?";

        ConfirmModal deleteConfirmModal = detailPage.showDeleteCommChannelModal();

        String actualModalMessage = deleteConfirmModal.getConfirmMsg();

        assertThat(actualModalMessage).isEqualTo(expectedModalMessage);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDeleteTcp_CancelDelete_NavigatesToCorrectUrl() {
        setRefreshPage(true);
        ConfirmModal deleteConfirmModal = detailPage.showDeleteCommChannelModal();

        deleteConfirmModal.clickCancelBtnByNameAndWait();

        assertThat(deleteConfirmModal.isModalAvailable()).isFalse();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDeleteTcp_CloseDelete_NavigatesToCorrectUrl() {
        setRefreshPage(true);
        ConfirmModal deleteConfirmModal = detailPage.showDeleteCommChannelModal();

        deleteConfirmModal.clickCloseAndWait();

        assertThat(deleteConfirmModal.isModalDisplayed()).isFalse();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDeleteTcp_Delete_Success() {
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "TCP Comm Channel " + timeStamp;

        // Creating one TCP port comm channel using hard coded json file.
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", name);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        Integer deleteCommChannelId = createResponse.path("id");

        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + deleteCommChannelId);

        String expectedMessage = name + " deleted successfully.";
        ConfirmModal deleteConfirmModal = detailPage.showDeleteCommChannelModal();
        deleteConfirmModal.clickOkAndWaitForModalToClose();
        CommChannelsListPage listPage = new CommChannelsListPage(driverExt);
        String userMsg = listPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expectedMessage);
    }
}
