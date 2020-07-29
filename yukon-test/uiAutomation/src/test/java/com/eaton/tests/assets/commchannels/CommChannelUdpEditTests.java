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
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + commChannelId);
        detailPage = new CommChannelUdpDetailPage(driverExt, commChannelId);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_ModalTitleCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);
        
        String actualModalTitle = editModal.getModalTitle();
        
        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_Name_RequiredValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Name is required.";
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getName().setInputValue(" ");
        editModal.clickOkAndWait();

        assertThat(editModal.getName().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelUdpEdit_Name_InvalidChars() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getName().setInputValue("/,udp|");
        editModal.clickOkAndWait();

        assertThat(editModal.getName().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_PortNumber_MinValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";
        String portNumber = "0";
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getPortNumber().setInputValue(portNumber);
        editModal.clickOkAndWait();

        assertThat(editModal.getPortNumber().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_PortNumber_MaxValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";
        String portNumber = "65536";
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getPortNumber().setInputValue(portNumber);
        editModal.clickOkAndWait();

        assertThat(editModal.getPortNumber().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_PreTxWait_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        
        editModal.getPreTxWait().setInputValue("-1");
        editModal.clickOkAndWait();

        assertThat(editModal.getPreTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_PreTxWait_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("10000001");
        editModal.clickOkAndWait();

        assertThat(editModal.getPreTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_RtsToTxWait_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("-1");
        editModal.clickOkAndWait();

        assertThat(editModal.getRtsToTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_RtsToTxWait_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("10000001");
        editModal.clickOkAndWait();

        assertThat(editModal.getRtsToTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_PostTxWait_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("-1");
        editModal.clickOkAndWait();

        assertThat(editModal.getPostTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_PostTxWait_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("10000001");
        editModal.clickOkAndWait();

        assertThat(editModal.getPostTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_ReceiveDataWait_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("-1");
        editModal.clickOkAndWait();

        assertThat(editModal.getReceiveDataWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_ReceiveDataWait_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("1001");
        editModal.clickOkAndWait();

        assertThat(editModal.getReceiveDataWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_AdditionalTimeOut_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("-1");
        editModal.clickOkAndWait();

        assertThat(editModal.getAdditionalTimeOut().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_AdditionalTimeOut_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("1000");
        editModal.clickOkAndWait();

        assertThat(editModal.getAdditionalTimeOut().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_SocketNumber_ACS_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue("0");
        editModal.clickOkAndWait();

        assertThat(editModal.getSocketNumber().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_SocketNumber_ACS_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue("65536");
        editModal.clickOkAndWait();

        assertThat(editModal.getSocketNumber().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_SocketNumber_ACS_BlankValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue(" ");
        editModal.clickOkAndWait();

        assertThat(editModal.getSocketNumber().getValidationError()).isEqualTo(EXPECTED_MSG);
    }
    

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_SocketNumber_ILEX_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue("0");
        editModal.clickOkAndWait();

        assertThat(editModal.getSocketNumber().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_SocketNumber_ILEX_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue("65536");
        editModal.clickOkAndWait();

        assertThat(editModal.getSocketNumber().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_SocketNumber_ILEX_BlankValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Socket Number must be between 1 and 65,535.";
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getSocketNumber().setInputValue(" ");
        editModal.clickOkAndWait();

        assertThat(editModal.getSocketNumber().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_TabLabelsCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        List<String> titles = editModal.getTabs().getTitles();

        softly.assertThat(titles.size()).isEqualTo(2);
        softly.assertThat(titles.get(0)).isEqualTo("Info");
        softly.assertThat(titles.get(1)).isEqualTo("Configuration");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_ConfigurationLabelsCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        String tabName = "Configuration";
        editModal.getTabs().clickTabAndWait(tabName);

        List<String> labels = editModal.getTabs().getTabLabels("Configuration");
        softly.assertThat(labels.size()).isEqualTo(10);

        softly.assertThat(labels.get(0)).isEqualTo("Protocol Wrap:");
        softly.assertThat(labels.get(1)).isEqualTo("Carrier Detect Wait:");
        softly.assertThat(labels.get(2)).isEqualTo("Encryption key:");
        softly.assertThat(labels.get(3)).isEqualTo("Pre Tx Wait:");
        softly.assertThat(labels.get(4)).isEqualTo("RTS To Tx Wait:");
        softly.assertThat(labels.get(5)).isEqualTo("Post Tx Wait:");
        softly.assertThat(labels.get(6)).isEqualTo("Receive Data Wait:");
        softly.assertThat(labels.get(7)).isEqualTo("Additional Time Out:");
        softly.assertThat(labels.get(8)).isEqualTo("Shared Port Type:");
        softly.assertThat(labels.get(9)).isEqualTo("Socket Number:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_InfoTabLabelsCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        String tabName = "Info";
        editModal.getTabs().clickTabAndWait(tabName);

        List<String> labels = editModal.getTabs().getTabLabels(tabName);
        softly.assertThat(labels.size()).isEqualTo(5);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).isEqualTo("Type:");
        softly.assertThat(labels.get(2)).isEqualTo("Port Number:");
        softly.assertThat(labels.get(3)).isEqualTo("Baud Rate:");

        softly.assertThat(labels.get(4)).isEqualTo("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_InfoFieldsValuesCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        String tabName = "Info";
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);

        softly.assertThat(editModal.getName().getInputValue()).isEqualTo(commChannelName);
        softly.assertThat(editModal.getPortNumber().getInputValue()).isEqualTo(portNumber.toString());
        softly.assertThat(editModal.getBaudRate().getSelectedValue()).isEqualTo("2400");
        softly.assertThat(editModal.getStatus().getCheckedValue()).isEqualTo("Enabled");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })	
    public void commChannelUdpEdit_ConfigurationFieldsValuesCorrect() {	
        String expectedModalTitle = "Edit " + commChannelName;	
        String tabName = "Configuration";	
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);	
	
        editModal.getTabs().clickTabAndWait(tabName);	
	   
        softly.assertThat(editModal.getProtocolWrap().getValueChecked()).isEqualTo("IDLC"); 	
        softly.assertThat(editModal.getCarrierDetectWait().getCheckedValue()).isEqualTo("Yes");	
        softly.assertThat(editModal.getCarrierDetectWaitTextBox().getInputValue()).isEqualTo("544");	
        softly.assertThat(editModal.getEncryptionKey().getCheckedValue()).isEqualTo("Yes");	
        softly.assertThat(editModal.getEncryptionKeyTextBox().getInputValue()).isEqualTo("00112233445566778899aabbccddeeff");	
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
    public void commChannelUdpEdit_CancelNavigatesCorrectly() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_TITLE = commChannelName;

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);
        editModal.clickCancelAndWait();

        String actualPageTitle = detailPage.getPageTitle();
        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_NameAlreadyExists() {
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
        editModal.clickOkAndWait();

        assertThat(editModal.getName().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_CreateOpensPopupCorrect() {
        String EXPECTED_CREATE_MODEL_TITLE = "Create Comm Channel";
        CreateCommChannelModal createModel = detailPage.showCreateCommChannelModal();
        String actualCreateModelTitle = createModel.getModalTitle();

        assertThat(actualCreateModelTitle).isEqualTo(EXPECTED_CREATE_MODEL_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_ConfigTabTimingSectionDisplayed() {
        String expectedModalTitle = "Edit " + commChannelName;
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);
        editModal.getTabs().clickTabAndWait(tabName);

        Section timing = editModal.getTimingSection();
        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelUdpEdit_ConfigTabSharingSectionDisplayed() {
        String expectedModalTitle = "Edit " + commChannelName;
        String tabName = "Configuration";

        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);
        editModal.getTabs().clickTabAndWait(tabName);
        
        editModal.getSharedPortType().moveTo();

        Section shared = editModal.getSharedSection();
        assertThat(shared.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })	
    public void commChannelUdpEdit_UpdateAllFieldsSuccess() {	
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String expectedModalTitle = "Edit " + commChannelName;	
        String commChannelName = "CommChannel_Udp_Updatepe " + timeStamp;	
        String baudRate = "4800";	
        String configFieldsValues[] = { "55", "10", "20", "15", "500" };	
        String tabName = "Configuration";	
	
        EditUdpCommChannelModal editModal = detailPage.showUdpCommChannelEditModal(expectedModalTitle);	
        editModal.getName().setInputValue(commChannelName);	
        editModal.getBaudRate().selectItemByText(baudRate);	
	
        editModal.getTabs().clickTabAndWait(tabName);
        
        editModal.getProtocolWrap().setByValue("IDLC", true);
        editModal.getCarrierDetectWait().setValue(false);	
        editModal.getEncryptionKey().setValue(false);	
        editModal.getPreTxWait().setInputValue(configFieldsValues[0]);
        editModal.getRtsToTxWait().setInputValue(configFieldsValues[1]);	
        editModal.getPostTxWait().setInputValue(configFieldsValues[2]);	
        editModal.getReceiveDataWait().setInputValue(configFieldsValues[3]);	
        editModal.getAdditionalTimeOut().setInputValue(configFieldsValues[4]);	
        editModal.clickOkAndWait();	
                       	
        String userMsg = detailPage.getUserMessage();        	
	
        ExtractableResponse<?> response = AssetsGetRequestAPI.getCommChannel(commChannelId.toString());	     	
        softly.assertThat(userMsg).isEqualTo(commChannelName + " saved successfully.");	
        softly.assertThat(response.path("name").toString()).isEqualTo(commChannelName);	
        softly.assertThat(response.path("baudRate").toString()).isEqualTo("BAUD_"+baudRate);	
        softly.assertThat(response.path("keyInHex").toString()).isEqualTo("");       	
        softly.assertThat(response.path("carrierDetectWaitInMilliseconds").toString()).isEqualTo("0");	
        softly.assertThat(response.path("timing.preTxWait").toString()).isEqualTo((configFieldsValues[0]));	
        softly.assertThat(response.path("timing.rtsToTxWait").toString()).isEqualTo((configFieldsValues[1]));	
        softly.assertThat(response.path("timing.postTxWait").toString()).isEqualTo((configFieldsValues[2]));	
        softly.assertThat(response.path("timing.receiveDataWait").toString()).isEqualTo((configFieldsValues[3]));	
        softly.assertThat(response.path("timing.extraTimeOut").toString()).isEqualTo((configFieldsValues[4]));	
        softly.assertAll();	
    }	


}
