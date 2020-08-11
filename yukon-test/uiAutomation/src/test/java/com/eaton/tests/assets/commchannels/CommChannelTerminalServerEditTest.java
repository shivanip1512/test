package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.elements.modals.commchannel.EditTerminalServerCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.rest.api.drsetup.*;
import com.eaton.pages.assets.commchannels.CommChannelTerminalServerDetailPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.assets.AssetsGetRequestAPI;

import io.restassured.response.ExtractableResponse;

public class CommChannelTerminalServerEditTest extends SeleniumTestSetup {
    private CommChannelTerminalServerDetailPage channelDetailPage;
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
        channelDetailPage = new CommChannelTerminalServerDetailPage(driverExt, commChannelId);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(channelDetailPage);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_ModalTitleCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);
        String actualModalTitle = editModal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_NameRequired() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Name is required.";
        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getName().setInputValue(" ");
        editModal.clickOkAndWait();

        assertThat(editModal.getName().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_NameInvalidChars() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";

        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getName().setInputValue("/,terminal|");
        editModal.clickOkAndWait();

        assertThat(editModal.getName().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit__NameUniqueValidation() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String EXPECTED_MSG = "Name already exists";
        String commChannelNameTwo = "Terminal Server Comm Channel" + timeStamp;
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTerminalServer.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        randomNum = getRandomNum();
        jo = (JSONObject) body;
        jo.put("name", commChannelNameTwo);
        portNumber = randomNum.nextInt(65536);
        jo.put("portNumber", portNumber);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);

        String expectedModalTitle = "Edit " + commChannelName;
        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getName().setInputValue(commChannelNameTwo);
        editModal.clickOkAndWait();

        assertThat(editModal.getName().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_CancelNavigatesCorrectly() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_TITLE = commChannelName;

        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);
        editModal.clickCancelAndWait();

        String actualPageTitle = channelDetailPage.getPageTitle();
        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_TabLabelsCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        List<String> titles = editModal.getTabs().getTitles();

        softly.assertThat(titles.size()).isEqualTo(2);
        softly.assertThat(titles.get(0)).isEqualTo("Info");
        softly.assertThat(titles.get(1)).isEqualTo("Configuration");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_InfoTabLabelsCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        String tabName = "Info";
        editModal.getTabs().clickTabAndWait(tabName);

        List<String> labels = editModal.getTabs().getTabLabels(tabName);
        softly.assertThat(labels.size()).isEqualTo(6);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).contains("Type:");
        softly.assertThat(labels.get(2)).contains("IP Address:");
        softly.assertThat(labels.get(3)).contains("Port Number:");
        softly.assertThat(labels.get(4)).contains("Baud Rate:");
        softly.assertThat(labels.get(5)).contains("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_ConfigurationLabelsCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        String tabName = "Configuration";
        editModal.getTabs().clickTabAndWait(tabName);

        List<String> labels = editModal.getTabs().getTabLabels("Configuration");
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

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_ConfigurationsValuesCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        String tabName = "Configuration";
        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);

        softly.assertThat(editModal.getProtocolWrap().getValueChecked()).isEqualTo("IDLC");
        softly.assertThat(editModal.getCarrierDetectWait().getCheckedValue()).isEqualTo("Yes");
        softly.assertThat(editModal.getCarrierDetectWaitTextBox().getInputValue()).isEqualTo("123");
        softly.assertThat(editModal.getPreTxWait().getInputValue()).isEqualTo("87");
        softly.assertThat(editModal.getRtsToTxWait().getInputValue()).isEqualTo("823");
        softly.assertThat(editModal.getPostTxWait().getInputValue()).isEqualTo("89");
        softly.assertThat(editModal.getReceiveDataWait().getInputValue()).isEqualTo("76");
        softly.assertThat(editModal.getAdditionalTimeOut().getInputValue()).isEqualTo("98");
        softly.assertThat(editModal.getSocketNumber().getInputValue()).isEqualTo("100");
        softly.assertThat(editModal.getSharedPortType().getValueChecked()).isEqualTo("ACS");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_PreTxWaitMinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("-1");
        editModal.clickOkAndWait();

        assertThat(editModal.getPreTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_PreTxWaitMaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("10000001");
        editModal.clickOkAndWait();

        assertThat(editModal.getPreTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_RTSTxWaitMinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("-1");
        editModal.clickOkAndWait();

        assertThat(editModal.getRtsToTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_RTSTxWaitMaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("10000001");
        editModal.clickOkAndWait();

        assertThat(editModal.getRtsToTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_PostTxWaitMinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("-1");
        editModal.clickOkAndWait();

        assertThat(editModal.getPostTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_PostTxWaitMaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("10000001");
        editModal.clickOkAndWait();

        assertThat(editModal.getPostTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_RecDataWaitMinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("-1");
        editModal.clickOkAndWait();

        assertThat(editModal.getReceiveDataWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_RecDataWaitMaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("1001");
        editModal.clickOkAndWait();

        assertThat(editModal.getReceiveDataWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_AdditionalTimeOutMinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("-1");
        editModal.clickOkAndWait();

        assertThat(editModal.getAdditionalTimeOut().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_AdditionalTimeOutMaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("1000");
        editModal.clickOkAndWait();

        assertThat(editModal.getAdditionalTimeOut().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_InfoFieldsValuesCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        String tabName = "Info";
        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);

        softly.assertThat(editModal.getName().getInputValue()).isEqualTo(commChannelName);
        softly.assertThat(editModal.getPortNumber().getInputValue()).isEqualTo(portNumber.toString());
        softly.assertThat(editModal.getBaudRate().getSelectedValue()).isEqualTo("2400");
        softly.assertThat(editModal.getStatus().getCheckedValue()).isEqualTo("Enabled");
        softly.assertThat(editModal.getIpAddress().getInputValue()).isEqualTo("Localhost");

        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_ConfigTabTimingSectionDisplayed() {
        String expectedModalTitle = "Edit " + commChannelName;
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);
        editModal.getTabs().clickTabAndWait(tabName);

        Section timing = editModal.getTimingSection();
        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_UpdateAllFieldsSuccess() {
        String expectedModalTitle = "Edit " + commChannelName;
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String commChannelName = "CommChannel_TerminalServer_Update" + timeStamp;
        String baudRate = "4800";
        String configFieldsValues[] = { "55", "10", "20", "15", "500" };
        String tabName = "Configuration";
        portNumber = randomNum.nextInt(65536);

        EditTerminalServerCommChannelModal editM = new EditTerminalServerCommChannelModal(this.driverExt, Optional.of(expectedModalTitle), Optional.empty());
        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);
        editM.getIpAddress().setInputValue("10.0.0.1");
        editModal.getName().setInputValue(commChannelName);
        editModal.getBaudRate().selectItemByText(baudRate);
        editModal.getPortNumber().setInputValue(portNumber.toString());

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getProtocolWrap().setByValue("IDLC", false);
        editModal.getCarrierDetectWait().setValue(true);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue(configFieldsValues[0]);
        editModal.getRtsToTxWait().setInputValue(configFieldsValues[1]);
        editModal.getPostTxWait().setInputValue(configFieldsValues[2]);
        editModal.getReceiveDataWait().setInputValue(configFieldsValues[3]);
        editModal.getAdditionalTimeOut().setInputValue(configFieldsValues[4]);
        editModal.clickOkAndWait();

        String userMsg = channelDetailPage.getUserMessage();

        ExtractableResponse<?> response = AssetsGetRequestAPI.getCommChannel(commChannelId.toString());

        softly.assertThat(userMsg).isEqualTo(commChannelName + " saved successfully.");
        softly.assertThat(response.path("name").toString()).isEqualTo(commChannelName);
        softly.assertThat(response.path("baudRate").toString()).isEqualTo("BAUD_" + baudRate);
        softly.assertThat(response.path("timing.preTxWait").toString()).isEqualTo((configFieldsValues[0]));
        softly.assertThat(response.path("timing.rtsToTxWait").toString()).isEqualTo((configFieldsValues[1]));
        softly.assertThat(response.path("timing.postTxWait").toString()).isEqualTo((configFieldsValues[2]));
        softly.assertThat(response.path("timing.receiveDataWait").toString()).isEqualTo((configFieldsValues[3]));
        softly.assertThat(response.path("timing.extraTimeOut").toString()).isEqualTo((configFieldsValues[4]));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_ConfigTabSharingSectionDisplayed() {
        String expectedModalTitle = "Edit " + commChannelName;
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);
        editModal.getTabs().clickTabAndWait(tabName);

        editModal.getSharedPortType().moveTo();

        Section shared = editModal.getSharedSection();
        assertThat(shared.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_SocketNumber_RequiredValidation() {
        throw new SkipException("Development Defect: QA-6176");
//        String expectedModalTitle = "Edit " + commChannelName;
//        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
//        String tabName = "Configuration";
//
//        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);
//
//        editModal.getTabs().clickTabAndWait(tabName);
//        editModal.getSocketNumber().setInputValue(" ");
//        editModal.clickOkAndWait();
//
//        assertThat(editModal.getSocketNumber().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_SocketNumber_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue("0");
        editModal.clickOkAndWait();

        assertThat(editModal.getSocketNumber().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_SocketNumber_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue("65537");
        editModal.clickOkAndWait();

        assertThat(editModal.getSocketNumber().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_InfoTab_IpAddressIsRequired() {
        String expectedModalTitle = "Edit " + commChannelName;
        String expected_msg = "IP Address is required.";

        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getIpAddress().setInputValue(" ");

        editModal.clickOkAndWait();

        assertThat(editModal.getIpAddress().getValidationError()).isEqualTo(expected_msg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_InfoTab_VerifyIfIpAddressIsValid() {
        String expectedModalTitle = "Edit " + commChannelName;
        String expected_msg = "Invalid IP/Host Name.";
        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getIpAddress().setInputValue("@123");

        editModal.clickOkAndWait();

        assertThat(editModal.getIpAddress().getValidationError()).isEqualTo(expected_msg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_ConfigurationTab_EmptyCarrierWaitSavesDefaultValueAsNo() {
        String expectedModalTitle = "Edit " + commChannelName;
        EditTerminalServerCommChannelModal editModal = channelDetailPage
                .showTerminalServerCommChannelEditModal(expectedModalTitle);
        editModal.getTabs().clickTabAndWait("Configuration");
        editModal.getCarrierDetectWaitTextBox().getInputValue();
        editModal.getCarrierDetectWaitTextBox().clearInputValue();

        editModal.clickOkAndWait();
        assertThat(editModal.getCarrierDetectWait().getCheckedValue()).isEqualTo("No");
    }

}