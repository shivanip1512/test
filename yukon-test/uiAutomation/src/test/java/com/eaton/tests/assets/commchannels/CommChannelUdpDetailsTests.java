package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Random;

import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.elements.modals.CreateCommChannelModal;
import com.eaton.elements.modals.EditCommChannelModal;
import com.eaton.elements.tabs.CommChannelTab;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commChannels.CommChannelDetailPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.dr.JsonFileHelper;

import io.restassured.response.ExtractableResponse;

public class CommChannelUdpDetailsTests extends SeleniumTestSetup {

    private CommChannelDetailPage channelDetailPage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;
    private String commChannelId;
    private String commChannelName;
    private String portNumber;
    private CommChannelTab tab;
    private JSONObject jo;

    @BeforeClass(alwaysRun = true)
    public void creatingCommChannel() {
        driverExt = getDriverExt();
        softly = new SoftAssertions();
        channelDetailPage = new CommChannelDetailPage(driverExt);

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        commChannelName = "UDP Comm Channel " + timeStamp;

        SimpleDateFormat timeStampp = new SimpleDateFormat(TestConstants.TIME_FORMAT);
        // Creating one UDP port comm channel using hard coded json file.
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelUDP.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        Random generator = new Random();
        jo = (JSONObject) body;
        jo.put("name", commChannelName);
        int newPortNumber = generator.nextInt(65536 - 1 + 1);;
        jo.put("portNumber", newPortNumber);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannelUDP(body);
        commChannelId = createResponse.path("id").toString();
    }
    
    @Test()
    public void test() {
        
    }
    

