package com.eaton.tests.assets.commchannels;

import com.eaton.framework.SeleniumTestSetup;
import static org.assertj.core.api.Assertions.assertThat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.elements.modals.CreateCommChannelModal;
import com.eaton.elements.modals.commchannel.EditUdpCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelUdpDetailPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.assets.AssetsGetRequestAPI;
import com.eaton.rest.api.drsetup.JsonFileHelper;

import io.restassured.response.ExtractableResponse;

public class CommChannelUdpEditTests extends SeleniumTestSetup {

    private CommChannelUdpDetailPage detailPage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;
    private Integer commChannelId;
    private String commChannelName;
    private Integer portNumber;
    private Random randomNum;    
    private JSONObject jo;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        softly = new SoftAssertions();
        randomNum = getRandomNum();
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        commChannelName = "UDP Comm Channel " + timeStamp;

        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelUDP.json";

        portNumber = randomNum.nextInt(65534);
        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", commChannelName);
        jo.put("portNumber", portNumber);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        commChannelId = createResponse.path("id");
        
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + commChannelId);
        detailPage = new CommChannelUdpDetailPage(driverExt, commChannelId);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        refreshPage(detailPage);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_ModalTitleCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);
        
        String actualModalTitle = editModal.getModalTitle();
        
        assertThat(expectedModalTitle).isEqualTo(actualModalTitle);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_Name_RequiredValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Name is required.";
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getName().clearInputValue();
        editModal.clickOk();
        
        String errorMsg = editModal.getName().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelUdpEdit_Name_InvalidCharsValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getName().setInputValue("/,udp|");
        editModal.clickOk();

        String errorMsg = editModal.getName().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_PortNumber_MinValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";
        String portNumber = "0";
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getPortNumber().setInputValue(portNumber);
        editModal.clickOk();

        String errorMsg = editModal.getPortNumber().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_PortNumber_MaxValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";
        String portNumber = "65536";
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getPortNumber().setInputValue(portNumber);
        editModal.clickOk();

        String errorMsg = editModal.getPortNumber().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_PreTxWait_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        
        editModal.getPreTxWait().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getPreTxWait().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_PreTxWait_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("10000001");
        editModal.clickOk();

        String errorMsg = editModal.getPreTxWait().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_RtsToTxWait_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getRtsToTxWait().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_RtsToTxWait_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("10000001");
        editModal.clickOk();

        String errorMsg = editModal.getRtsToTxWait().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_PostTxWait_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getPostTxWait().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_PostTxWait_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("10000001");
        editModal.clickOk();

        String errorMsg = editModal.getPostTxWait().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_ReceiveDataWait_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getReceiveDataWait().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_ReceiveDataWait_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("1001");
        editModal.clickOk();

        String errorMsg = editModal.getReceiveDataWait().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_AdditionalTimeOut_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getAdditionalTimeOut().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_AdditionalTimeOut_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("1000");
        editModal.clickOk();

        String errorMsg = editModal.getAdditionalTimeOut().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }       

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_SocketNumber_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue("0");
        editModal.clickOk();

        String errorMsg = editModal.getSocketNumber().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_SocketNumber_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue("65536");
        editModal.clickOk();

        String errorMsg = editModal.getSocketNumber().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_SocketNumber_BlankValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().clearInputValue();
        editModal.clickOk();

        String errorMsg = editModal.getSocketNumber().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_TabLabelsCorrect() {
        softly = new SoftAssertions();
        String expectedModalTitle = "Edit " + commChannelName;
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        List<String> titles = editModal.getTabs().getTitles();

        softly.assertThat(2).isEqualTo(titles.size());
        softly.assertThat("Info").isEqualTo(titles.get(0));
        softly.assertThat("Configuration").isEqualTo(titles.get(1));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_ConfigurationLabelsCorrect() {
        softly = new SoftAssertions();
        String expectedModalTitle = "Edit " + commChannelName;
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        String tabName = "Configuration";
        editModal.getTabs().clickTabAndWait(tabName);

        List<String> labels = editModal.getTabs().getTabLabels("Configuration");
        softly.assertThat(labels.size()).isEqualTo(10);

        softly.assertThat("Protocol Wrap:").isEqualTo(labels.get(0));
        softly.assertThat("Carrier Detect Wait:").isEqualTo(labels.get(1));
        softly.assertThat("Encryption key:").isEqualTo(labels.get(2));
        softly.assertThat("Pre Tx Wait:").isEqualTo(labels.get(3));
        softly.assertThat("RTS To Tx Wait:").isEqualTo(labels.get(4));
        softly.assertThat("Post Tx Wait:").isEqualTo(labels.get(5));
        softly.assertThat("Receive Data Wait:").isEqualTo(labels.get(6));
        softly.assertThat("Additional Time Out:").isEqualTo(labels.get(7));
        softly.assertThat("Shared Port Type:").isEqualTo(labels.get(8));
        softly.assertThat("Socket Number:").isEqualTo(labels.get(9));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_InfoTabLabelsCorrect() {
        softly = new SoftAssertions();
        String expectedModalTitle = "Edit " + commChannelName;
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        String tabName = "Info";
        editModal.getTabs().clickTabAndWait(tabName);

        List<String> labels = editModal.getTabs().getTabLabels(tabName);

        softly.assertThat(5).isEqualTo(labels.size());
        softly.assertThat("Name:").isEqualTo(labels.get(0));
        softly.assertThat("Type:").isEqualTo(labels.get(1));
        softly.assertThat("Port Number:").isEqualTo(labels.get(2));
        softly.assertThat("Baud Rate:").isEqualTo(labels.get(3));
        softly.assertThat("Status:").isEqualTo(labels.get(4));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_InfoFieldsValuesCorrect() {
        softly = new SoftAssertions();
        String expectedModalTitle = "Edit " + commChannelName;
        String tabName = "Info";
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);

        softly.assertThat(commChannelName).isEqualTo(editModal.getName().getInputValue());
        softly.assertThat(portNumber.toString()).isEqualTo(editModal.getPortNumber().getInputValue());
        softly.assertThat("2400").isEqualTo(editModal.getBaudRate().getSelectedValue());
        softly.assertThat("Enabled").isEqualTo(editModal.getStatus().getCheckedValue());
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })	
    public void commChannelUdpEdit_ConfigurationFieldsValuesCorrect() {	
        softly = new SoftAssertions();
        String expectedModalTitle = "Edit " + commChannelName;	
        String tabName = "Configuration";	
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);	
	
        editModal.getTabs().clickTabAndWait(tabName);	
	   
        softly.assertThat("IDLC").isEqualTo(editModal.getProtocolWrap().getValueChecked()); 	
        softly.assertThat("Yes").isEqualTo(editModal.getCarrierDetectWait().getCheckedValue());	
        softly.assertThat("544").isEqualTo(editModal.getCarrierDetectWaitTextBox().getInputValue());	
        softly.assertThat("Yes").isEqualTo(editModal.getEncryptionKey().getCheckedValue());	
        softly.assertThat("00112233445566778899aabbccddeeff").isEqualTo(editModal.getEncryptionKeyTextBox().getInputValue());	
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
    public void commChannelUdpEdit_NameAlreadyExistsValidation() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String commChannelNameUdp = "UDP Comm Channel " + timeStamp;
        String EXPECTED_MSG = "Name already exists";
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelUDP.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", commChannelNameUdp);
        jo.put("portNumber", randomNum.nextInt(65534));
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);

        String expectedModalTitle = "Edit " + commChannelName;
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getName().setInputValue(commChannelNameUdp);
        editModal.clickOk();
        
        String errorMsg = editModal.getName().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_Create_OpensCorrectModal() {
        String EXPECTED_CREATE_MODEL_TITLE = "Create Comm Channel";
        CreateCommChannelModal createModel = detailPage.showCreateCommChannelModal();
        String actualCreateModelTitle = createModel.getModalTitle();

        assertThat(EXPECTED_CREATE_MODEL_TITLE).isEqualTo(actualCreateModelTitle);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_ConfigTab_TimingSectionDisplayed() {
        String expectedModalTitle = "Edit " + commChannelName;
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);
        editModal.getTabs().clickTabAndWait(tabName);

        Section timing = editModal.getTimingSection();
        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_ConfigTab_SharingSectionDisplayed() {
        String expectedModalTitle = "Edit " + commChannelName;
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);
        editModal.getTabs().clickTabAndWait(tabName);
        
        editModal.getSharedPortType().moveTo();

        Section shared = editModal.getSharedSection();
        assertThat(shared.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })	
    public void commChannelUdpEdit_AllFieldsSuccess() {	
        softly = new SoftAssertions();
        randomNum = getRandomNum();
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "UDP " + timeStamp;
        String updateName = "Update UPD " + timeStamp;

        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelUDP.json";

        Integer portNum = randomNum.nextInt(65534);
        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", name);
        jo.put("portNumber", portNum);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        Integer id = createResponse.path("id");
        
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + id);
        
        String expectedModalTitle = "Edit " + name;	
        String baudRate = "BAUD_4800";	
        String configFieldsValues[] = { "55", "10", "20", "15", "500" };	
        String tabName = "Configuration";	
	
        portNum = randomNum.nextInt(65534);
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);	
        editModal.getName().setInputValue(updateName);	
        editModal.getBaudRate().selectItemByValue(baudRate);	
        editModal.getPortNumber().setInputValue(portNum.toString());
	
        editModal.getTabs().clickTabAndWait(tabName);
        
        editModal.getProtocolWrap().selectByValue("IDLC");
        editModal.getCarrierDetectWait().selectValue("No");	
        editModal.getEncryptionKey().selectValue("No");	
        editModal.getPreTxWait().setInputValue(configFieldsValues[0]);
        editModal.getRtsToTxWait().setInputValue(configFieldsValues[1]);	
        editModal.getPostTxWait().setInputValue(configFieldsValues[2]);	
        editModal.getReceiveDataWait().setInputValue(configFieldsValues[3]);	
        editModal.getAdditionalTimeOut().setInputValue(configFieldsValues[4]);	
        editModal.clickOkAndWaitForModalToClose();	
                       	
        String userMsg = detailPage.getUserMessage();        	
	
        ExtractableResponse<?> response = AssetsGetRequestAPI.getCommChannel(id.toString());	
        
        softly.assertThat(updateName + " saved successfully.").isEqualTo(userMsg);	
        softly.assertThat(updateName).isEqualTo(response.path("name").toString());
        softly.assertThat(portNum.toString()).isEqualTo(response.path("portNumber").toString());
        softly.assertThat(baudRate.toString()).isEqualTo(response.path("baudRate").toString());	
        softly.assertThat(response.path("keyInHex").toString()).isEqualTo("");       	
        softly.assertThat("0").isEqualTo(response.path("carrierDetectWaitInMilliseconds").toString());	
        softly.assertThat(configFieldsValues[0]).isEqualTo(response.path("timing.preTxWait").toString());	
        softly.assertThat(configFieldsValues[1]).isEqualTo(response.path("timing.rtsToTxWait").toString());	
        softly.assertThat(configFieldsValues[2]).isEqualTo(response.path("timing.postTxWait").toString());	
        softly.assertThat(configFieldsValues[3]).isEqualTo(response.path("timing.receiveDataWait").toString());	
        softly.assertThat(configFieldsValues[4]).isEqualTo(response.path("timing.extraTimeOut").toString());	
        softly.assertAll();	
    }	
}
