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
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelsListPage;
import com.eaton.pages.assets.commchannels.CommChannelTerminalServerDetailPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.drsetup.JsonFileHelper;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class CommChannelTerminalServerDetailsTests extends SeleniumTestSetup {
    private CommChannelTerminalServerDetailPage detailPage;
    private DriverExtensions driverExt;    
    private Integer commChannelId;
    private String commChannelName;
    private JSONObject jo;
    private Integer portNumber;
    private Faker faker;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        commChannelName = "Terminal Server Comm Channel " + timeStamp;

        // Creating one TerminalServer port comm channel using hard coded json file.
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTerminalServer.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        faker = SeleniumTestSetup.getFaker();
        jo = (JSONObject) body;
        jo.put("name", commChannelName);
        portNumber = faker.number().numberBetween(1, 65536);
        jo.put("portNumber", portNumber);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        commChannelId = createResponse.path("id");
        
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + commChannelId);
        detailPage = new CommChannelTerminalServerDetailPage(driverExt, commChannelId);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(detailPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsTerminalServer_Page_TitleCorrect() {
        String EXPECTED_TITLE = commChannelName;
        
        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsTerminalServer_InfoTab_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        String infoTitle = "Info";
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        List<String> labels = detailPage.getTabElement().getTabLabels(infoTitle);

        softly.assertThat(labels.size()).isEqualTo(6);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).contains("Type:");
        softly.assertThat(labels.get(2)).contains("IP Address:");
        softly.assertThat(labels.get(3)).contains("Port Number:");
        softly.assertThat(labels.get(4)).contains("Baud Rate:");
        softly.assertThat(labels.get(5)).contains("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsTerminalServer_InfoTab_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getTabElement().getTabValues("Info");

        softly.assertThat(values.size()).isEqualTo(6);
        softly.assertThat(values.get(0)).isEqualTo(commChannelName);
        softly.assertThat(values.get(1)).isEqualTo("Terminal Server");
        softly.assertThat(values.get(2)).isEqualTo("Localhost");
        softly.assertThat(values.get(3).toString()).isEqualTo(portNumber.toString());
        softly.assertThat(values.get(4)).isEqualTo("2400");
        softly.assertThat(values.get(5)).isEqualTo("Enabled");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsTerminalServer_ConfigTabTimingSection_Displayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section timing = detailPage.getTimingSection();
        
        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsTerminalServer_ConfigTabGeneralSection_Displayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section general = detailPage.getGeneralSection();
        
        assertThat(general.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsTerminalServer_ConfigTabSharedSection_Displayed() {
        String infoTitle = "Configuration";
        
        detailPage.getTabElement().clickTabAndWait(infoTitle);
        Section shared = detailPage.getSharedSection();
        
        assertThat(shared.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelDetailsTerminalServer_ConfigTab_LabelsCorrect() {
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
    public void commChannelDetailsTerminalServer_ConfigTab_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        detailPage.getTabElement().clickTabAndWait("Configuration");

        List<String> values = detailPage.getTabElement().getTabValues("Configuration");

        softly.assertThat(values.size()).isEqualTo(9);
        softly.assertThat(values.get(0)).isEqualTo("IDLC");
        softly.assertThat(values.get(1)).isEqualTo("123 ms");
        softly.assertThat(values.get(2)).isEqualTo("87  ms");
        softly.assertThat(values.get(3)).isEqualTo("823  ms");
        softly.assertThat(values.get(4)).isEqualTo("89  ms");
        softly.assertThat(values.get(5)).isEqualTo("76  ms");
        softly.assertThat(values.get(6)).isEqualTo("98  sec");
        softly.assertThat(values.get(7)).isEqualTo("ACS");
        softly.assertThat(values.get(8)).isEqualTo("100");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS})
    public void commChannelDeleteTerminalServer_Delete_Success() {
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String deleteCommChannelName = "Terminal Server " + timeStamp;
        String expectedMessage = deleteCommChannelName +" deleted successfully.";

        // Creating one TerminalServer port comm channel using hard coded json file.
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTerminalServer.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", deleteCommChannelName);
        Integer deletePortNumber = faker.number().numberBetween(1, 65536);
        jo.put("portNumber", deletePortNumber);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        Integer deleteCommChannelId = createResponse.path("id");
        
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + deleteCommChannelId);        
        
        ConfirmModal deleteConfirmModal = detailPage.showDeleteCommChannelModal();
        
        deleteConfirmModal.clickOkAndWaitForModalToClose();
        
        CommChannelsListPage listPage = new CommChannelsListPage(driverExt);
        String userMsg = listPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expectedMessage);
    }
}