    @BeforeMethod
    public void navigatingToCreatedCommChannel() {
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + commChannelId);
        tab = new CommChannelTab(driverExt);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsUDPPageTitleCorrect()" })
    public void commChannelDetailsUDPPageTitleCorrect() {
        String EXPECTED_TITLE = commChannelName;
        String actualPageTitle = channelDetailPage.getPageTitle();
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsUDPInfoTabDisplayed()" })
    public void commChannelDetailsUDPInfoTabDisplayed() {
        String tabName = "Info";
        assertThat(tab.TabDisplayed(tabName)).isTrue();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsUDPConfigurationTabDisplayed()" })
    public void commChannelDetailsUDPConfigurationTabDisplayed() {
        String tabName = "Configuration";
        assertThat(tab.TabDisplayed(tabName)).isTrue();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsUDPInfoTabLabelsCorrect()" })
    public void commChannelDetailsUDPInfoTabLabelsCorrect() {
        String[] infoFields = { "Name:", "Type:", "Baud Rate:", "Status:", "Port Number:" };

        // Validated all fields for the UDP Info tab
        for (int i = 0; i < infoFields.length; i++) {

            // Validate all fields for the UDP Info tab
            boolean infoFieldPresence = tab.getFieldLabelPresence(infoFields[i]);
            softly.assertThat(infoFieldPresence).isTrue();
        }
        softly.assertAll();

    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsUDPInfoTabValuesCorrect()" })
    public void commChannelDetailsUDPInfoTabValuesCorrect() {
        String[] infoFields = { "Name:", "Type:", "Baud Rate:", "Status:", "Port Number:" };

        // Data keys as in json body request for the field values
        String[] infoFieldsData = { "name", "type", "baudRate", "enable", "portNumber" };

        // Validate all fields in Info Tab
        // fetching data for Info tab field values
        for (int i = 0; i < infoFields.length; i++) {
            String infoFieldValue;
            if (i != 3) {
                infoFieldValue = (String) jo.get(infoFieldsData[i]);
            } else {
                Boolean bfieldValue = (Boolean) jo.get(infoFieldsData[i]);
                if (bfieldValue == true) {
                    infoFieldValue = "Enabled";
                } else {
                    infoFieldValue = "Disabled";
                }
            }

            // Validate data against the Info tab fields is the same as passed in json file.
            boolean infoFieldsDataPresence = tab.getInfoFieldDataPresence(infoFields[i], infoFieldValue);
            softly.assertThat(infoFieldsDataPresence).isTrue();
        }
        softly.assertAll();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsUDPConfigTabTimingSectionDisplayed()" })
    public void commChannelDetailsUDPConfigTabTimingSectionDisplayed() {
        // using tab text clicking on particular comm channel tab
        tab.clickTab("Configuration");
        Section timingSection = new Section(driverExt, "Timing");
        assertThat(timingSection.sectionDisplayed()).isTrue();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsUDPConfigTabLabelsCorrect()" })
    public void commChannelDetailsUDPConfigTabLabelsCorrect() {
        // using tab text clicking on particular comm channel tab
        tab.clickTab("Configuration");

        String[] configurationFields = { "Pre Tx Wait:", "RTS To Tx Wait:", "Post Tx Wait:", "Receive Data Wait:",
                "Additional Time Out:", "Shared Port Type:", "Socket Number:" };

        // Default Data values for Timing section in Configuration tab as it is not passed in json body request for the respective
        // field values
        String[] configFieldsData = { "25", "0", "0", "0", "0", "None", "1025" };

        // Validate all fields in Info Tab
        for (int j = 0; j < configurationFields.length; j++) {

            String sConfigValue = configFieldsData[j];
            // Validate all fields for the UDP Configuration tab
            boolean configFieldPresence = tab.getFieldLabelPresence(sConfigValue);
            softly.assertThat(configFieldPresence).isTrue();
        }
        softly.assertAll();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsUDPConfigTabValuesCorrect()" })
    public void commChannelDetailsUDPConfigTabValuesCorrect() {
        // using tab text clicking on particular comm channel tab
        tab.clickTab("Configuration");

        String[] configurationFields = { "Pre Tx Wait:", "RTS To Tx Wait:", "Post Tx Wait:", "Receive Data Wait:",
                "Additional Time Out:" };

        // Data keys as in json body request for the field values
        String[] configFieldsData = { "preTxWait", "rtsToTxWait", "postTxWait", "receiveDataWait", "extraTimeOut" };

        // Validate all fields in Info Tab
        // fetching data for Configuration tab field values
        JSONObject timingValue = (JSONObject) jo.get("timing");

        for (int j = 0; j < configurationFields.length; j++) {
            Long configValue = (Long) timingValue.get(configFieldsData[j]);
            String sConfigValue = configValue.toString();

            // Validate data against the Configuration tab fields is the same as passed in json file.
            boolean configFieldsDataPresence = tab.getConfigFieldDataPresence(configurationFields[j], sConfigValue);
            softly.assertThat(configFieldsDataPresence).isTrue();
        }
        softly.assertAll();

    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsUDPPanelTitleCorrect()" })
    public void commChannelDetailsUDPPanelTitleCorrect() {
        // Validate Comm Channel Info Panel Text
        String expectedPanelText = "Comm Channel Information";
        // panel text validation
        String actualPanelText = channelDetailPage.getCommChannelInfoPanelText();
        assertThat(actualPanelText).isEqualTo(expectedPanelText);
    }
}

/*
 * import static org.assertj.core.api.Assertions.assertThat;
 * 
 * 
 * 
 * String[] configurationFields = { "Pre Tx Wait:", "RTS To Tx Wait:", "Post Tx Wait:", "Receive Data Wait:",
 * "Additional Time Out:", "Shared Port Type:", "Socket Number:" };
 * 
 * // Default Data values for Timing section in Configuration tab as it is not passed in json body request for the respective
 * // field values
 * String[] configFieldsData = { "25", "0", "0", "0", "0" , "None", "1025"};
 * 
 * // Validate all fields in Info Tab
 * for (int j = 0; j < configurationFields.length; j++) {
 * 
 * String sConfigValue = configFieldsData[j];
 * 
 * // 6. Validated all fields for the UDP Configuration tab
 * boolean configFieldPresence = tab.getFieldPresence(configurationFields[j]);
 * softly.assertThat(configFieldPresence).isTrue();
 * 
 * // 7. Validated data against the Configuration tab fields is the same as passed in json file.
 * boolean configFieldsDataPresence = tab.getConfigFieldDataPresence(configurationFields[j], sConfigValue);
 * softly.assertThat(configFieldsDataPresence).isTrue();
 * }
 * softly.assertAll();
 * 
 * // 8. Deleting the created comm channel via API request.
 * AssetsDeleteRequestAPI.deleteCommChannelUDP(commChannelId);
 * 
 * // 9. Validating that it is no longer visible on list page.
 * navigate(Urls.Assets.Comm_Channels_List);
 * 
 * // validate comm channel is getting deleted with the given name in API request
 * assertThat(listPage.getLinkVisibility(commChannelName)).isFalse();
 * }
 */