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
import com.eaton.elements.modals.commchannel.EditTerminalServerCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.rest.api.drsetup.*;
import com.github.javafaker.Faker;
import com.eaton.pages.assets.commchannels.CommChannelTerminalServerDetailPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.assets.AssetsGetRequestAPI;

import io.restassured.response.ExtractableResponse;

public class CommChannelTerminalServerEditTest extends SeleniumTestSetup {
    private CommChannelTerminalServerDetailPage channelDetailPage;
    private DriverExtensions driverExt;
    private Integer commChannelId;
    private String commChannelName;
    private JSONObject jo;
    private Faker faker;
    private Integer portNumber;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        commChannelName = "Terminal Server " + timeStamp;

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
        channelDetailPage = new CommChannelTerminalServerDetailPage(driverExt, commChannelId);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(channelDetailPage);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_Modal_TitleCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();
        String actualModalTitle = editModal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_Name_RequiredValidation() {
        String exprectedMsg = "Name is required.";
        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getName().setInputValue(" ");
        editModal.clickOk();

        String errorMsg = editModal.getName().getValidationError();

        assertThat(errorMsg).isEqualTo(exprectedMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_Name_InvalidCharsValidation() {
        String exprectedMsg = "Name must not contain any of the following characters: / \\ , ' \" |.";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getName().setInputValue("/,terminal|");
        editModal.clickOk();

        String errorMsg = editModal.getName().getValidationError();

        assertThat(errorMsg).isEqualTo(exprectedMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_Name_AlreadyExitsValidation() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String exprectedMsg = "Name already exists";
        String name = "Terminal Server " + timeStamp;
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTerminalServer.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", name);
        Integer portNum = faker.number().numberBetween(1, 65536);
        jo.put("portNumber", portNum);
        AssetsCreateRequestAPI.createCommChannel(body);

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getName().setInputValue(name);
        editModal.clickOk();

        String errorMsg = editModal.getName().getValidationError();

        assertThat(errorMsg).isEqualTo(exprectedMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_Cancel_NavigatesToCorrectUrl() {
        String expectedTitle = commChannelName;

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();
        editModal.clickCancelAndWait();

        String actualPageTitle = channelDetailPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(expectedTitle);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_Tab_TitlesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        List<String> titles = editModal.getTabs().getTitles();

        softly.assertThat(titles.size()).isEqualTo(2);
        softly.assertThat(titles.get(0)).isEqualTo("Info");
        softly.assertThat(titles.get(1)).isEqualTo("Configuration");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_InfoTab_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

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

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_ConfigTab_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

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

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_ConfigTab_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        String tabName = "Configuration";
        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

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

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_PreTxWait_MinValueValidation() {
        String expectedMsg = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getPreTxWait().getValidationError();

        assertThat(errorMsg).isEqualTo(expectedMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_PreTxWait_MaxValueValidation() {
        String expectedMsg = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("10000001");
        editModal.clickOk();

        String errorMsg = editModal.getPreTxWait().getValidationError();

        assertThat(errorMsg).isEqualTo(expectedMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_RtsToTxWait_MinValueValidation() {
        String expectedMsg = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getRtsToTxWait().getValidationError();

        assertThat(errorMsg).isEqualTo(expectedMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_RtsToTxWait_MaxValueValidation() {
        String expectedMsg = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("10000001");
        editModal.clickOk();

        String errorMsg = editModal.getRtsToTxWait().getValidationError();

        assertThat(errorMsg).isEqualTo(expectedMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_PostTxWait_MinValueValidation() {
        String expectedMsg = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getPostTxWait().getValidationError();

        assertThat(errorMsg).isEqualTo(expectedMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_PostTxWait_MaxValueValidation() {
        String expectedMsg = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("10000001");
        editModal.clickOk();

        String errorMsg = editModal.getPostTxWait().getValidationError();

        assertThat(errorMsg).isEqualTo(expectedMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_ReceiveDataWait_MinValueValidation() {
        String expectedMsg = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getReceiveDataWait().getValidationError();

        assertThat(errorMsg).isEqualTo(expectedMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_ReceiveDataWait_MaxValueValidation() {
        String expectedMsg = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("1001");
        editModal.clickOk();

        String errorMsg = editModal.getReceiveDataWait().getValidationError();

        assertThat(errorMsg).isEqualTo(expectedMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_AdditionalTimeOut_MinValueValidation() {
        String expectedMsg = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getAdditionalTimeOut().getValidationError();

        assertThat(errorMsg).isEqualTo(expectedMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_AdditionalTimeOut_MaxValueValidation() {
        String expectedMsg = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("1000");
        editModal.clickOk();

        String errorMsg = editModal.getAdditionalTimeOut().getValidationError();

        assertThat(errorMsg).isEqualTo(expectedMsg);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_InfoTab_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        String tabName = "Info";
        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);

        softly.assertThat(editModal.getName().getInputValue()).isEqualTo(commChannelName);
        softly.assertThat(editModal.getPortNumber().getInputValue()).isEqualTo(portNumber.toString());
        softly.assertThat(editModal.getBaudRate().getSelectedValue()).isEqualTo("2400");
        softly.assertThat(editModal.getStatus().getCheckedValue()).isEqualTo("Enabled");
        softly.assertThat(editModal.getIpAddress().getInputValue()).isEqualTo("Localhost");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_ConfigTabTimingSection_Displayed() {
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();
        editModal.getTabs().clickTabAndWait(tabName);

        Section timing = editModal.getTimingSection();

        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_AllFields_Success() {
        SoftAssertions softly = new SoftAssertions();
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "Terminal Server " + timeStamp;
        String updateName = "Update Terminal Server " + timeStamp;

        // Creating one TerminalServer port comm channel using hard coded json file.
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTerminalServer.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", name);
        Integer portNum = faker.number().numberBetween(1, 65536);
        jo.put("portNumber", portNum);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        Integer id = createResponse.path("id");

        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + id);

        String baudRate = "BAUD_4800";
        String configFieldsValues[] = { "55", "10", "20", "15", "500" };
        String tabName = "Configuration";
        Integer port = faker.number().numberBetween(1, 65536);

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();
        editModal.getIpAddress().setInputValue("10.0.0.1");
        editModal.getName().setInputValue(updateName);
        editModal.getBaudRate().selectItemByValue(baudRate);
        editModal.getPortNumber().setInputValue(port.toString());

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

        softly.assertThat(userMsg).isEqualTo(updateName + " saved successfully.");
        softly.assertThat(response.path("name").toString()).isEqualTo(updateName);
        softly.assertThat(response.path("baudRate").toString()).isEqualTo(baudRate);
        softly.assertThat(response.path("timing.preTxWait").toString()).isEqualTo((configFieldsValues[0]));
        softly.assertThat(response.path("timing.rtsToTxWait").toString()).isEqualTo((configFieldsValues[1]));
        softly.assertThat(response.path("timing.postTxWait").toString()).isEqualTo((configFieldsValues[2]));
        softly.assertThat(response.path("timing.receiveDataWait").toString()).isEqualTo((configFieldsValues[3]));
        softly.assertThat(response.path("timing.extraTimeOut").toString()).isEqualTo((configFieldsValues[4]));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_ConfigTabSharingSection_Displayed() {
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();
        editModal.getTabs().clickTabAndWait(tabName);

        editModal.getSharedPortType().moveTo();

        Section shared = editModal.getSharedSection();

        assertThat(shared.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_SocketNumber_RequiredValidation() {
        String expectedMsg = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue(" ");
        editModal.clickOk();

        String errorMsg = editModal.getSocketNumber().getValidationError();

        assertThat(errorMsg).isEqualTo(expectedMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_SocketNumber_MinValueValidation() {
        String expectedMsg = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue("0");
        editModal.clickOk();

        String errorMsg = editModal.getSocketNumber().getValidationError();

        assertThat(errorMsg).isEqualTo(expectedMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_SocketNumber_MaxValueValidation() {
        String expectedMsg = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue("65537");
        editModal.clickOk();

        String errorMsg = editModal.getSocketNumber().getValidationError();

        assertThat(errorMsg).isEqualTo(expectedMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_IpAddress_RequiredValidation() {
        String expectedMsg = "IP Address is required.";

        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getIpAddress().setInputValue(" ");

        editModal.clickOk();

        String errorMsg = editModal.getIpAddress().getValidationError();

        assertThat(errorMsg).isEqualTo(expectedMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTerminalServerEdit_IpAddress_InvalidValidation() {
        String expectedMsg = "Invalid IP/Host Name.";
        EditTerminalServerCommChannelModal editModal = channelDetailPage.showTerminalServerCommChannelEditModal();

        editModal.getIpAddress().setInputValue("@123");

        editModal.clickOk();

        String errorMsg = editModal.getIpAddress().getValidationError();

        assertThat(errorMsg).isEqualTo(expectedMsg);
    }
}