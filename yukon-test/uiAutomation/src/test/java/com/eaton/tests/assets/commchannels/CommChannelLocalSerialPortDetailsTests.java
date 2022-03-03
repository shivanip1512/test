package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
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
    private Integer commChannelId;
    private String commChannelName;
    private JSONObject jo;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);

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

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(detailPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsLocalSerialPort_Page_TitleCorrect() {
        String EXPECTED_TITLE = commChannelName;
        
        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsLocalSerialPort_InfoTab_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        String infoTitle = "Info";
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        List<String> labels = detailPage.getTabElement().getTabLabels(infoTitle);

        softly.assertThat(labels.size()).isEqualTo(5);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).isEqualTo("Type:");
        softly.assertThat(labels.get(2)).isEqualTo("Physical Port:");
        softly.assertThat(labels.get(3)).isEqualTo("Baud Rate:");
        softly.assertThat(labels.get(4)).isEqualTo("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsLocalSerialPort_InfoTab_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getTabElement().getTabValues("Info");

        softly.assertThat(values.size()).isEqualTo(5);
        softly.assertThat(values.get(0)).isEqualTo(commChannelName);
        softly.assertThat(values.get(1)).isEqualTo("Local Serial Port");
        softly.assertThat(values.get(2).toString()).isEqualTo("com1");
        softly.assertThat(values.get(3)).isEqualTo("2400");
        softly.assertThat(values.get(4)).isEqualTo("Enabled");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsLocalSerialPort_ConfigTabTimingSection_Displayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section timing = detailPage.getTimingSection();
        
        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsLocalSerialPort_ConfigTabGeneralSection_Displayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section general = detailPage.getGeneralSection();
        
        assertThat(general.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsLocalSerialPort_ConfigTabSharedSection_Displayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section shared = detailPage.getSharedSection();
        
        assertThat(shared.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsLocalSerialPort_ConfigTab_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        String infoTitle = "Configuration";

        detailPage.getTabElement().clickTabAndWait(infoTitle);
        List<String> labels = detailPage.getTabElement().getTabLabels(infoTitle);

        softly.assertThat(labels.size()).isEqualTo(9);
        softly.assertThat(labels.get(0)).isEqualTo("Protocol Wrap:");
        softly.assertThat(labels.get(1)).isEqualTo("Carrier Detect Wait:");
        softly.assertThat(labels.get(2)).isEqualTo("Pre Tx Wait:");
        softly.assertThat(labels.get(3)).isEqualTo("RTS To Tx Wait:");
        softly.assertThat(labels.get(4)).isEqualTo("Post Tx Wait:");
        softly.assertThat(labels.get(5)).isEqualTo("Receive Data Wait:");
        softly.assertThat(labels.get(6)).isEqualTo("Additional Time Out:");
        softly.assertThat(labels.get(7)).isEqualTo("Shared Port Type:");
        softly.assertThat(labels.get(8)).isEqualTo("Socket Number:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsLocalSerialPort_ConfigTab_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        detailPage.getTabElement().clickTabAndWait("Configuration");

        List<String> values = detailPage.getTabElement().getTabValues("Configuration");

        softly.assertThat(values.size()).isEqualTo(9);
        softly.assertThat(values.get(0)).isEqualTo("IDLC");
        softly.assertThat(values.get(1)).isEqualTo("123 ms");
        softly.assertThat(values.get(2)).isEqualTo("87  ms");
        softly.assertThat(values.get(3)).isEqualTo("82  ms");
        softly.assertThat(values.get(4)).isEqualTo("89  ms");
        softly.assertThat(values.get(5)).isEqualTo("76  ms");
        softly.assertThat(values.get(6)).isEqualTo("98  sec");
        softly.assertThat(values.get(7)).isEqualTo("ACS");
        softly.assertThat(values.get(8)).isEqualTo("100");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS})
    public void commChannelDeleteLocalSerial_Delete_Success() {
        setRefreshPage(true);
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

        assertThat(userMsg).isEqualTo(expectedMessage);
    }
}