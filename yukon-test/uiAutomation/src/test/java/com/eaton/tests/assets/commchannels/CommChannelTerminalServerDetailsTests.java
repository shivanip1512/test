package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelsListPage;
import com.eaton.pages.assets.commchannels.CommChannelTerminalServerDetailPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.drsetup.JsonFileHelper;

import io.restassured.response.ExtractableResponse;

public class CommChannelTerminalServerDetailsTests extends SeleniumTestSetup {

    private CommChannelTerminalServerDetailPage detailPage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;
    private Integer commChannelId;
    private String commChannelName;
    private JSONObject jo;
    private Random randomNum;
    private Integer portNumber;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        softly = new SoftAssertions();

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        commChannelName = "Terminal Server Comm Channel " + timeStamp;

        // Creating one TerminalServer port comm channel using hard coded json file.
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTerminalServer.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        randomNum = getRandomNum();
        jo = (JSONObject) body;
        jo.put("name", commChannelName);
        portNumber = randomNum.nextInt(65536);
        jo.put("portNumber", portNumber);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        commChannelId = createResponse.path("id");
        
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + commChannelId);
        detailPage = new CommChannelTerminalServerDetailPage(driverExt, commChannelId);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        refreshPage(detailPage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTerminalServer_PageTitleCorrect() {
        String EXPECTED_TITLE = commChannelName;
        
        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTerminalServer_TabTitlesCorrect() {
        List<String> titles = detailPage.getTabElement().getTitles();

        softly.assertThat(2).isEqualTo(titles.size());
        softly.assertThat("Info").isEqualTo(titles.get(0));
        softly.assertThat("Configuration").isEqualTo(titles.get(1));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTerminalServer_InfoTab_LabelsCorrect() {
        String infoTitle = "Info";
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        List<String> labels = detailPage.getTabElement().getTabLabels(infoTitle);

        softly.assertThat(6).isEqualTo(labels.size());
        softly.assertThat("Name:").isEqualTo(labels.get(0));
        softly.assertThat("Type:").isEqualTo(labels.get(1));
        softly.assertThat("IP Address:").isEqualTo(labels.get(2));
        softly.assertThat("Port Number:").isEqualTo(labels.get(3));
        softly.assertThat("Baud Rate:").isEqualTo(labels.get(4));
        softly.assertThat("Status:").isEqualTo(labels.get(5));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTerminalServer_InfoTab_ValuesCorrect() {
        List<String> values = detailPage.getTabElement().getTabValues("Info");

        softly.assertThat(6).isEqualTo(values.size());
        softly.assertThat(commChannelName).isEqualTo(values.get(0));
        softly.assertThat("Terminal Server").isEqualTo(values.get(1));
        softly.assertThat("Localhost").isEqualTo(values.get(2));
        softly.assertThat(portNumber.toString()).isEqualTo(values.get(3).toString());
        softly.assertThat("2400").isEqualTo(values.get(4));
        softly.assertThat("Enabled").isEqualTo(values.get(5));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTerminalServer_ConfigTab_TimingSectionDisplayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section timing = detailPage.getTimingSection();
        
        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTerminalServer_ConfigTab_GeneralSectionDisplayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section general = detailPage.getGeneralSection();
        
        assertThat(general.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTerminalServer_ConfigTab_SharedSectionDisplayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section shared = detailPage.getSharedSection();
        
        assertThat(shared.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTerminalServer_ConfigTab_LabelsCorrect() {
        String infoTitle = "Configuration";

        detailPage.getTabElement().clickTabAndWait(infoTitle);
        List<String> labels = detailPage.getTabElement().getTabLabels(infoTitle);

        softly.assertThat(labels.size()).isEqualTo(9);

        softly.assertThat("Protocol Wrap:").isEqualTo(labels.get(0));
        softly.assertThat("Carrier Detect Wait:").isEqualTo(labels.get(1));
        softly.assertThat("Pre Tx Wait:").isEqualTo(labels.get(2));
        softly.assertThat("RTS To Tx Wait:").isEqualTo(labels.get(3));
        softly.assertThat("Post Tx Wait:").isEqualTo(labels.get(4));
        softly.assertThat("Receive Data Wait:").isEqualTo(labels.get(5));
        softly.assertThat("Additional Time Out:").isEqualTo(labels.get(6));
        softly.assertThat("Shared Port Type:").isEqualTo(labels.get(7));
        softly.assertThat("Socket Number:").isEqualTo(labels.get(8));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTerminalServer_ConfigTab_ValuesCorrect() {
        detailPage.getTabElement().clickTabAndWait("Configuration");

        List<String> values = detailPage.getTabElement().getTabValues("Configuration");

        softly.assertThat(9).isEqualTo(values.size());
        softly.assertThat("IDLC").isEqualTo(values.get(0));
        softly.assertThat("123 ms").isEqualTo(values.get(1));
        softly.assertThat("87  ms").isEqualTo(values.get(2));
        softly.assertThat("823  ms").isEqualTo(values.get(3));
        softly.assertThat("89  ms").isEqualTo(values.get(4));
        softly.assertThat("76  ms").isEqualTo(values.get(5));
        softly.assertThat("98  sec").isEqualTo(values.get(6));
        softly.assertThat("ACS").isEqualTo(values.get(7));
        softly.assertThat("100").isEqualTo(values.get(8));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsTerminalServer_PanelTitleCorrect() {
        String expectedPanelText = "Comm Channel Information";
        String actualPanelText = detailPage.getCommChannelInfoPanel().getPanelName();
        
        assertThat(expectedPanelText).isEqualTo(actualPanelText);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDeleteTerminalServer_DeleteCommChannelSuccessfully() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String deleteCommChannelName = "Terminal Server " + timeStamp;
        String expectedMessage = deleteCommChannelName +" deleted successfully.";

        // Creating one TerminalServer port comm channel using hard coded json file.
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTerminalServer.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", deleteCommChannelName);
        Integer deletePortNumber = randomNum.nextInt(65536);
        jo.put("portNumber", deletePortNumber);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        Integer deleteCommChannelId = createResponse.path("id");
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + deleteCommChannelId);        
        
        ConfirmModal deleteConfirmModal = detailPage.showDeleteCommChannelModal();
        
        deleteConfirmModal.clickOkAndWaitForModalToClose();
        
        CommChannelsListPage listPage = new CommChannelsListPage(driverExt);
        String userMsg = listPage.getUserMessage();

        assertThat(expectedMessage).isEqualTo(userMsg);
    }
}
