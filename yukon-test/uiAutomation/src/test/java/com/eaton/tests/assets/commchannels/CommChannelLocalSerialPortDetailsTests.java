package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

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
import com.eaton.pages.assets.commchannels.CommChannelLocalSerialPortDetailPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.drsetup.JsonFileHelper;

import io.restassured.response.ExtractableResponse;

public class CommChannelLocalSerialPortDetailsTests extends SeleniumTestSetup {

    private CommChannelLocalSerialPortDetailPage detailPage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;
    private Integer commChannelId;
    private String commChannelName;
    private JSONObject jo;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        softly = new SoftAssertions();

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        commChannelName = "Local Serial Port " + timeStamp;

        // Creating one UDP port comm channel using hard coded json file.
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelLocalSerialPort.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", commChannelName);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        commChannelId = createResponse.path("id");
        
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + commChannelId);        
        detailPage = new CommChannelLocalSerialPortDetailPage(driverExt, commChannelId);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        refreshPage(detailPage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsLocalSerialPort_PageTitleCorrect() {
        String EXPECTED_TITLE = commChannelName;
        
        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsLocalSerialPort_InfoTab_LabelsCorrect() {
        String infoTitle = "Info";
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        List<String> labels = detailPage.getTabElement().getTabLabels(infoTitle);

        softly.assertThat(5).isEqualTo(labels.size());
        softly.assertThat("Name:").isEqualTo(labels.get(0));
        softly.assertThat("Type:").isEqualTo(labels.get(1));
        softly.assertThat("Physical Port:").isEqualTo(labels.get(2));
        softly.assertThat("Baud Rate:").isEqualTo(labels.get(3));
        softly.assertThat("Status:").isEqualTo(labels.get(4));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsLocalSerialPort_InfoTab_ValuesCorrect() {
        List<String> values = detailPage.getTabElement().getTabValues("Info");

        softly.assertThat(5).isEqualTo(values.size());
        softly.assertThat(commChannelName).isEqualTo(values.get(0));
        softly.assertThat("Local Serial Port").isEqualTo(values.get(1));
        softly.assertThat("com1").isEqualTo(values.get(2).toString());
        softly.assertThat("2400").isEqualTo(values.get(3));
        softly.assertThat("Enabled").isEqualTo(values.get(4));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsLocalSerialPort_ConfigTab_TimingSectionDisplayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section timing = detailPage.getTimingSection();
        
        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsLocalSerialPort_ConfigTab_GeneralSectionDisplayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section general = detailPage.getGeneralSection();
        
        assertThat(general.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsLocalSerialPort_ConfigTab_SharedSectionDisplayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section shared = detailPage.getSharedSection();
        
        assertThat(shared.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsLocalSerialPort_ConfigTab_LabelsCorrect() {
        String infoTitle = "Configuration";

        detailPage.getTabElement().clickTabAndWait(infoTitle);
        List<String> labels = detailPage.getTabElement().getTabLabels(infoTitle);

        softly.assertThat(9).isEqualTo(labels.size());
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
    public void commChannelDetailsLocalSerialPort_ConfigTab_ValuesCorrect() {
        detailPage.getTabElement().clickTabAndWait("Configuration");

        List<String> values = detailPage.getTabElement().getTabValues("Configuration");

        softly.assertThat(9).isEqualTo(values.size());
        softly.assertThat("IDLC").isEqualTo(values.get(0));
        softly.assertThat("123 ms").isEqualTo(values.get(1));
        softly.assertThat("87  ms").isEqualTo(values.get(2));
        softly.assertThat("82  ms").isEqualTo(values.get(3));
        softly.assertThat("89  ms").isEqualTo(values.get(4));
        softly.assertThat("76  ms").isEqualTo(values.get(5));
        softly.assertThat("98  sec").isEqualTo(values.get(6));
        softly.assertThat("ACS").isEqualTo(values.get(7));
        softly.assertThat("100").isEqualTo(values.get(8));
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDeleteLocalSerial_DeleteCommChannelSuccessfully() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String deleteCommChannelName = "Local Serial Comm Channel " + timeStamp;

        // Creating one LocalSerial port comm channel using hard coded json file.
        String payloadFile = System.getProperty("user.dir") + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelLocalSerialPort.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", deleteCommChannelName);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        Integer deleteCommChannelId = createResponse.path("id");
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + deleteCommChannelId);
        
        String expectedMessage = deleteCommChannelName + " deleted successfully.";
        
        ConfirmModal deleteConfirmModal = detailPage.showDeleteCommChannelModal();
        
        deleteConfirmModal.clickOk();
        
        waitForUrlToLoad(Urls.Assets.COMM_CHANNELS_LIST, Optional.empty());
        
        CommChannelsListPage listPage = new CommChannelsListPage(driverExt);
        
        String userMsg = listPage.getUserMessage();

        assertThat(expectedMessage).isEqualTo(userMsg);
    }
}