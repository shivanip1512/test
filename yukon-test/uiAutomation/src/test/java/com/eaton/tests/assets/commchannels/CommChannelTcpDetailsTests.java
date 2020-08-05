package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CreateCommChannelModal;
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
    private SoftAssertions softly;
    private Integer commChannelId;
    private String commChannelName;
    private JSONObject jo;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();        

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
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + commChannelId);
        detailPage = new CommChannelTcpDetailPage(driverExt, commChannelId);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTcp_PageTitleCorrect() {
        String EXPECTED_TITLE = commChannelName;
        String actualPageTitle = detailPage.getPageTitle();
        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTcp_TabTitlesCorrect() {
        softly = new SoftAssertions();
        List<String> titles = detailPage.getTabElement().getTitles();

        softly.assertThat(titles.size()).isEqualTo(2);
        softly.assertThat(titles.get(0)).isEqualTo("Info");
        softly.assertThat(titles.get(1)).isEqualTo("Configuration");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTcp_InfoTabLabelsCorrect() {
        softly = new SoftAssertions();
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

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTcp_InfoTabValuesCorrect() {
        softly = new SoftAssertions();
        List<String> values = detailPage.getTabElement().getTabValues("Info");

        softly.assertThat(values.size()).isEqualTo(4);
        softly.assertThat(values).contains(commChannelName);
        softly.assertThat(values).contains("TCP");
        softly.assertThat(values).contains("1200");
        softly.assertThat(values).contains("Enabled");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTcp_ConfigTabTimingSectionDisplayed() {
        String infoTitle = "Configuration";

        detailPage.getTabElement().clickTabAndWait(infoTitle);

        Section timing = detailPage.getTimingSection();

        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTcp_ConfigTabLabelsCorrect() {
        softly = new SoftAssertions();
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

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTcp_ConfigTabValuesCorrect() {
        softly = new SoftAssertions();
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

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTcp_PanelTitleCorrect() {
        String expectedPanelText = "Comm Channel Information";

        String actualPanelText = detailPage.getCommChannelInfoPanel().getPanelName();

        assertThat(actualPanelText).isEqualTo(expectedPanelText);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTcp_Edit_NavigatesCorrectly() {
        String expectedModalTitle = "Edit " + commChannelName;

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

        String actualModalTitle = editModal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }

    //TODO This only needs to be tested once in the context of Comm Channels??
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTCP_CreateNavigatesCorrectly() {
        String expectedModalTitle = "Create Comm Channel";

        CreateCommChannelModal createModal = detailPage.showCreateCommChannelModal();
        String actualModalTitle = createModal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDeleteTcp_DeleteTitleCorrect() {
        String expectedModalTitle = "Confirm Delete";
        
        ConfirmModal deleteConfirmModal = detailPage.showDeleteCommChannelModal();
        
        String actualModalTitle = deleteConfirmModal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDeleteTcp_DeleteConfirmMessageCorrect() {
        String expectedModalMessage = "Are you sure you want to delete \"" + commChannelName + "\"?";
        
        ConfirmModal deleteConfirmModal = detailPage.showDeleteCommChannelModal();
        
        String actualModalMessage = deleteConfirmModal.getConfirmMsg();

        assertThat(actualModalMessage).isEqualTo(expectedModalMessage);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDeleteTcp_CancelDeleteNavigatesCorrectly() {
        ConfirmModal deleteConfirmModal = detailPage.showDeleteCommChannelModal();
        
        deleteConfirmModal.clickCancelByNameAndWait();
        
        assertThat(deleteConfirmModal.isModalAvailable()).isFalse();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDeleteTcp_CloseDeleteNavigatesCorrectly() {
        ConfirmModal deleteConfirmModal = detailPage.showDeleteCommChannelModal();
        
        deleteConfirmModal.clickCloseAndWait();
        
        assertThat(deleteConfirmModal.isModalDisplayed()).isFalse();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDeleteTcp_DeleteSuccessfully() {
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
        detailPage = new CommChannelTcpDetailPage(driverExt, deleteCommChannelId);
        
        String expectedMessage = name + " deleted successfully.";
        ConfirmModal deleteConfirmModal = detailPage.showDeleteCommChannelModal();
        deleteConfirmModal.clickOkAndWaitForModalToClose();
        CommChannelsListPage listPage = new CommChannelsListPage(driverExt);
        String userMsg = listPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expectedMessage);
    }
}
