package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.builders.assets.commchannel.CommChannelTypes;
import com.eaton.builders.assets.commchannel.CommChannelUdpCreateBuilder;
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
        
        Pair<JSONObject, JSONObject> pair = new CommChannelUdpCreateBuilder.Builder(Optional.empty())
                .withEnable(Optional.empty())
                .withBaudRate(Optional.empty())
                .withPortNumber(Optional.empty())
                .withCarrierDetectWaitMs(Optional.empty())
                .withProtocolWrap(Optional.empty())
                .withPreTxWait(Optional.empty())
                .withRtsToTxWait(Optional.empty())
                .withPostTxWait(Optional.empty())
                .withReceiveDataWait(Optional.empty())
                .withExtraTimeOut(Optional.empty())
                .withSharedPortType(Optional.of(CommChannelTypes.SharedPortType.ACS))
                .withSharedSocketNumber(Optional.empty())
                .create();

        // Creating one UDP port comm channel using hard coded json file.
//        String payloadFile = System.getProperty("user.dir")
//                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelUDP.json";
//
//        Object body = JsonFileHelper.parseJSONFile(payloadFile);
//        randomNum = getRandomNum();
//        jo = (JSONObject) body;
//        jo.put("name", commChannelName);
//        portNumber = randomNum.nextInt(65536);
//        jo.put("portNumber", portNumber);
//        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
//        commChannelId = createResponse.path("id");
        
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + commChannelId);
        detailPage = new CommChannelUdpDetailPage(driverExt, commChannelId);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        refreshPage(detailPage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_PageTitleCorrect() {
        String EXPECTED_TITLE = commChannelName;
        
        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_InfoTab_LabelsCorrect() {
        String infoTitle = "Info";
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        List<String> labels = detailPage.getTabElement().getTabLabels(infoTitle);

        softly.assertThat(5).isEqualTo(labels.size());
        softly.assertThat("Name:").isEqualTo(labels.get(0));
        softly.assertThat("Type:").isEqualTo(labels.get(1));
        softly.assertThat("Port Number:").isEqualTo(labels.get(2));
        softly.assertThat("Baud Rate:").isEqualTo(labels.get(3));
        softly.assertThat("Status:").isEqualTo(labels.get(4));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_InfoTab_ValuesCorrect() {        
        List<String> values = detailPage.getTabElement().getTabValues("Info");        
        
        softly.assertThat(5).isEqualTo(values.size());
        softly.assertThat(commChannelName).isEqualTo(values.get(0));
        softly.assertThat("UDP").isEqualTo(values.get(1));
        softly.assertThat(portNumber.toString()).isEqualTo(values.get(2).toString());
        softly.assertThat("2400").isEqualTo(values.get(3));
        softly.assertThat("Enabled").isEqualTo(values.get(4));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_ConfigTab_TimingSectionDisplayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section timing = detailPage.getTimingSection();
        
        assertThat(timing.getSection()).isNotNull();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_ConfigTab_GeneralSectionDisplayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section general = detailPage.getGeneralSection();
        
        assertThat(general.getSection()).isNotNull();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_ConfigTab_SharedSectionDisplayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section shared = detailPage.getTimingSection();
        
        assertThat(shared.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_ConfigTab_LabelsCorrect() {
        String infoTitle = "Configuration";

        detailPage.getTabElement().clickTabAndWait(infoTitle);
        List<String> labels = detailPage.getTabElement().getTabLabels(infoTitle);

        softly.assertThat(10).isEqualTo(labels.size());
        softly.assertThat("Protocol Wrap:").isEqualTo(labels.get(0));
        softly.assertThat("Carrier Detect Wait:").isEqualTo(labels.get(1));
        softly.assertThat("Encryption key:").isEqualTo(labels.get(2));
        softly.assertThat("Pre Tx Wait:").isEqualTo(labels.get(3));
        softly.assertThat("RTS To Tx Wait:").isEqualTo(labels.get(4));
        softly.assertThat("Post Tx Wait:").isEqualTo(labels.get(5));
        softly.assertThat("Receive Data Wait:").isEqualTo(labels.get(6));
        softly.assertThat("Additional Time Out:").isEqualTo(labels.get(7));
        softly.assertThat("Shared Port Type:").isEqualTo(labels.get(8));
        softly.assertThat("Socket Number:").isEqualTo(labels.get(9));
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsUdp_ConfigTab_ValuesCorrect() {
        detailPage.getTabElement().clickTabAndWait("Configuration");

        List<String> values = detailPage.getTabElement().getTabValues("Configuration");

        softly.assertThat(10).isEqualTo(values.size());
        softly.assertThat("IDLC").isEqualTo(values.get(0));
        softly.assertThat("544 ms").isEqualTo(values.get(1));
        softly.assertThat("00112233445566778899aabbccddeeff").isEqualTo(values.get(2));
        softly.assertThat("87  ms").isEqualTo(values.get(3));
        softly.assertThat("823  ms").isEqualTo(values.get(4));
        softly.assertThat("89  ms").isEqualTo(values.get(5));
        softly.assertThat("76  ms").isEqualTo(values.get(6));
        softly.assertThat("98  sec").isEqualTo(values.get(7));
        softly.assertThat("ACS").isEqualTo(values.get(8));
        softly.assertThat("100").isEqualTo(values.get(9));
        softly.assertAll();
    }
        
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDeleteUdp_DeleteCommChannelSuccessfully() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String deleteCommChannelName = "UDP Comm Channel " + timeStamp;

        // Creating one UDP port comm channel using hard coded json file.
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelUDP.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", deleteCommChannelName);
        jo.put("portNumber", randomNum.nextInt(65534));
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        
        Integer deleteCommChannelId = createResponse.path("id");
        
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + deleteCommChannelId);
        
        String expectedMessage = deleteCommChannelName +" deleted successfully.";
        ConfirmModal deleteConfirmModal = detailPage.showDeleteCommChannelModal();
        deleteConfirmModal.clickOkAndWaitForModalToClose();
        CommChannelsListPage listPage = new CommChannelsListPage(driverExt);
        String userMsg = listPage.getUserMessage();

        assertThat(expectedMessage).isEqualTo(userMsg);
    }
}