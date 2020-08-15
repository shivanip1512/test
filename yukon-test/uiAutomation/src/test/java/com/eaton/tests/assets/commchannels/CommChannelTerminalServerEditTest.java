package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
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
        commChannelName = "Terminal Server " + timeStamp;

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
        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();
        String actualModalTitle = editModal.getModalTitle();

        assertThat(expectedModalTitle).isEqualTo(actualModalTitle);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_Name_RequiredValidation() {
        String exprectedMsg = "Name is required.";
        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getName().setInputValue(" ");
        editModal.clickOk();

        String errorMsg = editModal.getName().getValidationError();

        assertThat(exprectedMsg).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_Name_InvalidCharsValidation() {
        String exprectedMsg = "Name must not contain any of the following characters: / \\ , ' \" |.";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getName().setInputValue("/,terminal|");
        editModal.clickOk();

        String errorMsg = editModal.getName().getValidationError();

        assertThat(exprectedMsg).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit__Name_AlreadyExitsValidation() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String exprectedMsg = "Name already exists";
        String name = "Terminal Server " + timeStamp;
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTerminalServer.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        randomNum = getRandomNum();
        jo = (JSONObject) body;
        jo.put("name", name);
        Integer portNum = randomNum.nextInt(65536);
        jo.put("portNumber", portNum);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getName().setInputValue(name);
        editModal.clickOk();

        String errorMsg = editModal.getName().getValidationError();

        assertThat(exprectedMsg).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_Cancel_NavigatesCorrectly() {
        String expectedTitle = commChannelName;

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();
        editModal.clickCancelAndWait();

        String actualPageTitle = channelDetailPage.getPageTitle();

        assertThat(expectedTitle).isEqualTo(actualPageTitle);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_TabLabelsCorrect() {
        softly = new SoftAssertions();
        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        List<String> titles = editModal.getTabs().getTitles();

        softly.assertThat(2).isEqualTo(titles.size());
        softly.assertThat("Info").isEqualTo(titles.get(0));
        softly.assertThat("Configuration").isEqualTo(titles.get(1));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_InfoTab_LabelsCorrect() {
        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        String tabName = "Info";
        editModal.getTabs().clickTabAndWait(tabName);

        List<String> labels = editModal.getTabs().getTabLabels(tabName);

        softly.assertThat(6).isEqualTo(labels.size());
        softly.assertThat("Name:").isEqualTo(labels.get(0));
        softly.assertThat("Type:").isEqualTo(labels.get(1));
        softly.assertThat("IP Address:").isEqualTo(labels.get(2));
        softly.assertThat("Port Number:").isEqualTo(labels.get(3));
        softly.assertThat("Baud Rate:").isEqualTo(labels.get(4));
        softly.assertThat("Status:").isEqualTo(labels.get(5));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_ConfigTab_LabelsCorrect() {
        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        String tabName = "Configuration";
        editModal.getTabs().clickTabAndWait(tabName);

        List<String> labels = editModal.getTabs().getTabLabels("Configuration");
        softly.assertThat(labels.size()).isEqualTo(9);

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

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_ConfigTab_ValuesCorrect() {
        String tabName = "Configuration";
        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);

        softly.assertThat("IDLC").isEqualTo(editModal.getProtocolWrap().getValueChecked());
        softly.assertThat("Yes").isEqualTo(editModal.getCarrierDetectWait().getCheckedValue());
        softly.assertThat("123").isEqualTo(editModal.getCarrierDetectWaitTextBox().getInputValue());
        softly.assertThat("87").isEqualTo(editModal.getPreTxWait().getInputValue());
        softly.assertThat("823").isEqualTo(editModal.getRtsToTxWait().getInputValue());
        softly.assertThat("89").isEqualTo(editModal.getPostTxWait().getInputValue());
        softly.assertThat("76").isEqualTo(editModal.getReceiveDataWait().getInputValue());
        softly.assertThat("98").isEqualTo(editModal.getAdditionalTimeOut().getInputValue());
        softly.assertThat("100").isEqualTo(editModal.getSocketNumber().getInputValue());
        softly.assertThat("ACS").isEqualTo(editModal.getSharedPortType().getValueChecked());
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_PreTxWait_MinValueValidation() {
        String expectedMsg = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getPreTxWait().getValidationError();

        assertThat(expectedMsg).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_PreTxWaitMaxValueValidation() {
        String expectedMsg = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("10000001");
        editModal.clickOk();

        String errorMsg = editModal.getPreTxWait().getValidationError();

        assertThat(expectedMsg).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_RTSTxWaitMinValueValidation() {
        String expectedMsg = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getRtsToTxWait().getValidationError();

        assertThat(expectedMsg).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_RTSTxWaitMaxValueValidation() {
        String expectedMsg = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("10000001");
        editModal.clickOk();

        String errorMsg = editModal.getRtsToTxWait().getValidationError();

        assertThat(expectedMsg).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_PostTxWaitMinValueValidation() {
        String expectedMsg = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getPostTxWait().getValidationError();

        assertThat(expectedMsg).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_PostTxWaitMaxValueValidation() {
        String expectedMsg = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("10000001");
        editModal.clickOk();

        String errorMsg = editModal.getPostTxWait().getValidationError();

        assertThat(expectedMsg).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_RecDataWaitMinValueValidation() {
        String expectedMsg = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getReceiveDataWait().getValidationError();

        assertThat(expectedMsg).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_RecDataWaitMaxValueValidation() {
        String expectedMsg = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("1001");
        editModal.clickOk();

        String errorMsg = editModal.getReceiveDataWait().getValidationError();

        assertThat(expectedMsg).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_AdditionalTimeOutMinValueValidation() {
        String expectedMsg = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getAdditionalTimeOut().getValidationError();

        assertThat(expectedMsg).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_AdditionalTimeOutMaxValueValidation() {
        String expectedMsg = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("1000");
        editModal.clickOk();

        String errorMsg = editModal.getAdditionalTimeOut().getValidationError();

        assertThat(expectedMsg).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_InfoTab_ValuesCorrect() {
        String tabName = "Info";
        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);

        softly.assertThat(commChannelName).isEqualTo(editModal.getName().getInputValue());
        softly.assertThat(portNumber.toString()).isEqualTo(editModal.getPortNumber().getInputValue());
        softly.assertThat("2400").isEqualTo(editModal.getBaudRate().getSelectedValue());
        softly.assertThat("Enabled").isEqualTo(editModal.getStatus().getCheckedValue());
        softly.assertThat("Localhost").isEqualTo(editModal.getIpAddress().getInputValue());
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_ConfigTabTimingSectionDisplayed() {
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();
        editModal.getTabs().clickTabAndWait(tabName);

        Section timing = editModal.getTimingSection();

        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_AllFieldsSuccess() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "Terminal Server " + timeStamp;
        String updateName = "Update Terminal Server " + timeStamp;

        // Creating one TerminalServer port comm channel using hard coded json file.
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTerminalServer.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        randomNum = getRandomNum();
        jo = (JSONObject) body;
        jo.put("name", name);
        Integer portNum = randomNum.nextInt(65536);
        jo.put("portNumber", portNum);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        Integer id = createResponse.path("id");

        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + id);

        String baudRate = "BAUD_4800";
        String configFieldsValues[] = { "55", "10", "20", "15", "500" };
        String tabName = "Configuration";
        Integer port = randomNum.nextInt(65536);

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();
        editModal.getIpAddress().setInputValue("10.0.0.1");
        editModal.getName().setInputValue(updateName);
        editModal.getBaudRate().selectItemByValue(baudRate);
        editModal.getPortNumber().setInputValue(portNum.toString());

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getProtocolWrap().selectByValue("IDLC");
        editModal.getCarrierDetectWait().selectValue("Yes");

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue(configFieldsValues[0]);
        editModal.getRtsToTxWait().setInputValue(configFieldsValues[1]);
        editModal.getPostTxWait().setInputValue(configFieldsValues[2]);
        editModal.getReceiveDataWait().setInputValue(configFieldsValues[3]);
        editModal.getAdditionalTimeOut().setInputValue(configFieldsValues[4]);
        editModal.clickOkAndWaitForModalToClose();

        String userMsg = channelDetailPage.getUserMessage();

        ExtractableResponse<?> response = AssetsGetRequestAPI.getCommChannel(id.toString());

        softly.assertThat(updateName + " saved successfully.").isEqualTo(userMsg);
        softly.assertThat(updateName).isEqualTo(response.path("name").toString());
        softly.assertThat(baudRate).isEqualTo(response.path("baudRate").toString());
        softly.assertThat(configFieldsValues[0]).isEqualTo(response.path("timing.preTxWait").toString());
        softly.assertThat(configFieldsValues[1]).isEqualTo(response.path("timing.rtsToTxWait").toString());
        softly.assertThat(configFieldsValues[2]).isEqualTo(response.path("timing.postTxWait").toString());
        softly.assertThat(configFieldsValues[3]).isEqualTo(response.path("timing.receiveDataWait").toString());
        softly.assertThat(configFieldsValues[4]).isEqualTo(response.path("timing.extraTimeOut").toString());
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_ConfigTab_SharingSectionDisplayed() {
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();
        editModal.getTabs().clickTabAndWait(tabName);

        editModal.getSharedPortType().moveTo();

        Section shared = editModal.getSharedSection();

        assertThat(shared.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_SocketNumber_RequiredValidation() {
        String expectedMsg = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue(" ");
        editModal.clickOk();

        String errorMsg = editModal.getSocketNumber().getValidationError();

        assertThat(expectedMsg).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_SocketNumber_MinValueValidation() {
        String expectedMsg = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue("0");
        editModal.clickOk();

        String errorMsg = editModal.getSocketNumber().getValidationError();

        assertThat(expectedMsg).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_SocketNumber_MaxValueValidation() {
        String expectedMsg = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue("65537");
        editModal.clickOk();

        String errorMsg = editModal.getSocketNumber().getValidationError();

        assertThat(expectedMsg).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_InfoTab_IpAddressIsRequired() {
        String expectedMsg = "IP Address is required.";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getIpAddress().setInputValue(" ");

        editModal.clickOk();

        String errorMsg = editModal.getIpAddress().getValidationError();

        assertThat(expectedMsg).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_InfoTab_VerifyIfIpAddressIsValid() {
        String expectedMsg = "Invalid IP/Host Name.";
        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getIpAddress().setInputValue("@123");

        editModal.clickOk();

        String errorMsg = editModal.getIpAddress().getValidationError();

        assertThat(expectedMsg).isEqualTo(errorMsg);
    }
}