package com.eaton.tests.assets.commchannels;

import com.eaton.framework.SeleniumTestSetup;
import static org.assertj.core.api.Assertions.assertThat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.elements.modals.commchannel.CreateCommChannelModal;
import com.eaton.elements.modals.commchannel.EditLocalSerialPortCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;

import com.eaton.pages.assets.commchannels.CommChannelLocalSerialPortDetailPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.assets.AssetsGetRequestAPI;
import com.eaton.rest.api.drsetup.JsonFileHelper;
import io.restassured.response.ExtractableResponse;

public class CommChannelLocalSerialPortEditTests extends SeleniumTestSetup {
    private CommChannelLocalSerialPortDetailPage detailPage;
    private DriverExtensions driverExt;
    private Integer commChannelId;
    private String commChannelName;
    private JSONObject jo;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        commChannelName = "Local Serial Comm Channel " + timeStamp;

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
        refreshPage(detailPage);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_Modal_TitleCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        
        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();
        
        String actualModalTitle = editModal.getModalTitle();
        
        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_Name_RequiredValidation() {
        String EXPECTED_MSG = "Name is required.";
        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getName().clearInputValue();
        editModal.clickOkAndWaitForSpinner();
        
        String errorMsg = editModal.getName().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelLocalSerialEdit_Name_InvalidCharsValidation() {
        String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getName().setInputValue("/,LocalSerial|");
        editModal.clickOkAndWaitForSpinner();
        
        String errorMsg = editModal.getName().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_PhysicalPortOther_RequiredValidation() {
        String EXPECTED_MSG = "Physical Port is required.";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();
        editModal.getPhysicalPort().selectItemByValue("Other");
        editModal.clickOkAndWaitForSpinner();
        
        String errorMsg = editModal.getPhysicalPort().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_PreTxWait_MinValueValidation() {
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("-1");
        editModal.clickOkAndWaitForSpinner();

        String errorMsg = editModal.getPreTxWait().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_PreTxWait_MaxValueValidation() {
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("10000001");
        editModal.clickOkAndWaitForSpinner();
        
        String errorMsg = editModal.getPreTxWait().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_RtsToTxWait_MinValueValidation() {
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("-1");
        editModal.clickOkAndWaitForSpinner();
        
        String errorMsg = editModal.getRtsToTxWait().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_RtsToTxWait_MaxValueValidation() {
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("10000001");
        editModal.clickOkAndWaitForSpinner();

        String errorMsg = editModal.getRtsToTxWait().getValidationError(); 
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_PostTxWait_MinValueValidation() {
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        
        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait("Configuration");
        editModal.getPostTxWait().setInputValue("-1");
        editModal.clickOkAndWaitForSpinner();
        
        String errorMsg = editModal.getPostTxWait().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_PostTxWait_MaxValueValidation() {
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("10000001");
        editModal.clickOkAndWaitForSpinner();

        String errorMsg = editModal.getPostTxWait().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_ReceiveDataWait_MinValueValidation() {
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("-1");
        editModal.clickOkAndWaitForSpinner();
        
        String errorMsg = editModal.getReceiveDataWait().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_ReceiveDataWait_MaxValueValidation() {
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("1001");
        editModal.clickOkAndWaitForSpinner();

        String errorMsg = editModal.getReceiveDataWait().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_AdditionalTimeOut_MinValueValidation() {
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("-1");
        editModal.clickOkAndWaitForSpinner();

        String errorMsg = editModal.getAdditionalTimeOut().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_AdditionalTimeOut_MaxValueValidation() {
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("1000");
        editModal.clickOkAndWaitForSpinner();
        
        String errorMsg = editModal.getAdditionalTimeOut().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_SocketNumber_MinValueValidation() {
        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue("0");
        editModal.clickOkAndWaitForSpinner();

        String errorMsg = editModal.getSocketNumber().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_SocketNumber_MaxValueValidation() {
        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue("65536");
        editModal.clickOkAndWaitForSpinner();
        
        String errorMsg = editModal.getSocketNumber().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_SocketNumber_RequiredValidation() {
        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        SeleniumTestSetup.moveToElement(editModal.getSocketNumber().getEditElement());
        editModal.getSocketNumber().clearInputValue();
        editModal.clickOkAndWaitForSpinner();
        
        String errorMsg = editModal.getSocketNumber().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_Tab_TitlesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        List<String> titles = editModal.getTabs().getTitles();

        softly.assertThat(titles.size()).isEqualTo(2);
        softly.assertThat(titles.get(0)).isEqualTo("Info");
        softly.assertThat(titles.get(1)).isEqualTo("Configuration");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_ConfigTab_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

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
    public void commChannelLocalSerialEdit_InfoTab_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        String tabName = "Info";
        editModal.getTabs().clickTabAndWait(tabName);

        List<String> labels = editModal.getTabs().getTabLabels(tabName);

        softly.assertThat(labels.size()).isEqualTo(5);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).isEqualTo("Type:");
        softly.assertThat(labels.get(2)).isEqualTo("Physical Port:");
        softly.assertThat(labels.get(3)).isEqualTo("Baud Rate:");
        softly.assertThat(labels.get(4)).isEqualTo("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_InfoTab_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        String tabName = "Info";
        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);

        softly.assertThat(editModal.getName().getInputValue()).isEqualTo(commChannelName);
        softly.assertThat(editModal.getPhysicalPort().getSelectedValue()).isEqualTo("com1");
        softly.assertThat(editModal.getBaudRate().getSelectedValue()).contains("2400");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_ConfigTab_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        String tabName = "Configuration";
        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);

        softly.assertThat(editModal.getProtocolWrap().getValueChecked()).isEqualTo("IDLC");
        softly.assertThat(editModal.getCarrierDetectWait().getCheckedValue()).isEqualTo("Yes");
        softly.assertThat(editModal.getCarrierDetectWaitTextBox().getInputValue()).isEqualTo("123");
        softly.assertThat(editModal.getPreTxWait().getInputValue()).isEqualTo("87");
        softly.assertThat(editModal.getRtsToTxWait().getInputValue()).isEqualTo("82");
        softly.assertThat(editModal.getPostTxWait().getInputValue()).isEqualTo("89");
        softly.assertThat(editModal.getReceiveDataWait().getInputValue()).isEqualTo("76");
        softly.assertThat(editModal.getAdditionalTimeOut().getInputValue()).isEqualTo("98");
        softly.assertThat(editModal.getSocketNumber().getInputValue()).isEqualTo("100");
        softly.assertThat(editModal.getSharedPortType().getValueChecked()).isEqualTo("ACS");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_Cancel_NavigatesToCorrectUrl() {
        String EXPECTED_TITLE = commChannelName;

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();
        editModal.clickCancelAndWait();

        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_Name_AlreadyExistsValidation() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String commChannelNameLocalSerial = "LocalSerial Comm Channel " + timeStamp;
        String EXPECTED_MSG = "Name already exists";
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelLocalSerialPort.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", commChannelNameLocalSerial);
        AssetsCreateRequestAPI.createCommChannel(body);

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getName().setInputValue(commChannelNameLocalSerial);
        editModal.clickOkAndWaitForSpinner();
        
        String errorMsg = editModal.getName().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_Create_OpensCorrectModal() {
        String EXPECTED_CREATE_MODEL_TITLE = "Create Comm Channel";
        
        CreateCommChannelModal createModel = detailPage.showCreateCommChannelModal();
        
        String actualCreateModelTitle = createModel.getModalTitle();

        assertThat(actualCreateModelTitle).isEqualTo(EXPECTED_CREATE_MODEL_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_ConfigTabTimingSection_Displayed() {
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();
        editModal.getTabs().clickTabAndWait(tabName);

        Section timing = editModal.getTimingSection();
        
        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_ConfigTabSharingSection_Displayed() {
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();
        editModal.getTabs().clickTabAndWait(tabName);

        Section sharing = editModal.getSharedSection();
        
        assertThat(sharing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_AllFields_Success() {
        SoftAssertions softly = new SoftAssertions();
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "Local Serial " + timeStamp;
        String updateName = "Update Local Serial " + timeStamp;

        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelLocalSerialPort.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", name);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        Integer id = createResponse.path("id");
        
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + id);
        
        String baudRate = "BAUD_4800";
        String configFieldsValues[] = { "55", "10", "20", "15", "500" };
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();
        editModal.getName().setInputValue(updateName);
        editModal.getBaudRate().selectItemByValue(baudRate);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getProtocolWrap().selectByValue("None");
        editModal.getCarrierDetectWait().selectValue("No");
        editModal.getPreTxWait().setInputValue(configFieldsValues[0]);
        editModal.getRtsToTxWait().setInputValue(configFieldsValues[1]);
        editModal.getPostTxWait().setInputValue(configFieldsValues[2]);
        editModal.getReceiveDataWait().setInputValue(configFieldsValues[3]);
        editModal.getAdditionalTimeOut().setInputValue(configFieldsValues[4]);
        editModal.clickOkAndWaitForModalToClose();

        String userMsg = detailPage.getUserMessage();

        ExtractableResponse<?> response = AssetsGetRequestAPI.getCommChannel(id.toString());

        softly.assertThat(userMsg).isEqualTo(updateName + " saved successfully.");
        softly.assertThat(response.path("name").toString()).isEqualTo(updateName);
        softly.assertThat(response.path("protocolWrap").toString()).isEqualTo("None");
        softly.assertThat(response.path("carrierDetectWaitInMilliseconds").toString()).isEqualTo("0");
        softly.assertThat(response.path("baudRate").toString()).isEqualTo(baudRate);
        softly.assertThat(response.path("timing.preTxWait").toString()).isEqualTo(configFieldsValues[0]);
        softly.assertThat(response.path("timing.rtsToTxWait").toString()).isEqualTo(configFieldsValues[1]);
        softly.assertThat(response.path("timing.postTxWait").toString()).isEqualTo(configFieldsValues[2]);
        softly.assertThat(response.path("timing.receiveDataWait").toString()).isEqualTo(configFieldsValues[3]);
        softly.assertThat(response.path("timing.extraTimeOut").toString()).isEqualTo(configFieldsValues[4]);
        softly.assertAll();
    }
}
