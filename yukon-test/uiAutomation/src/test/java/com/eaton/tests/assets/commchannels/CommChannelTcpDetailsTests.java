package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

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

public class CommChannelTcpDetailsTests extends SeleniumTestSetup {

    private CommChannelDetailPage channelDetailPage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;
    private String commChannelId;
    private String commChannelName;
    private CommChannelTab tab;
    private JSONObject jo;

    @BeforeClass(alwaysRun = true)
    public void creatingCommChannel() {
        driverExt = getDriverExt();
        softly = new SoftAssertions();
        channelDetailPage = new CommChannelDetailPage(driverExt);

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        commChannelName = "TCP Comm Channel " + timeStamp;

        // Creating one TCP port comm channel using hard coded json file.
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);

        jo = (JSONObject) body;
        jo.put("name", commChannelName);

        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannelTCP(body);
        commChannelId = createResponse.path("id").toString();
    }

    @BeforeMethod
    public void navigatingToCreatedCommChannel() {
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + commChannelId);
        tab = new CommChannelTab(driverExt);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsTcpPageTitleCorrect()" })
    public void commChannelDetailsTcpPageTitleCorrect() {
        String EXPECTED_TITLE = commChannelName;
        String actualPageTitle = channelDetailPage.getPageTitle();
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsTcpInfoTabDisplayed()" })
    public void commChannelDetailsTcpInfoTabDisplayed() {
        String tabName = "Info";
        assertThat(tab.TabDisplayed(tabName)).isTrue();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsTcpConfigurationTabDisplayed()" })
    public void commChannelDetailsTcpConfigurationTabDisplayed() {
        String tabName = "Configuration";
        assertThat(tab.TabDisplayed(tabName)).isTrue();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsTcpInfoTabLabelsCorrect()" })
    public void commChannelDetailsTcpInfoTabLabelsCorrect() {
        String[] infoFields = { "Name:", "Type:", "Baud Rate:", "Status:" };

        // Validated all fields for the TCP Info tab
        for (int i = 0; i < infoFields.length; i++) {

            // Validate all fields for the TCP Info tab
            boolean infoFieldPresence = tab.getFieldLabelPresence(infoFields[i]);
            softly.assertThat(infoFieldPresence).isTrue();
        }
        softly.assertAll();

    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsTcpInfoTabValuesCorrect()" })
    public void commChannelDetailsTcpInfoTabValuesCorrect() {
        String[] infoFields = { "Name:", "Type:", "Baud Rate:", "Status:" };

        // Data keys as in json body request for the field values
        String[] infoFieldsData = { "name", "type", "baudRate", "enable" };

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

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsTcpConfigTabTimingSectionDisplayed()" })
    public void commChannelDetailsTcpConfigTabTimingSectionDisplayed() {
        // using tab text clicking on particular comm channel tab
        tab.clickTab("Configuration");
        Section timingSection = new Section(driverExt, "Timing");
        assertThat(timingSection.sectionDisplayed()).isTrue();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsTcpConfigTabLabelsCorrect()" })
    public void commChannelDetailsTcpConfigTabLabelsCorrect() {
        // using tab text clicking on particular comm channel tab
        tab.clickTab("Configuration");

        String[] configurationFields = { "Pre Tx Wait:", "RTS To Tx Wait:", "Post Tx Wait:", "Receive Data Wait:",
                "Additional Time Out:" };

        for (int j = 0; j < configurationFields.length; j++) {

            // Validate all fields for the TCP Configuration tab
            boolean configFieldPresence = tab.getFieldLabelPresence(configurationFields[j]);
            softly.assertThat(configFieldPresence).isTrue();
        }
        softly.assertAll();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsTcpConfigTabValuesCorrect()" })
    public void commChannelDetailsTcpConfigTabValuesCorrect() {
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

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsTcpPanelTitleCorrect()" })
    public void commChannelDetailsTcpPanelTitleCorrect() {
        // Validate Comm Channel Info Panel Text
        String expectedPanelText = "Comm Channel Information";
        // panel text validation
        String actualPanelText = channelDetailPage.getCommChannelInfoPanelText();
        assertThat(actualPanelText).isEqualTo(expectedPanelText);

    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsEditOpensCorrectModal()" })
    public void commChannelDetailsEditOpensCorrectModal() {
        Optional<String> expectedModalTitle = Optional.of("Edit " + commChannelName);
        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);
        String actualModalTitle = editModal.getModalTitle();
        Optional<String> oActualModalTitle = Optional.of(actualModalTitle);
        assertThat(oActualModalTitle).isEqualTo(expectedModalTitle);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelDetailsTcpPanelTitleCorrect()" })
    public void commChannelDetailsCreateOpensCorrectModal() {
        String expectedModalTitle = "Create Comm Channel";
        CreateCommChannelModal createModal = channelDetailPage.showCreateCommChannelModal(expectedModalTitle);
        String actualModalTitle = createModal.getModalTitle();
        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }
}
