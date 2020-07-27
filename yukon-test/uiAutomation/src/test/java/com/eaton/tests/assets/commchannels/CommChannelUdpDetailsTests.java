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
import com.eaton.pages.assets.commchannels.CommChannelUdpDetailPage;
import com.eaton.pages.assets.commchannels.CommChannelsListPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.drsetup.JsonFileHelper;

import io.restassured.response.ExtractableResponse;

public class CommChannelUdpDetailsTests extends SeleniumTestSetup {

    private CommChannelUdpDetailPage detailPage;
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
        commChannelName = "UDP Comm Channel " + timeStamp;

        // Creating one UDP port comm channel using hard coded json file.
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelUDP.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        randomNum = getRandomNum();
        jo = (JSONObject) body;
        jo.put("name", commChannelName);
        portNumber = randomNum.nextInt(65536);
        jo.put("portNumber", portNumber);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        commChannelId = createResponse.path("id");
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + commChannelId);
        detailPage = new CommChannelUdpDetailPage(driverExt, commChannelId);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_PageTitleCorrect() {
        String EXPECTED_TITLE = commChannelName;
        
        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_TabTitlesCorrect() {        
        List<String> titles = detailPage.getTabElement().getTitles();

        softly.assertThat(titles.size()).isEqualTo(2);
        softly.assertThat(titles.get(0)).isEqualTo("Info");
        softly.assertThat(titles.get(1)).isEqualTo("Configuration");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_InfoTabLabelsCorrect() {
        String infoTitle = "Info";
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        List<String> labels = detailPage.getTabElement().getTabLabels(infoTitle);

        softly.assertThat(labels.size()).isEqualTo(5);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).contains("Type:");
        softly.assertThat(labels.get(2)).contains("Port Number:");
        softly.assertThat(labels.get(3)).contains("Baud Rate:");
        softly.assertThat(labels.get(4)).contains("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_InfoTabValuesCorrect() {        
        List<String> values = detailPage.getTabElement().getTabValues("Info");        
        
        softly.assertThat(values.size()).isEqualTo(5);
        softly.assertThat(values.get(0)).isEqualTo(commChannelName);
        softly.assertThat(values.get(1)).isEqualTo("UDP");
        softly.assertThat(values.get(2).toString()).isEqualTo(portNumber.toString());
        softly.assertThat(values.get(3)).isEqualTo("2400");
        softly.assertThat(values.get(4)).isEqualTo("Enabled");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_ConfigTabTimingSectionDisplayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section timing = detailPage.getTimingSection();
        
        assertThat(timing.getSection()).isNotNull();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_ConfigTabGeneralSectionDisplayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section general = detailPage.getGeneralSection();
        
        assertThat(general.getSection()).isNotNull();
    }
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_ConfigTabSharedSectionDisplayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section timing = detailPage.getTimingSection();
        
        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_ConfigTabLabelsCorrect() {
        String infoTitle = "Configuration";

        detailPage.getTabElement().clickTabAndWait(infoTitle);
        List<String> labels = detailPage.getTabElement().getTabLabels(infoTitle);

        softly.assertThat(labels.size()).isEqualTo(10);

        softly.assertThat(labels.get(0)).isEqualTo("Protocol Wrap:");
        softly.assertThat(labels.get(1)).isEqualTo("Carrier Detect Wait:");
        softly.assertThat(labels.get(2)).isEqualTo("Encryption key:");
        softly.assertThat(labels.get(3)).isEqualTo("Pre Tx Wait:");
        softly.assertThat(labels.get(4)).isEqualTo("RTS To Tx Wait:");
        softly.assertThat(labels.get(5)).isEqualTo("Post Tx Wait:");
        softly.assertThat(labels.get(6)).isEqualTo("Receive Data Wait:");
        softly.assertThat(labels.get(7)).isEqualTo("Additional Time Out:");
        softly.assertThat(labels.get(8)).isEqualTo("Shared Port Type:");
        softly.assertThat(labels.get(9)).isEqualTo("Socket Number:");
        softly.assertAll();
    }
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_ConfigTabValuesCorrect() {
        detailPage.getTabElement().clickTabAndWait("Configuration");

        List<String> values = detailPage.getTabElement().getTabValues("Configuration");

        softly.assertThat(values.size()).isEqualTo(10);
        softly.assertThat(values.get(0)).isEqualTo("IDLC");
        softly.assertThat(values.get(1)).isEqualTo("544 ms");
        softly.assertThat(values.get(2)).isEqualTo("00112233445566778899aabbccddeeff");
        softly.assertThat(values.get(3)).isEqualTo("87  ms");
        softly.assertThat(values.get(4)).isEqualTo("823  ms");
        softly.assertThat(values.get(5)).isEqualTo("89  ms");
        softly.assertThat(values.get(6)).isEqualTo("76  ms");
        softly.assertThat(values.get(7)).isEqualTo("98  sec");
        softly.assertThat(values.get(8)).isEqualTo("ACS");
        softly.assertThat(values.get(9)).isEqualTo("100");

        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_PanelTitleCorrect() {
        String expectedPanelText = "Comm Channel Information";
        String actualPanelText = detailPage.getCommChannelInfoPanel().getPanelName();
        assertThat(actualPanelText).isEqualTo(expectedPanelText);
    }
        
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDeleteUdp_ActionsDeleteModalDeleteButtonClick() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String deleteCommChannelName = "UDP Comm Channel " + timeStamp;

        // Creating one UDP port comm channel using hard coded json file.
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelUDP.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", deleteCommChannelName);
        Integer deletePortNumber = randomNum.nextInt(65536);
        jo.put("portNumber", deletePortNumber);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        Integer deleteCommChannelId = createResponse.path("id");
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + deleteCommChannelId);
        detailPage = new CommChannelUdpDetailPage(driverExt, deleteCommChannelId);
        String modalTitle = "Confirm Delete";
        String expectedMessage = deleteCommChannelName +" deleted successfully.";
        ConfirmModal deleteConfirmModal = detailPage.showDeleteCommChannelModal(modalTitle);
        deleteConfirmModal.clickBtnByNameAndWait("Delete");
        CommChannelsListPage listPage = new CommChannelsListPage(driverExt);
        String userMsg = listPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expectedMessage);
    }
}