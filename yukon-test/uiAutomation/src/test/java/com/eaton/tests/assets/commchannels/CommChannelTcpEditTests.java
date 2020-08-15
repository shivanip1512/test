package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.elements.modals.commchannel.EditTcpCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelTcpDetailPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.assets.AssetsGetRequestAPI;
import com.eaton.rest.api.drsetup.JsonFileHelper;

import io.restassured.response.ExtractableResponse;

public class CommChannelTcpEditTests extends SeleniumTestSetup {

    private CommChannelTcpDetailPage detailPage;
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
        commChannelName = "TCP Comm Channel " + timeStamp;

        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", commChannelName);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        commChannelId = createResponse.path("id");
        
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + commChannelId);
        detailPage = new CommChannelTcpDetailPage(driverExt, commChannelId);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        refreshPage(detailPage);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_ModalTitleCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();
        String actualModalTitle = editModal.getModalTitle();
        
        assertThat(expectedModalTitle).isEqualTo(actualModalTitle);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_Name_RequiredValidation() {
        String EXPECTED_MSG = "Name is required.";
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getName().clearInputValue();
        editModal.clickOk();
        
        String errorMsg = editModal.getName().getValidationError(); 

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_Name_InvalidChars() {
        String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getName().setInputValue("/,tcp|");
        editModal.clickOkAndWait();
        
        String errorMsg = editModal.getName().getValidationError(); 

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_Name_AlreadyExists() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String commChannelNameTcp = "TCP Comm Channel " + timeStamp;
        String EXPECTED_MSG = "Name already exists";
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", commChannelNameTcp);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getName().setInputValue(commChannelNameTcp);
        editModal.clickOkAndWait();
        
        String errorMsg = editModal.getName().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_CancelNavigatesCorrectly() {
        String EXPECTED_TITLE = commChannelName;

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();
        editModal.clickCancelAndWait();

        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_TabLabelsCorrect() {
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        List<String> titles = editModal.getTabs().getTitles();

        softly.assertThat(2).isEqualTo(titles.size());
        softly.assertThat("Info").isEqualTo(titles.get(0));
        softly.assertThat("Configuration").isEqualTo(titles.get(1));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_InfoTab_LabelsCorrect() {
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        String tabName = "Info";
        editModal.getTabs().clickTabAndWait(tabName);

        List<String> labels = editModal.getTabs().getTabLabels(tabName);

        softly.assertThat(4).isEqualTo(labels.size());
        softly.assertThat("Name:").isEqualTo(labels.get(0));
        softly.assertThat("Type:").isEqualTo(labels.get(1));
        softly.assertThat("Baud Rate:").isEqualTo(labels.get(2));
        softly.assertThat("Status:").isEqualTo(labels.get(3));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_ConfigTab_LabelsCorrect() {
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        String tabName = "Configuration";
        editModal.getTabs().clickTabAndWait(tabName);

        List<String> labels = editModal.getTabs().getTabLabels("Configuration");

        softly.assertThat(5).isEqualTo(labels.size());
        softly.assertThat("Pre Tx Wait:").isEqualTo(labels.get(0));
        softly.assertThat("RTS To Tx Wait:").isEqualTo(labels.get(1));
        softly.assertThat("Post Tx Wait:").isEqualTo(labels.get(2));
        softly.assertThat("Receive Data Wait:").isEqualTo(labels.get(3));
        softly.assertThat("Additional Time Out:").isEqualTo(labels.get(4));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_ConfigTab_ValuesCorrect() {
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        String tabName = "Configuration";
        editModal.getTabs().clickTabAndWait(tabName);
        
        softly.assertThat("25").isEqualTo(editModal.getPreTxWait().getInputValue());
        softly.assertThat("0").isEqualTo(editModal.getRtsToTxWait().getInputValue());
        softly.assertThat("10").isEqualTo(editModal.getPostTxWait().getInputValue());
        softly.assertThat("0").isEqualTo(editModal.getReceiveDataWait().getInputValue());
        softly.assertThat("0").isEqualTo(editModal.getAdditionalTimeOut().getInputValue());
        softly.assertAll();       
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_PreTxWait_MinValueValidation() {
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getPreTxWait().getValidationError();
        
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_PreTxWait_MaxValueValidation() {
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("10000001");
        editModal.clickOk();

        String errorMsg = editModal.getPreTxWait().getValidationError();
                
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_RtsToTxWait_MinValueValidation() {
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("-1");
        editModal.clickOk();
        
        String errorMsg = editModal.getRtsToTxWait().getValidationError(); 

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_RtsToTxWait_MaxValueValidation() {
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("10000001");
        editModal.clickOk();

        String errorMsg = editModal.getRtsToTxWait().getValidationError();
        
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_PostTxWai_MinValueValidation() {
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("-1");
        editModal.clickOk();
        
        String errorMsg = editModal.getPostTxWait().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_PostTxWait_MaxValueValidation() {
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("10000001");
        editModal.clickOk();
        
        String errorMsg = editModal.getPostTxWait().getValidationError(); 

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_ReceiveDataWait_MinValueValidation() {
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("-1");
        editModal.clickOk();
        
        String errorMsg = editModal.getReceiveDataWait().getValidationError();
                
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_ReceiveDataWait_MaxValueValidation() {
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("1001");
        editModal.clickOk();

        String errorMsg = editModal.getReceiveDataWait().getValidationError();
                
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_AdditionalTimeOut_MinValueValidation() {
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getAdditionalTimeOut().getValidationError();
                
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_AdditionalTimeOut_MaxValueValidation() {
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("1000");
        editModal.clickOk();;

        String errorMsg = editModal.getAdditionalTimeOut().getValidationError(); 
                
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_InfoTab_ValuesCorrect() {
        String tabName = "Info";
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);

        softly.assertThat(commChannelName).isEqualTo(editModal.getName().getInputValue());
        softly.assertThat("1200").isEqualTo(editModal.getBaudRate().getSelectedValue());
        softly.assertThat("Enabled").isEqualTo(editModal.getStatus().getCheckedValue());
        softly.assertAll();        
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_ConfigTab_TimingSectionDisplayed() {
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();
        editModal.getTabs().clickTabAndWait(tabName);

        Section timing = editModal.getTimingSection();
        
        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_AllFieldsSuccess() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "TCP  " + timeStamp;

        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", name);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        Integer id = createResponse.path("id");
        
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + id);
        
        String editName = "Edit TCP " + timeStamp;
        String baudRate = "BAUD_4800";
        String configFieldsValues[] = { "55", "10", "20", "15", "500" };
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();
        editModal.getName().setInputValue(editName);
        editModal.getBaudRate().selectItemByValue(baudRate);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue(configFieldsValues[0]);
        editModal.getRtsToTxWait().setInputValue(configFieldsValues[1]);
        editModal.getPostTxWait().setInputValue(configFieldsValues[2]);
        editModal.getReceiveDataWait().setInputValue(configFieldsValues[3]);
        editModal.getAdditionalTimeOut().setInputValue(configFieldsValues[4]);
        editModal.clickOkAndWaitForModalToClose();
                       
        String userMsg = detailPage.getUserMessage();        

        ExtractableResponse<?> response = AssetsGetRequestAPI.getCommChannel(id.toString());
        
        softly.assertThat(editName + " saved successfully.").isEqualTo(userMsg);
        softly.assertThat(editName).isEqualTo(response.path("name").toString());               
        softly.assertThat(baudRate).isEqualTo(response.path("baudRate").toString());
        softly.assertThat(configFieldsValues[0]).isEqualTo(response.path("timing.preTxWait").toString());
        softly.assertThat(configFieldsValues[1]).isEqualTo(response.path("timing.rtsToTxWait").toString());
        softly.assertThat(configFieldsValues[2]).isEqualTo(response.path("timing.postTxWait").toString());
        softly.assertThat(configFieldsValues[3]).isEqualTo(response.path("timing.receiveDataWait").toString());
        softly.assertThat(configFieldsValues[4]).isEqualTo(response.path("timing.extraTimeOut").toString());
        softly.assertAll();
    }
}
