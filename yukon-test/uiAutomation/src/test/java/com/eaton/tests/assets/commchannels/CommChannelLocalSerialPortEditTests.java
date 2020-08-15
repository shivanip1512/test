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
import com.eaton.elements.modals.CreateCommChannelModal;
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
    private SoftAssertions softly;
    private Integer commChannelId;
    private String commChannelName;
    private JSONObject jo;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        softly = new SoftAssertions();
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

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_ModalTitleCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        
        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();
        
        String actualModalTitle = editModal.getModalTitle();
        
        assertThat(expectedModalTitle).isEqualTo(actualModalTitle);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_Name_RequiredValidation() {
        String EXPECTED_MSG = "Name is required.";
        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getName().clearInputValue();
        editModal.clickOk();
        
        String errorMsg = editModal.getName().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelLocalSerialEdit_Name_InvalidCharsValidation() {
        String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getName().setInputValue("/,LocalSerial|");
        editModal.clickOk();
        
        String errorMsg = editModal.getName().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_PhysicalPortOther_RequiredValidation() {
        String EXPECTED_MSG = "Physical Port is required.";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();
        editModal.getPhysicalPort().selectItemByValue("Other");
        editModal.clickOk();
        
        String errorMsg = editModal.getPhysicalPort().getValidationError();
        
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_PreTxWait_MinValueValidation() {
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getPreTxWait().getValidationError();
        
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_PreTxWait_MaxValueValidation() {
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("10000001");
        editModal.clickOk();
        
        String errorMsg = editModal.getPreTxWait().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_RTSTxWait_MinValueValidation() {
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("-1");
        editModal.clickOk();
        
        String errorMsg = editModal.getRtsToTxWait().getValidationError();
        
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_RTSTxWait_MaxValueValidation() {
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("10000001");
        editModal.clickOk();

        String errorMsg = editModal.getRtsToTxWait().getValidationError(); 
        
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_PostTxWait_MinValueValidation() {
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        
        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait("Configuration");
        editModal.getPostTxWait().setInputValue("-1");
        editModal.clickOk();
        
        String errorMsg = editModal.getPostTxWait().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_PostTxWait_MaxValueValidation() {
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("10000001");
        editModal.clickOk();

        String errorMsg = editModal.getPostTxWait().getValidationError();
        
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_ReceiveDataWait_MinValueValidation() {
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("-1");
        editModal.clickOk();
        
        String errorMsg = editModal.getReceiveDataWait().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_ReceiveDataWait_MaxValueValidation() {
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("1001");
        editModal.clickOk();

        String errorMsg = editModal.getReceiveDataWait().getValidationError();
        
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_AdditionalTimeOut_MinValueValidation() {
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getAdditionalTimeOut().getValidationError();
        
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_AdditionalTimeOut_MaxValueValidation() {
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("1000");
        editModal.clickOk();
        
        String errorMsg = editModal.getAdditionalTimeOut().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_SocketNumber_MinValueValidation() {
        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue("0");
        editModal.clickOk();

        String errorMsg = editModal.getSocketNumber().getValidationError();
        
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_SocketNumber_MaxValueValidation() {
        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue("65536");
        editModal.clickOk();
        
        String errorMsg = editModal.getSocketNumber().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_SocketNumber_BlankValidation() {
        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        SeleniumTestSetup.moveToElement(editModal.getSocketNumber().getEditElement());
        editModal.getSocketNumber().clearInputValue();
        editModal.clickOk();
        
        String errorMsg = editModal.getSocketNumber().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_TabLabelsCorrect() {
        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        List<String> titles = editModal.getTabs().getTitles();

        softly.assertThat(2).isEqualTo(titles.size());
        softly.assertThat("Info").isEqualTo(titles.get(0));
        softly.assertThat("Configuration").isEqualTo(titles.get(1));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_ConfigTab_LabelsCorrect() {
        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

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
    public void commChannelLocalSerialEdit_InfoTab_LabelsCorrect() {
        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        String tabName = "Info";
        editModal.getTabs().clickTabAndWait(tabName);

        List<String> labels = editModal.getTabs().getTabLabels(tabName);

        softly.assertThat(5).isEqualTo(labels.size());
        softly.assertThat("Name:").isEqualTo(labels.get(0));
        softly.assertThat("Type:").isEqualTo(labels.get(1));
        softly.assertThat("Physical Port:").isEqualTo(labels.get(2));
        softly.assertThat("Baud Rate:").isEqualTo(labels.get(3));
        softly.assertThat("Status:").isEqualTo(labels.get(4));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_InfoTab_FieldsValuesCorrect() {
        String tabName = "Info";
        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);

        softly.assertThat(commChannelName).isEqualTo(editModal.getName().getInputValue());
        softly.assertThat("com1").isEqualTo(editModal.getPhysicalPort().getSelectedValue());
        softly.assertThat("2400").contains(editModal.getBaudRate().getSelectedValue());
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_ConfigTab_FieldsValuesCorrect() {
        String tabName = "Configuration";
        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);

        softly.assertThat("IDLC").isEqualTo(editModal.getProtocolWrap().getValueChecked());
        softly.assertThat("Yes").isEqualTo(editModal.getCarrierDetectWait().getCheckedValue());
        softly.assertThat("123").isEqualTo(editModal.getCarrierDetectWaitTextBox().getInputValue());

        softly.assertThat("87").isEqualTo(editModal.getPreTxWait().getInputValue());
        softly.assertThat("82").isEqualTo(editModal.getRtsToTxWait().getInputValue());
        softly.assertThat("89").isEqualTo(editModal.getPostTxWait().getInputValue());
        softly.assertThat("76").isEqualTo(editModal.getReceiveDataWait().getInputValue());
        softly.assertThat("98").isEqualTo(editModal.getAdditionalTimeOut().getInputValue());
        softly.assertThat("100").isEqualTo(editModal.getSocketNumber().getInputValue());
        softly.assertThat("ACS").isEqualTo(editModal.getSharedPortType().getValueChecked());
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_Cancel_NavigatesCorrectly() {
        String EXPECTED_TITLE = commChannelName;

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();
        editModal.clickCancelAndWait();

        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
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
        editModal.clickOk();
        
        String errorMsg = editModal.getName().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_Create_OpensPopupCorrect() {
        String EXPECTED_CREATE_MODEL_TITLE = "Create Comm Channel";
        
        CreateCommChannelModal createModel = detailPage.showCreateCommChannelModal();
        
        String actualCreateModelTitle = createModel.getModalTitle();

        assertThat(EXPECTED_CREATE_MODEL_TITLE).isEqualTo(actualCreateModelTitle);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_ConfigTab_TimingSectionDisplayed() {
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();
        editModal.getTabs().clickTabAndWait(tabName);

        Section timing = editModal.getTimingSection();
        
        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_ConfigTab_SharingSectionDisplayed() {
        String tabName = "Configuration";

        EditLocalSerialPortCommChannelModal editModal = detailPage.showLocalSerialPortCommChannelEditModal();
        editModal.getTabs().clickTabAndWait(tabName);

        Section sharing = editModal.getSharedSection();
        
        assertThat(sharing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelLocalSerialEdit_AllFieldsSuccess() {
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
        softly.assertThat(updateName).isEqualTo(response.path("name").toString());
        softly.assertThat("None").isEqualTo(response.path("protocolWrap").toString());
        softly.assertThat("0").isEqualTo(response.path("carrierDetectWaitInMilliseconds").toString());
        softly.assertThat(baudRate).isEqualTo(response.path("baudRate").toString());
        softly.assertThat(configFieldsValues[0]).isEqualTo(response.path("timing.preTxWait").toString());
        softly.assertThat(configFieldsValues[1]).isEqualTo(response.path("timing.rtsToTxWait").toString());
        softly.assertThat(configFieldsValues[2]).isEqualTo(response.path("timing.postTxWait").toString());
        softly.assertThat(configFieldsValues[3]).isEqualTo(response.path("timing.receiveDataWait").toString());
        softly.assertThat(configFieldsValues[4]).isEqualTo(response.path("timing.extraTimeOut").toString());
        softly.assertAll();
    }
}
