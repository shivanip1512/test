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
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commChannels.CommChannelDetailPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.dbetoweb.JsonFileHelper;

import io.restassured.response.ExtractableResponse;

public class CommChannelLocalSerialPortDetailsTests extends SeleniumTestSetup {

    private CommChannelDetailPage channelDetailPage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;
    private String commChannelId;
    private String commChannelName;
    private JSONObject jo;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        softly = new SoftAssertions();

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        commChannelName = "Local Serial Port Comm Channel " + timeStamp;

        // Creating one UDP port comm channel using hard coded json file.
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelLocalSerialPort.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", commChannelName);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        commChannelId = createResponse.path("id").toString();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + commChannelId);
        channelDetailPage = new CommChannelDetailPage(driverExt);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsLocalSerialPort_PageTitleCorrect() {
        String EXPECTED_TITLE = commChannelName;
        String actualPageTitle = channelDetailPage.getPageTitle();
        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsLocalSerialPort_TabTitlesCorrect() {
        List<String> titles = channelDetailPage.getTabElement().getTitles();

        softly.assertThat(titles.size()).isEqualTo(2);
        softly.assertThat(titles.get(0)).isEqualTo("Info");
        softly.assertThat(titles.get(1)).isEqualTo("Configuration");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsLocalSerialPort_InfoTabLabelsCorrect() {
        String infoTitle = "Info";
        channelDetailPage.getTabElement().clickTab(infoTitle);
        List<String> labels = channelDetailPage.getTabElement().getTabLabels(infoTitle);

        softly.assertThat(labels.size()).isEqualTo(5);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).contains("Type:");
        softly.assertThat(labels.get(2)).contains("Physical Port:");
        softly.assertThat(labels.get(3)).contains("Baud Rate:");
        softly.assertThat(labels.get(4)).contains("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsLocalSerialPort_InfoTabValuesCorrect() {        
        List<String> values = channelDetailPage.getTabElement().getTabValues("Info");        
        
        softly.assertThat(values.size()).isEqualTo(5);
        softly.assertThat(values.get(0)).isEqualTo(commChannelName);
        softly.assertThat(values.get(1)).isEqualTo("Local Serial Port");
        softly.assertThat(values.get(2).toString()).isEqualTo("com1");
        softly.assertThat(values.get(3)).isEqualTo("2400");
        softly.assertThat(values.get(4)).isEqualTo("Enabled");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsLocalSerialPort_ConfigTabTimingSectionDisplayed() {
        String infoTitle = "Configuration";
        channelDetailPage.getTabElement().clickTab(infoTitle);
        Section timing = channelDetailPage.getTimingSection();
        assertThat(timing.getSection()).isNotNull();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsLocalSerialPort_ConfigTabGeneralSectionDisplayed() {
        String infoTitle = "Configuration";
        channelDetailPage.getTabElement().clickTab(infoTitle);
        Section timing = channelDetailPage.getGeneralSection();
        assertThat(timing.getSection()).isNotNull();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsLocalSerialPort_ConfigTabSharedSectionDisplayed() {
        String infoTitle = "Configuration";
        channelDetailPage.getTabElement().clickTab(infoTitle);
        Section timing = channelDetailPage.getSharedSection();
        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsLocalSerialPort_ConfigTabLabelsCorrect() {
        String infoTitle = "Configuration";
        
        channelDetailPage.getTabElement().clickTab(infoTitle);
        List<String> labels = channelDetailPage.getTabElement().getTabLabels(infoTitle);
       
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
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS})
    public void commChannelDetailsLocalSerialPort_ConfigTabValuesCorrect() {
        channelDetailPage.getTabElement().clickTab("Configuration");
        
        List<String> values = channelDetailPage.getTabElement().getTabValues("Configuration");        
        
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

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDetailsLocalSerialPort_PanelTitleCorrect() {
        String expectedPanelText = "Comm Channel Information";
        String actualPanelText = channelDetailPage.getCommChannelInfoPanel().getPanelName();
        assertThat(actualPanelText).isEqualTo(expectedPanelText);
    }
}