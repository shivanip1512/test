package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.modals.CreateCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commChannels.CommChannelDetailPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;

import io.restassured.response.ExtractableResponse;

public class CommChannelTcpDetailsTests extends SeleniumTestSetup {

    private CommChannelDetailPage channelDetailPage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;
    private String commChannelId;
    private String commChannelName;
    private JSONObject json;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        softly = new SoftAssertions();
        
      String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
      commChannelName = "TCP Comm Channel " + timeStamp;
        
      json = new JSONObject()
                .put("name", commChannelName)
                .put("enable", true)
                .put("baudRate", "BAUD_1200")
                .put("type", "TCPPORT")
                .put("timing", new JSONObject().put("preTxWait", 25))
                .put("timing", new JSONObject().put("rtsToTxWait", 0))
                .put("timing", new JSONObject().put("postTxWait", 10))
                .put("timing", new JSONObject().put("receiveDataWait", 0))
                .put("timing", new JSONObject().put("extraTimeOut", 0)); 
        
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(json);
        commChannelId = createResponse.path("id").toString();                           
    } 
    
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + commChannelId);
        channelDetailPage = new CommChannelDetailPage(driverExt);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL})
    public void commChannelDetailsTcpPageTitleCorrect() {
        String EXPECTED_TITLE = commChannelName;     
        
        String actualPageTitle = channelDetailPage.getPageTitle();
        
        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);
    }        
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelDetailsTcpTabTitlesCorrect() {        
        List<String> titles = channelDetailPage.getTabElement().getTitles();
        
        softly.assertThat(titles).contains("Info");
        softly.assertThat(titles).contains("Configuration");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelDetailsTcpInfoTabLabelsCorrect() {
        String infoTitle = "Info";
        channelDetailPage.getTabElement().clickTab(infoTitle);
        List<String> labels = channelDetailPage.getTabElement().getTabLabels(infoTitle);
        
        softly.assertThat(labels).contains("Name:");
        softly.assertThat(labels).contains("Type:");
        softly.assertThat(labels).contains("Baud Rate:");
        softly.assertThat(labels).contains("Status:");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelDetailsTcpInfoTabValuesCorrect() {        
        List<String> values = channelDetailPage.getTabElement().getTabValues("Info");        
        
        softly.assertThat(values.size()).isEqualTo(4);           
        softly.assertThat(values).contains(json.getString("name"));
        softly.assertThat(values).contains("TCP");
        softly.assertThat(values).contains("1200");
        softly.assertThat(values).contains("Enabled");
        softly.assertAll();
    }        

//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
//    public void commChannelDetailsTcpConfigTabTimingSectionDisplayed() {
//        // using tab text clicking on particular comm channel tab
//        tab.clickTab("Configuration");
//        Section timingSection = new Section(driverExt, "Timing");
//        assertThat(timingSection.sectionDisplayed()).isTrue();
//    }
//
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelDetailsTcp_ConfigTabLabelsCorrect() {
        String infoTitle = "Configuration";
        
        channelDetailPage.getTabElement().clickTab(infoTitle);
        List<String> labels = channelDetailPage.getTabElement().getTabLabels(infoTitle);
                
        softly.assertThat(labels.size()).isEqualTo(5);
        softly.assertThat(labels.get(0)).isEqualTo("Pre Tx Wait:");
        softly.assertThat(labels.get(1)).isEqualTo("RTS To Tx Wait:");
        softly.assertThat(labels.get(2)).isEqualTo("Post Tx Wait:");
        softly.assertThat(labels.get(3)).isEqualTo("Receive Data Wait:");
        softly.assertThat(labels.get(4)).isEqualTo("Additional Time Out:");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL})
    public void commChannelDetailsTcp_ConfigTabValuesCorrect() {
        channelDetailPage.getTabElement().clickTab("Configuration");
        
        List<String> values = channelDetailPage.getTabElement().getTabValues("Configuration");        
        
        softly.assertThat(values.size()).isEqualTo(5);
        softly.assertThat(values.get(0)).isEqualTo("");
        softly.assertThat(values.get(1)).isEqualTo("");
        softly.assertThat(values.get(2)).isEqualTo("");
        softly.assertThat(values.get(3)).isEqualTo("");
        softly.assertThat(values.get(4)).isEqualTo("");

        softly.assertAll();
    }

//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
//    public void commChannelDetailsTcpPanelTitleCorrect() {
//        // Validate Comm Channel Info Panel Text
//        String expectedPanelText = "Comm Channel Information";
//        // panel text validation
//        String actualPanelText = channelDetailPage.getCommChannelInfoPanelText();
//        assertThat(actualPanelText).isEqualTo(expectedPanelText);
//    }
//
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
//    public void commChannelDetailsEditOpensCorrectModal() {
//        Optional<String> expectedModalTitle = Optional.of("Edit " + commChannelName);
//        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);
//        String actualModalTitle = editModal.getModalTitle();
//        Optional<String> oActualModalTitle = Optional.of(actualModalTitle);
//        assertThat(oActualModalTitle).isEqualTo(expectedModalTitle);
//    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelDetailsCreateOpensCorrectModal() {
        String expectedModalTitle = "Create Comm Channel";
        
        CreateCommChannelModal createModal = channelDetailPage.showCreateCommChannelModal(expectedModalTitle);
        
        String actualModalTitle = createModal.getModalTitle();
        
        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }
}
